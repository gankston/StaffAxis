# Contexto del Proyecto StaffAxis - Para Otro Agente

Documento de contexto para continuar el desarrollo en otra computadora o con otro agente de IA.

---

## 1. Resumen del Proyecto

**StaffAxis** es una aplicación Android nativa para control de asistencia de empleados. Permite registrar entrada/salida, gestionar empleados por sector, ver calendarios con períodos personalizados (21-20), exportar reportes a Excel/CSV y actualizarse automáticamente desde un JSON remoto.

- **Repositorio**: https://github.com/gankston/StaffAxis
- **Usuario GitHub**: gankston
- **Versión actual**: 1.7.7 (versionCode: 14)
- **Package**: `com.registro.empleados`

---

## 2. Stack Tecnológico

| Tecnología | Versión / Detalle |
|------------|-------------------|
| Kotlin | 1.9.22 |
| Android | compileSdk 34, minSdk 26, targetSdk 34 |
| UI | Jetpack Compose + Material 3 |
| Arquitectura | MVVM + Clean Architecture |
| Base de datos | Room |
| DI | Dagger Hilt |
| Navegación | Navigation Compose |
| Red | OkHttp, Retrofit |
| Exportación Excel | JExcelApi (jxl) 2.6.12 |

---

## 3. Reglas Obligatorias al Compilar

**PRIMER PASO SIEMPRE - NO OLVIDAR:** Antes de compilar, actualizar versiones en `app/build.gradle.kts`:
1. **versionCode**: Incrementar obligatoriamente (+1). Último: 14. Próximo: 15.
2. **versionName**: Incrementar (ej: "1.7.7" → "1.7.8"). Última: 1.7.7.
3. Si no se hace esto, NO ejecutar assembleRelease.
3. **Firma**: Usar SIEMPRE la misma keystore. La app tiene sistema de actualizaciones internas; si cambia la firma, los usuarios no podrán actualizar.

**Keystore**:
- Ubicación: `%USERPROFILE%\Desktop\staffaxis-release.keystore`
- Alias: `staffaxis`
- Passwords: `staffaxis123` (store y key)
- **NUNCA** subir el keystore a GitHub

**APK**:
- Nombre del archivo: `StaffAxis_v[versionName].apk` (ej: `StaffAxis_v1.7.2.apk`)
- Copiar al escritorio después de compilar

---

## 4. Estructura de Carpetas

```
app/src/main/java/com/registro/empleados/
├── data/
│   ├── local/          # Room (entities, DAOs, database, preferences)
│   ├── repository/     # Implementaciones
│   ├── export/         # ExcelExportService, CsvExportService
│   └── mapper/
├── domain/
│   ├── model/          # Empleado, RegistroAsistencia, ConfiguracionApp, etc.
│   ├── repository/     # Interfaces
│   └── usecase/        # Casos de uso por dominio
├── presentation/
│   ├── screens/        # Pantallas Compose (Dashboard, Empleados, Calendario, Reportes, etc.)
│   ├── viewmodel/
│   ├── navigation/
│   ├── components/
│   ├── utils/          # UpdateChecker.kt
│   └── theme/
└── di/                 # DatabaseModule, UseCaseModule
```

---

## 5. Funcionalidades Clave

### Sectores
- Cada empleado tiene un `sector` (string).
- La app guarda el sector seleccionado en `AppPreferences` (`sector_seleccionado`).
- En **Reportes**: estadísticas (Activos, Totales) y datos exportados son **solo del sector seleccionado**.
- En **Empleados** y **Dashboard**: se filtran por sector.

### Calendario / Períodos
- **Período laboral**: del **21** de un mes al **20** del mes siguiente (no 26-25).
- Usado en exportación Excel/CSV y en la lógica de reportes.

### Exportación Excel/CSV
- Formato de archivo: `asistencia[NombreDelSector][YYYYMMDD].xls` (ej: `asistenciaCONSTRUCCION20250213.xls`)
- `NombreDelSector`: sin espacios, mayúsculas.
- Período de días: 21-31 (mes anterior) + 1-20 (mes actual).
- Al final del Excel: fila **TOTAL** con suma de horas.

