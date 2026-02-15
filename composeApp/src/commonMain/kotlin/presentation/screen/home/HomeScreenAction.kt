package presentation.screen.home

sealed class HomeScreenAction {
    data object StartTimer : HomeScreenAction()
    data object StopTimer : HomeScreenAction()
    data object TogglePausePlay : HomeScreenAction()
    data object SkipBlock : HomeScreenAction()
    data object ExtendBlock : HomeScreenAction()
}
