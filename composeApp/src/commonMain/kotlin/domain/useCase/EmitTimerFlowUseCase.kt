package domain.useCase

import domain.model.Timer
import domain.repository.TimerRepository
import domain.util.UseCase
import kotlinx.coroutines.flow.Flow

class EmitTimerFlowUseCase(
    private val timerRepository: TimerRepository,
) : UseCase<Unit, Flow<Timer?>> {
    override suspend fun call(value: Unit): Result<Flow<Timer?>> =
        runCatching {
            timerRepository.timerFlow
        }
}
