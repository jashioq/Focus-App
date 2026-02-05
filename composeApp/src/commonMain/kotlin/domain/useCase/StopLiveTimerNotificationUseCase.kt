package domain.useCase

import domain.repository.LiveTimerNotificationRepository
import domain.util.UseCase

class StopLiveTimerNotificationUseCase(
    private val liveTimerNotificationRepository: LiveTimerNotificationRepository,
) : UseCase<Unit, Unit> {
    override suspend fun call(value: Unit): Result<Unit> =
        runCatching {
            liveTimerNotificationRepository.stop()
        }
}
