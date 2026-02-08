package platform.notification

import domain.model.Timer
import kotlinx.coroutines.flow.Flow

expect class LiveTimerNotification {
    val timerToggleFlow: Flow<Unit>
    val notificationDismissedFlow: Flow<Unit>
    fun isNotificationActive(): Boolean
    fun set(timer: Timer)
    fun clear()
}
