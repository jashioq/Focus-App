package data.repository

import domain.model.Timer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import platform.notification.LiveTimerNotification

class TimerRepository(
    private val liveTimerNotification: LiveTimerNotification,
) : domain.repository.TimerRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _timerFlow = MutableStateFlow<Timer?>(null)
    private var countdownJob: Job? = null

    override val timerFlow: Flow<Timer?> = _timerFlow.asStateFlow()

    init {
        scope.launch {
            liveTimerNotification.timerUpdateFlow.collect { timer ->
                _timerFlow.value = timer
                countdownJob?.cancel()
                if (!timer.isPaused) {
                    startCountdown()
                }
            }
        }
    }

    override fun start(timer: Timer) {
        _timerFlow.value = timer
        liveTimerNotification.start(timer)
        countdownJob?.cancel()
        if (!timer.isPaused) {
            startCountdown()
        }
    }

    override fun stop() {
        countdownJob?.cancel()
        _timerFlow.value = null
        liveTimerNotification.stop()
    }

    override fun pause() {
        val current = _timerFlow.value ?: return
        countdownJob?.cancel()
        _timerFlow.value = Timer(
            sequence = current.sequence,
            secondsElapsed = current.secondsElapsed,
            isPaused = true,
        )
        liveTimerNotification.pause()
    }

    override fun resume() {
        val current = _timerFlow.value ?: return
        _timerFlow.value = Timer(
            sequence = current.sequence,
            secondsElapsed = current.secondsElapsed,
            isPaused = false,
        )
        liveTimerNotification.resume()
        startCountdown()
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
}
