package domain.repository

import domain.model.Task
import domain.model.TimerSession
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    val tasksFlow: Flow<List<Task>>
    suspend fun add(task: Task): Result<Unit>
    suspend fun delete(id: Long): Result<Unit>
    suspend fun update(
        id: Long,
        name: String? = null,
        description: String? = null,
        color: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        timerSessions: List<TimerSession>? = null,
    ): Result<Unit>
}
