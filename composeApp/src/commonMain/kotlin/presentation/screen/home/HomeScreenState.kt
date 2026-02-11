package presentation.screen.home

data class HomeScreenState(
    val timerText: String = "00:00",
    val isRunning: Boolean = false,
    val isPaused: Boolean = true,
    val progress: Float = 0f,
    val blockLabel: String = "Focus",
    val addButtonText: String = "1 min",
)
