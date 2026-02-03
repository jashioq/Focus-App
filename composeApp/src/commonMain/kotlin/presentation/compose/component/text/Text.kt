package presentation.compose.component.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun Text(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    text: String,
    fontSize: TextUnit = 24.sp,
    lineHeight: TextUnit = 36.sp,
    fontWeight: FontWeight = FontWeight.Normal,
) {
    androidx.compose.material3.Text(
        modifier = modifier,
        color = color,
        text = text,
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontWeight = fontWeight,
    )
}
