package data.dataSource.appState

import domain.model.AppFocusState

expect class AppStateObserver() {
    fun startObserving(onStateChange: (AppFocusState) -> Unit)
    fun stopObserving()
}