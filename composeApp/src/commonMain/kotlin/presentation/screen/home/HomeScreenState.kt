package presentation.screen.home

data class HomeScreenState(
    val timerText: String = "00:00",
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
)
