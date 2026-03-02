package presentation.screen.newTask

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.compose.currentKoinScope
import org.koin.core.parameter.parametersOf
import presentation.screen.newTask.viewModel.NewTaskScreenViewModel

@Composable
fun NewTaskScreen(
    date: String,
) {
    val scope = currentKoinScope()
    val viewModel: NewTaskScreenViewModel = viewModel {
        scope.get<NewTaskScreenViewModel>(parameters = { parametersOf(date) })
    }
    val state by viewModel.state.collectAsState()

    NewTaskScreenView(state = state)
}
