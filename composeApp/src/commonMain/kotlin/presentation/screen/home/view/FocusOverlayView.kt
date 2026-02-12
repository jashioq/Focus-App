package presentation.screen.home.view

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import presentation.compose.component.ring.FocusTimerRing

private const val FocusOverlayEnterDurationMs = 3000

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FocusOverlayView(
    progress: Float,
    isPaused: Boolean,
    timerText: String,
    springButtonText: String,
    onSpringButtonClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        with(sharedTransitionScope) {
            FocusTimerRing(
                progress = progress,
                isPaused = isPaused,
                timerText = timerText,
                springButtonText = springButtonText,
                onSpringButtonClick = onSpringButtonClick,
                modifier = Modifier
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = "timerRing"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = BoundsTransform { _, _ ->
                            tween(FocusOverlayEnterDurationMs)
                        },
                    )
                    .fillMaxWidth(),
            )
        }
    }
}
