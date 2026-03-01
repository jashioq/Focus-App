package presentation.screen.calendar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.plusMonths
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.YearMonth

@Composable
fun CalendarScreenView(
    state: CalendarScreenState,
    onVisibleMonthChanged: (YearMonth) -> Unit,
    onScrollToCurrentMonth: () -> Unit,
) {
    val currentMonth = state.currentMonth
    val startMonth = remember(currentMonth) { currentMonth.minusMonths(12) }
    val endMonth = remember(currentMonth) { currentMonth.plusMonths(12) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
    val coroutineScope = rememberCoroutineScope()

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek,
    )

    LaunchedEffect(calendarState) {
        snapshotFlow { calendarState.firstVisibleMonth }
            .collectLatest { month ->
                onVisibleMonthChanged(month.yearMonth)
            }
    }

    val backButtonAlpha by animateFloatAsState(
        targetValue = if (state.isOnCurrentMonth) 0f else 1f,
        animationSpec = tween(durationMillis = 500),
        label = "backButtonAlpha",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
    ) {
        Button(
            onClick = {
                onScrollToCurrentMonth()
                coroutineScope.launch {
                    calendarState.animateScrollToMonth(currentMonth)
                }
            },
            enabled = !state.isOnCurrentMonth,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .graphicsLayer { alpha = backButtonAlpha },
        ) {
            Text(text = "Back to current month")
        }

        HorizontalCalendar(
            state = calendarState,
            monthHeader = { month ->
                MonthHeader(month.yearMonth)
            },
            dayContent = { day ->
                Day(day)
            },
        )
    }
}

@Composable
private fun MonthHeader(yearMonth: YearMonth) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "${yearMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${yearMonth.year}",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        DaysOfWeekHeader()
    }
}

@Composable
private fun DaysOfWeekHeader() {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    Row(modifier = Modifier.fillMaxWidth()) {
        for (day in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                text = day,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Day(day: CalendarDay) {
    val isCurrentMonth = day.position == DayPosition.MonthDate
    Box(
        modifier = Modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = if (isCurrentMonth) {
                MaterialTheme.colorScheme.onBackground
            } else {
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            },
            fontSize = 14.sp,
        )
    }
}
