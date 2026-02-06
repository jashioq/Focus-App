package platform.notification

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import platform.model.TimerMode
import platform.model.TimerToggleState

actual class LiveTimerNotification {
    actual val toggleState: Flow<TimerToggleState> = emptyFlow()

    actual fun start(mode: TimerMode, timeLeftSeconds: Int, totalTimeSeconds: Int, isPaused: Boolean) {
        LiveActivityBridge.start(leftText = "te", rightText = "st")
    }

    actual fun update(
        mode: TimerMode?,
        timeLeftSeconds: Int?,
        totalTimeSeconds: Int?,
        isPaused: Boolean?,
    ) {
        // No-op for static content
    }

    actual fun stop() {
        LiveActivityBridge.stop()
    }
}
