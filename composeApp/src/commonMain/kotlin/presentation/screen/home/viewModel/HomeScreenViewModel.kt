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
    private val skipBlockUseCase: UseCase<Unit, Unit>,
    scope: CoroutineScope? = null,
    logger: Logger? = null,
) : CoreViewModel<HomeScreenState, HomeScreenAction>(
    initialState = HomeScreenState(),
    scope = scope,
    logger = logger,
) {
    private val timerSequence = listOf(
        TimerBlock(mode = TimerMode.FOCUS, seconds = 150),
        TimerBlock(mode = TimerMode.BREAK, seconds = 100),
        TimerBlock(mode = TimerMode.FOCUS, seconds = 200),
        TimerBlock(mode = TimerMode.BREAK, seconds = 150),
        TimerBlock(mode = TimerMode.FOCUS, seconds = 100),
    )

    init {
        vmScope.launch {
            emitTimerFlowUseCase.call(Unit).onSuccess { flow ->
                flow.collect { timer ->
                    stateFlow.update {
                        if (timer == null) {
                            HomeScreenState()
                        } else {
                            val (block, secondsInBlock) = timer.getCurrentBlock()
                                ?: return@collect stateFlow.update { HomeScreenState() }

                            val remaining = block.seconds - secondsInBlock
                            val m = remaining / 60
                            val s = remaining % 60
                            val timerText = "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"

                            val blockProgress = if (block.seconds > 0) {
                                ((secondsInBlock + 1).toFloat() / block.seconds.toFloat()).coerceIn(0f, 1f)
                            } else {
                                0f
                            }
                            val progress = when (block.mode) {
                                TimerMode.FOCUS -> blockProgress
                                TimerMode.BREAK -> 1f - blockProgress
                            }

                            val blockLabel = when (block.mode) {
                                TimerMode.FOCUS -> "Focus"
                                TimerMode.BREAK -> "Break"
                            }

                            HomeScreenState(
                                timerText = timerText,
                                isRunning = true,
                                isPaused = timer.isPaused,
                                progress = progress,
                                blockLabel = blockLabel,
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
            HomeScreenAction.SkipBlock -> skipBlock()
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

    private fun skipBlock() {
        vmScope.launch { skipBlockUseCase.call(Unit) }
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
