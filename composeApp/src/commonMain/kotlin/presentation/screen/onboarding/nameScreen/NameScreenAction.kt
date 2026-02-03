package presentation.screen.onboarding.nameScreen

open class NameScreenAction {
    data class UpdateName(
        val newValue: String,
    ) : NameScreenAction()

    data object SaveName : NameScreenAction()
}
