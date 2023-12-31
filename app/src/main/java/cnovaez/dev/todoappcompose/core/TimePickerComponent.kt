package cnovaez.dev.todoappcompose.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cnovaez.dev.todoappcompose.R
import cnovaez.dev.todoappcompose.add_tasks.ui.TaskViewModel
import cnovaez.dev.todoappcompose.utils.curr_context
import cnovaez.dev.todoappcompose.utils.defaultValueTimer
import cnovaez.dev.todoappcompose.utils.getLocaleByLanguage
import cnovaez.dev.todoappcompose.utils.identificarLocaleDesdeFormatoHora
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 ** Created by Carlos A. Novaez Guerrero on 10/29/2023 8:36 PM
 ** cnovaez.dev@outlook.com
 **/
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TimePickerComponent(
    taskViewModel: TaskViewModel,
    onTimeSelected: (String) -> Unit,
    noteTime: String
) {
    val showTimePicker by taskViewModel.showTimePicker.observeAsState(false)
    var initialHour = -1
    var initialMinutes = -1
    //Add the noteTime to the timePickerState
    if (noteTime.isNotEmpty() && !noteTime.equals(defaultValueTimer)) {
        val cal = Calendar.getInstance()
        val formatter = SimpleDateFormat("hh:mm a", identificarLocaleDesdeFormatoHora(noteTime) ?: getLocaleByLanguage(curr_context!!))
        cal.time = formatter.parse(noteTime)!!
        initialHour = cal.get(Calendar.HOUR_OF_DAY)
        initialMinutes = cal.get(Calendar.MINUTE)
    }
    val state = if (initialHour != -1 && initialMinutes != -1) {
        rememberTimePickerState(initialHour = initialHour, initialMinute = initialMinutes)
    } else {
        rememberTimePickerState()
    }

    val formatter = remember { SimpleDateFormat("hh:mm a") }
    val showingPicker = remember { mutableStateOf(true) }
    val configuration = LocalConfiguration.current

    if (showTimePicker) {
        TimePickerDialog(
            title = if (showingPicker.value) {
                stringResource(R.string.select_time)
            } else {
                stringResource(R.string.enter_time)
            },
            onCancel = { taskViewModel.showTimePicker(false) },
            onConfirm = {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, state.hour)
                cal.set(Calendar.MINUTE, state.minute)
                cal.isLenient = false
                onTimeSelected(formatter.format(cal.time))
                taskViewModel.showTimePicker(false)
            },
            toggle = {
                if (configuration.screenHeightDp > 400) {
                    IconButton(onClick = { showingPicker.value = !showingPicker.value }) {
                        val icon = if (showingPicker.value) {
                            Icons.Outlined.Keyboard
                        } else {
                            Icons.Outlined.Schedule
                        }
                        Icon(
                            icon,
                            contentDescription = if (showingPicker.value) {
                                stringResource(R.string.switch_to_text_input)
                            } else {
                                stringResource(R.string.switch_to_touch_input)
                            }
                        )
                    }
                }
            }
        ) {
            if (showingPicker.value && configuration.screenHeightDp > 400) {
                TimePicker(state = state)
            } else {
                TimeInput(state = state)
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel
                    ) { Text("Cancel") }
                    TextButton(
                        onClick = onConfirm
                    ) { Text("OK") }
                }
            }
        }
    }
}