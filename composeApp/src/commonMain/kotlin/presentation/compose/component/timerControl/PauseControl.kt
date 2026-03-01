package presentation.compose.component.timerControl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
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
import presentation.compose.component.border.tiltBorder
import presentation.compose.component.transition.MorphTransition

// region Configuration

private val ButtonSize = 84.dp
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

// endregion

@Composable
fun PauseControl(
    modifier: Modifier = Modifier,
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

    val displayOffset = if (isDragging) dragOffsetPx else releaseAnimatable.value
    val stopThreshold = StopThreshold

    val draggableState = rememberDraggableState { delta ->
        if (!isDragValid) return@rememberDraggableState
        val maxDrag = (containerWidthPx - buttonSizePx).coerceAtLeast(0f)
        dragOffsetPx = (dragOffsetPx + delta).coerceIn(0f, maxDrag)
        if (maxDrag > 0f && dragOffsetPx >= maxDrag) {
            onStop()
        }
    }

    // Shimmer animation — restarts from the beginning each time the track is revealed
    val shimmerAnimatable = remember { Animatable(0f) }
    LaunchedEffect(isRunning) {
        if (!isRunning) {
            shimmerAnimatable.snapTo(0f)
            while (true) {
                shimmerAnimatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = ShimmerDurationMs, easing = LinearEasing),
                )
                shimmerAnimatable.snapTo(0f)
            }
        }
    }
    val shimmerProgress = shimmerAnimatable.value

    // Track width: interpolate between button size and full container width
    val trackWidthPx = if (containerWidthPx > 0f) {
        buttonSizePx + widthFraction * (containerWidthPx - buttonSizePx)
    } else {
        buttonSizePx
    }
    val trackWidthDp = with(density) { trackWidthPx.toDp() }
    val containerWidthDp = with(density) { containerWidthPx.toDp() }

    Box(
        modifier = modifier
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
                .tiltBorder(
                    color = MaterialTheme.colorScheme.primary,
                    thickness = 1.5.dp,
                    visibilityBound = 0.5f,
                    shape = RoundedCornerShape(50),
                )
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.09f)),
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
                // The button "pulls a curtain" over the text: only the portion
                // to the right of the button is visible.
                val curtainLeft = displayOffset + buttonSizePx
                Box(
                    modifier = Modifier
                        .requiredWidth(containerWidthDp)
                        .fillMaxHeight()
                        .drawWithContent {
                            clipRect(left = curtainLeft) {
                                this@drawWithContent.drawContent()
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Text(
                        text = "Slide to stop",
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
        Box(modifier = Modifier.offset { IntOffset(displayOffset.roundToInt(), 0) }) {
            ScaleOnTouchButton(
                onClick = { if (isRunning) onPause() else onPlay() },
                isExternallyPressed = isDragging,
                modifier = Modifier
                    .size(ButtonSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
            ) {
                MorphTransition(
                    targetState = isRunning,
                    modifier = Modifier.size(ButtonSize),
                    label = "iconTransition",
                ) { targetRunning: Boolean ->
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
