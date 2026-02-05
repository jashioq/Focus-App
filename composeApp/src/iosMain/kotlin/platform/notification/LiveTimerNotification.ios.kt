package platform.notification

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import platform.model.TimerMode
import platform.model.TimerToggleState

actual class LiveTimerNotification {
    actual val toggleState: Flow<TimerToggleState> = emptyFlow()

    actual fun start(mode: TimerMode, timeLeftSeconds: Int, totalTimeSeconds: Int, isPaused: Boolean) {
        println("LiveTimerNotification.start(mode=$mode, timeLeft=$timeLeftSeconds, total=$totalTimeSeconds, isPaused=$isPaused)")
    }

    actual fun update(
        mode: TimerMode?,
        timeLeftSeconds: Int?,
        totalTimeSeconds: Int?,
        isPaused: Boolean?,
    ) {
        println("LiveTimerNotification.update(mode=$mode, timeLeft=$timeLeftSeconds, total=$totalTimeSeconds, isPaused=$isPaused)")
    }

    actual fun stop() {
        println("LiveTimerNotification.stop()")
    }
}
