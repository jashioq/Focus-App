package presentation.screen.newTask

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class GlassShapeState {
    var isExpanded by mutableStateOf(false)
        private set

    fun expand() { isExpanded = true }
    fun collapse() { isExpanded = false }
}
