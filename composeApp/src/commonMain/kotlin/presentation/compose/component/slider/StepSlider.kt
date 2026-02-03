package presentation.compose.component.slider

import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun StepSlider(
    position: Float,
    onPositionChange: (Float) -> Unit,
    steps: Int = 6,
    range: ClosedFloatingPointRange<Float> = 0f..7f,
    colors: SliderColors = DefaultColors(),
) {
    Slider(
        value = position,
        onValueChange = onPositionChange,
        colors = colors,
        steps = steps,
        valueRange = range,
    )
}

@Composable
fun DefaultColors() = SliderDefaults.colors(
    thumbColor = Color.Black,
    activeTrackColor = Color.Black,
    inactiveTickColor = Color.Black,
    activeTickColor = Color.White,
    inactiveTrackColor = Color.Gray,
)
