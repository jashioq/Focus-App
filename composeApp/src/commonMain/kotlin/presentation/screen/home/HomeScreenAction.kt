package presentation.screen.home

open class HomeScreenAction {
    data object ShowNotification : HomeScreenAction()
    data object DismissNotification : HomeScreenAction()
    data object TogglePausePlay : HomeScreenAction()
    data object SkipBlock : HomeScreenAction()
}
