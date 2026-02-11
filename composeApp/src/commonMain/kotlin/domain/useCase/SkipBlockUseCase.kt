package domain.useCase

import domain.repository.TimerRepository
import domain.util.UseCase

class SkipBlockUseCase(
    private val timerRepository: TimerRepository,
) : UseCase<Unit, Unit> {
    override suspend fun call(value: Unit): Result<Unit> =
        runCatching {
            timerRepository.skipBlock()
        }
}
