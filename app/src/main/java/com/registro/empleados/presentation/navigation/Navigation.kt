package com.registro.empleados.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.registro.empleados.presentation.components.BottomNavigation
import com.registro.empleados.presentation.components.NavigationRailBar
import com.registro.empleados.presentation.screens.*
import com.registro.empleados.presentation.utils.ScreenType
import androidx.compose.material3.windowsizeclass.WindowSizeClass

/**
 * Navegación principal de la aplicación.
 * Se adapta a diferentes tamaños de pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    // Navegar automáticamente al dashboard al iniciar (evita pantalla en blanco)
    LaunchedEffect(currentRoute) {
        if (currentRoute == null || currentRoute == "main") {
            navController.navigate("dashboard") {
                launchSingleTop = true
                restoreState = true
                popUpTo("main") { saveState = true }
            }
        }
    }

    val screenType = when (windowSizeClass.widthSizeClass) {
        androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Compact -> "compact"
        androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Medium -> "medium"
        androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Expanded -> "expanded"
        else -> "compact"
    }

    when (screenType) {
        "compact" -> {
            // Navegación para teléfonos (Bottom Navigation)
            Scaffold(
                bottomBar = {
                    BottomNavigation(
                        navController = navController,
                        currentRoute = currentRoute
                    )
                }
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = "main",
                    modifier = modifier.padding(paddingValues)
                ) {
                    composable("main") {
                        // Esta es la entrada padre para scoping de ViewModels
                    }
                    composable("dashboard") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("main")
                        }
                        DashboardScreen(
                            windowSizeClass = windowSizeClass,
                            viewModel = hiltViewModel(parentEntry),
                            onNavigateBack = {
                                // No hacer nada, ya estamos en dashboard
                            }
                        )
                    }
                    composable("empleados") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("main")
                        }
                        EmpleadosScreen(
                            windowSizeClass = windowSizeClass,
                            viewModel = hiltViewModel(parentEntry)
                        )
                    }
                    composable("ausencias") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("main")
                        }
                        RegistroAusenciasScreen(
                            viewModel = hiltViewModel(parentEntry)
                        )
                    }
                    composable("reportes") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("main")
                        }
                        ReportesScreen(
                            windowSizeClass = windowSizeClass,
                            viewModel = hiltViewModel(parentEntry)
                        )
                    }
                }
            }
        }
        "medium" -> {
            // Navegación para tablets (Navigation Rail)
            Row {
                NavigationRailBar(navController = navController)
                Scaffold { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = "main",
                        modifier = modifier.padding(paddingValues)
                    ) {
                        composable("main") {
                            // Esta es la entrada padre para scoping de ViewModels
                        }
                        composable("dashboard") { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry("main")
                            }
                            DashboardScreen(
                                windowSizeClass = windowSizeClass,
                                viewModel = hiltViewModel(parentEntry),
                                onNavigateBack = {
                                    // No hacer nada, ya estamos en dashboard
                                }
                            )
                        }
                        composable("empleados") { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry("main")
                            }
                            EmpleadosScreen(
                                windowSizeClass = windowSizeClass,
                                viewModel = hiltViewModel(parentEntry)
                            )
                        }
                        composable("ausencias") { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry("main")
                            }
                            RegistroAusenciasScreen(
                                viewModel = hiltViewModel(parentEntry)
                            )
                        }
                        composable("reportes") { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry("main")
                            }
                            ReportesScreen(
                                windowSizeClass = windowSizeClass,
                                viewModel = hiltViewModel(parentEntry)
                            )
                        }
                    }
                }
            }
        }
        "expanded" -> {
            // Navegación para desktop (Navigation Drawer)
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        Text(
                            text = "Registro de Empleados",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(16.dp)
                        )
                        HorizontalDivider()
                        NavigationRailBar(navController = navController)
                    }
                }
            ) {
                NavHost(
                    navController = navController,
                    startDestination = "main",
                    modifier = modifier
                ) {
                    composable("main") {
                        // Esta es la entrada padre para scoping de ViewModels
                    }
                    composable("dashboard") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("main")
                        }
                        DashboardScreen(
                            windowSizeClass = windowSizeClass,
                            viewModel = hiltViewModel(parentEntry),
                            onNavigateBack = {
                                // No hacer nada, ya estamos en dashboard
                            }
                        )
                    }
                    composable("empleados") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("main")
                        }
                        EmpleadosScreen(
                            windowSizeClass = windowSizeClass,
                            viewModel = hiltViewModel(parentEntry)
                        )
                    }
                    composable("ausencias") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("main")
                        }
                        RegistroAusenciasScreen(
                            viewModel = hiltViewModel(parentEntry)
                        )
                    }
                    composable("reportes") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("main")
                        }
                        ReportesScreen(
                            windowSizeClass = windowSizeClass,
                            viewModel = hiltViewModel(parentEntry)
                        )
                    }
                }
            }
        }
    }
}