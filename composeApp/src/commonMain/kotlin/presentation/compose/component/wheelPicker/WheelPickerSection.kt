package presentation.compose.component.wheelPicker

import androidx.compose.runtime.Composable

data class WheelPickerSection(
    val items: List<String>,
    val initialIndex: Int = 0,
    val leadingContent: (@Composable () -> Unit)? = null,
    val trailingContent: (@Composable () -> Unit)? = null,
    val enabled: Boolean = true,
)
