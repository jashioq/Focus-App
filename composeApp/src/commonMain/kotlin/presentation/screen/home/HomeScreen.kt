package presentation.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import presentation.screen.home.viewModel.HomeScreenViewModel
import presentation.util.koinViewModel

@Composable
fun HomeScreen(
    homeScreenViewModel: HomeScreenViewModel = koinViewModel(),
) {
    val state by homeScreenViewModel.state.collectAsState()

    HomeScreenView(
        timerText = state.timerText,
        isRunning = state.isRunning,
        isPaused = state.isPaused,
        progress = state.progress,
        blockLabel = state.blockLabel,
        onShowNotification = {
            homeScreenViewModel.sendAction(HomeScreenAction.ShowNotification)
        },
        onDismissNotification = {
            homeScreenViewModel.sendAction(HomeScreenAction.DismissNotification)
        },
        onTogglePausePlay = {
            homeScreenViewModel.sendAction(HomeScreenAction.TogglePausePlay)
        },
        onSkipBlock = {
            homeScreenViewModel.sendAction(HomeScreenAction.SkipBlock)
        },
    )
}
