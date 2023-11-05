package cnovaez.dev.todoappcompose.add_tasks.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cnovaez.dev.todoappcompose.R
import cnovaez.dev.todoappcompose.add_tasks.ui.TaskViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.compose.legend.horizontalLegend
import com.patrykandpatrick.vico.compose.legend.legendItem
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf

/**
 ** Created by Carlos A. Novaez Guerrero on 11/4/2023 8:26 PM
 ** cnovaez.dev@outlook.com
 **/

@Composable
fun CustomChart(modifier: Modifier, taskViewModel: TaskViewModel, nigthMode: Boolean) {

    val showChartLoading by taskViewModel.showChartLoading.observeAsState(initial = false)

    val tasks = taskViewModel.alltaskList

    val tasksDates = tasks.distinctBy { it.date }.map { it.date }.sortedByDescending { it }

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

    if (showChartLoading) {
        Dialog(onDismissRequest = { taskViewModel.updateShowChart(false) } ) {
            Card(
                modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (nigthMode) Color.DarkGray else Color.LightGray,
                )
            ) {
                Column {
                    Row(Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.productivity_statistics),
                            modifier = Modifier.padding(8.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(imageVector = Icons.Filled.Close,
                            contentDescription = "Close Chart",
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    taskViewModel.updateShowChart(false)
                                })
                    }
                    Chart(
                        chartScrollState = rememberChartScrollState(),
                        chart = columnChart(

                            columns = listOf(
                                lineComponent(
                                    color = Color(0xFFF58484),
                                    thickness = 8.dp,
                                    shape = RoundedCornerShape(4.dp),
                                ),
                                lineComponent(
                                    color = Color(0xFF70BD91),
                                    thickness = 8.dp,
                                    shape = RoundedCornerShape(4.dp),
                                ),
                            ),

                            ),
                        model = chartEntryModel,
                        startAxis = rememberStartAxis(label = textComponent(color = if (nigthMode) Color.White else Color.Black)),
                        bottomAxis = if (tasksDates.isNotEmpty()) rememberBottomAxis(
                            valueFormatter = bottomAxisValueFormatter(
                                tasksDates
                            ),
                            label = textComponent(color = if (nigthMode) Color.White else Color.Black),
                        ) else rememberBottomAxis(label = textComponent(color = if (nigthMode) Color.White else Color.Black)),
                        legend = horizontalLegend(
                            items = listOf(
                                legendItem(
                                    icon = shapeComponent(
                                        color = Color(0xFFF58484),
                                        margins = dimensionsOf(4.dp)
                                    ),
                                    label = textComponent(color = if (nigthMode) Color.White else Color.Black),
                                    labelText = stringResource(R.string.incomplete),

                                    ),
                                legendItem(
                                    icon = shapeComponent(
                                        color = Color(0xFF70BD91),
                                        margins = dimensionsOf(4.dp)
                                    ),
                                    label = textComponent(color = if (nigthMode) Color.White else Color.Black),
                                    labelText = stringResource(R.string.completed)
                                )
                            ), iconSize = 20.dp,
                            iconPadding = 8.dp
                        ),
                        modifier = Modifier.padding(16.dp)

                    )
                }


            }
        }
    } else {
        CircularProgressIndicator(modifier = modifier.size(60.dp))
    }
}

//@Composable
//private fun rememberLegend() = verticalLegend(
//    items = chartColors.mapIndexed { index, chartColor ->
//        legendItem(
//            icon = shapeComponent(Shapes.pillShape, chartColor),
//            label = textComponent(
//                color = currentChartStyle.axis.axisLabelColor,
//                textSize = legendItemLabelTextSize,
//                typeface = Typeface.MONOSPACE,
//            ),
//            labelText = stringResource(R.string.data_set_x, index + 1),
//        )
//    },
//    iconSize = legendItemIconSize,
//    iconPadding = legendItemIconPaddingValue,
//    spacing = legendItemSpacing,
//    padding = legendPadding,
//)

@Composable
fun IconLegend() {
    Row {
        Icon(
            imageVector = Icons.Filled.StackedBarChart,
            contentDescription = "Statistics",
            modifier = Modifier
                .padding(start = 8.dp, top = 48.dp)
        )
        Text(text = "Completed Tasks", modifier = Modifier.padding(start = 8.dp))
    }
}

private val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private fun bottomAxisValueFormatter(tasksDates: List<String>) =
    AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _ -> tasksDates[x.toInt() % if (tasksDates.isNotEmpty()) tasksDates.size else 1] }