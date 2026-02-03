package presentation.screen.onboarding.startScreen

import androidx.compose.runtime.Composable

@Composable
fun StartScreen(
    onNavigateToNameScreen: () -> Unit,
) {
    StartScreenView(
        onPrimaryButtonClick = {
            onNavigateToNameScreen()
        },
    )
}
