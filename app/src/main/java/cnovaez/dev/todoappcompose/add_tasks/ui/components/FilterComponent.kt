package cnovaez.dev.todoappcompose.add_tasks.ui.components

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import cnovaez.dev.todoappcompose.R
import cnovaez.dev.todoappcompose.add_tasks.ui.MainActivity
import cnovaez.dev.todoappcompose.add_tasks.ui.TaskViewModel
import cnovaez.dev.todoappcompose.utils.getLanguage
import cnovaez.dev.todoappcompose.utils.setLanguage
import cnovaez.dev.todoappcompose.utils.setLocale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 ** Created by Carlos A. Novaez Guerrero on 11/2/2023 8:06 PM
 ** cnovaez.dev@outlook.com
 **/

@Composable
fun FilterComponent(taskViewModel: TaskViewModel, activity: MainActivity) {
    val searchQuery by taskViewModel.searchQuery.observeAsState(initial = "")
    val showFilter by taskViewModel.showFilter.observeAsState(initial = false)
    val defaultLanguage = Locale.getDefault().language
    val context = LocalContext.current

    val colorTheme by taskViewModel.nightMode.observeAsState(initial = false)
    if (!showFilter) {
        Row {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                modifier = Modifier
                    .padding(start = 8.dp, top = 48.dp)
                    .clickable { taskViewModel.showFilter(true) }
            )
            Icon(
                imageVector = Icons.Filled.BarChart,
                contentDescription = stringResource(R.string.statistics),
                modifier = Modifier
                    .padding(start = 8.dp, top = 48.dp)
                    .clickable {
                        taskViewModel.loadAllTasks()
                        taskViewModel.updateShowChart(true)
                    }
            )
            Spacer(modifier = Modifier.weight(1f))
            LanguageSwitcher(context, onLanguageSelected = {
                setLanguage(context, it)
                setLocale(activity = activity, it)
                taskViewModel.viewModelScope.launch {
                    delay(500)
                    withContext(Dispatchers.Main) {
                        activity.recreate()
                    }
                }
            })
        }

    } else {
        Row(Modifier.fillMaxWidth()) {
            Badge(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, top = 40.dp),
                containerColor = Color.LightGray

            ) {

                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable { taskViewModel.showFilter(false) },
                    tint = if (colorTheme) Color.DarkGray else Color.Black

                )

                //TextField sin el borde exterior
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { taskViewModel.updateSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search),
                            fontSize = 10.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.align(Alignment.Top)
                        )
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                        .padding(start = 8.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = if (colorTheme) Color.White else Color.Black

                    ),
                    textStyle = TextStyle(fontSize = 10.sp)

                )
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable {
                            if (searchQuery.isNotEmpty()) {
                                taskViewModel.updateSearchQuery("")
                            } else {
                                taskViewModel.showFilter(false)
                            }
                        },
                    tint = if (colorTheme) Color.DarkGray else Color.Black
                )

            }
        }

    }
}

@Composable
fun LanguageSwitcher(
    context: Context,
    onLanguageSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage = getLanguage(context) // Idioma predeterminado


    Column(modifier = Modifier.padding(top = 36.dp, end = 2.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = selectedLanguage, fontSize = 14.sp)
            IconButton(
                onClick = { expanded = true }
            ) {
                Icon(Icons.Default.Language, contentDescription = "Cambiar idioma")
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Español") },
                onClick = {
                    selectedLanguage = "es"
                    onLanguageSelected(selectedLanguage)
                    expanded = false
                },
                enabled = selectedLanguage != "es"
            )

            DropdownMenuItem(
                text = { Text("English") },
                onClick = {
                    selectedLanguage = "en"
                    onLanguageSelected(selectedLanguage)
                    expanded = false
                },
                enabled = selectedLanguage != "en"
            )
        }
    }
}