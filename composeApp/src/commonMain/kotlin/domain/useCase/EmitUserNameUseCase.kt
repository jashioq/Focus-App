package domain.useCase

import domain.model.USER_NAME_KEY
import domain.repository.DataStoreRepository
import domain.util.UseCase
import kotlinx.coroutines.flow.Flow

open class EmitUserNameUseCase(
    private val dataStoreRepository: DataStoreRepository,
) : UseCase<Unit, Flow<String>> {
    override suspend fun call(value: Unit): Result<Flow<String>> =
        dataStoreRepository.emitStringPreference(
            key = USER_NAME_KEY,
            default = "",
        )
}
