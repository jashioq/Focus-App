package presentation.compose.component.button

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import focus.composeapp.generated.resources.Res
import focus.composeapp.generated.resources.ic_fast_forward
import focus.composeapp.generated.resources.ic_fast_rewind
import org.jetbrains.compose.resources.painterResource

@Composable
fun CurrentMonthButton(
    isBeforeCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CircleGlassButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(
                if (isBeforeCurrent) Res.drawable.ic_fast_forward else Res.drawable.ic_fast_rewind,
            ),
            contentDescription = "Back to current month",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp),
        )
    }
}
