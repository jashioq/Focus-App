package platform.notification

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

private var onStartOrUpdateCallback: ((String, String, Int, Boolean) -> Unit)? = null
private var onStopCallback: (() -> Unit)? = null
private var onIsActiveCallback: (() -> Boolean)? = null
private var onScheduleRefreshCallback: ((Int) -> Unit)? = null

private val dismissedChannel = Channel<Unit>(Channel.BUFFERED)

fun registerLiveActivityCallbacks(
    onStartOrUpdate: (String, String, Int, Boolean) -> Unit,
    onStop: () -> Unit,
    onIsActive: () -> Boolean,
    onScheduleRefresh: (Int) -> Unit,
) {
    onStartOrUpdateCallback = onStartOrUpdate
    onStopCallback = onStop
    onIsActiveCallback = onIsActive
    onScheduleRefreshCallback = onScheduleRefresh
}

fun notifyActivityDismissed() {
    dismissedChannel.trySend(Unit)
}

internal object LiveActivityBridge {
    fun startOrUpdate(blockSeconds: String, blockModes: String, secondsElapsed: Int, isPaused: Boolean) {
        onStartOrUpdateCallback?.invoke(blockSeconds, blockModes, secondsElapsed, isPaused)
    }

    fun stop() {
        onStopCallback?.invoke()
    }

    fun isActive(): Boolean {
        return onIsActiveCallback?.invoke() ?: false
    }

    fun scheduleBackgroundRefresh(afterSeconds: Int) {
        onScheduleRefreshCallback?.invoke(afterSeconds)
    }

    val dismissedFlow: Flow<Unit> = dismissedChannel.receiveAsFlow()
}
