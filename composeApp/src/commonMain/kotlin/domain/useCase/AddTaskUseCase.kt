package domain.useCase

import domain.model.Task
import domain.repository.TaskRepository
import domain.util.UseCase

class AddTaskUseCase(
    private val taskRepository: TaskRepository,
) : UseCase<Task, Unit> {
    override suspend fun call(value: Task): Result<Unit> = taskRepository.add(value)
}
