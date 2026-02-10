package util

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.tiltBorder(
    color: Color,
    thickness: Dp = 2.dp,
    visibilityBound: Float = 0.15f,
    shape: Shape = RoundedCornerShape(50),
): Modifier = this.drawWithContent {
    drawContent()

    val outline = shape.createOutline(size, layoutDirection, this)
    val thicknessPx = thickness.toPx()

    // Sweep gradient starts at 3 o'clock and goes clockwise.
    // Bright spot at top = 270° in sweep coords = 0.75 normalized shift.
    val shift = 0.75f

    // Opacity pattern: front=1, +90°=0, back=visibilityBound, -90°=0
    val shiftedStops = listOf(
        (0.00f + shift) % 1f to color.copy(alpha = 1.0f),
        (0.25f + shift) % 1f to color.copy(alpha = 0.0f),
        (0.50f + shift) % 1f to color.copy(alpha = visibilityBound),
        (0.75f + shift) % 1f to color.copy(alpha = 0.0f),
    ).sortedBy { it.first }

    val gradientStops = buildSweepGradientStops(shiftedStops)

    val brush = Brush.sweepGradient(
        colorStops = gradientStops.toTypedArray(),
        center = Offset(size.width / 2f, size.height / 2f),
    )

    val path = Path().apply { addOutline(outline) }
    drawPath(
        path = path,
        brush = brush,
        style = Stroke(width = thicknessPx),
    )
}

private fun buildSweepGradientStops(
    sortedStops: List<Pair<Float, Color>>,
): List<Pair<Float, Color>> {
    if (sortedStops.isEmpty()) return emptyList()

    val result = mutableListOf<Pair<Float, Color>>()

    if (sortedStops.first().first > 0.001f) {
        val last = sortedStops.last()
        val first = sortedStops.first()
        val gap = 1f - last.first + first.first
        val t = if (gap > 0f) first.first / gap else 0f
        result.add(0f to lerp(last.second, first.second, t))
    }

    result.addAll(sortedStops)

    if (sortedStops.last().first < 0.999f) {
        result.add(1f to result.first().second)
    }

    return result
}
