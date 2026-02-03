package presentation.screen.home.viewModel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import presentation.screen.home.HomeScreenAction
import presentation.screen.home.HomeScreenState
import presentation.util.CoreViewModel
import util.Logger

class HomeScreenViewModel(
    scope: CoroutineScope? = null,
    logger: Logger? = null,
) : CoreViewModel<HomeScreenState, HomeScreenAction>(
    initialState = HomeScreenState(
        timer = 0,
    ),
    scope = scope,
    logger = logger,
) {
    init {
        vmScope.launch {

        }
    }

    override fun HomeScreenAction.process() {
        when (val action = this@process) {
            HomeScreenAction.StartTimer -> {
            }
        }
    }
}
