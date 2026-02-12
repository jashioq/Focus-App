package presentation.screen.home.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import presentation.compose.component.ring.FocusTimerRing
import presentation.compose.component.timerControl.MorphTransition
import presentation.compose.component.timerControl.TimerControls

private const val IdleTimeoutMs = 5_000L
private const val ExtendedIdleTimeoutMs = 15_000L
private const val FocusOverlayEnterDurationMs = 3000
private const val FocusOverlayExitDurationMs = 500

@OptIn(ExperimentalSharedTransitionApi::class)
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
    var isFocusOverlayActive by remember { mutableStateOf(false) }
    var isOverlaySettled by remember { mutableStateOf(false) }
    var interactionKey by remember { mutableIntStateOf(0) }
    var idleTimeoutMs by remember { mutableStateOf(IdleTimeoutMs) }
    val canStartIdleTimer = blockLabel == "Focus" && !isPaused

    // Dismiss overlay only when paused — NOT on break switch
    LaunchedEffect(isPaused) {
        if (isPaused) isFocusOverlayActive = false
    }

    // Reset idle timeout to 5s when a new focus block starts
    LaunchedEffect(blockLabel) {
        if (blockLabel == "Focus") {
            idleTimeoutMs = IdleTimeoutMs
        }
    }

    // Gate touch-to-dismiss until enter transition completes
    LaunchedEffect(isFocusOverlayActive) {
        if (isFocusOverlayActive) {
            isOverlaySettled = false
            delay(FocusOverlayEnterDurationMs.toLong())
            isOverlaySettled = true
        } else {
            isOverlaySettled = false
        }
    }

    // Idle timer — only runs during focus mode when overlay is not active
    LaunchedEffect(canStartIdleTimer, isFocusOverlayActive, interactionKey, idleTimeoutMs) {
        if (canStartIdleTimer && !isFocusOverlayActive) {
            delay(idleTimeoutMs)
            isFocusOverlayActive = true
        }
    }

    SharedTransitionLayout(
        modifier = modifier.pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    val hasNewDown = event.changes.any { !it.previousPressed && it.pressed }
                    if (hasNewDown) {
                        if (isFocusOverlayActive && isOverlaySettled) {
                            isFocusOverlayActive = false
                            idleTimeoutMs = ExtendedIdleTimeoutMs
                        }
                        interactionKey++
                    }
                }
            }
        },
    ) {
        AnimatedContent(
            targetState = isFocusOverlayActive,
            transitionSpec = {
                if (targetState) {
                    fadeIn(tween(FocusOverlayEnterDurationMs)) togetherWith
                            fadeOut(tween(FocusOverlayEnterDurationMs))
                } else {
                    fadeIn(tween(FocusOverlayExitDurationMs)) togetherWith
                            fadeOut(tween(FocusOverlayExitDurationMs))
                }
            },
            label = "focusOverlayTransition",
        ) { showFocusOverlay ->
            if (showFocusOverlay) {
                FocusOverlayView(
                    progress = progress,
                    isPaused = isPaused,
                    timerText = timerText,
                    springButtonText = blockLabel,
                    onSpringButtonClick = onSkipBlock,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedContent,
                )
            } else {
                TimerViewContent(
                    timerText = timerText,
                    isPaused = isPaused,
                    progress = progress,
                    blockLabel = blockLabel,
                    addButtonText = addButtonText,
                    onDismissNotification = onDismissNotification,
                    onTogglePausePlay = onTogglePausePlay,
                    onSkipBlock = onSkipBlock,
                    onExtendBlock = onExtendBlock,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedContent,
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun TimerViewContent(
    timerText: String,
    isPaused: Boolean,
    progress: Float,
    blockLabel: String,
    addButtonText: String,
    onDismissNotification: () -> Unit,
    onTogglePausePlay: () -> Unit,
    onSkipBlock: () -> Unit,
    onExtendBlock: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .displayCutoutPadding()
            .navigationBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        with(sharedTransitionScope) {
            FocusTimerRing(
                progress = progress,
                isPaused = isPaused,
                timerText = timerText,
                springButtonText = blockLabel,
                onSpringButtonClick = onSkipBlock,
                modifier = Modifier.sharedElement(
                    sharedContentState = rememberSharedContentState(key = "timerRing"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = BoundsTransform { _, _ ->
                        tween(FocusOverlayExitDurationMs)
                    },
                ),
            )
        }

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
