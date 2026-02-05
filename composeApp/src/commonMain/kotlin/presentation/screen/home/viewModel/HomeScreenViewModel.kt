package presentation.screen.home.viewModel

import domain.useCase.StartLiveTimerNotificationParams
import domain.useCase.UpdateLiveTimerNotificationParams
import domain.util.UseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import platform.model.TimerMode
import platform.model.TimerToggleState
import presentation.screen.home.HomeScreenAction
import presentation.screen.home.HomeScreenState
import presentation.util.CoreViewModel
import util.Logger

class HomeScreenViewModel(
    private val startLiveTimerNotificationUseCase: UseCase<StartLiveTimerNotificationParams, Unit>,
    private val updateLiveTimerNotificationUseCase: UseCase<UpdateLiveTimerNotificationParams, Unit>,
    private val stopLiveTimerNotificationUseCase: UseCase<Unit, Unit>,
    private val emitTimerToggleStateUseCase: UseCase<Unit, Flow<TimerToggleState>>,
    scope: CoroutineScope? = null,
    logger: Logger? = null,
) : CoreViewModel<HomeScreenState, HomeScreenAction>(
    initialState = HomeScreenState(),
    scope = scope,
    logger = logger,
) {
    private companion object {
        const val FOCUS_DURATION_SECONDS = 40
        const val BREAK_DURATION_SECONDS = 20
    }

    private var timerJob: Job? = null
    private var currentMode: TimerMode = TimerMode.FOCUS
    private var timeLeftSeconds: Int = FOCUS_DURATION_SECONDS

    init {
        vmScope.launch {
            emitTimerToggleStateUseCase.call(Unit).onSuccess { toggleFlow ->
                toggleFlow.collect { toggleState ->
                    when (toggleState) {
                        TimerToggleState.PAUSE_REQUESTED -> pauseLocal()
                        TimerToggleState.RESUME_REQUESTED -> resumeLocal()
                    }
                }
            }
        }
    }

    override fun HomeScreenAction.process() {
        when (this) {
            HomeScreenAction.ShowNotification -> showNotification()
            HomeScreenAction.DismissNotification -> dismissNotification()
            HomeScreenAction.TogglePausePlay -> togglePausePlay()
        }
    }

    private fun showNotification() {
        if (stateFlow.value.isRunning) return

        currentMode = TimerMode.FOCUS
        timeLeftSeconds = FOCUS_DURATION_SECONDS

        stateFlow.update {
            it.copy(
                isRunning = true,
                isPaused = false,
                timerText = formatTime(timeLeftSeconds),
            )
        }

        vmScope.launch {
            startLiveTimerNotificationUseCase.call(
                StartLiveTimerNotificationParams(
                    mode = currentMode,
                    timeLeftSeconds = timeLeftSeconds,
                    totalTimeSeconds = FOCUS_DURATION_SECONDS,
                    isPaused = false,
                )
            )
        }

        startCountdown()
    }

    private fun dismissNotification() {
        timerJob?.cancel()
        timerJob = null

        vmScope.launch {
            stopLiveTimerNotificationUseCase.call(Unit)
        }

        stateFlow.update {
            HomeScreenState()
        }
    }

    private fun togglePausePlay() {
        if (!stateFlow.value.isRunning) return

        if (stateFlow.value.isPaused) {
            resumeLocal()
        } else {
            pauseLocal()
        }
    }

    private fun pauseLocal() {
        if (!stateFlow.value.isRunning || stateFlow.value.isPaused) return

        timerJob?.cancel()
        stateFlow.update { it.copy(isPaused = true) }

        vmScope.launch {
            updateLiveTimerNotificationUseCase.call(
                UpdateLiveTimerNotificationParams(isPaused = true)
            )
        }
    }

    private fun resumeLocal() {
        if (!stateFlow.value.isRunning || !stateFlow.value.isPaused) return

        stateFlow.update { it.copy(isPaused = false) }

        vmScope.launch {
            updateLiveTimerNotificationUseCase.call(
                UpdateLiveTimerNotificationParams(
                    timeLeftSeconds = timeLeftSeconds,
                    isPaused = false,
                )
            )
        }

        startCountdown()
    }

    private fun startCountdown() {
        timerJob?.cancel()
        timerJob = vmScope.launch {
            while (timeLeftSeconds > 0) {
                delay(1000)
                timeLeftSeconds--
                stateFlow.update {
                    it.copy(timerText = formatTime(timeLeftSeconds))
                }
            }
            switchMode()
        }
    }

    private fun switchMode() {
        currentMode = if (currentMode == TimerMode.FOCUS) TimerMode.BREAK else TimerMode.FOCUS
        val newTotalTime = if (currentMode == TimerMode.FOCUS) FOCUS_DURATION_SECONDS else BREAK_DURATION_SECONDS
        timeLeftSeconds = newTotalTime

        stateFlow.update {
            it.copy(timerText = formatTime(timeLeftSeconds))
        }

        vmScope.launch {
            updateLiveTimerNotificationUseCase.call(
                UpdateLiveTimerNotificationParams(
                    mode = currentMode,
                    timeLeftSeconds = timeLeftSeconds,
                    totalTimeSeconds = newTotalTime,
                )
            )
        }

        startCountdown()
    }

    private fun formatTime(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return String.format("%02d:%02d", m, s)
    }
}
