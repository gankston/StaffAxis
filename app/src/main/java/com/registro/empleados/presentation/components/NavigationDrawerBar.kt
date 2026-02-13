package com.registro.empleados.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Drawer de navegación para pantallas grandes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawerBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination
    
    NavigationDrawerItem(
        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
        label = { Text("Dashboard") },
        selected = currentDestination?.hierarchy?.any { it.route == "dashboard" } == true,
        onClick = { navController.navigate("dashboard") }
    )
    
    NavigationDrawerItem(
        icon = { Icon(Icons.Default.People, contentDescription = "Empleados") },
        label = { Text("Empleados") },
        selected = currentDestination?.hierarchy?.any { it.route == "empleados" } == true,
        onClick = { navController.navigate("empleados") }
    )
    
    NavigationDrawerItem(
        icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Calendario") },
        label = { Text("Calendario") },
        selected = currentDestination?.hierarchy?.any { it.route == "calendario" } == true,
        onClick = { navController.navigate("calendario") }
    )
    
    NavigationDrawerItem(
        icon = { Icon(Icons.Default.Assessment, contentDescription = "Reportes") },
        label = { Text("Reportes") },
        selected = currentDestination?.hierarchy?.any { it.route == "reportes" } == true,
        onClick = { navController.navigate("reportes") }
    )
}