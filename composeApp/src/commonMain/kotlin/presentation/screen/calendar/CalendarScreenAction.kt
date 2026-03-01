package presentation.screen.calendar

import kotlinx.datetime.YearMonth

sealed class CalendarScreenAction {
    data class VisibleMonthChanged(val yearMonth: YearMonth) : CalendarScreenAction()
    data object ScrollToCurrentMonth : CalendarScreenAction()
}
