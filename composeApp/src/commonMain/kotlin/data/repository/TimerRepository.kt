package data.repository

import domain.model.TIMER_STATE_KEY
import domain.model.Timer
import domain.model.TimerBlock
import domain.model.TimerMode
import domain.repository.DataStoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import platform.notification.LiveTimerNotification
import util.currentTimeSeconds

class TimerRepository(
    private val liveTimerNotification: LiveTimerNotification,
    private val dataStoreRepository: DataStoreRepository,
) : domain.repository.TimerRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _timerFlow = MutableStateFlow<Timer?>(null)
    private var countdownJob: Job? = null
    private var notificationDismissed: Boolean = false

    override val timerFlow: Flow<Timer?> = _timerFlow.asStateFlow()

    init {
        // 1. Restore from DataStore (single source of truth)
        scope.launch {
            val restored = restoreTimerState() ?: return@launch
            _timerFlow.value = restored
            if (!restored.isPaused) startCountdown()

            if (!notificationDismissed) {
                if (liveTimerNotification.isNotificationActive()) {
                    liveTimerNotification.set(restored)
                } else {
                    notificationDismissed = true
                    saveTimerState(restored)
                }
            }
        }

        // 2. Notification/activity toggle button pressed
        scope.launch {
            liveTimerNotification.timerToggleFlow.collect {
                val current = _timerFlow.value ?: return@collect
                if (current.isPaused) resume() else pause()
            }
        }

        // 3. Notification/live activity dismissed while app is running
        scope.launch {
            liveTimerNotification.notificationDismissedFlow.collect {
                notificationDismissed = true
                val current = _timerFlow.value ?: return@collect
                saveTimerState(current)
            }
        }
    }

    override fun start(timer: Timer) {
        notificationDismissed = false
        _timerFlow.value = timer
        liveTimerNotification.set(timer)
        countdownJob?.cancel()
        if (!timer.isPaused) {
            startCountdown()
        }
        scope.launch { saveTimerState(timer) }
    }

    override fun stop() {
        countdownJob?.cancel()
        _timerFlow.value = null
        liveTimerNotification.clear()
        scope.launch { clearTimerState() }
    }

    override fun pause() {
        val current = _timerFlow.value ?: return
        countdownJob?.cancel()
        val pausedTimer = Timer(
            sequence = current.sequence,
            secondsElapsed = current.secondsElapsed,
            isPaused = true,
        )
        _timerFlow.value = pausedTimer
        if (!notificationDismissed) liveTimerNotification.set(pausedTimer)
        scope.launch { saveTimerState(pausedTimer) }
    }

    override fun skipBlock() {
        val current = _timerFlow.value ?: return
        var accumulatedSeconds = 0
        for (block in current.sequence) {
            accumulatedSeconds += block.seconds
            if (current.secondsElapsed < accumulatedSeconds) {
                if (accumulatedSeconds >= current.totalTime) {
                    stop()
                } else {
                    val skippedTimer = Timer(
                        sequence = current.sequence,
                        secondsElapsed = accumulatedSeconds,
                        isPaused = current.isPaused,
                    )
                    _timerFlow.value = skippedTimer
                    if (!notificationDismissed) liveTimerNotification.set(skippedTimer)
                    scope.launch { saveTimerState(skippedTimer) }
                }
                return
            }
        }
    }

    override fun resume() {
        val current = _timerFlow.value ?: return
        val resumedTimer = Timer(
            sequence = current.sequence,
            secondsElapsed = current.secondsElapsed,
            isPaused = false,
        )
        _timerFlow.value = resumedTimer
        if (!notificationDismissed) liveTimerNotification.set(resumedTimer)
        startCountdown()
        scope.launch { saveTimerState(resumedTimer) }
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = scope.launch {
            while (true) {
                delay(1000)
                val current = _timerFlow.value ?: break
                if (current.isPaused) break
                val newSecondsElapsed = current.secondsElapsed + 1
                if (newSecondsElapsed >= current.totalTime) {
                    stop()
                    break
                }
                _timerFlow.value = Timer(
                    sequence = current.sequence,
                    secondsElapsed = newSecondsElapsed,
                    isPaused = false,
                )
            }
        }
    }

    private suspend fun saveTimerState(timer: Timer) {
        val blockSeconds = timer.sequence.joinToString(",") { it.seconds.toString() }
        val blockModes = timer.sequence.joinToString(",") { it.mode.name }
        val timestamp = currentTimeSeconds()
        val serialized = "$blockSeconds|$blockModes|${timer.secondsElapsed}|${timer.isPaused}|$timestamp|$notificationDismissed"
        dataStoreRepository.putStringPreference(TIMER_STATE_KEY, serialized)
    }

    private suspend fun clearTimerState() {
        dataStoreRepository.putStringPreference(TIMER_STATE_KEY, "")
    }

    private suspend fun restoreTimerState(): Timer? {
        val serialized = dataStoreRepository.emitStringPreference(TIMER_STATE_KEY, "")
            .getOrNull()?.first() ?: return null
        if (serialized.isBlank()) return null

        val parts = serialized.split("|")
        if (parts.size < 6) return null

        val blockSecondsList = parts[0].split(",").mapNotNull { it.toIntOrNull() }
        val blockModesList = parts[1].split(",").mapNotNull {
            runCatching { TimerMode.valueOf(it) }.getOrNull()
        }
        if (blockSecondsList.isEmpty() || blockSecondsList.size != blockModesList.size) return null

        val secondsElapsed = parts[2].toIntOrNull() ?: return null
        val isPaused = parts[3].toBooleanStrictOrNull() ?: return null
        val savedTimestamp = parts[4].toDoubleOrNull() ?: return null
        notificationDismissed = parts[5].toBooleanStrictOrNull() ?: false

        val sequence = blockSecondsList.zip(blockModesList) { s, m -> TimerBlock(m, s) }
        val totalTime = sequence.sumOf { it.seconds }

        val now = currentTimeSeconds()
        val actualElapsed = if (isPaused) {
            secondsElapsed
        } else {
            secondsElapsed + (now - savedTimestamp).toInt()
        }

        if (actualElapsed >= totalTime) {
            clearTimerState()
            return null
        }

        return Timer(
            sequence = sequence,
            secondsElapsed = actualElapsed,
            isPaused = isPaused,
        )
    }
}
