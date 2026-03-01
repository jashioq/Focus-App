package data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.jan.focus.database.AppDatabase
import domain.model.Task
import domain.model.TimerBlock
import domain.model.TimerSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TaskRepository(
    private val db: AppDatabase,
) : domain.repository.TaskRepository {

    override val tasksFlow: Flow<List<Task>> =
        db.taskQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows -> rows.map { it.toDomainWithSessions() } }

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

    override suspend fun delete(task: Task): Result<Unit> = runCatching {
        val id = requireNotNull(task.id) { "Cannot delete a task without an id" }
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

    private fun com.jan.focus.database.Task.toDomainWithSessions(): Task {
        val sessions = db.timerSessionQueries.selectByTaskId(id).executeAsList().map { s ->
            TimerSession(
                id = s.id,
                taskId = s.taskId,
                startDate = s.startDate,
                sequence = Json.decodeFromString<List<TimerBlock>>(s.sequence),
            )
        }
        return Task(
            id = id,
            name = name,
            description = description,
            color = color,
            startDate = startDate,
            endDate = endDate,
            timerSessions = sessions,
        )
    }
}
