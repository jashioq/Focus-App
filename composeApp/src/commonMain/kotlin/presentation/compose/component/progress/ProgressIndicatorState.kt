package presentation.compose.component.progress

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ProgressIndicatorState(
    initialProgress: Float = 0f,
) {
    var progress by mutableStateOf(initialProgress)
        private set

    fun updateProgress(newProgress: Float) {
        progress = newProgress.coerceIn(0f, 100f)
    }
}
