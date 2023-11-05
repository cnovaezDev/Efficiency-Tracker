package cnovaez.dev.todoappcompose.add_tasks.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cnovaez.dev.todoappcompose.add_tasks.ui.TaskViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf

/**
 ** Created by Carlos A. Novaez Guerrero on 11/4/2023 8:26 PM
 ** cnovaez.dev@outlook.com
 **/

@Composable
fun CustomChart(modifier: Modifier, taskViewModel: TaskViewModel) {

    val tasks = taskViewModel.alltaskList

    val tasksDates = tasks.distinctBy { it.date }.map { it.date }

    val tasksByDate = tasks.groupBy { it.date }

    val incompleteTasks = mutableListOf<Pair<Int, Float>>()
    val completedTasks = mutableListOf<Pair<Int, Float>>()
    var idx = 0
    tasksDates.forEach { date ->
        val curr_tasks = tasksByDate[date]
        var completed = 0
        var incompleted = 0
        var doPercent = false
        curr_tasks?.forEach {
            doPercent = true
            if (it.isCompleted) {
                completed++
            } else {
                incompleted++
            }
        }
        if (doPercent) {
            completedTasks.add(Pair(idx, ((completed * 100) / (curr_tasks!!.size)).toFloat()))
            incompleteTasks.add(Pair(idx, ((incompleted * 100) / (curr_tasks!!.size)).toFloat()))
            idx++
        }

    }


    val chartEntryModel = entryModelOf(
        incompleteTasks.map {
            entryOf(it.first, it.second)
        },
        completedTasks.map {
            entryOf(it.first, it.second)
        }

    )
    val showChart by taskViewModel.showChart.observeAsState(initial = false)

    if (showChart) {
        Card(modifier) {
            Chart(
                chartScrollState = rememberChartScrollState(),
                chart = columnChart(

                    columns = listOf(
                        lineComponent(
                            color = Color.Red,
                            thickness = 8.dp,
                            shape = RoundedCornerShape(4.dp),
                        ),
                        lineComponent(
                            color = Color.Green,
                            thickness = 8.dp,
                            shape = RoundedCornerShape(4.dp),
                        ),
                    ),

                    ),
                model = chartEntryModel,
                startAxis = rememberStartAxis(),
                bottomAxis = if (tasksDates.isNotEmpty()) rememberBottomAxis(
                    valueFormatter = bottomAxisValueFormatter(
                        tasksDates
                    )
                ) else rememberBottomAxis()
            )


        }
    } else {
        CircularProgressIndicator(modifier = modifier.size(60.dp))
    }
}

private val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private fun bottomAxisValueFormatter(tasksDates: List<String>) =
    AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _ -> tasksDates[x.toInt() % if (tasksDates.isNotEmpty()) tasksDates.size else 1] }