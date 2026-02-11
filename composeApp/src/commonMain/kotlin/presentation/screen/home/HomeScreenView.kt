package presentation.screen.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import presentation.screen.home.view.PlanningView
import presentation.screen.home.view.TimerView

private const val SlideTransitionDurationMs = 600

@Composable
fun HomeScreenView(
    modifier: Modifier = Modifier,
    timerText: String,
    isRunning: Boolean,
    isPaused: Boolean,
    progress: Float,
    blockLabel: String,
    addButtonText: String,
    onShowNotification: () -> Unit,
    onDismissNotification: () -> Unit,
    onTogglePausePlay: () -> Unit,
    onSkipBlock: () -> Unit,
    onExtendBlock: () -> Unit,
) {
    AnimatedContent(
        targetState = isRunning,
        modifier = modifier
            .fillMaxSize()
            .displayCutoutPadding()
            .navigationBarsPadding(),
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> if (targetState) fullWidth else -fullWidth },
                animationSpec = tween(SlideTransitionDurationMs, easing = FastOutSlowInEasing),
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { fullWidth -> if (targetState) -fullWidth else fullWidth },
                animationSpec = tween(SlideTransitionDurationMs, easing = FastOutSlowInEasing),
            )
        },
        label = "homeScreenSlideTransition",
    ) { targetIsRunning ->
        if (targetIsRunning) {
            TimerView(
                timerText = timerText,
                isPaused = isPaused,
                progress = progress,
                blockLabel = blockLabel,
                addButtonText = addButtonText,
                onDismissNotification = onDismissNotification,
                onTogglePausePlay = onTogglePausePlay,
                onSkipBlock = onSkipBlock,
                onExtendBlock = onExtendBlock,
            )
        } else {
            PlanningView(
                onStartTimer = onShowNotification,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
