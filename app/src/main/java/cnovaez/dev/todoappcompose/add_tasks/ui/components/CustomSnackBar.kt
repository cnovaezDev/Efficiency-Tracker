package cnovaez.dev.todoappcompose.add_tasks.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cnovaez.dev.todoappcompose.add_tasks.ui.TaskViewModel
import kotlinx.coroutines.delay

/**
 ** Created by Carlos A. Novaez Guerrero on 11/3/2023 1:28 PM
 ** cnovaez.dev@outlook.com
 **/
@Composable
fun CustomSnackBar(
    taskViewModel: TaskViewModel,
    message: String,
    actionLabel: String? = null,
    duration: Long = 3000, // Duración predeterminada en milisegundos
    onActionClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {


    LaunchedEffect(true) {
        delay(duration)
        onDismiss()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFB45F54))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.WarningAmber,
                contentDescription = "Warning",
                modifier = Modifier.size(24.dp),
                tint = Color(0xFFE8B980)
            )
            Text(
                text = message,
                color = Color.White,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically).weight(1f)
            )
            Icon(imageVector = Icons.Filled.Close, contentDescription = "", modifier = Modifier.size(24.dp).clickable { onDismiss() }, tint = Color.White)
        }
        if (!actionLabel.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    taskViewModel.showDeleteSnackBar(false)
                    onActionClick()
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = actionLabel)
            }
        }
    }
}
@Preview
@Composable
fun CustomGraph() {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        // Dibuja un círculo en el centro de la pantalla
        drawCircle(
            color = Color.Blue,
            center = Offset(size.width / 2f, size.height / 2f),
            radius = 100f
        )

        // Dibuja una línea diagonal
        drawLine(
            color = Color.Red,
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height),
            strokeWidth = 5f
        )
    }
}


