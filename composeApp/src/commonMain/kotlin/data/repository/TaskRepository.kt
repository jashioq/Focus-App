package data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.jan.focus.database.AppDatabase
import domain.model.Task
import domain.model.TimerBlock
import domain.model.TimerSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TaskRepository(
    private val db: AppDatabase,
) : domain.repository.TaskRepository {

    override val tasksFlow: Flow<List<Task>> = combine(
        db.taskQueries.selectAll().asFlow().mapToList(Dispatchers.Default),
        db.timerSessionQueries.selectAll().asFlow().mapToList(Dispatchers.Default),
    ) { taskRows, sessionRows ->
        val sessionsByTaskId = sessionRows.groupBy { it.taskId }
        taskRows.map { task -> task.toDomain(sessionsByTaskId[task.id] ?: emptyList()) }
    }

    override suspend fun add(task: Task): Result<Unit> = runCatching {
        db.transaction {
            db.taskQueries.insert(
                name = task.name,
                description = task.description,
                color = task.color,
                startDate = task.startDate,
                endDate = task.endDate,
            )
            if (task.timerSessions.isNotEmpty()) {
                val taskId = db.taskQueries.lastInsertedId().executeAsOne()
                task.timerSessions.forEach { session ->
                    db.timerSessionQueries.insert(
                        taskId = taskId,
                        startDate = session.startDate,
                        sequence = Json.encodeToString(session.sequence),
                    )
                }
            }
        }
    }

    override suspend fun delete(id: Long): Result<Unit> = runCatching {
        db.taskQueries.deleteById(id)
    }

    override suspend fun update(
        id: Long,
        name: String?,
        description: String?,
        color: String?,
        startDate: String?,
        endDate: String?,
        timerSessions: List<TimerSession>?,
    ): Result<Unit> = runCatching {
        db.transaction {
            // Positional order matches generated params: value, value_, value__, value___, value____, id
            db.taskQueries.update(name, description, color, startDate, endDate, id)
            if (timerSessions != null) {
                db.timerSessionQueries.deleteByTaskId(id)
                timerSessions.forEach { session ->
                    db.timerSessionQueries.insert(
                        taskId = id,
                        startDate = session.startDate,
                        sequence = Json.encodeToString(session.sequence),
                    )
                }
            }
        }
    }

    private fun com.jan.focus.database.Task.toDomain(
        sessions: List<com.jan.focus.database.TimerSession>,
    ): Task = Task(
        id = id,
        name = name,
        description = description,
        color = color,
        startDate = startDate,
        endDate = endDate,
        timerSessions = sessions.map { s ->
            TimerSession(
                id = s.id,
                taskId = s.taskId,
                startDate = s.startDate,
                sequence = Json.decodeFromString<List<TimerBlock>>(s.sequence),
            )
        },
    )
}
