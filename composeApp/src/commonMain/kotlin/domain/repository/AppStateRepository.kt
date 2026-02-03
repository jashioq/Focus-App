package domain.repository

import domain.model.AppFocusState
import kotlinx.coroutines.flow.Flow

interface AppStateRepository {
    val appFocusState: Result<Flow<AppFocusState>>
}