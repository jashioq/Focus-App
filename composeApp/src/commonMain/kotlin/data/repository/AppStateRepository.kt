package data.repository

import data.dataSource.appState.AppStateObserver
import domain.model.AppFocusState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AppStateRepository(
    private val appStateObserver: AppStateObserver
): domain.repository.AppStateRepository {
    override val appFocusState : Result<Flow<AppFocusState>> =
        runCatching {
            callbackFlow {
                appStateObserver.startObserving { state ->
                    trySend(state)
                }

                awaitClose {
                    appStateObserver.stopObserving()
                }
            }
        }
}
