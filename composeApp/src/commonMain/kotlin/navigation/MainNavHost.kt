package navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import presentation.screen.calendar.CalendarScreen
import presentation.screen.calendar.CalendarScreenDestination
import presentation.screen.home.HomeScreen
import presentation.screen.home.HomeScreenDestination

@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = CalendarScreenDestination,
    ) {
        composable<CalendarScreenDestination> {
            CalendarScreen()
        }
        composable<HomeScreenDestination> {
            HomeScreen()
        }
    }
}
