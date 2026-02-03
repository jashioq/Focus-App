package navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import presentation.compose.component.progress.ProgressIndicatorState
import presentation.screen.onboarding.nameScreen.NameScreen
import presentation.screen.onboarding.nameScreen.NameScreenDestination
import presentation.screen.onboarding.startScreen.StartScreen
import presentation.screen.onboarding.startScreen.StartScreenDestination

@Composable
fun OnboardingNavHost() {
    val navController = rememberNavController()
    val progressIndicatorState = remember { ProgressIndicatorState() }
    NavHost(
        navController = navController,
        startDestination = StartScreenDestination,
    ) {
        composable<StartScreenDestination> {
            StartScreen(
                onNavigateToNameScreen = {
                    navController.navigate(
                        NameScreenDestination,
                    )
                },
            ).also {
                progressIndicatorState.updateProgress(0f)
            }
        }

        composable<NameScreenDestination> {
            NameScreen(
                progressIndicatorState = progressIndicatorState,
                onOnboardingFinished = {
                    progressIndicatorState.updateProgress(1f)
                }
            ).also {
                progressIndicatorState.updateProgress(0.5f)
            }
        }
    }
}
