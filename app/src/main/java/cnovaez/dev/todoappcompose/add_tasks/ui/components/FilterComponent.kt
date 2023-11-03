package cnovaez.dev.todoappcompose.add_tasks.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cnovaez.dev.todoappcompose.add_tasks.ui.TaskViewModel

/**
 ** Created by Carlos A. Novaez Guerrero on 11/2/2023 8:06 PM
 ** cnovaez.dev@outlook.com
 **/

@Composable
fun FilterComponent(taskViewModel: TaskViewModel) {
    val searchQuery by taskViewModel.searchQuery.observeAsState(initial = "")
    val showFilter by taskViewModel.showFilter.observeAsState(initial = false)

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
                imageVector = Icons.Filled.AutoGraph,
                contentDescription = "Statistics",
                modifier = Modifier
                    .padding(start = 8.dp, top = 48.dp)
                    .clickable { }
            )
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
                            text = "Search",
                            fontSize = 10.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.align(Alignment.CenterVertically)
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