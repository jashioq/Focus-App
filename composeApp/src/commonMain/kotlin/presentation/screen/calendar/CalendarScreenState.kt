package presentation.screen.calendar

import kotlinx.datetime.YearMonth

data class CalendarScreenState(
    val currentMonth: YearMonth,
    val isOnCurrentMonth: Boolean = true,
)
