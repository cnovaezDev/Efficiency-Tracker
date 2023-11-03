package cnovaez.dev.todoappcompose.add_tasks.use_cases

import cnovaez.dev.todoappcompose.add_tasks.data.repository.TasksRepository
import javax.inject.Inject

/**
 ** Created by Carlos A. Novaez Guerrero on 10/29/2023 12:17 PM
 ** cnovaez.dev@outlook.com
 **/
class GetTasksUseCase @Inject constructor(private val tasksRepository: TasksRepository) {
    operator fun invoke(date: String) = tasksRepository.getTasks(date)
}