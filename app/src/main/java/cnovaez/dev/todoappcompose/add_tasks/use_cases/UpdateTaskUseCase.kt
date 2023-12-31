package cnovaez.dev.todoappcompose.add_tasks.use_cases

import cnovaez.dev.todoappcompose.add_tasks.data.TaskEntity
import cnovaez.dev.todoappcompose.add_tasks.data.repository.TasksRepository
import javax.inject.Inject

/**
 ** Created by Carlos A. Novaez Guerrero on 10/29/2023 12:17 PM
 ** cnovaez.dev@outlook.com
 **/
class UpdateTaskUseCase @Inject constructor(private val tasksRepository: TasksRepository) {
    suspend operator fun invoke(taskEntity: TaskEntity) = tasksRepository.updateTask(taskEntity)

}