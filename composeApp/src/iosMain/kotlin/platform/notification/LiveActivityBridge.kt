package platform.notification

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

private var onStartOrUpdateCallback: ((Int, Int, Boolean) -> Unit)? = null
private var onStopCallback: (() -> Unit)? = null
private var onIsActiveCallback: (() -> Boolean)? = null
private var onGetActivityStateCallback: (() -> Pair<Boolean, Int>?)? = null

private val dismissedChannel = Channel<Unit>(Channel.BUFFERED)
private val toggleChannel = Channel<Unit>(Channel.BUFFERED)

fun registerLiveActivityCallbacks(
    onStartOrUpdate: (Int, Int, Boolean) -> Unit,
    onStop: () -> Unit,
    onIsActive: () -> Boolean,
    onGetActivityState: () -> Pair<Boolean, Int>?,
    onToggle: (() -> Unit) -> Unit,
) {
    onStartOrUpdateCallback = onStartOrUpdate
    onStopCallback = onStop
    onIsActiveCallback = onIsActive
    onGetActivityStateCallback = onGetActivityState

    onToggle {
        toggleChannel.trySend(Unit)
    }
}

fun notifyActivityDismissed() {
    dismissedChannel.trySend(Unit)
}

internal object LiveActivityBridge {
    fun startOrUpdate(totalSeconds: Int, secondsElapsed: Int, isPaused: Boolean) {
        onStartOrUpdateCallback?.invoke(totalSeconds, secondsElapsed, isPaused)
    }

    fun stop() {
        onStopCallback?.invoke()
    }

    fun isActive(): Boolean {
        return onIsActiveCallback?.invoke() ?: false
    }

    fun getActivityState(): Pair<Boolean, Int>? {
        return onGetActivityStateCallback?.invoke()
    }

    fun emitToggle() {
        toggleChannel.trySend(Unit)
    }

    val dismissedFlow: Flow<Unit> = dismissedChannel.receiveAsFlow()
    val toggleFlow: Flow<Unit> = toggleChannel.receiveAsFlow()
}
