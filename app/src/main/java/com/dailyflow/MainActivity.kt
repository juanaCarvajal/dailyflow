package com.dailyflow

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.dailyflow.ui.navigation.AppNavGraph
import com.dailyflow.ui.onboarding.OnboardingPreferences
import com.dailyflow.ui.theme.DailyFlowTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // El usuario aceptó o rechazó el permiso
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Solicitar permiso de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permiso ya concedido
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Mostrar explicación al usuario
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Solicitar permiso directamente
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }

        // Determinar start destination basado en si el onboarding fue completado
        val startDestination = if (OnboardingPreferences.isOnboardingCompleted(this)) {
            "dashboard"
        } else {
            "onboarding"
        }

        setContent {
            DailyFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = com.dailyflow.ui.theme.BackgroundApp
                ) {
                    val navController = rememberNavController()
                    AppNavGraph(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
