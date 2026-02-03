package presentation.compose.component.progress

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SharedProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    progressIndicatorState: ProgressIndicatorState = remember { ProgressIndicatorState() },
    progressAnimDuration: Int = 1500,
) {
    val progressAnimation by animateFloatAsState(
        targetValue = progressIndicatorState.progress,
        animationSpec = tween(durationMillis = progressAnimDuration, easing = FastOutSlowInEasing),
    )

    androidx.compose.material3.CircularProgressIndicator(
        progress = { progressAnimation },
        modifier = modifier.size(32.dp),
        trackColor = Color.Transparent,
        color = color,
        strokeWidth = 6.dp,
    )
}
