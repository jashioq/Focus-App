import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    primary = Color.White,
    inversePrimary = Color(0xFF141415),
    onPrimary = Color(0xFF141415),
    primaryContainer = Color(0xFFf9f4ec),
    surfaceContainer = Color(0xFF29292c),
    background = Color(0xFF141415),
)

private val LightColorPalette = lightColorScheme(
    primary = Color.Black,
    inversePrimary = Color.White,
    onPrimary = Color.White,
    primaryContainer = Color.Black,
    surfaceContainer = Color.White,
    background = Color.White,
)

/**
 * Custom material theme to be used in the app. Handles light and dark themes.
 */
@Composable
fun Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        content = {
            Surface(color = MaterialTheme.colorScheme.background) {
                content()
            }
        },
    )
}
