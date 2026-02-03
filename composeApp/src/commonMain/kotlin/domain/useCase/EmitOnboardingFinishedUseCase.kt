package domain.useCase

import domain.model.ONBOARDING_FINISHED_KEY
import domain.repository.DataStoreRepository
import domain.util.UseCase
import kotlinx.coroutines.flow.Flow

/**
 * Use case used for emitting onboarding finished status from the datastore as a [Flow].
 * @param dataStoreRepository a [DataStoreRepository] instance.
 */
open class EmitOnboardingFinishedUseCase(
    private val dataStoreRepository: DataStoreRepository,
) : UseCase<Unit, Flow<Boolean>> {
    /**
     * @return a [Flow] of [Boolean] representing onboarding finished status.
     */
    override suspend fun call(value: Unit): Result<Flow<Boolean>> =
        dataStoreRepository.emitBooleanPreference(
            key = ONBOARDING_FINISHED_KEY,
            default = false,
        )
}
