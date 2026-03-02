package domain.useCase

import domain.model.Timer
import domain.repository.TimerRepository
import domain.util.StreamUseCase
import kotlinx.coroutines.flow.Flow

class EmitTimerFlowUseCase(
    private val timerRepository: TimerRepository,
) : StreamUseCase<Unit, Timer?> {
    override fun stream(value: Unit): Flow<Timer?> = timerRepository.timerFlow
}
