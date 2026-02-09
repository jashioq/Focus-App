package presentation.compose.component.timerControl

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
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

// region Configuration

private val ButtonSize = 64.dp
private val IconSize = 28.dp
private val TextFontSize = 16.sp

// Slide-to-stop
private const val StopThreshold = 0.95f

// Track expansion animation
private const val TrackExpandDurationMs = 300

// Shimmer
private const val ShimmerDurationMs = 2000
private const val ShimmerBandFraction = 0.15f
private const val ShimmerDimAlpha = 0.3f
private const val ShimmerBrightAlpha = 0.8f

// Snap-back / snap-to-end after drag release
private const val SnapToEndDurationMs = 100
private const val SnapBackDurationMs = 300

// Icon morph transition
private const val IconTransitionDurationMs = 600
private const val IconTransitionScale = 1.5f
private const val IconTransitionBlurRadius = 40f

// endregion

@Composable
fun TimerControl(
    isRunning: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
) {
    val density = LocalDensity.current
    val buttonSizePx = with(density) { ButtonSize.toPx() }
    var containerWidthPx by remember { mutableFloatStateOf(0f) }

    // Track expansion: 0 = collapsed (button only), 1 = expanded (full track)
    val widthFraction by animateFloatAsState(
        targetValue = if (isRunning) 0f else 1f,
        animationSpec = tween(TrackExpandDurationMs, easing = FastOutSlowInEasing),
        label = "expansion",
    )

    // Drag state
    var dragOffsetPx by remember { mutableFloatStateOf(0f) }
    val releaseAnimatable = remember { Animatable(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var isDragValid by remember { mutableStateOf(false) }

    // Reset drag state when switching to running
    LaunchedEffect(isRunning) {
        if (isRunning) {
            isDragging = false
            isDragValid = false
            dragOffsetPx = 0f
            releaseAnimatable.snapTo(0f)
        }
    }

    val maxDragPx = (containerWidthPx - buttonSizePx).coerceAtLeast(0f)
    val displayOffset = if (isDragging) dragOffsetPx else releaseAnimatable.value
    val dragProgress = if (maxDragPx > 0f) (displayOffset / maxDragPx).coerceIn(0f, 1f) else 0f
    val stopThreshold = StopThreshold

    val draggableState = rememberDraggableState { delta ->
        if (!isDragValid) return@rememberDraggableState
        val maxDrag = (containerWidthPx - buttonSizePx).coerceAtLeast(0f)
        dragOffsetPx = (dragOffsetPx + delta).coerceIn(0f, maxDrag)
    }

    // Shimmer animation
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = ShimmerDurationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerProgress",
    )

    // Track width: interpolate between button size and full container width
    val trackWidthPx = if (containerWidthPx > 0f) {
        buttonSizePx + widthFraction * (containerWidthPx - buttonSizePx)
    } else {
        buttonSizePx
    }
    val trackWidthDp = with(density) { trackWidthPx.toDp() }
    val containerWidthDp = with(density) { containerWidthPx.toDp() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(ButtonSize)
            .onSizeChanged { containerWidthPx = it.width.toFloat() }
            .draggable(
                state = draggableState,
                orientation = Orientation.Horizontal,
                enabled = !isRunning,
                onDragStarted = { startOffset ->
                    val buttonLeft = releaseAnimatable.value
                    val buttonRight = buttonLeft + buttonSizePx
                    isDragValid = startOffset.x in buttonLeft..buttonRight
                    if (isDragValid) isDragging = true
                },
                onDragStopped = {
                    if (isDragValid) {
                        val finalOffset = dragOffsetPx
                        val maxDrag = (containerWidthPx - buttonSizePx).coerceAtLeast(0f)
                        val progress =
                            if (maxDrag > 0f) (finalOffset / maxDrag).coerceIn(0f, 1f) else 0f

                        releaseAnimatable.snapTo(finalOffset)
                        isDragging = false

                        if (progress >= stopThreshold) {
                            releaseAnimatable.animateTo(maxDrag, tween(SnapToEndDurationMs))
                            onStop()
                        } else {
                            releaseAnimatable.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(
                                    durationMillis = SnapBackDurationMs,
                                    easing = FastOutSlowInEasing,
                                ),
                            )
                        }
                        dragOffsetPx = 0f
                    }
                    isDragValid = false
                },
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        // Track background — grows from button-sized circle to full-width pill.
        // Text is inside and clipped by the track, creating a wipe-reveal effect.
        Box(
            modifier = Modifier
                .width(trackWidthDp)
                .height(ButtonSize)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceContainer),
        ) {
            if (containerWidthPx > 0f) {
                val textColor = MaterialTheme.colorScheme.primary
                val bandHalf = containerWidthPx * ShimmerBandFraction
                val sweepRange = containerWidthPx + 2 * bandHalf
                val shimmerCenter = -bandHalf + shimmerProgress * sweepRange
                val shimmerBrush = Brush.linearGradient(
                    colors = listOf(
                        textColor.copy(alpha = ShimmerDimAlpha),
                        textColor.copy(alpha = ShimmerBrightAlpha),
                        textColor.copy(alpha = ShimmerDimAlpha),
                    ),
                    start = Offset(x = shimmerCenter - bandHalf, y = 0f),
                    end = Offset(x = shimmerCenter + bandHalf, y = 0f),
                )

                // Text sized to full container width — overflows the track and gets clipped,
                // so it reveals left-to-right as the track grows.
                Box(
                    modifier = Modifier
                        .requiredWidth(containerWidthDp)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Text(
                        text = "Slide to stop",
                        modifier = Modifier
                            .alpha(widthFraction * (1f - dragProgress)),
                        style = TextStyle(
                            brush = shimmerBrush,
                            fontSize = TextFontSize,
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                }
            }
        }

        // Button — always visible, icon crossfades between pause and play
        Box(
            modifier = Modifier
                .offset { IntOffset(displayOffset.roundToInt(), 0) }
                .size(ButtonSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) { if (isRunning) onPause() else onPlay() },
            contentAlignment = Alignment.Center,
        ) {
            AnimatedContent(
                targetState = isRunning,
                transitionSpec = {
                    (fadeIn(tween(IconTransitionDurationMs)) + scaleIn(
                        initialScale = IconTransitionScale,
                        animationSpec = tween(IconTransitionDurationMs),
                    )).togetherWith(
                        fadeOut(tween(IconTransitionDurationMs)) + scaleOut(
                            targetScale = IconTransitionScale,
                            animationSpec = tween(IconTransitionDurationMs),
                        ),
                    )
                },
                label = "iconTransition",
            ) { targetRunning ->
                val blurRadius by transition.animateFloat(
                    transitionSpec = { tween(IconTransitionDurationMs) },
                    label = "iconBlur",
                ) { state ->
                    when (state) {
                        EnterExitState.Visible -> 0f
                        else -> IconTransitionBlurRadius
                    }
                }

                Box(
                    modifier = Modifier
                        .size(ButtonSize)
                        .blur(blurRadius.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(
                            if (targetRunning) Res.drawable.ic_pause else Res.drawable.ic_play,
                        ),
                        contentDescription = if (targetRunning) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(IconSize),
                    )
                }
            }
        }
    }
}
