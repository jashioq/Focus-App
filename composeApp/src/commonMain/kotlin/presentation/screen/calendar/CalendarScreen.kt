package presentation.screen.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import presentation.screen.calendar.viewModel.CalendarScreenViewModel
import presentation.util.koinViewModel

@Composable
fun CalendarScreen(
    viewModel: CalendarScreenViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    CalendarScreenView(
        state = state,
        onVisibleMonthChanged = { yearMonth ->
            viewModel.sendAction(CalendarScreenAction.VisibleMonthChanged(yearMonth))
        },
        onScrollToCurrentMonth = {
            viewModel.sendAction(CalendarScreenAction.ScrollToCurrentMonth)
        },
    )
}
