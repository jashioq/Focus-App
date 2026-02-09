package presentation.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import presentation.compose.component.text.Text
import presentation.compose.component.timerControl.TimerControl

@Composable
fun HomeScreenView(
    modifier: Modifier = Modifier,
    timerText: String,
    isRunning: Boolean,
    isPaused: Boolean,
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = timerText,
            fontSize = 48.sp,
            lineHeight = 56.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            Button(
                onClick = onShowNotification,
            ) {
                androidx.compose.material3.Text("Start")
            }

            Button(
                onClick = onTogglePausePlay,
            ) {
                androidx.compose.material3.Text(if (isPaused) "Resume" else "Pause")
            }

            Button(
                onClick = onDismissNotification,
            ) {
                androidx.compose.material3.Text("Dismiss")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        TimerControl(
            isRunning = isRunning && !isPaused,
            onPlay = onTogglePausePlay,
            onPause = onTogglePausePlay,
            onStop = onDismissNotification,
        )
    }
}
