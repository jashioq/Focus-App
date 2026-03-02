package domain.useCase

import domain.model.ONBOARDING_FINISHED_KEY
import domain.repository.DataStoreRepository
import domain.util.StreamUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * Use case used for emitting onboarding finished status from the datastore as a [Flow].
 * @param dataStoreRepository a [DataStoreRepository] instance.
 */
open class EmitOnboardingFinishedUseCase(
    private val dataStoreRepository: DataStoreRepository,
) : StreamUseCase<Unit, Boolean> {
    /**
     * @return a [Flow] of [Boolean] representing onboarding finished status.
     */
    override fun stream(value: Unit): Flow<Boolean> = flow {
        emitAll(dataStoreRepository.emitBooleanPreference(key = ONBOARDING_FINISHED_KEY, default = false).getOrThrow())
    }
}
