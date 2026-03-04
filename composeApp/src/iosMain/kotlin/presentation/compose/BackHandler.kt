package presentation.compose

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(onBack: () -> Unit) {
    // iOS handles back navigation via native swipe gesture
}
