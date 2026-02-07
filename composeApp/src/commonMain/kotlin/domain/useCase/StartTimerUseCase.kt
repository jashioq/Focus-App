package domain.useCase

import domain.model.Timer
import domain.repository.TimerRepository
import domain.util.UseCase

class StartTimerUseCase(
    private val timerRepository: TimerRepository,
) : UseCase<Timer, Unit> {
    override suspend fun call(value: Timer): Result<Unit> =
        runCatching {
            timerRepository.start(value)
        }
}
