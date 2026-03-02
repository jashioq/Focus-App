package navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import presentation.screen.calendar.CalendarScreen
import presentation.screen.calendar.CalendarScreenDestination
import presentation.screen.home.HomeScreen
import presentation.screen.home.HomeScreenDestination
import presentation.screen.newTask.NewTaskScreen
import presentation.screen.newTask.NewTaskScreenDestination

@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = CalendarScreenDestination,
    ) {
        composable<CalendarScreenDestination> {
            CalendarScreen(
                onNavigateToNewTask = { date ->
                    navController.navigate(NewTaskScreenDestination(date = date))
                },
            )
        }
        composable<HomeScreenDestination> {
            HomeScreen()
        }
        composable<NewTaskScreenDestination> { backStackEntry ->
            val destination: NewTaskScreenDestination = backStackEntry.toRoute()
            NewTaskScreen(date = destination.date)
        }
    }
}
