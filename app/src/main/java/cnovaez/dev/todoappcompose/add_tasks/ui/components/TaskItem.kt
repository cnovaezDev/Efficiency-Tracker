package cnovaez.dev.todoappcompose.add_tasks.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cnovaez.dev.todoappcompose.add_tasks.ui.TaskViewModel
import cnovaez.dev.todoappcompose.add_tasks.ui.model.TaskModel
import cnovaez.dev.todoappcompose.utils.MODE_DARK
import cnovaez.dev.todoappcompose.utils.getMode
import cnovaez.dev.todoappcompose.utils.isDateEarlyThanToday
import cnovaez.dev.todoappcompose.utils.isDateToday
import cnovaez.dev.todoappcompose.utils.isTimeValid

/**
 ** Created by Carlos A. Novaez Guerrero on 11/2/2023 8:09 PM
 ** cnovaez.dev@outlook.com
 **/

@Composable
fun TaskItem(task: TaskModel, taskViewModel: TaskViewModel) {
    val context = LocalContext.current
    val mode = getMode(context)
    val nightMode by taskViewModel.nightMode.observeAsState(initial = mode == MODE_DARK)
    if (isDateEarlyThanToday(task.date)) Text(text = task.date, color = Color(0xFFE23232), fontSize = 10.sp, modifier = Modifier.padding(start = 16.dp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    taskViewModel.deleteTaskFromMemory(task)
                    taskViewModel.showDeleteSnackBar(true, task)
                },
                    onTap = {
                        taskViewModel.showNewTaskDialog(task)
                    }
                )
            },
        colors = CardColors(
            containerColor = if (!isDateToday(task.date)) (if (!nightMode) Color(0xFFF5A1A1) else Color(
                0xFF692C2C
            )) else if (!nightMode) Color.LightGray else Color.DarkGray,
            contentColor = Color.Black,
            disabledContentColor = Color.Gray,
            disabledContainerColor = Color.Transparent,
        ),
    )

    {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = task.description,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                color = if (nightMode) Color.White else Color.DarkGray,
            )
            if (task.important) {
                Icon(
                    imageVector = Icons.Filled.Flag,
                    contentDescription = "",
                    tint = Color(0xFFE23232)
                )
            }
            if (task.notify) {
                Icon(
                    imageVector = if (isDateToday(task.date) && isTimeValid(task.time, task.date)) Icons.Filled.NotificationsActive else Icons.Filled.NotificationsOff,
                    contentDescription = "",
                    tint = if (nightMode) Color.LightGray else Color.Black,
                )
            }
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { taskViewModel.updateTaskCheckState(task) })
        }
    }
}
