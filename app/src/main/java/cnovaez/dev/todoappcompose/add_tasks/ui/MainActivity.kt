package cnovaez.dev.todoappcompose.add_tasks.ui

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cnovaez.dev.todoappcompose.add_tasks.ui.components.TasksScreen
import cnovaez.dev.todoappcompose.core.Routes
import cnovaez.dev.todoappcompose.login.ui.LoginViewModel
import cnovaez.dev.todoappcompose.login.ui.PinEntryScreen
import cnovaez.dev.todoappcompose.utils.curr_context
import cnovaez.dev.todoappcompose.utils.getDailyNotify
import cnovaez.dev.todoappcompose.utils.getLanguage
import cnovaez.dev.todoappcompose.utils.setDailyNotify
import cnovaez.dev.todoappcompose.utils.setLanguage
import cnovaez.dev.todoappcompose.utils.setLocale
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val taskViewModel: TaskViewModel by viewModels()
    val loginViewModel: LoginViewModel by viewModels()

    private val REQUEST_NOTIFICATION_PERMISSION = 123
    private var mInterstitialAd: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        curr_context = this
        loadIntersitialAdd("ca-app-pub-1269790857555936/4273418123")

        // En tu actividad o fragmento de JetPack Compose
        if (checkNotificationPermission(this)) {
            // Puedes mostrar notificaciones
        } else {
            // Debes solicitar permiso de notificación
            requestNotificationPermission(this)
        }
        checkAndRequestPermissions()
        loginViewModel.getLogin()
        val lang = getLanguage(this)

        if (lang == "na") {
            val defaultLanguage = Locale.getDefault().language
            setLocale(this, defaultLanguage)
            setLanguage(this, defaultLanguage)
        } else {
            setLocale(this, lang)
        }

        val isDailyNotifySet = getDailyNotify(this)
        if (!isDailyNotifySet) {
            taskViewModel.scheduleSystemRepeatingNotification(this)
            setDailyNotify(this, true)
        }

        setContent {

            val navigationController = rememberNavController()
            NavHost(
                navController = navigationController,
                startDestination = Routes.Login.route
            ) {
                composable(Routes.Login.route) {
                    PinEntryScreen(loginViewModel, navigationController)
                }
                composable(Routes.Tasks.route) {
                    TasksScreen(
                        taskViewModel = taskViewModel,
                        navigationController,
                        activity = this@MainActivity
                    )
                }
            }


        }

    }


    private fun loadIntersitialAdd(adId: String) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            adId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.show(this@MainActivity)
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    mInterstitialAd = null
                }
            })
    }

    fun checkNotificationPermission(context: Context): Boolean {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.areNotificationsEnabled()
    }

    fun requestNotificationPermission(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}
