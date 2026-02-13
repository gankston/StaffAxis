package com.registro.empleados.presentation

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.registro.empleados.data.local.preferences.AppPreferences
import com.registro.empleados.presentation.navigation.AppNavigation
import com.registro.empleados.presentation.screens.BienvenidaScreen
import com.registro.empleados.presentation.theme.AsistenciaTheme
import com.registro.empleados.presentation.screens.UpdateDownloadScreen
import com.registro.empleados.presentation.utils.UpdateChecker
import com.registro.empleados.presentation.utils.UpdateInfo
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Actividad principal de la aplicación.
 * Configura la UI principal con navegación y tema responsivo.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var appPreferences: AppPreferences
    
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            
            AsistenciaTheme {
                // Colores de sistema centralizados en AsistenciaTheme
                
                Box(
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF6A1B9A),  // Púrpura oscuro arriba
                                    Color(0xFF4A148C),  // Púrpura más oscuro
                                    Color(0xFF1E1E2E)   // Casi negro abajo
                                )
                            )
                        )
                ) {
                    AsistenciaApp(
                        windowSizeClass = windowSizeClass,
                        appPreferences = appPreferences,
                        activity = this@MainActivity
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun AsistenciaApp(
    windowSizeClass: androidx.compose.material3.windowsizeclass.WindowSizeClass,
    appPreferences: AppPreferences,
    activity: ComponentActivity
) {
    // Estado reactivo para la configuración
    var configuracionCompletada by remember { mutableStateOf(appPreferences.tieneConfiguracion()) }
    
    // Estado para la pantalla de actualización
    var updateInfo by remember { mutableStateOf<UpdateInfo?>(null) }
    
    // Verificar actualizaciones al iniciar la app
    LaunchedEffect(Unit) {
        UpdateChecker.checkForUpdates(activity, activity.lifecycleScope) { info ->
            updateInfo = info
        }
    }
    
    Log.d("MainActivity", "Configuración completada: $configuracionCompletada")
    
    // Mostrar pantalla de descarga si hay actualización disponible
    updateInfo?.let { info ->
        UpdateDownloadScreen(
            updateInfo = info,
            onClose = {
                if (!info.mandatory) {
                    updateInfo = null
                }
            },
            onInstall = {
                // La instalación se maneja dentro de UpdateDownloadScreen
                updateInfo = null
            }
        )
    } ?: run {
        // Mostrar app normal o bienvenida
        if (configuracionCompletada) {
            // Navegación normal de la app
            AppNavigation(windowSizeClass = windowSizeClass)
        } else {
            // Pantalla de bienvenida
            BienvenidaScreen(
                onConfiguracionCompletada = {
                    Log.d("MainActivity", "Configuración completada, navegando a app principal")
                    configuracionCompletada = true
                }
            )
        }
    }
}

