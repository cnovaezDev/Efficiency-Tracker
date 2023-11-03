package cnovaez.dev.todoappcompose.add_tasks.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cnovaez.dev.todoappcompose.add_tasks.ui.Space
import cnovaez.dev.todoappcompose.add_tasks.ui.TaskViewModel
import cnovaez.dev.todoappcompose.add_tasks.ui.model.TaskModel
import kotlin.math.roundToInt

/**
 ** Created by Carlos A. Novaez Guerrero on 11/2/2023 8:05 PM
 ** cnovaez.dev@outlook.com
 **/
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskList(tasks: List<TaskModel>, taskViewModel: TaskViewModel) {
    val filter by taskViewModel.showFilter.observeAsState(initial = false)
    val all_tasks = tasks.sortedBy { it.isCompleted }.groupBy { it.isCompleted }
    LazyColumn(Modifier.padding(top = if (filter) 88.dp else 88.dp)) {
        all_tasks.forEach { (isCompleted, tasks) ->

            stickyHeader {
                Badge(
                    modifier = Modifier.padding(8.dp), containerColor = (if (isCompleted) Color(
                        0xFF70BD91
                    ) else Color(0xFFF58484))
                ) {
                    Space(i = 8)
                    Text(
                        text = (if (isCompleted) "Tasks Completed Today: " else "Tasks Pending for Today: ") + "${
                            ((tasks.size * 100) / taskViewModel.taskList.size).toDouble()
                                .roundToInt()

                        }%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp),
                        color = if (taskViewModel.nightMode.value == true) Color.DarkGray else Color.Black
                    )
                    Space(i = 8)
                }
            }

            items(tasks, key = { it.id }) { task ->
                TaskItem(task = task, taskViewModel)
            }
        }
    }
}