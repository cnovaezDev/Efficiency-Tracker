package cnovaez.dev.todoappcompose

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cnovaez.dev.todoappcompose.add_tasks.ui.TaskViewModel
import cnovaez.dev.todoappcompose.add_tasks.ui.TasksScreen
import cnovaez.dev.todoappcompose.ui.theme.TodoAppComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val taskViewModel: TaskViewModel by viewModels()

    private val REQUEST_NOTIFICATION_PERMISSION = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // En tu actividad o fragmento de JetPack Compose
        if (checkNotificationPermission(this)) {
            // Puedes mostrar notificaciones
        } else {
            // Debes solicitar permiso de notificación
            requestNotificationPermission(this)
        }
        checkAndRequestPermissions()

        setContent {
            TasksScreen(taskViewModel = taskViewModel)
        }
    }

    fun checkNotificationPermission(context: Context): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.areNotificationsEnabled()
    }

    fun requestNotificationPermission(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.areNotificationsEnabled()) {
            // Lleva al usuario a la configuración de notificaciones para habilitarlas.
        }
    }


    // Maneja la respuesta de los permisos
    private fun checkAndRequestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableListOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
            )
        } else mutableListOf(
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
        )

        val permissionsToRequest = mutableListOf<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_NOTIFICATION_PERMISSION
            )
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    //Permiso concedido
                } else {
                    //permiso denegado
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TodoAppComposeTheme {
        Greeting("Android")
    }
}