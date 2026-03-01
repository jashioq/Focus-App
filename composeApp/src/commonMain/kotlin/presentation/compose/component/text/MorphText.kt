package presentation.compose.component.text

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import presentation.compose.component.transition.MorphTransition

private const val DefaultMorphDurationMs = 300
private const val DefaultMorphScale = 1.5f
private val DefaultMorphBlurRadius = 6.dp

@Composable
fun MorphText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    fontSize: TextUnit = 24.sp,
    lineHeight: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    morphDurationMs: Int = DefaultMorphDurationMs,
    morphScale: Float = DefaultMorphScale,
    morphBlurRadius: Dp = DefaultMorphBlurRadius,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        text.forEachIndexed { index, char ->
            key(index) {
                MorphTransition(
                    targetState = char,
                    durationMs = morphDurationMs,
                    morphScale = morphScale,
                    blurRadius = morphBlurRadius,
                    label = "morphChar$index",
                ) { targetChar ->
                    Text(
                        text = targetChar.toString(),
                        style = TextStyle(
                            color = color,
                            fontSize = fontSize,
                            lineHeight = lineHeight,
                            fontWeight = fontWeight,
                            fontFeatureSettings = "tnum",
                        ),
                    )
                }
            }
        }
    }
}
