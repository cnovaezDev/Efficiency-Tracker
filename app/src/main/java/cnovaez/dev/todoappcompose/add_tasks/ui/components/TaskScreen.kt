package cnovaez.dev.todoappcompose.add_tasks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cnovaez.dev.todoappcompose.R
import cnovaez.dev.todoappcompose.add_tasks.ui.MainActivity
import cnovaez.dev.todoappcompose.add_tasks.ui.TaskViewModel
import cnovaez.dev.todoappcompose.core.AddBannerComponent
import cnovaez.dev.todoappcompose.core.DatePickerView
import cnovaez.dev.todoappcompose.utils.MODE_DARK
import cnovaez.dev.todoappcompose.utils.MODE_LIGHT
import cnovaez.dev.todoappcompose.utils.getMode
import cnovaez.dev.todoappcompose.utils.logs.LogInfo
import cnovaez.dev.todoappcompose.utils.setMode

/**
 ** Created by Carlos A. Novaez Guerrero on 10/28/2023 5:07 PM
 ** cnovaez.dev@outlook.com
 **/

@Composable
fun TasksScreen(taskViewModel: TaskViewModel, navigationController: NavHostController, activity: MainActivity) {
    val context = LocalContext.current
    val mode = getMode(context)
    val nightMode by taskViewModel.nightMode.observeAsState(initial = mode == MODE_DARK)
    val showDatePicker by taskViewModel.showDatePicker.observeAsState(initial = false)
    val displayedDate by taskViewModel.displayedDate.observeAsState(initial = taskViewModel.today)
    val snackBarHostState = taskViewModel.snackBarHostState
    val snackBarVisible by taskViewModel.showDeleteSnackBar.observeAsState(
        initial = Pair(
            false,
            null
        )
    )
    val showChart by taskViewModel.showChart.observeAsState(initial = false)

    MaterialTheme(
        colorScheme = if (nightMode) darkColorScheme() else lightColorScheme(),
    ) {
        Surface(modifier = Modifier.fillMaxSize())
        {

            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackBarHostState,
                        modifier = Modifier.statusBarsPadding()
                    )
                },
                bottomBar = {
                    AddBannerComponent(
                        adId = "ca-app-pub-1269790857555936/5567114920"
                    )
                },
                modifier = Modifier.fillMaxSize()


            )
            { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    //? Toggle Night Mode
                    ToggleNightModeButton(
                        nightMode,
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) { taskViewModel.toggleNightMode(it) }

                    //? New Task Dialog
                    NewTaskDialg(
                        viewModel = taskViewModel,
                        onDismissReques = { taskViewModel.hideNewTaskDialog() },
                        onTaskAdded = { pair->
                            taskViewModel.createNewTask(
                                pair.first,
                                context,
                                pair.second
                            )
                        },
                        nightMode

                    )
                    //? Tasks list
                    TaskList(taskViewModel)

                    //? New Task Foab
                    AddTaskFoab(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        taskViewModel = taskViewModel
                    )

                    //? Date Picker
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clickable { taskViewModel.showDatePicker(true) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EditCalendar,
                            contentDescription = "",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = displayedDate,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }


                    if (showDatePicker) {
                        DatePickerView(
                            selectedDate = displayedDate, onDateSelection = {
                                taskViewModel.displayedDate(it)
                                taskViewModel.showDatePicker(false)
                                taskViewModel.reloadTasksList()
                            }, onDismissRequest = {
                                taskViewModel.showDatePicker(false)
                            })
                    }


                    FilterComponent(taskViewModel, activity)

                    if (snackBarVisible.first) {
                        Row(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 64.dp, start = 8.dp, end = 8.dp)) {
                            CustomSnackBar(
                                taskViewModel = taskViewModel,
                                message = stringResource(
                                    R.string.note_deleted,
                                    snackBarVisible.second?.description.orEmpty()
                                ),
                                actionLabel = stringResource(R.string.undo),
                                duration = 5000,
                                onActionClick = {
                                    LogInfo("Undo Clicked")
                                    taskViewModel.reloadTasksList()
                                },
                                onDismiss = {
                                    LogInfo("SnackBar Dismissed")
//                                    snackBarVisible.second?.let { task ->
//                                        taskViewModel.deleteTask(task, context)
//                                    }
                                    taskViewModel.showDeleteSnackBar(false)
                                },
                            )

                        }
                    }

                    if (showChart){
                        CustomChart(
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center), taskViewModel, nightMode)
                    }

                }
            }
        }
    }

}


@Composable
fun ToggleNightModeButton(nightMode: Boolean, modifier: Modifier, toggleMode: (Boolean) -> Unit) {
    val context = LocalContext.current
    Icon(imageVector = if (nightMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
        contentDescription = "",
        modifier = modifier.clickable {
            setMode(
                context,
                if (nightMode) MODE_LIGHT else MODE_DARK
            )
            toggleMode(!nightMode)
        })
}

@Composable
fun AddTaskFoab(modifier: Modifier, taskViewModel: TaskViewModel) {
    FloatingActionButton(
        onClick = { taskViewModel.showNewTaskDialog() },
        modifier = modifier.padding(horizontal = 16.dp, vertical = 64.dp),
        elevation = FloatingActionButtonDefaults.elevation(8.dp),

    ) {
        Icon(imageVector = Icons.AutoMirrored.Filled.NoteAdd, contentDescription = "")
    }
}


@Composable
fun Space(i: Int) {
    Spacer(modifier = Modifier.size(i.dp))
}

