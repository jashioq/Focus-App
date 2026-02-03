package presentation.screen.onboarding.nameScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import presentation.compose.component.progress.ProgressIndicatorState
import presentation.screen.onboarding.nameScreen.viewModel.NameScreenViewModel
import presentation.util.koinViewModel

@Composable
fun NameScreen(
    nameScreenViewModel: NameScreenViewModel = koinViewModel(),
    onOnboardingFinished: () -> Unit,
    progressIndicatorState: ProgressIndicatorState,
) {
    val state by nameScreenViewModel.state.collectAsState()

    NameScreenView(
        textFieldValue = state.name,
        onInputDone = {
            nameScreenViewModel.sendAction(
                NameScreenAction.SaveName,
            )
            onOnboardingFinished()
        },
        onTextFieldValueChange = {
            nameScreenViewModel.sendAction(
                NameScreenAction.UpdateName(it),
            )
        },
        progressIndicatorState = progressIndicatorState,
    )
}
