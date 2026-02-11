package presentation.compose.component.button

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import util.Haptic
import util.rememberHapticFeedback
import util.tiltBorder

data class ScaleKeyframe(
    val targetScale: Float,
    val durationMs: Int,
    val easing: Easing = EaseOut,
)

enum class HapticType { LIGHT, MEDIUM, HEAVY }

data class HapticTick(
    val timeMs: Long,
    val type: HapticType? = null,
)

private val DefaultPressKeyframes = listOf(
    ScaleKeyframe(targetScale = 1.3f, durationMs = 1500, easing = FastOutSlowInEasing),
    ScaleKeyframe(targetScale = 1.4f, durationMs = 500, easing = EaseOut),
)

private const val DefaultReleaseDamping = 0.4f
private const val DefaultReleaseStiffness = Spring.StiffnessMedium

private fun defaultHapticTicks(): List<HapticTick> =
    listOf(
        HapticTick(timeMs = 0, type = HapticType.LIGHT),
        HapticTick(timeMs = 200, type = HapticType.LIGHT),
        HapticTick(timeMs = 400, type = HapticType.LIGHT),
        HapticTick(timeMs = 600, type = HapticType.MEDIUM),

        HapticTick(timeMs = 1500, type = HapticType.LIGHT),
        HapticTick(timeMs = 1600, type = HapticType.LIGHT),
        HapticTick(timeMs = 1700, type = HapticType.MEDIUM),
    )


private fun Haptic.perform(type: HapticType) = when (type) {
    HapticType.LIGHT -> performLightImpact()
    HapticType.MEDIUM -> performMediumImpact()
    HapticType.HEAVY -> performHeavyImpact()
}

@Composable
fun SpringButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    pressKeyframes: List<ScaleKeyframe> = DefaultPressKeyframes,
    releaseSpringDamping: Float = DefaultReleaseDamping,
    releaseSpringStiffness: Float = DefaultReleaseStiffness,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    borderThickness: Dp = 1.5.dp,
    borderVisibilityBound: Float = 0.3f,
    borderUpperAlpha: Float = 0.8f,
    alphaThresholdScale: Float = pressKeyframes.firstOrNull()?.targetScale ?: 1.3f,
    hapticTicks: List<HapticTick> = remember { defaultHapticTicks() },
    snapHapticType: HapticType = HapticType.HEAVY,
    glowRadius: Dp = 20.dp,
    glowIntensity: Float = 0.5f,
    shape: Shape = RoundedCornerShape(16.dp),
    content: @Composable () -> Unit,
) {
    val haptic = rememberHapticFeedback()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scaleAnimatable = remember { Animatable(1f) }

    val minimumAlpha = if (alphaThresholdScale > 1f) {
        ((scaleAnimatable.value - 1f) / (alphaThresholdScale - 1f)).coerceIn(0f, 1f)
    } else {
        0f
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            // Launch haptic ticker alongside animation — delay-based for consistent timing
            val hapticJob = launch {
                var elapsed = 0L
                for (tick in hapticTicks) {
                    val wait = tick.timeMs - elapsed
                    if (wait > 0) delay(wait)
                    elapsed = tick.timeMs
                    tick.type?.let { haptic.perform(it) }
                }
            }

            for (keyframe in pressKeyframes) {
                scaleAnimatable.animateTo(
                    targetValue = keyframe.targetScale,
                    animationSpec = tween(keyframe.durationMs, easing = keyframe.easing),
                )
            }

            // All keyframes completed — snap haptic, trigger action, spring back
            hapticJob.cancel()
            haptic.perform(snapHapticType)
            onClick()
            scaleAnimatable.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = releaseSpringDamping,
                    stiffness = releaseSpringStiffness,
                ),
            )
        } else {
            // Released before completion — just spring back, no action
            scaleAnimatable.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = releaseSpringDamping,
                    stiffness = releaseSpringStiffness,
                ),
            )
        }
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scaleAnimatable.value
                scaleY = scaleAnimatable.value
            }
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        // Content layer — this determines the outer Box size
        Box(
            modifier = Modifier
                .tiltBorder(
                    color = borderColor,
                    thickness = borderThickness,
                    visibilityBound = borderVisibilityBound,
                    minimumAlpha = minimumAlpha,
                    upperAlpha = borderUpperAlpha,
                    shape = shape,
                )
                .clip(shape)
                .background(Color.White.copy(alpha = 0.09f))
                .clickable(
                    indication = null,
                    interactionSource = interactionSource,
                ) { },
            contentAlignment = Alignment.Center,
        ) {
            // Inner glow — wrapper clips all child rendering including blur overflow
            if (minimumAlpha > 0f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(shape),
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .graphicsLayer { alpha = minimumAlpha * glowIntensity }
                            .blur(glowRadius, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                            .background(borderColor),
                    )
                }
            }
            content()
        }
    }
}
