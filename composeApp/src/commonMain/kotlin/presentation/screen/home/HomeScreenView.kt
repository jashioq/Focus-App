package presentation.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import presentation.compose.component.ring.FocusTimerRing
import presentation.compose.component.timerControl.TimerControl

@Composable
fun HomeScreenView(
    modifier: Modifier = Modifier,
    timerText: String,
    isRunning: Boolean,
    isPaused: Boolean,
    progress: Float,
    onShowNotification: () -> Unit,
    onDismissNotification: () -> Unit,
    onTogglePausePlay: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
            .displayCutoutPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        FocusTimerRing(
            progress = progress,
            isPaused = isPaused,
            timerText = timerText,
            springButtonText = "Focus",
            onSpringButtonClick = onShowNotification,
        )

        Spacer(modifier = Modifier.weight(1f))

        TimerControl(
            isRunning = isRunning && !isPaused,
            onPlay = onTogglePausePlay,
            onPause = onTogglePausePlay,
            onStop = onDismissNotification,
        )
    }
}
