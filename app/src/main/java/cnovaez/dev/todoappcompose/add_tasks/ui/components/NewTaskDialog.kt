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
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.OutlinedFlag
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cnovaez.dev.todoappcompose.R
import cnovaez.dev.todoappcompose.add_tasks.ui.TaskViewModel
import cnovaez.dev.todoappcompose.add_tasks.ui.model.TaskModel
import cnovaez.dev.todoappcompose.core.DatePickerView
import cnovaez.dev.todoappcompose.core.TimePickerComponent
import cnovaez.dev.todoappcompose.utils.defaultValueTimer
import cnovaez.dev.todoappcompose.utils.isDateEarlyThanToday
import cnovaez.dev.todoappcompose.utils.isTimeValid
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
    onTaskAdded: (Pair<TaskModel, Boolean>) -> Unit,
    nightMode: Boolean,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val show by viewModel.addTaskDialog.observeAsState(initial = Pair(false, null))

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
    val errorStateDate by viewModel.errorStateDate.observeAsState(initial = false)

    var taskContent by rememberSaveable {
        mutableStateOf("")
    }
    var errorMsg by rememberSaveable {
        mutableStateOf("")
    }
    var valueAsigned by rememberSaveable {
        mutableStateOf(false)
    }
    var repeatNotification by rememberSaveable {
        mutableStateOf(false)
    }
    var important by rememberSaveable {
        mutableStateOf(false)
    }

    var followUp by rememberSaveable {
        mutableStateOf(true)
    }
    var editNote by rememberSaveable {
        mutableStateOf(false)
    }

    if (restarData) {
        noteDate = viewModel.today
        noteTime = defaultValueTimer
        notify = false
        taskContent = ""
        viewModel.updateErrorState(false)
        viewModel.updateErrorStateTimer(false)
        viewModel.updateErrorStateDate(false)
        valueAsigned = false
        errorMsg = ""
        repeatNotification = false
        important = false
        followUp = true
        editNote = false
        restarData = false
    }

    if (showTimePicker) {
        TimePickerComponent(taskViewModel = viewModel, onTimeSelected = {
            noteTime = it
            val valid = !isTimeValid(noteTime, noteDate, context)
            viewModel.updateErrorStateTimer(valid)
            if (valid) {
                errorMsg =
                    context.getString(R.string.task_time_for_notification_can_t_be_early_than_now)
            }
        }, noteTime)
    }

    if (showDateSelection) {
        DatePickerView(
            selectedDate = noteDate, onDateSelection = {
                noteDate = it
                val invalid = isDateEarlyThanToday(noteDate, context)
                viewModel.updateErrorStateDate(invalid)
                if (invalid) {
                    errorMsg =
                        context.getString(R.string.task_date_of_notification_can_t_be_early_than_today)
                }
                showDateSelection = false
            }, onDismissRequest = {
                showDateSelection = false
            })
    }

    var id = System.currentTimeMillis()
    if (show.first) {
        if (!showDateSelection && !showTimePicker && show.second != null) {
            if (!valueAsigned) {
                noteDate = show.second!!.date
                noteTime = show.second!!.time
                notify = show.second!!.notify
                taskContent = show.second!!.description
                valueAsigned = true
                viewModel.updateErrorStateDate(isDateEarlyThanToday(noteDate, context))
                if (noteTime != defaultValueTimer)
                    viewModel.updateErrorStateTimer(!isTimeValid(noteTime, noteDate, context))
                repeatNotification = show.second!!.repeat
                important = show.second!!.important
                editNote = true
            }
            id = show.second!!.id
        }

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
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.notify_me),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(text = stringResource(R.string.repeat_daily), fontSize = 12.sp)
                            Checkbox(
                                checked = repeatNotification,
                                onCheckedChange = { repeatNotification = !repeatNotification })
                        }
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(onClick = {
                                showDateSelection = true
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.EditCalendar,
                                    contentDescription = "Note Date",
                                    tint = if (errorStateDate) Color(0xFFF58484) else if (!nightMode) Color.DarkGray else Color.LightGray
                                )
                            }
                            Text(text = noteDate, fontSize = 12.sp)
                            IconButton(onClick = {
                                viewModel.showTimePicker(true)
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.AccessTime,
                                    contentDescription = "Note Time",
                                    tint = if (errorStateTimer) Color(0xFFF58484) else if (!nightMode) Color.DarkGray else Color.LightGray
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
                            text = stringResource(R.string.new_task), modifier = Modifier
                                .padding(8.dp)
                                .weight(1f),
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(onClick = { followUp = !followUp }) {
                            Icon(
                                imageVector = Icons.Outlined.TrackChanges,
                                contentDescription = "Follow up task",
                                tint = if (followUp) Color(0xFF64A73B) else if (nightMode) Color.LightGray else Color.DarkGray
                            )
                        }
                        IconButton(onClick = { important = !important }) {
                            Icon(
                                imageVector = if (important) Icons.Filled.Flag else Icons.Filled.OutlinedFlag,
                                contentDescription = "Task Priority",
                                tint = if (important) Color(0xFFE23232) else if (nightMode) Color.LightGray else Color.DarkGray
                            )
                        }
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
                        placeholder = {
                            Text(
                                text = stringResource(R.string.task_content),
                                color = Color.LightGray
                            )
                        },
                        modifier = Modifier.padding(8.dp),
                        trailingIcon = {
                            IconButton(onClick = { taskContent = "" }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "")
                            }
                        },
                        isError = errorState,
                    )
                    if (errorState || errorStateTimer || errorStateDate) {
                        Text(
                            text = errorMsg,
                            fontSize = 10.sp,
                            color = Color(0xFFF58484),
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
                            Text(text = stringResource(R.string.cancel))
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                if (validateContent(taskContent)) {
                                    if ((notify && validateNotificationTime(noteTime)) || !notify) {
                                        if (!notify || isTimeValid(
                                                noteTime,
                                                noteDate,
                                                context
                                            ) && !isDateEarlyThanToday(
                                                noteDate,
                                                context
                                            )
                                        ) {
                                            onTaskAdded(
                                                Pair(
                                                    TaskModel(
                                                        id = id,
                                                        description = taskContent,
                                                        date = noteDate,
                                                        time = noteTime,
                                                        isCompleted = false,
                                                        notify = notify,
                                                        repeat = repeatNotification,
                                                        important = important,
                                                        followUp = followUp
                                                    ), show.second != null
                                                )
                                            )
                                            restarData = true

                                        } else {
                                            errorMsg =
                                                context.getString(R.string.task_time_and_date_can_t_be_early_than_today_and_now)
                                        }

                                    } else {
                                        viewModel.updateErrorStateTimer(true)
                                        errorMsg =
                                            context.getString(R.string.task_time_can_t_be_empty)
                                    }
                                } else {
                                    viewModel.updateErrorState(true)
                                    errorMsg =
                                        context.getString(R.string.task_content_can_t_be_empty)
                                }
                            },
                            modifier = Modifier
                                .padding(end = 8.dp),
                        ) {
                            Text(
                                text = if (!editNote) stringResource(R.string.add_task) else stringResource(
                                    R.string.edit_task
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}