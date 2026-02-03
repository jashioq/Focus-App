package navigation.viewModel

import domain.model.OnboardingState
import domain.util.UseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import presentation.util.CoreViewModel
import util.Logger

/**
 * View model used for handling onboarding state. It controls which NavHost should be used.
 * @param emitOnboardingFinishedUseCase a [UseCase] used for emitting onboarding finished status.
 * @see MainNavHost
 * @see OnboardingNavHost
 */
class NavigationViewModel(
    private val emitOnboardingFinishedUseCase: UseCase<Unit, Flow<Boolean>>,
    scope: CoroutineScope? = null,
    logger: Logger? = null,
) : CoreViewModel<OnboardingState, Unit>(
    initialState = OnboardingState.LOADING,
    scope = scope,
    logger = logger,
) {
    init {
        vmScope.launch {
            emitOnboardingFinishedUseCase.call(value = Unit).onSuccess {
                it.collect { finished ->
                    stateFlow.update {
                        if (finished) {
                            OnboardingState.FINISHED
                        } else {
                            OnboardingState.NOT_FINISHED
                        }
                    }
                }
            }
        }
    }

    override fun Unit.process() {}
}
