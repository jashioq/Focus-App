package presentation.screen.home.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import presentation.compose.component.ring.FocusTimerRing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import presentation.compose.component.timerControl.MorphTransition
import presentation.compose.component.timerControl.TimerControls

@Composable
fun TimerView(
    timerText: String,
    isPaused: Boolean,
    progress: Float,
    blockLabel: String,
    addButtonText: String,
    onDismissNotification: () -> Unit,
    onTogglePausePlay: () -> Unit,
    onSkipBlock: () -> Unit,
    onExtendBlock: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        FocusTimerRing(
            progress = progress,
            isPaused = isPaused,
            timerText = timerText,
            springButtonText = blockLabel,
            onSpringButtonClick = onSkipBlock,
        )

        Spacer(modifier = Modifier.weight(1f))

        TimerControls(
            modifier = Modifier.padding(16.dp),
            isRunning = !isPaused,
            onPlay = onTogglePausePlay,
            onPause = onTogglePausePlay,
            onStop = onDismissNotification,
            onAddButtonClick = onExtendBlock,
            addButtonContent = {
                MorphTransition(
                    targetState = addButtonText,
                    label = "addButtonText",
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                ) { text ->
                    Text(
                        text = text,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
        )
    }
}
