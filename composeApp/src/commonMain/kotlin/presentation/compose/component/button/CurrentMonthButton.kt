package presentation.compose.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import focus.composeapp.generated.resources.Res
import focus.composeapp.generated.resources.ic_fast_forward
import focus.composeapp.generated.resources.ic_fast_rewind
import org.jetbrains.compose.resources.painterResource
import presentation.compose.component.border.tiltBorder

@Composable
fun CurrentMonthButton(
    isBeforeCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .tiltBorder(
                color = MaterialTheme.colorScheme.primary,
                thickness = 1.dp,
                upperAlpha = 0.5f,
                shape = CircleShape,
            )
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.09f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
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
