package presentation.compose.component.transition

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val DefaultDurationMs = 600
private const val DefaultMorphScale = 1.5f
private val DefaultBlurRadius = 40.dp

@Composable
fun <T> MorphTransition(
    targetState: T,
    modifier: Modifier = Modifier,
    durationMs: Int = DefaultDurationMs,
    morphScale: Float = DefaultMorphScale,
    blurRadius: Dp = DefaultBlurRadius,
    label: String = "morphTransition",
    content: @Composable (T) -> Unit,
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        transitionSpec = {
            (fadeIn(tween(durationMs)) + scaleIn(
                initialScale = morphScale,
                animationSpec = tween(durationMs),
            )).togetherWith(
                fadeOut(tween(durationMs)) + scaleOut(
                    targetScale = morphScale,
                    animationSpec = tween(durationMs),
                ),
            ).using(SizeTransform(clip = false))
        },
        label = label,
    ) { state ->
        val blur by transition.animateFloat(
            transitionSpec = { tween(durationMs) },
            label = "${label}Blur",
        ) { enterExitState ->
            when (enterExitState) {
                EnterExitState.Visible -> 0f
                else -> blurRadius.value
            }
        }

        Box(
            modifier = Modifier.blur(blur.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded),
            contentAlignment = Alignment.Center,
        ) {
            content(state)
        }
    }
}
