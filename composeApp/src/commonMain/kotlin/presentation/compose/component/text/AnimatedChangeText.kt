package presentation.compose.component.text

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedChangeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
    fontSize: TextUnit = 64.sp,
    lineHeight: TextUnit = 72.sp,
    fontWeight: FontWeight = FontWeight.Bold,
) {
    var currentText by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("") }

    LaunchedEffect(key1 = text) {
        targetText = text
    }

    val opacity by animateFloatAsState(
        targetValue = if (currentText == targetText) 1f else 0f,
        animationSpec = tween(durationMillis = 150, easing = FastOutLinearInEasing),
        label = "opacity",
    )

    SideEffect {
        if (opacity == 0f && currentText != targetText) {
            currentText = targetText
        }
    }

    Text(
        modifier = modifier.alpha(opacity),
        color = color,
        text = currentText,
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontWeight = fontWeight,
    )
}
