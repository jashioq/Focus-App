package presentation.screen.newTask.viewModel

import kotlinx.coroutines.CoroutineScope
import presentation.screen.newTask.NewTaskScreenAction
import presentation.screen.newTask.NewTaskScreenState
import presentation.util.CoreViewModel
import util.Logger

class NewTaskScreenViewModel(
    date: String,
    scope: CoroutineScope? = null,
    logger: Logger? = null,
) : CoreViewModel<NewTaskScreenState, NewTaskScreenAction>(
    initialState = NewTaskScreenState(date = date),
    scope = scope,
    logger = logger,
) {
    override fun NewTaskScreenAction.process() {}
}
