package domain.useCase

import domain.model.AppFocusState
import domain.repository.AppStateRepository
import domain.util.UseCase
import kotlinx.coroutines.flow.Flow

class EmitAppFocusStateUseCase(
    private val appStateRepository: AppStateRepository,
): UseCase<Unit, Flow<AppFocusState>> {
    override suspend fun call(value: Unit): Result<Flow<AppFocusState>> =
        appStateRepository.appFocusState
}
