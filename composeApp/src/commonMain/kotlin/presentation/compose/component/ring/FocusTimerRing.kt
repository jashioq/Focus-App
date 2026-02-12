package presentation.compose.component.ring

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import presentation.compose.component.button.SpringButton
import presentation.compose.component.text.TimerText
import presentation.compose.component.timerControl.MorphTransition
import kotlin.math.asin
import kotlin.math.min

private class RingLayoutState {
    var radius = 0f
    var buttonGapAngleDeg = 0f
    var overlayTextGapAngleDeg = 0f
    var centerX = 0f
    var centerY = 0f
}

@Composable
fun FocusTimerRing(
    progress: Float,
    isPaused: Boolean,
    timerText: String,
    springButtonText: String,
    onSpringButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    overlayFraction: Float = 0f,
    buttonGapSpacing: Dp = 42.dp,
    strokeWidth: Dp = 22.dp,
    trackColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
    progressColor: Color = MaterialTheme.colorScheme.primary,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    borderThickness: Dp = 1.5.dp,
) {
    val density = LocalDensity.current
    val buttonGapSpacingPx = with(density) { buttonGapSpacing.toPx() }
    val strokeWidthPx = with(density) { strokeWidth.toPx() }
    val borderThicknessPx = with(density) { borderThickness.toPx() }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "ringProgress",
    )

    val ring = remember { RingLayoutState() }

    Layout(
        content = {
            // Slot 0: Timer text
            TimerText(
                text = timerText,
                isPaused = isPaused,
                fontSize = 64.sp,
                lineHeight = 72.sp,
                fontWeight = FontWeight.Bold,
            )
            // Slot 1: Spring button (fades out as overlay activates)
            Box(modifier = Modifier.graphicsLayer { alpha = 1f - overlayFraction }) {
                SpringButton(
                    onClick = onSpringButtonClick,
                ) {
                    MorphTransition(
                        targetState = springButtonText,
                        label = "springButtonTextMorph",
                    ) { text: String ->
                        Text(
                            text = text,
                            fontWeight = FontWeight.Normal,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
                        )
                    }
                }
            }
            // Slot 2: Overlay label (fades in as overlay activates)
            Box(modifier = Modifier.graphicsLayer { alpha = overlayFraction }) {
                MorphTransition(
                    targetState = springButtonText,
                    label = "overlayLabelMorph",
                ) { text: String ->
                    Text(
                        text = text,
                        fontWeight = FontWeight.Normal,
                        fontSize = 28.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        modifier = modifier.drawBehind {
            if (ring.radius <= 0f) return@drawBehind

            val center = Offset(ring.centerX, ring.centerY)
            val arcSize = Size(ring.radius * 2, ring.radius * 2)
            val topLeft = Offset(center.x - ring.radius, center.y - ring.radius)

            // Interpolate gap angle between button and overlay text
            val currentGapAngle = ring.buttonGapAngleDeg +
                    (ring.overlayTextGapAngleDeg - ring.buttonGapAngleDeg) * overlayFraction
            val arcSpan = 360f - currentGapAngle
            val startAngle = 90f + currentGapAngle / 2f
            val stroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)

            // Track
            drawArc(
                color = trackColor,
                startAngle = startAngle,
                sweepAngle = arcSpan,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke,
            )

            // Progress
            if (animatedProgress > 0f) {
                drawArc(
                    color = progressColor,
                    startAngle = startAngle,
                    sweepAngle = arcSpan * animatedProgress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = stroke,
                )
            }

            // Tilt border — sweep gradient bright at top, fading to sides
            val borderBrush = Brush.sweepGradient(
                0.00f to borderColor.copy(alpha = 0.1f),
                0.25f to borderColor.copy(alpha = 0.1f),
                0.50f to borderColor.copy(alpha = 0.1f),
                0.75f to borderColor.copy(alpha = 1f),
                1.00f to borderColor.copy(alpha = 0.1f),
                center = center,
            )
            drawArc(
                brush = borderBrush,
                startAngle = startAngle,
                sweepAngle = arcSpan,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = borderThicknessPx, cap = StrokeCap.Round),
            )
        },
        measurePolicy = remember(buttonGapSpacingPx, strokeWidthPx) {
            RingMeasurePolicy(ring, buttonGapSpacingPx, strokeWidthPx)
        },
    )
}

private class RingMeasurePolicy(
    private val ring: RingLayoutState,
    private val buttonGapSpacingPx: Float,
    private val strokeWidthPx: Float,
) : MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureResult {
        val textPlaceable = measurables[0].measure(Constraints())
        val buttonPlaceable = measurables[1].measure(Constraints())
        val overlayTextPlaceable = measurables[2].measure(Constraints())

        val side = min(constraints.maxWidth, constraints.maxHeight)
        val ringRadius = side / 2f - strokeWidthPx / 2f

        // Gap angle for the spring button
        val buttonChord = buttonPlaceable.width + 2f * buttonGapSpacingPx
        val buttonGapRatio = (buttonChord / (2f * ringRadius)).coerceIn(-1f, 1f)
        val buttonGapAngleDeg = (2f * asin(buttonGapRatio) * 180f / kotlin.math.PI).toFloat()

        // Gap angle for the overlay text
        val overlayTextChord = overlayTextPlaceable.width + 2f * buttonGapSpacingPx
        val overlayTextGapRatio = (overlayTextChord / (2f * ringRadius)).coerceIn(-1f, 1f)
        val overlayTextGapAngleDeg =
            (2f * asin(overlayTextGapRatio) * 180f / kotlin.math.PI).toFloat()

        val cx = side / 2f
        val cy = side / 2f

        ring.radius = ringRadius
        ring.buttonGapAngleDeg = buttonGapAngleDeg
        ring.overlayTextGapAngleDeg = overlayTextGapAngleDeg
        ring.centerX = cx
        ring.centerY = cy

        return layout(side, side) {
            textPlaceable.place(
                (cx - textPlaceable.width / 2f).toInt(),
                (cy - textPlaceable.height / 2f).toInt(),
            )
            buttonPlaceable.place(
                (cx - buttonPlaceable.width / 2f).toInt(),
                (cy + ringRadius - buttonPlaceable.height / 2f).toInt(),
            )
            overlayTextPlaceable.place(
                (cx - overlayTextPlaceable.width / 2f).toInt(),
                (cy + ringRadius - overlayTextPlaceable.height / 2f).toInt(),
            )
        }
    }
}
