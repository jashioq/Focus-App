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
    private val extendBlockUseCase: UseCase<Int, Unit>,
    scope: CoroutineScope? = null,
    logger: Logger? = null,
) : CoreViewModel<HomeScreenState, HomeScreenAction>(
    initialState = HomeScreenState(),
    scope = scope,
    logger = logger,
) {
    private val timerSequence = listOf(
        TimerBlock(mode = TimerMode.FOCUS, seconds = 1800),
        TimerBlock(mode = TimerMode.BREAK, seconds = 600),
        TimerBlock(mode = TimerMode.FOCUS, seconds = 1800),
        TimerBlock(mode = TimerMode.BREAK, seconds = 600),
        TimerBlock(mode = TimerMode.FOCUS, seconds = 1800),
    )

    init {
        vmScope.launch {
            emitTimerFlowUseCase.call(Unit).onSuccess { flow ->
                flow.collect { timer ->
                    if (timer == null) {
                        stateFlow.update { HomeScreenState() }
                        return@collect
                    }

                    val position = timer.getCurrentBlock()
                        ?: return@collect stateFlow.update { HomeScreenState() }

                    val remaining = position.block.seconds - position.secondsInBlock
                    val m = remaining / 60
                    val s = remaining % 60
                    val timerText = "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"

                    val blockProgress = if (position.block.seconds > 0) {
                        ((position.secondsInBlock + 1).toFloat() / position.block.seconds.toFloat()).coerceIn(0f, 1f)
                    } else {
                        0f
                    }
                    val progress = when (position.block.mode) {
                        TimerMode.FOCUS -> blockProgress
                        TimerMode.BREAK -> 1f - blockProgress
                    }

                    val blockLabel = when (position.block.mode) {
                        TimerMode.FOCUS -> "Focus"
                        TimerMode.BREAK -> "Break"
                    }

                    stateFlow.update { current ->
                        val blockChanged = current.isRunning && current.blockLabel != blockLabel
                        val extendPressCount = if (blockChanged) 0 else current.extendPressCount

                        HomeScreenState(
                            timerText = timerText,
                            isRunning = true,
                            isPaused = timer.isPaused,
                            progress = progress,
                            blockLabel = blockLabel,
                            extendPressCount = extendPressCount,
                            addButtonText = addButtonText(extendPressCount, blockLabel),
                        )
                    }
                }
            }
        }
    }

    override fun HomeScreenAction.process() {
        when (this) {
            HomeScreenAction.StartTimer -> startTimer()
            HomeScreenAction.StopTimer -> stopTimer()
            HomeScreenAction.TogglePausePlay -> togglePausePlay()
            HomeScreenAction.SkipBlock -> skipBlock()
            HomeScreenAction.ExtendBlock -> extendBlock()
        }
    }

    private fun startTimer() {
        val timer = Timer(
            sequence = timerSequence,
            secondsElapsed = 0,
            isPaused = false,
        )
        vmScope.launch { startTimerUseCase.call(timer) }
    }

    private fun stopTimer() {
        vmScope.launch { stopTimerUseCase.call(Unit) }
    }

    private fun skipBlock() {
        vmScope.launch { skipBlockUseCase.call(Unit) }
    }

    private fun extendBlock() {
        val current = stateFlow.value
        val seconds = when (current.extendPressCount) {
            0 -> 60
            1 -> 300
            else -> if (current.blockLabel == "Break") 300 else 900
        }
        stateFlow.update {
            val newCount = it.extendPressCount + 1
            it.copy(
                extendPressCount = newCount,
                addButtonText = addButtonText(newCount, it.blockLabel),
            )
        }
        vmScope.launch { extendBlockUseCase.call(seconds) }
    }

    private fun addButtonText(extendPressCount: Int, blockLabel: String): String =
        when (extendPressCount) {
            0 -> "1 min"
            1 -> "5 min"
            else -> if (blockLabel == "Break") "5 min" else "15 min"
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
