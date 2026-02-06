package platform.notification

private var onStartCallback: ((String, String) -> Unit)? = null
private var onStopCallback: (() -> Unit)? = null

fun registerLiveActivityCallbacks(
    onStart: (String, String) -> Unit,
    onStop: () -> Unit
) {
    onStartCallback = onStart
    onStopCallback = onStop
}

internal object LiveActivityBridge {
    fun start(leftText: String, rightText: String) {
        onStartCallback?.invoke(leftText, rightText)
    }

    fun stop() {
        onStopCallback?.invoke()
    }
}
