package platform.notification

import kotlinx.coroutines.flow.Flow
import platform.model.TimerMode
import platform.model.TimerToggleState

expect class LiveTimerNotification {
    val toggleState: Flow<TimerToggleState>

    fun start(mode: TimerMode, timeLeftSeconds: Int, totalTimeSeconds: Int, isPaused: Boolean)
    fun update(
        mode: TimerMode?,
        timeLeftSeconds: Int?,
        totalTimeSeconds: Int?,
        isPaused: Boolean?,
    )
    fun stop()
}
