package domain.model

sealed interface AppFocusState {
    data object Foreground : AppFocusState
    data object Background : AppFocusState
}