package presentation.screen.calendar.viewModel

import com.kizitonwose.calendar.core.now
import domain.util.UseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.YearMonth
import presentation.screen.calendar.CalendarScreenAction
import presentation.screen.calendar.CalendarScreenState
import presentation.util.CoreViewModel
import util.Logger

class CalendarScreenViewModel(
    private val emitUserNameUseCase: UseCase<Unit, Flow<String>>,
    scope: CoroutineScope? = null,
    logger: Logger? = null,
) : CoreViewModel<CalendarScreenState, CalendarScreenAction>(
    initialState = CalendarScreenState(currentMonth = YearMonth.now()),
    scope = scope,
    logger = logger,
) {
    init {
        vmScope.launch {
            emitUserNameUseCase.call(Unit).getOrNull()?.collect { name ->
                stateFlow.update { it.copy(userName = name) }
            }
        }
    }

    override fun CalendarScreenAction.process() {
        when (this) {
            is CalendarScreenAction.VisibleMonthChanged -> {
                stateFlow.update { it.copy(isOnCurrentMonth = yearMonth == it.currentMonth) }
            }
            CalendarScreenAction.ScrollToCurrentMonth -> {
                stateFlow.update { it.copy(isOnCurrentMonth = true) }
            }
        }
    }
}
