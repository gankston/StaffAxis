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
 * Barra de navegación lateral para tablets.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationRailBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination
    
    NavigationRail(modifier = modifier) {
        NavigationRailItem(
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Dashboard") },
            selected = currentDestination?.hierarchy?.any { it.route == "dashboard" } == true,
            onClick = { navController.navigate("dashboard") }
        )
        
        
        NavigationRailItem(
            icon = { Icon(Icons.Default.EventBusy, contentDescription = "Ausencias") },
            label = { Text("Ausencias") },
            selected = currentDestination?.hierarchy?.any { it.route == "ausencias" } == true,
            onClick = { navController.navigate("ausencias") }
        )
        
        NavigationRailItem(
            icon = { Icon(Icons.Default.Assessment, contentDescription = "Reportes") },
            label = { Text("Reportes") },
            selected = currentDestination?.hierarchy?.any { it.route == "reportes" } == true,
            onClick = { navController.navigate("reportes") }
        )
    }
}