package com.registro.empleados.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Componente de navegación inferior para la aplicación.
 */
@Composable
fun BottomNavigation(
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    // Detectar insets de navegación del sistema
    val navigationBarsPadding = WindowInsets.navigationBars.asPaddingValues()
    val bottomPadding = with(LocalDensity.current) {
        navigationBarsPadding.calculateBottomPadding().toPx()
    }
    
    // Si el padding es mayor a 24px, significa que hay gestos de navegación
    // Si es 0 o muy pequeño, hay botones tradicionales
    val tieneGestos = bottomPadding > 24f
    
    Log.d("BottomNav", "Bottom padding: ${bottomPadding}px - Tiene gestos: $tieneGestos")
    
    NavigationBar(
        containerColor = Color(0xFF2A2A3E),  // Fondo oscuro
        contentColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 90.dp)  // Altura mínima garantizada
            .wrapContentHeight()  // Altura automática flexible
            .then(
                // AGREGAR padding SOLO si tiene gestos
                if (tieneGestos) {
                    Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                } else {
                    Modifier  // Sin padding extra
                }
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E1E2E),
                        Color(0xFF2A2A3E)
                    )
                )
            ),
        tonalElevation = 8.dp,
        windowInsets = WindowInsets(0.dp)  // No usar insets automáticos
    ) {
        // Dashboard -> Carga de Horas
        NavigationBarItem(
            selected = currentRoute == "dashboard",
            onClick = {
                if (currentRoute == "dashboard") return@NavigationBarItem
                navController.navigate("dashboard") {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                }
            },
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            brush = if (currentRoute == "dashboard") {
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF9C27B0),
                                        Color(0xFF26C6DA)
                                    )
                                )
                            } else {
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0x33FFFFFF),
                                        Color(0x11FFFFFF)
                                    )
                                )
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Dashboard,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = {
                Text(
                    "Carga de Horas",
                    color = if (currentRoute == "dashboard") Color.White else Color(0xFF888888),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            },
            alwaysShowLabel = true,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color(0xFF888888),
                selectedTextColor = Color.White,
                unselectedTextColor = Color(0xFF888888),
                indicatorColor = Color.Transparent
            )
        )
        
        // Ausencias
        NavigationBarItem(
            selected = currentRoute == "ausencias",
            onClick = {
                if (currentRoute == "ausencias") return@NavigationBarItem
                navController.navigate("ausencias") {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                }
            },
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            brush = if (currentRoute == "ausencias") {
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFE91E63),
                                        Color(0xFF9C27B0)
                                    )
                                )
                            } else {
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0x33FFFFFF),
                                        Color(0x11FFFFFF)
                                    )
                                )
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.EventBusy,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = {
                Text(
                    "Ausencias",
                    color = if (currentRoute == "ausencias") Color.White else Color(0xFF888888),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            },
            alwaysShowLabel = true,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color(0xFF888888),
                selectedTextColor = Color.White,
                unselectedTextColor = Color(0xFF888888),
                indicatorColor = Color.Transparent
            )
        )
        
        // Reportes
        NavigationBarItem(
            selected = currentRoute == "reportes",
            onClick = {
                if (currentRoute == "reportes") return@NavigationBarItem
                navController.navigate("reportes") {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                }
            },
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            brush = if (currentRoute == "reportes") {
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF5E35B1),
                                        Color(0xFF26C6DA)
                                    )
                                )
                            } else {
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0x33FFFFFF),
                                        Color(0x11FFFFFF)
                                    )
                                )
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Assessment,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = {
                Text(
                    "Reportes",
                    color = if (currentRoute == "reportes") Color.White else Color(0xFF888888),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            },
            alwaysShowLabel = true,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color(0xFF888888),
                selectedTextColor = Color.White,
                unselectedTextColor = Color(0xFF888888),
                indicatorColor = Color.Transparent
            )
        )
    }
}
