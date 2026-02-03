package navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import presentation.screen.home.HomeScreen
import presentation.screen.home.HomeScreenDestination

@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = HomeScreenDestination,
    ) {
        composable<HomeScreenDestination> {
            HomeScreen()
        }
    }
}