### Sistema de Actualizaciones
- URL del JSON: `https://gankston.github.io/staffaxis-updates/version.json`
- Estructura del JSON:
  ```json
  {
    "versionCode": 9,
    "versionName": "1.7.2",
    "apkUrl": "https://gankston.github.io/staffaxis-updates/staffaxis-1.7.2.apk",
    "mandatory": false,
    "notes": "Descripción de la actualización"
  }
  ```
- Al iniciar la app, se compara `versionCode` local vs remoto.
- Si hay actualización: AlertDialog → UpdateDownloadScreen (descarga e instalación dentro de la app).
- Si `mandatory=true`: diálogo no cancelable.
- Permisos: `INTERNET`, `REQUEST_INSTALL_PACKAGES`.

---

## 6. Archivos Importantes

| Archivo | Propósito |
|---------|-----------|
| `app/build.gradle.kts` | Versión, signing, dependencias |
| `UpdateChecker.kt` | Verificación de actualizaciones |
| `UpdateDownloadScreen.kt` | Descarga e instalación de APK |
| `ExcelExportService.kt` | Exportación Excel (período 21-20, nombre con sector) |
| `CsvExportService.kt` | Exportación CSV (misma lógica) |
| `ReportesViewModel.kt` | Filtra por sector, `empleadosDelSector` |
| `ReportesScreen.kt` | Estadísticas por sector, sin botón "Limpiar Base de Datos" |
| `AppPreferences.kt` | Sector, nombre encargado, primera vez |
| `update-version.ps1` | Script para subir versión, compilar y generar JSON |

---

## 7. Comandos Útiles

```powershell
# Compilar APK release (requiere keystore en Escritorio)
.\gradlew.bat assembleRelease

# Script de actualización (incrementa versionCode, pide versionName)
.\update-version.ps1

# Git (ruta completa si no está en PATH)
& "C:\Program Files\Git\bin\git.exe" status
& "C:\Program Files\Git\bin\git.exe" add .
& "C:\Program Files\Git\bin\git.exe" commit -m "Descripción"
& "C:\Program Files\Git\bin\git.exe" push origin main
```

---

## 8. Convenciones del Usuario

- **Push a GitHub**: Después de cada cambio relevante, hacer commit y push a `https://github.com/gankston/StaffAxis`.
- **Producción**: Evitar warnings; la app está en producción.
- **Misma firma**: Siempre compilar con la misma keystore.
- **Nombre APK**: Siempre `StaffAxis_v[versionName].apk`.

---

## 9. Base de Datos

- Nombre: `registro_empleados_database.db`
- Ubicación en dispositivo: `/data/data/com.registro.empleados/databases/`
- Entidades principales: `Empleado`, `RegistroAsistencia`, `DiaLaboral`, `HorasEmpleadoMes`, `Ausencia`

---

## 10. Pantallas Principales

1. **BienvenidaScreen**: Configuración inicial (nombre encargado, sector)
2. **DashboardScreen**: Registro entrada/salida por legajo
3. **EmpleadosScreen**: Lista, búsqueda, agregar empleado (con sector)
4. **CalendarioScreen**: Vista de calendario con períodos
5. **ReportesScreen**: Rango de fechas, estadísticas por sector, exportar Excel/CSV
6. **RegistroAusenciasScreen**: Gestión de ausencias
7. **UpdateDownloadScreen**: Descarga e instalación de actualizaciones

---

## 11. Notas Adicionales

- El botón "Limpiar Base de Datos" fue **eliminado** de ReportesScreen.
- Las estadísticas en Reportes (Activos, Totales) cuentan **solo empleados del sector seleccionado**.
- El README.md puede tener info desactualizada (ej: período 26-25; en realidad es 21-20).
- En Windows, Git puede no estar en PATH; usar `C:\Program Files\Git\bin\git.exe`.

---

*Documento generado para transferencia de contexto entre agentes. Actualizar cuando cambien reglas o funcionalidades importantes.*
