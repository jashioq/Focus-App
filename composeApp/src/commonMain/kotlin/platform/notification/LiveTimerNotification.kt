package platform.notification

import domain.model.Timer
import kotlinx.coroutines.flow.Flow

expect class LiveTimerNotification {
    val timerUpdateFlow: Flow<Timer>
    val notificationDismissedFlow: Flow<Unit>

    fun isNotificationActive(): Boolean
    fun start(timer: Timer)
    fun stop()
    fun pause()
    fun resume()
}
