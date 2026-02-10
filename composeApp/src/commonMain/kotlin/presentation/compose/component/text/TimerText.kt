package presentation.compose.component.text

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val DefaultBlinkDurationMs = 2000
private const val DefaultVisibilityBound = 0.2f

@Composable
fun TimerText(
    text: String,
    isPaused: Boolean,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    fontSize: TextUnit = 24.sp,
    lineHeight: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    visibilityBound: Float = DefaultVisibilityBound,
    blinkDurationMs: Int = DefaultBlinkDurationMs,
    morphDurationMs: Int = 300,
    morphScale: Float = 1.5f,
    morphBlurRadius: Dp = 6.dp,
) {
    val blinkAnimatable = remember { Animatable(1f) }
    LaunchedEffect(isPaused) {
        if (isPaused) {
            while (true) {
                blinkAnimatable.animateTo(
                    targetValue = visibilityBound,
                    animationSpec = tween(blinkDurationMs / 2, easing = EaseInOut),
                )
                blinkAnimatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(blinkDurationMs / 2, easing = EaseInOut),
                )
            }
        } else {
            blinkAnimatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(blinkDurationMs / 2, easing = EaseInOut),
            )
        }
    }

    MorphText(
        text = text,
        modifier = modifier.graphicsLayer { alpha = blinkAnimatable.value },
        color = color,
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontWeight = fontWeight,
        morphDurationMs = morphDurationMs,
        morphScale = morphScale,
        morphBlurRadius = morphBlurRadius,
    )
}
