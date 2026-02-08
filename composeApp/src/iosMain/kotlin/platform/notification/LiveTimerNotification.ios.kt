package platform.notification

import domain.model.Timer
import kotlinx.coroutines.flow.Flow

actual class LiveTimerNotification {

    actual val timerToggleFlow: Flow<Unit> = LiveActivityBridge.toggleFlow

    actual val notificationDismissedFlow: Flow<Unit> = LiveActivityBridge.dismissedFlow

    actual fun isNotificationActive(): Boolean = LiveActivityBridge.isActive()

    actual fun set(timer: Timer) {
        LiveActivityBridge.startOrUpdate(timer.totalTime, timer.secondsElapsed, timer.isPaused)
    }

    actual fun clear() {
        LiveActivityBridge.stop()
    }
}
