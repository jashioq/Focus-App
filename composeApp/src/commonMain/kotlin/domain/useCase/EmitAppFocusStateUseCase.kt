package domain.useCase

import domain.model.AppFocusState
import domain.repository.AppStateRepository
import domain.util.StreamUseCase
import kotlinx.coroutines.flow.Flow

class EmitAppFocusStateUseCase(
    private val appStateRepository: AppStateRepository,
) : StreamUseCase<Unit, AppFocusState> {
    override fun stream(value: Unit): Flow<AppFocusState> = appStateRepository.appFocusState.getOrThrow()
}
