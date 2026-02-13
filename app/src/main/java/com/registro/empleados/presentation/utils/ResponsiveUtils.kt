package com.registro.empleados.presentation.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Utilidades para diseño responsive.
 */

/**
 * Obtiene el padding responsive basado en el tamaño de ventana.
 */
@Composable
fun responsivePadding(
    windowSizeClass: WindowSizeClass,
    compact: PaddingValues = PaddingValues(16.dp),
    medium: PaddingValues = PaddingValues(24.dp),
    expanded: PaddingValues = PaddingValues(32.dp)
): PaddingValues {
    return when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> compact
        WindowWidthSizeClass.Medium -> medium
        WindowWidthSizeClass.Expanded -> expanded
        else -> compact
    }
}

/**
 * Obtiene el espaciado responsive basado en el tamaño de ventana.
 */
@Composable
fun responsiveSpacing(
    windowSizeClass: WindowSizeClass,
    compact: Int = 8,
    medium: Int = 12,
    expanded: Int = 16
): Int {
    return when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> compact
        WindowWidthSizeClass.Medium -> medium
        WindowWidthSizeClass.Expanded -> expanded
        else -> compact
    }
}

/**
 * Obtiene el número de columnas para grid responsive.
 */
@Composable
fun responsiveColumns(
    windowSizeClass: WindowSizeClass,
    compact: Int = 1,
    medium: Int = 2,
    expanded: Int = 3
): Int {
    return when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> compact
        WindowWidthSizeClass.Medium -> medium
        WindowWidthSizeClass.Expanded -> expanded
        else -> compact
    }
}

/**
 * Obtiene el alineamiento horizontal responsive.
 */
@Composable
fun responsiveHorizontalAlignment(
    windowSizeClass: WindowSizeClass,
    compact: Alignment.Horizontal = Alignment.Start,
    medium: Alignment.Horizontal = Alignment.CenterHorizontally,
    expanded: Alignment.Horizontal = Alignment.CenterHorizontally
): Alignment.Horizontal {
    return when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> compact
        WindowWidthSizeClass.Medium -> medium
        WindowWidthSizeClass.Expanded -> expanded
        else -> compact
    }
}

/**
 * Obtiene el arreglo responsive para LazyColumn.
 */
@Composable
fun responsiveArrangement(
    windowSizeClass: WindowSizeClass,
    compact: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    medium: Arrangement.Vertical = Arrangement.spacedBy(12.dp),
    expanded: Arrangement.Vertical = Arrangement.spacedBy(16.dp)
): Arrangement.Vertical {
    return when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> compact
        WindowWidthSizeClass.Medium -> medium
        WindowWidthSizeClass.Expanded -> expanded
        else -> compact
    }
}

/**
 * Modifier para centrar contenido responsive.
 */
@Composable
fun Modifier.responsiveCenter(
    windowSizeClass: WindowSizeClass
): Modifier {
    return when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> this.fillMaxSize()
        WindowWidthSizeClass.Medium -> this.fillMaxSize()
        WindowWidthSizeClass.Expanded -> this.fillMaxSize()
        else -> this.fillMaxSize()
    }
}

/**
 * Crea un span para grid responsive.
 */
fun responsiveSpan(
    windowSizeClass: WindowSizeClass,
    compact: Int = 1,
    medium: Int = 1,
    expanded: Int = 1
): GridItemSpan {
    val span = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> compact
        WindowWidthSizeClass.Medium -> medium
        WindowWidthSizeClass.Expanded -> expanded
        else -> compact
    }
    return GridItemSpan(span)
}