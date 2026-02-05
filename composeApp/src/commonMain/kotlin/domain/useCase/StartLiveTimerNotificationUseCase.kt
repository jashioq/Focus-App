package domain.useCase

import domain.repository.LiveTimerNotificationRepository
import domain.util.UseCase
import platform.model.TimerMode

class StartLiveTimerNotificationUseCase(
    private val liveTimerNotificationRepository: LiveTimerNotificationRepository,
) : UseCase<StartLiveTimerNotificationParams, Unit> {
    override suspend fun call(value: StartLiveTimerNotificationParams): Result<Unit> =
        runCatching {
            liveTimerNotificationRepository.start(
                mode = value.mode,
                timeLeftSeconds = value.timeLeftSeconds,
                totalTimeSeconds = value.totalTimeSeconds,
                isPaused = value.isPaused,
            )
        }
}

data class StartLiveTimerNotificationParams(
    val mode: TimerMode,
    val timeLeftSeconds: Int,
    val totalTimeSeconds: Int,
    val isPaused: Boolean,
)
