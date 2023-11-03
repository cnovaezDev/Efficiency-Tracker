package cnovaez.dev.todoappcompose.add_tasks.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cnovaez.dev.todoappcompose.add_tasks.ui.Space
import cnovaez.dev.todoappcompose.add_tasks.ui.TaskViewModel
import cnovaez.dev.todoappcompose.add_tasks.ui.model.TaskModel
import cnovaez.dev.todoappcompose.core.DatePickerView
import cnovaez.dev.todoappcompose.core.TimePickerComponent
import cnovaez.dev.todoappcompose.utils.defaultValueTimer
import cnovaez.dev.todoappcompose.utils.validateContent
import cnovaez.dev.todoappcompose.utils.validateNotificationTime

/**
 ** Created by Carlos A. Novaez Guerrero on 11/2/2023 8:04 PM
 ** cnovaez.dev@outlook.com
 **/

@Composable
fun NewTaskDialg(
    viewModel: TaskViewModel,
    onDismissReques: () -> Unit,
    onTaskAdded: (TaskModel) -> Unit
) {
    val scope = rememberCoroutineScope()
    val show by viewModel.addTaskDialog.observeAsState(initial = false)
    val showTimePicker by viewModel.showTimePicker.observeAsState(initial = false)
    var showDateSelection by rememberSaveable {
        mutableStateOf(false)
    }
    var noteDate by rememberSaveable {
        mutableStateOf(viewModel.today)
    }
    var noteTime by rememberSaveable {
        mutableStateOf(defaultValueTimer)
    }
    var notify by rememberSaveable {
        mutableStateOf(false)
    }
    var restarData by rememberSaveable {
        mutableStateOf(false)
    }
    val errorState by viewModel.errorState.observeAsState(initial = false)
    val errorStateTimer by viewModel.errorStateTimer.observeAsState(initial = false)

    var taskContent by rememberSaveable {
        mutableStateOf("")
    }
    var errorMsg by rememberSaveable {
        mutableStateOf("")
    }

    if (restarData) {
        noteDate = viewModel.today
        noteTime = defaultValueTimer
        notify = false
        taskContent = ""
        viewModel.updateErrorState(false)
        viewModel.updateErrorStateTimer(false)
        restarData = false
    }

    if (showTimePicker) {
        TimePickerComponent(taskViewModel = viewModel, onTimeSelected = {
            noteTime = it
            viewModel.updateErrorStateTimer(false)
        }, noteTime)
    }

    if (showDateSelection) {
        DatePickerView(
            selectedDate = noteDate, onDateSelection = {
                noteDate = it
                showDateSelection = false
            }, onDismissRequest = {
                showDateSelection = false
            })
    }


    if (show) {
        //? Task Dialog
        Space(i = 4)
        Dialog(onDismissRequest = {
        }) {
            Column {
                if (notify) {
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "Notify me",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(onClick = { showDateSelection = true }) {
                                Icon(
                                    imageVector = Icons.Filled.EditCalendar,
                                    contentDescription = "Note Date"
                                )
                            }
                            Text(text = noteDate, fontSize = 12.sp)
                            IconButton(onClick = { viewModel.showTimePicker(true) }) {
                                Icon(
                                    imageVector = Icons.Filled.AccessTime,
                                    contentDescription = "Note Time",
                                    tint = if (errorStateTimer) Color.Red else Color.DarkGray
                                )
                            }
                            Text(text = noteTime, fontSize = 12.sp)
                            Spacer(modifier = Modifier.weight(1f))
                        }

                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "New Task", modifier = Modifier
                                .padding(8.dp)
                                .weight(1f),
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(onClick = { notify = !notify }) {
                            Icon(
                                imageVector = if (notify) Icons.Filled.NotificationsActive else Icons.Filled.NotificationsNone,
                                contentDescription = "Notifications"
                            )
                        }


                    }
                    Space(16)
                    TextField(
                        value = taskContent,
                        onValueChange = {
                            taskContent = it
                            if (taskContent.trim().isNotEmpty() && taskContent.trim()
                                    .isNotBlank()
                            ) {
                                viewModel.updateErrorState(false)
                            }
                        },
                        placeholder = { Text(text = "Task Content", color = Color.LightGray) },
                        modifier = Modifier.padding(8.dp),
                        trailingIcon = {
                            IconButton(onClick = { taskContent = "" }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "")
                            }
                        },
                        isError = errorState,
                    )
                    if (errorState || errorStateTimer) {
                        Text(
                            text = errorMsg,
                            fontSize = 10.sp,
                            color = Color.Red,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Space(i = 16)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp, top = 24.dp)
                    ) {

                        Button(
                            onClick = {
                                onDismissReques()
                                restarData = true
                            },
                            modifier = Modifier
                                .padding(start = 8.dp),
                            colors = ButtonColors(
                                containerColor = Color(0xFFF15D5D),
                                contentColor = if (viewModel.nightMode.value == true) Color.DarkGray else Color.White,
                                disabledContentColor = Color.White,
                                disabledContainerColor = Color(0xFFF15D5D)
                            )
                        )
                        {
                            Text(text = "Cancel")
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                if (validateContent(taskContent)) {
                                    if ((notify && validateNotificationTime(noteTime)) || !notify) {
                                        onTaskAdded(
                                            TaskModel(
                                                description = taskContent,
                                                date = noteDate,
                                                time = noteTime,
                                                isCompleted = false,
                                                notify = notify
                                            )
                                        )
                                        restarData = true
                                    } else {
                                        viewModel.updateErrorStateTimer(true)
                                        errorMsg = "Task time can't be empty"
                                    }
                                } else {
                                    viewModel.updateErrorState(true)
                                    errorMsg = "Task content can't be empty"
                                }
                            },
                            modifier = Modifier
                                .padding(end = 8.dp),
                        ) {
                            Text(text = "Add Task")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}