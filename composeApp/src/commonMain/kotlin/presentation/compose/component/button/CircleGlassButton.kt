package presentation.compose.component.button

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import presentation.compose.component.border.tiltBorder

private const val GlowIntensity = 0.6f
private val GlowRadius = 20.dp
private val ButtonShape = RoundedCornerShape(32.dp)

@Composable
fun CircleGlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showGlow: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val interactionSource = remember { MutableInteractionSource() }
    val glowAlpha = remember { Animatable(0f) }

    LaunchedEffect(interactionSource) {
        val scope = this
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Press && showGlow) {
                scope.launch {
                    glowAlpha.snapTo(0f)
                    glowAlpha.animateTo(1f, animationSpec = tween(150))
                    glowAlpha.animateTo(0f, animationSpec = tween(150))
                }
            }
        }
    }

    Box(
        modifier = modifier
            .size(56.dp)
            .tiltBorder(
                color = primaryColor,
                thickness = 1.dp,
                upperAlpha = 0.5f,
                shape = ButtonShape,
            )
            .clip(ButtonShape)
            .background(Color.White.copy(alpha = 0.09f))
            .clickable(
                indication = null,
                interactionSource = interactionSource,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        // Glow overlay — clips blur overflow, renders behind content
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(ButtonShape),
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { alpha = glowAlpha.value * GlowIntensity }
                    .blur(GlowRadius, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    .background(primaryColor),
            )
        }

        content()
    }
}
