package presentation.screen.home.viewModel

import domain.model.Timer
import domain.model.TimerBlock
import domain.model.TimerMode
import domain.util.UseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import presentation.screen.home.HomeScreenAction
import presentation.screen.home.HomeScreenState
import presentation.util.CoreViewModel
import util.Logger

class HomeScreenViewModel(
    private val startTimerUseCase: UseCase<Timer, Unit>,
    private val stopTimerUseCase: UseCase<Unit, Unit>,
    private val pauseTimerUseCase: UseCase<Unit, Unit>,
    private val resumeTimerUseCase: UseCase<Unit, Unit>,
    private val emitTimerFlowUseCase: UseCase<Unit, Flow<Timer?>>,
    scope: CoroutineScope? = null,
    logger: Logger? = null,
) : CoreViewModel<HomeScreenState, HomeScreenAction>(
    initialState = HomeScreenState(),
    scope = scope,
    logger = logger,
) {
    private val timerSequence = listOf(
        TimerBlock(mode = TimerMode.FOCUS, seconds = 30),
        TimerBlock(mode = TimerMode.BREAK, seconds = 30),
        TimerBlock(mode = TimerMode.FOCUS, seconds = 70),
    )

    init {
        vmScope.launch {
            emitTimerFlowUseCase.call(Unit).onSuccess { flow ->
                flow.collect { timer ->
                    stateFlow.update {
                        if (timer == null) {
                            HomeScreenState()
                        } else {
                            val remaining = timer.totalTime - timer.secondsElapsed
                            val m = remaining / 60
                            val s = remaining % 60
                            HomeScreenState(
                                timerText = "$m:$s",
                                isRunning = true,
                                isPaused = timer.isPaused,
                            )
                        }
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
        val timer = Timer(
            sequence = timerSequence,
            secondsElapsed = 0,
            isPaused = false,
        )
        vmScope.launch { startTimerUseCase.call(timer) }
    }

    private fun dismissNotification() {
        vmScope.launch { stopTimerUseCase.call(Unit) }
    }

    private fun togglePausePlay() {
        vmScope.launch {
            if (stateFlow.value.isPaused) {
                resumeTimerUseCase.call(Unit)
            } else {
                pauseTimerUseCase.call(Unit)
            }
        }
    }
}
