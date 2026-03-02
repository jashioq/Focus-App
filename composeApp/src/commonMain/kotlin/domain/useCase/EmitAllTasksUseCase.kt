package domain.useCase

import domain.model.Task
import domain.repository.TaskRepository
import domain.util.StreamUseCase
import kotlinx.coroutines.flow.Flow

class EmitAllTasksUseCase(
    private val taskRepository: TaskRepository,
) : StreamUseCase<Unit, List<Task>> {
    override fun stream(value: Unit): Flow<List<Task>> = taskRepository.tasksFlow
}
