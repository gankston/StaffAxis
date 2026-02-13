package com.registro.empleados.presentation.utils

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass

/**
 * Utilidades para detectar el tipo de pantalla y adaptar el layout.
 * Soporta diferentes tamaños de pantalla: móvil, tablet y desktop.
 */
object ScreenType {
    
    /**
     * Enum que representa los diferentes tipos de pantalla.
     */
    enum class DeviceType {
        PHONE,          // Teléfono
        TABLET,         // Tablet
        DESKTOP         // Desktop (pantallas muy grandes)
    }
    
    /**
     * Enum que representa las orientaciones.
     */
    enum class Orientation {
        PORTRAIT,       // Vertical
        LANDSCAPE       // Horizontal
    }
    
    /**
     * Determina el tipo de dispositivo basado en WindowSizeClass.
     */
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    fun getDeviceType(windowSizeClass: WindowSizeClass): DeviceType {
        return when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> DeviceType.PHONE
            WindowWidthSizeClass.Medium -> DeviceType.TABLET
            WindowWidthSizeClass.Expanded -> DeviceType.DESKTOP
            else -> DeviceType.PHONE
        }
    }
    
    /**
     * Determina la orientación basada en WindowSizeClass.
     */
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    fun getOrientation(windowSizeClass: WindowSizeClass): Orientation {
        return when (windowSizeClass.heightSizeClass) {
            WindowHeightSizeClass.Compact -> Orientation.LANDSCAPE
            WindowHeightSizeClass.Medium,
            WindowHeightSizeClass.Expanded -> Orientation.PORTRAIT
            else -> Orientation.PORTRAIT
        }
    }
    
    /**
     * Verifica si es una pantalla compacta (teléfono).
     */
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    fun isCompact(windowSizeClass: WindowSizeClass): Boolean {
        return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    }
    
    /**
     * Verifica si es una pantalla media (tablet).
     */
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    fun isMedium(windowSizeClass: WindowSizeClass): Boolean {
        return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium
    }
    
    /**
     * Verifica si es una pantalla expandida (desktop).
     */
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    fun isExpanded(windowSizeClass: WindowSizeClass): Boolean {
        return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    }
    
    /**
     * Verifica si es landscape.
     */
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    fun isLandscape(windowSizeClass: WindowSizeClass): Boolean {
        return getOrientation(windowSizeClass) == Orientation.LANDSCAPE
    }
    
    /**
     * Verifica si es portrait.
     */
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    fun isPortrait(windowSizeClass: WindowSizeClass): Boolean {
        return getOrientation(windowSizeClass) == Orientation.PORTRAIT
    }
}
