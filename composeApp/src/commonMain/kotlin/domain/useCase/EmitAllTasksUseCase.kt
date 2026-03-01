package domain.useCase

import domain.model.Task
import domain.repository.TaskRepository
import domain.util.UseCase
import kotlinx.coroutines.flow.Flow

class EmitAllTasksUseCase(
    private val taskRepository: TaskRepository,
) : UseCase<Unit, Flow<List<Task>>> {
    override suspend fun call(value: Unit): Result<Flow<List<Task>>> =
        runCatching { taskRepository.tasksFlow }
}
