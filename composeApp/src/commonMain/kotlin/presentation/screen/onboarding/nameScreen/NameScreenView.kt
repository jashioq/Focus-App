package presentation.screen.onboarding.nameScreen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import focus.composeapp.generated.resources.Res
import focus.composeapp.generated.resources.enter_name
import focus.composeapp.generated.resources.text_field_placeholder
import focus.composeapp.generated.resources.who_are_you_title
import org.jetbrains.compose.resources.stringResource
import presentation.compose.component.progress.ProgressIndicatorState
import presentation.compose.component.progress.SharedProgressIndicator
import presentation.compose.component.text.Text
import presentation.compose.component.textField.PillTextField

@Composable
fun NameScreenView(
    modifier: Modifier = Modifier,
    textFieldValue: String,
    onTextFieldValueChange: (String) -> Unit,
    onInputDone: () -> Unit,
    progressIndicatorState: ProgressIndicatorState,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .pointerInput(Unit) {
                detectTapGestures {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            },
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopEnd,
            ) {
                SharedProgressIndicator(
                    modifier = Modifier.padding(16.dp),
                    progressIndicatorState = progressIndicatorState,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.who_are_you_title),
                fontSize = 32.sp,
                lineHeight = 48.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(Res.string.enter_name),
            )
        }

        PillTextField(
            modifier = Modifier
                .imePadding(),
            value = textFieldValue,
            capitalizeKeyboard = true,
            onValueChange = onTextFieldValueChange,
            displayDoneButton = textFieldValue.isNotEmpty(),
            onDoneClick = {
                if (textFieldValue.isNotEmpty()) onInputDone()
            },
            placeholder = {
                Text(
                    text = stringResource(Res.string.text_field_placeholder),
                    fontSize = 24.sp,
                )
            },
        )
    }
}
