package domain.useCase

import domain.model.USER_NAME_KEY
import domain.repository.DataStoreRepository
import domain.util.StreamUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

open class EmitUserNameUseCase(
    private val dataStoreRepository: DataStoreRepository,
) : StreamUseCase<Unit, String> {
    override fun stream(value: Unit): Flow<String> = flow {
        emitAll(dataStoreRepository.emitStringPreference(key = USER_NAME_KEY, default = "").getOrThrow())
    }
}
