package util

import androidx.compose.runtime.Composable
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual class Haptic {
    actual fun performLightImpact() {
        dispatch_async(dispatch_get_main_queue()) {
            val generator = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)
            generator.prepare()
            generator.impactOccurred()
        }
    }

    actual fun performMediumImpact() {
        dispatch_async(dispatch_get_main_queue()) {
            val generator = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
            generator.prepare()
            generator.impactOccurred()
        }
    }

    actual fun performHeavyImpact() {
        dispatch_async(dispatch_get_main_queue()) {
            val generator = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)
            generator.prepare()
            generator.impactOccurred()
        }
    }
}

@Composable
actual fun rememberHapticFeedback(): Haptic {
    return Haptic()
}