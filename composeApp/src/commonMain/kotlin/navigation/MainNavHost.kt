package navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import presentation.screen.calendar.CalendarScreen
import presentation.screen.calendar.CalendarScreenDestination
import presentation.screen.dayPreview.DayPreviewScreen
import presentation.screen.dayPreview.DayPreviewScreenDestination
import presentation.screen.home.HomeScreen
import presentation.screen.home.HomeScreenDestination
import presentation.screen.newTask.NewTaskScreen
import presentation.screen.newTask.NewTaskScreenDestination

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = CalendarScreenDestination,
        ) {
            composable<CalendarScreenDestination> {
                CalendarScreen(
                    onNavigateToNewTask = { date ->
                        navController.navigate(DayPreviewScreenDestination(date = date))
                    },
                )
            }
            composable<HomeScreenDestination> {
                HomeScreen()
            }
            composable<DayPreviewScreenDestination> { backStackEntry ->
                val destination: DayPreviewScreenDestination = backStackEntry.toRoute()
                DayPreviewScreen(
                    date = destination.date,
                    onNavigateToNewTask = { date ->
                        navController.navigate(NewTaskScreenDestination(date = date))
                    },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                )
            }
            composable<NewTaskScreenDestination>(
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
            ) { backStackEntry ->
                val destination: NewTaskScreenDestination = backStackEntry.toRoute()
                NewTaskScreen(
                    date = destination.date,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                )
            }
        }
    }
}
