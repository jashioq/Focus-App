package domain.util

import kotlinx.coroutines.flow.Flow

interface StreamUseCase<in Val, out T> {
    fun stream(value: Val): Flow<T>
}
