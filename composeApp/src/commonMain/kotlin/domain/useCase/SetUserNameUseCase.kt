package domain.useCase

import domain.model.USER_NAME_KEY
import domain.repository.DataStoreRepository
import domain.util.UseCase

open class SetUserNameUseCase(
    private val dataStoreRepository: DataStoreRepository,
) : UseCase<String, Unit> {
    override suspend fun call(value: String): Result<Unit> =
        dataStoreRepository.putStringPreference(
            key = USER_NAME_KEY,
            value = value,
        )
}
