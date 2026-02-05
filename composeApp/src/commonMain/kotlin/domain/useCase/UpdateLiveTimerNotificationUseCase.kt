package domain.useCase

import domain.repository.LiveTimerNotificationRepository
import domain.util.UseCase
import platform.model.TimerMode

class UpdateLiveTimerNotificationUseCase(
    private val liveTimerNotificationRepository: LiveTimerNotificationRepository,
) : UseCase<UpdateLiveTimerNotificationParams, Unit> {
    override suspend fun call(value: UpdateLiveTimerNotificationParams): Result<Unit> =
        runCatching {
            liveTimerNotificationRepository.update(
                mode = value.mode,
                timeLeftSeconds = value.timeLeftSeconds,
                totalTimeSeconds = value.totalTimeSeconds,
                isPaused = value.isPaused,
            )
        }
}

data class UpdateLiveTimerNotificationParams(
    val mode: TimerMode? = null,
    val timeLeftSeconds: Int? = null,
    val totalTimeSeconds: Int? = null,
    val isPaused: Boolean? = null,
)
