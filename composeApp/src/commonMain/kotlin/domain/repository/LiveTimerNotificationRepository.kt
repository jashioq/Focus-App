package domain.repository

import kotlinx.coroutines.flow.Flow
import platform.model.TimerMode
import platform.model.TimerToggleState

interface LiveTimerNotificationRepository {
    val toggleState: Result<Flow<TimerToggleState>>

    fun start(mode: TimerMode, timeLeftSeconds: Int, totalTimeSeconds: Int, isPaused: Boolean)
    fun update(
        mode: TimerMode? = null,
        timeLeftSeconds: Int? = null,
        totalTimeSeconds: Int? = null,
        isPaused: Boolean? = null,
    )
    fun stop()
}
