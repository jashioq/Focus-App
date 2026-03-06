package presentation.screen.newTask.view

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import presentation.compose.component.textField.MultilineTextField
import presentation.compose.component.textField.PillTextField

@Composable
fun NameDescriptionView(
    name: String,
    description: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PillTextField(
            value = name,
            onValueChange = onNameChange,
            displayDoneButton = false,
            placeholder = {
                Text(
                    text = "Name",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                )
            },
        )
        MultilineTextField(
            value = description,
            onValueChange = onDescriptionChange,
            placeholder = "Description",
        )
    }
}
