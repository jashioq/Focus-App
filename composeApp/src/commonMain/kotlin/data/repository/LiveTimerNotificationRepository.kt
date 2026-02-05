package data.repository

import kotlinx.coroutines.flow.Flow
import platform.notification.LiveTimerNotification
import platform.model.TimerMode
import platform.model.TimerToggleState

class LiveTimerNotificationRepository(
    private val liveTimerNotification: LiveTimerNotification,
) : domain.repository.LiveTimerNotificationRepository {
    override val toggleState: Result<Flow<TimerToggleState>> =
        runCatching {
            liveTimerNotification.toggleState
        }

    override fun start(mode: TimerMode, timeLeftSeconds: Int, totalTimeSeconds: Int, isPaused: Boolean) {
        liveTimerNotification.start(mode, timeLeftSeconds, totalTimeSeconds, isPaused)
    }

    override fun update(
        mode: TimerMode?,
        timeLeftSeconds: Int?,
        totalTimeSeconds: Int?,
        isPaused: Boolean?,
    ) {
        liveTimerNotification.update(mode, timeLeftSeconds, totalTimeSeconds, isPaused)
    }

    override fun stop() {
        liveTimerNotification.stop()
    }
}
