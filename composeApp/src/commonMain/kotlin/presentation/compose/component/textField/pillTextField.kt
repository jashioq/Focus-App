package presentation.compose.component.textField

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import focus.composeapp.generated.resources.Res
import focus.composeapp.generated.resources.circle_arrow
import org.jetbrains.compose.resources.painterResource

@Composable
fun PillTextField(
    modifier: Modifier = Modifier,
    colors: TextFieldColors = defaultTextFieldColors(),
    placeholder: @Composable () -> Unit,
    isEnabled: Boolean = true,
    onValueChange: (String) -> Unit,
    value: String,
    capitalizeKeyboard: Boolean = false,
    displayDoneButton: Boolean = true,
    onDoneClick: () -> Unit = {},
) {
    TextField(
        modifier = modifier
            .height(64.dp)
            .fillMaxWidth()
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(50)),
        value = value,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(50),
        enabled = isEnabled,
        placeholder = placeholder,
        textStyle = TextStyle.Default.copy(
            fontSize = 24.sp,
        ),
        singleLine = true,
        keyboardActions = KeyboardActions {
            onDoneClick()
        },
        keyboardOptions = if (capitalizeKeyboard) {
            KeyboardOptions(capitalization = KeyboardCapitalization.Words)
        } else {
            KeyboardOptions()
        },
        colors = colors,
        trailingIcon = {
            if (displayDoneButton) {
                Icon(
                    painter = painterResource(resource = Res.drawable.circle_arrow),
                    contentDescription = "Done",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) {
                            onDoneClick()
                        },
                )
            }
        },
    )
}

@Composable
fun defaultTextFieldColors() = TextFieldDefaults.colors().copy(
    focusedTextColor = MaterialTheme.colorScheme.primary,
    unfocusedTextColor = MaterialTheme.colorScheme.primary,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    focusedPlaceholderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
    cursorColor = MaterialTheme.colorScheme.primary,
)
