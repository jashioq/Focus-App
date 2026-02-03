package presentation.compose.component.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    colors: ButtonColors = defaultPrimaryButtonColors(),
    text: String = "START",
    onClick: () -> Unit = {},
    isEnabled: Boolean = true,
) {
    Button(
        modifier = modifier
            .height(64.dp)
            .fillMaxWidth(),
        onClick = onClick,
        enabled = isEnabled,
        shape = RoundedCornerShape(50),
        colors = colors,
    ) {
        Text(
            fontSize = 24.sp,
            text = text,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun defaultPrimaryButtonColors() = ButtonDefaults.buttonColors(
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    contentColor = MaterialTheme.colorScheme.onPrimary,
    disabledContentColor = MaterialTheme.colorScheme.onPrimary,
)
