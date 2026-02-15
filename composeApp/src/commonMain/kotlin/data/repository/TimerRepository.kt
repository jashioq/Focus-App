package data.repository

import domain.model.TIMER_STATE_KEY
import domain.model.Timer
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
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.notification.LiveTimerNotification
import util.currentTimeSeconds

class TimerRepository(
    private val liveTimerNotification: LiveTimerNotification,
    private val dataStoreRepository: DataStoreRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) : domain.repository.TimerRepository {

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
        val pausedTimer = current.copy(isPaused = true)
        _timerFlow.value = pausedTimer
        if (!notificationDismissed) liveTimerNotification.set(pausedTimer)
        scope.launch { saveTimerState(pausedTimer) }
    }

    override fun skipBlock() {
        val current = _timerFlow.value ?: return
        val position = current.getCurrentBlock() ?: return
        val blockEndSeconds = position.blockStartSeconds + position.block.seconds
        if (blockEndSeconds >= current.totalTime) {
            stop()
        } else {
            val skippedTimer = current.copy(secondsElapsed = blockEndSeconds)
            _timerFlow.value = skippedTimer
            if (!notificationDismissed) liveTimerNotification.set(skippedTimer)
            scope.launch { saveTimerState(skippedTimer) }
        }
    }

    override fun extendBlock(seconds: Int) {
        val current = _timerFlow.value ?: return
        val position = current.getCurrentBlock() ?: return
        val newSequence = current.sequence.toMutableList()
        newSequence[position.index] = position.block.copy(seconds = position.block.seconds + seconds)
        val extendedTimer = current.copy(sequence = newSequence)
        _timerFlow.value = extendedTimer
        if (!notificationDismissed) liveTimerNotification.set(extendedTimer)
        scope.launch { saveTimerState(extendedTimer) }
    }

    override fun resume() {
        val current = _timerFlow.value ?: return
        val resumedTimer = current.copy(isPaused = false)
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
                _timerFlow.value = current.copy(secondsElapsed = newSecondsElapsed)
            }
        }
    }

    @Serializable
    private data class PersistedTimerState(
        val timer: Timer,
        val timestamp: Double,
        val notificationDismissed: Boolean,
    )

    private suspend fun saveTimerState(timer: Timer) {
        val state = PersistedTimerState(
            timer = timer,
            timestamp = currentTimeSeconds(),
            notificationDismissed = notificationDismissed,
        )
        dataStoreRepository.putStringPreference(TIMER_STATE_KEY, Json.encodeToString(state))
    }

    private suspend fun clearTimerState() {
        dataStoreRepository.putStringPreference(TIMER_STATE_KEY, "")
    }

    private suspend fun restoreTimerState(): Timer? {
        val serialized = dataStoreRepository.emitStringPreference(TIMER_STATE_KEY, "")
            .getOrNull()?.first() ?: return null
        if (serialized.isBlank()) return null

        val state = runCatching { Json.decodeFromString<PersistedTimerState>(serialized) }
            .getOrNull() ?: return null

        notificationDismissed = state.notificationDismissed

        val now = currentTimeSeconds()
        val actualElapsed = if (state.timer.isPaused) {
            state.timer.secondsElapsed
        } else {
            state.timer.secondsElapsed + (now - state.timestamp).toInt()
        }

        if (actualElapsed >= state.timer.totalTime) {
            clearTimerState()
            return null
        }

        return state.timer.copy(secondsElapsed = actualElapsed)
    }
}
