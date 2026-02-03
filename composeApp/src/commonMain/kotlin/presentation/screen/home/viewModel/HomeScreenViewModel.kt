package presentation.screen.home.viewModel

import domain.model.AppFocusState
import domain.util.UseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import presentation.screen.home.HomeScreenAction
import presentation.screen.home.HomeScreenState
import presentation.util.CoreViewModel
import util.Logger

class HomeScreenViewModel(
    private val emitAppFocusStateUseCase: UseCase<Unit, Flow<AppFocusState>>,
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
            emitAppFocusStateUseCase.call(Unit).onSuccess { appFocusStateFlow ->
                appFocusStateFlow.collect { appFocusState ->
                    vmLogger.d("dupa", "appFocusState: $appFocusState")
                }
            }
        }
    }

    override fun HomeScreenAction.process() {
        when (val action = this@process) {
            HomeScreenAction.StartTimer -> {
            }
        }
    }
}
