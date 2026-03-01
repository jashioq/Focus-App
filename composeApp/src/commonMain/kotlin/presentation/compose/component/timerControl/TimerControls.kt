package presentation.compose.component.timerControl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import presentation.compose.component.transition.MorphTransition

private val ButtonSize = 84.dp
private const val AddButtonMorphDurationMs = 300

@Composable
fun TimerControls(
    modifier: Modifier = Modifier,
    isRunning: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onAddButtonClick: () -> Unit,
    addButtonContent: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ButtonSize),
    ) {
        // PauseControl takes full width — when running only the circle button shows,
        // when paused the slide track expands to full width covering the add button area.
        PauseControl(
            modifier = Modifier.fillMaxWidth(),
            isRunning = isRunning,
            onPlay = onPlay,
            onPause = onPause,
            onStop = onStop,
        )

        // Add button at the right — morphs out when the timer is paused
        // so the slide track can fill the space.
        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            MorphTransition(
                targetState = isRunning,
                durationMs = AddButtonMorphDurationMs,
                label = "addButtonTransition",
                morphScale = 0f,
            ) { targetRunning ->
                if (targetRunning) {
                    ScaleOnTouchButton(
                        onClick = onAddButtonClick,
                        modifier = Modifier
                            .size(ButtonSize)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                    ) {
                        addButtonContent()
                    }
                } else {
                    Box(modifier = Modifier.size(ButtonSize))
                }
            }
        }
    }
}
