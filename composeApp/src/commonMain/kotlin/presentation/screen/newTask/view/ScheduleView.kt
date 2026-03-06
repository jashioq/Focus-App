package presentation.screen.newTask.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import presentation.compose.component.wheelPicker.WheelPicker
import presentation.compose.component.wheelPicker.WheelPickerSection

private val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
private val days = (1..31).map { it.toString().padStart(2, '0') }
private val years = (2024..2035).map { it.toString() }
private val hours = (0..23).map { it.toString().padStart(2, '0') }
private val minutes = (0..59).map { it.toString().padStart(2, '0') }

@Composable
fun ScheduleView(
    defaultStartDate: LocalDate,
    defaultDeadline: LocalDate,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        PickerSection(title = "Deadline") {
            WheelPicker(
                sections = listOf(
                    WheelPickerSection(
                        items = days,
                        initialIndex = defaultDeadline.dayOfMonth - 1,
                    ),
                    WheelPickerSection(
                        items = months,
                        initialIndex = defaultDeadline.monthNumber - 1,
                    ),
                    WheelPickerSection(
                        items = years,
                        initialIndex = (defaultDeadline.year - 2024).coerceIn(0, years.size - 1),
                    ),
                ),
                flingVelocityMultiplier = 0.8f
            )
        }

        PickerSection(title = "Total focus time") {
            WheelPicker(
                sections = listOf(
                    WheelPickerSection(
                        items = hours,
                        initialIndex = 1,
                        trailingContent = { Text("h", style = MaterialTheme.typography.bodyLarge) },
//                        leadingContent = { Text("l", style = MaterialTheme.typography.bodyLarge) },
                    ),
                    WheelPickerSection(
                        items = minutes,
                        initialIndex = 0,
                        trailingContent = { Text("m", style = MaterialTheme.typography.bodyLarge) },
//                        leadingContent = { Text("l", style = MaterialTheme.typography.bodyLarge) },
                    ),
                ),
            )
        }

        PickerSection(title = "Start date") {
            WheelPicker(
                sections = listOf(
                    WheelPickerSection(
                        items = days,
                        initialIndex = defaultStartDate.dayOfMonth - 1,
                    ),
                    WheelPickerSection(
                        items = months,
                        initialIndex = defaultStartDate.monthNumber - 1,
                    ),
                    WheelPickerSection(
                        items = years,
                        initialIndex = (defaultStartDate.year - 2024).coerceIn(0, years.size - 1),
                    ),
                ),
            )
        }
    }
}

@Composable
private fun PickerSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        content()
    }
}
