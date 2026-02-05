package domain.useCase

import domain.repository.LiveTimerNotificationRepository
import domain.util.UseCase
import kotlinx.coroutines.flow.Flow
import platform.model.TimerToggleState

class EmitTimerToggleStateUseCase(
    private val liveTimerNotificationRepository: LiveTimerNotificationRepository,
) : UseCase<Unit, Flow<TimerToggleState>> {
    override suspend fun call(value: Unit): Result<Flow<TimerToggleState>> =
        liveTimerNotificationRepository.toggleState
}
