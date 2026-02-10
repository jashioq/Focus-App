package presentation.compose.component.timerControl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import util.rememberHapticFeedback

private const val DefaultScale = 1.15f
private const val DefaultScaleDurationMs = 100

@Composable
fun ScaleOnTouchButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isExternallyPressed: Boolean = false,
    scale: Float = DefaultScale,
    scaleDurationMs: Int = DefaultScaleDurationMs,
    content: @Composable () -> Unit,
) {
    val haptic = rememberHapticFeedback()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isTouching = isPressed || isExternallyPressed

    val scaleAnimatable = remember { Animatable(1f) }
    LaunchedEffect(isTouching) {
        if (isTouching) {
            scaleAnimatable.animateTo(
                targetValue = scale,
                animationSpec = tween(scaleDurationMs, easing = FastOutSlowInEasing),
            )
            haptic.performHeavyImpact()
        } else {
            haptic.performHeavyImpact()
            scaleAnimatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(scaleDurationMs, easing = FastOutSlowInEasing),
            )
        }
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scaleAnimatable.value
                scaleY = scaleAnimatable.value
            }
            .then(modifier)
            .clickable(
                indication = null,
                interactionSource = interactionSource,
            ) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
