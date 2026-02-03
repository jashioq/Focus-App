package util

import androidx.compose.runtime.Composable

/**
 * Expected interface for performing haptic feedback.
 */
expect class Haptic {
    /**
     * Performs a light haptic feedback.
     */
    fun performLightImpact()

    /**
     * Performs a medium haptic feedback.
     */
    fun performMediumImpact()

    /**
     * Performs a heavy haptic feedback.
     */
    fun performHeavyImpact()
}

/**
 * A Composable helper to get the platform-specific HapticFeedback instance.
 * This is useful because on Android, you need a [Context] which is only available
 * within a Composable or through specific Android-level access.
 */
@Composable
expect fun rememberHapticFeedback(): Haptic
