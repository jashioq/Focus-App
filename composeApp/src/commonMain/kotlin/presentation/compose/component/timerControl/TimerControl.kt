package presentation.compose.component.timerControl

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import focus.composeapp.generated.resources.Res
import focus.composeapp.generated.resources.ic_pause
import focus.composeapp.generated.resources.ic_play
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.painterResource

@Composable
fun TimerControl(
    isRunning: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = isRunning,
            transitionSpec = {
                fadeIn(tween(200)) togetherWith fadeOut(tween(200))
            },
            label = "TimerControlTransition",
        ) { running ->
            if (running) {
                PauseButton(onPause = onPause)
            } else {
                SlideToStopTrack(
                    onPlay = onPlay,
                    onStop = onStop,
                )
            }
        }
    }
}

@Composable
private fun PauseButton(onPause: () -> Unit) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) { onPause() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(resource = Res.drawable.ic_pause),
            contentDescription = "Pause",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(28.dp),
        )
    }
}

@Composable
private fun SlideToStopTrack(
    onPlay: () -> Unit,
    onStop: () -> Unit,
) {
    val density = LocalDensity.current
    val buttonSizePx = with(density) { 64.dp.toPx() }

    var trackWidthPx by remember { mutableFloatStateOf(0f) }

    // Synchronous drag offset — updated directly in the draggable callback (no coroutine)
    var dragOffsetPx by remember { mutableFloatStateOf(0f) }
    // Animatable used only for the release animation (snap-back / snap-to-end)
    val releaseAnimatable = remember { Animatable(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var isDragValid by remember { mutableStateOf(false) }

    val maxDragPx = (trackWidthPx - buttonSizePx).coerceAtLeast(0f)
    val displayOffset = if (isDragging) dragOffsetPx else releaseAnimatable.value
    val dragProgress = if (maxDragPx > 0f) (displayOffset / maxDragPx).coerceIn(0f, 1f) else 0f

    val stopThreshold = 0.85f
    val coroutineScope = rememberCoroutineScope()

    val draggableState = rememberDraggableState { delta ->
        if (!isDragValid) return@rememberDraggableState
        val maxDrag = (trackWidthPx - buttonSizePx).coerceAtLeast(0f)
        dragOffsetPx = (dragOffsetPx + delta).coerceIn(0f, maxDrag)
    }

    // Shimmer animation — sweeps a bright band from off-screen left to off-screen right
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerProgress",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .onSizeChanged { size -> trackWidthPx = size.width.toFloat() }
            .draggable(
                state = draggableState,
                orientation = Orientation.Horizontal,
                onDragStarted = { startOffset ->
                    val buttonLeft = releaseAnimatable.value
                    val buttonRight = buttonLeft + buttonSizePx
                    isDragValid = startOffset.x in buttonLeft..buttonRight
                    if (isDragValid) isDragging = true
                },
                onDragStopped = {
                    if (isDragValid) {
                        val finalOffset = dragOffsetPx
                        val maxDrag = (trackWidthPx - buttonSizePx).coerceAtLeast(0f)
                        val progress = if (maxDrag > 0f) (finalOffset / maxDrag).coerceIn(0f, 1f) else 0f

                        // Switch from drag tracking to animatable
                        releaseAnimatable.snapTo(finalOffset)
                        isDragging = false

                        if (progress >= stopThreshold) {
                            releaseAnimatable.animateTo(maxDrag, tween(100))
                            onStop()
                        } else {
                            releaseAnimatable.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = FastOutSlowInEasing,
                                ),
                            )
                        }
                        dragOffsetPx = 0f
                    }
                    isDragValid = false
                },
            ),
    ) {
        // "Slide to stop" text with shimmer
        // The bright band is 30% of track width. It starts fully off the left edge
        // and ends fully off the right edge so it smoothly enters and exits.
        val textColor = MaterialTheme.colorScheme.primary
        val bandHalf = trackWidthPx * 0.15f
        val sweepRange = trackWidthPx + 2 * bandHalf
        val shimmerCenter = -bandHalf + shimmerProgress * sweepRange
        val shimmerBrush = Brush.linearGradient(
            colors = listOf(
                textColor.copy(alpha = 0.3f),
                textColor.copy(alpha = 0.8f),
                textColor.copy(alpha = 0.3f),
            ),
            start = Offset(x = shimmerCenter - bandHalf, y = 0f),
            end = Offset(x = shimmerCenter + bandHalf, y = 0f),
        )

        androidx.compose.material3.Text(
            text = "Slide to stop",
            modifier = Modifier
                .align(Alignment.Center)
                .alpha((1f - dragProgress).coerceIn(0f, 1f)),
            style = TextStyle(
                brush = shimmerBrush,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            ),
        )

        // Play button (moves with drag)
        Box(
            modifier = Modifier
                .offset { IntOffset(displayOffset.roundToInt(), 0) }
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) { onPlay() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(resource = Res.drawable.ic_play),
                contentDescription = "Play",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}
