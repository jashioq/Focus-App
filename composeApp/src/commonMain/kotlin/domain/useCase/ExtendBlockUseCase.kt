package domain.useCase

import domain.repository.TimerRepository
import domain.util.UseCase

class ExtendBlockUseCase(
    private val timerRepository: TimerRepository,
) : UseCase<Int, Unit> {
    override suspend fun call(value: Int): Result<Unit> =
        runCatching {
            timerRepository.extendBlock(value)
        }
}
