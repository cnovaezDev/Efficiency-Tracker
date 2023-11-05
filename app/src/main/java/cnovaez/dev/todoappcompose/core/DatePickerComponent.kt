package cnovaez.dev.todoappcompose.core


import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cnovaez.dev.todoappcompose.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

/**
 ** Created by Carlos A. Novaez Guerrero on 10/29/2023 3:35 PM
 ** cnovaez.dev@outlook.com
 **/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerView(
    selectedDate: String,
    onDateSelection: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = parseDate(selectedDate).time)
    DatePickerDialog(

        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = {
                onDateSelection(
                    convertMillisToDate(
                        datePickerState.selectedDateMillis ?: "0".toLong()
                    )
                )
            }) {
                Text("Ok")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}


private fun parseDate(dateString: String): Date {
    val formatter = SimpleDateFormat("dd-MM-yyyy")
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.parse(dateString)
}

private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd-MM-yyyy")
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date(millis))
}