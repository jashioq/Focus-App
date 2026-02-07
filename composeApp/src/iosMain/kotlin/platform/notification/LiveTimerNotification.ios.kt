package platform.notification

import domain.model.Timer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

actual class LiveTimerNotification {
    actual val timerUpdateFlow: Flow<Timer> = emptyFlow()

    actual fun start(timer: Timer) {
        // TODO: iOS implementation
    }

    actual fun stop() {
        LiveActivityBridge.stop()
    }

    actual fun pause() {
        // TODO: iOS implementation
    }

    actual fun resume() {
        // TODO: iOS implementation
    }
}
