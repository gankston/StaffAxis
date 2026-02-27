# Registro de Empleados - App Android

Aplicación Android nativa para control de asistencia de empleados con calendario personalizado (períodos 26-25), registro de entrada/salida, cálculo de horas trabajadas y exportación de reportes.

## 🚀 Características

- **Control de Asistencia**: Registro de entrada y salida de empleados
- **Calendario Personalizado**: Períodos laborales del 26 al 25 de cada mes
- **Gestión de Empleados**: CRUD completo de empleados
- **Reportes y Exportación**: Exportación a Excel y CSV
- **Funcionamiento Offline**: Base de datos local con Room
- **Sincronización de Feriados**: API de feriados argentinos
- **UI Moderna**: Jetpack Compose con Material 3 Design

## 🛠️ Stack Tecnológico

- **Lenguaje**: Kotlin 1.9+
- **UI**: Jetpack Compose
- **Arquitectura**: MVVM + Clean Architecture
- **Base de Datos**: Room Database
- **Inyección de Dependencias**: Dagger Hilt
- **Navegación**: Navigation Compose
- **API**: Retrofit + Gson
- **Trabajo en Background**: WorkManager
- **Exportación**: Apache POI

## 📱 Pantallas

### 1. Dashboard (Inicio)
- Campo para ingresar legajo
- Botones de registro de entrada/salida
- Información del empleado y registro del día
- Estado del registro actual

### 2. Empleados
- Lista de empleados activos
- Búsqueda por legajo, nombre o apellido
- Formulario para agregar nuevos empleados
- Validaciones de datos

### 3. Calendario
- Vista de calendario personalizado (26 al 25)
- Indicadores visuales por tipo de día:
  - Verde: Días laborales
  - Rojo: Feriados
  - Gris: Fines de semana
  - Azul: Días con asistencia registrada
- Navegación entre períodos
- Detalles de registros por fecha

### 4. Reportes
- Configuración de rango de fechas
- Filtro por empleado (opcional)
- Estadísticas del reporte
- Exportación a Excel y CSV
- Vista previa de datos

## 🏗️ Arquitectura

```
app/
├── data/
│   ├── database/          # Room entities, DAOs, Database
│   ├── repository/        # Implementaciones de repositorios
│   ├── remote/            # API services (Retrofit)
│   └── mapper/            # Convertidores entre capas
├── domain/
│   ├── model/             # Modelos de dominio
│   ├── repository/        # Interfaces de repositorios
│   └── usecase/           # Casos de uso
├── presentation/
│   ├── screens/           # Pantallas Compose
│   ├── viewmodel/         # ViewModels
│   ├── navigation/        # Configuración de navegación
│   ├── components/        # Componentes reutilizables
│   └── theme/             # Temas y colores
└── di/                    # Módulos de Dagger Hilt
```

## 🗄️ Base de Datos

### Entidades

1. **Empleado**
   - legajo (PK)
   - nombre, apellido
   - fecha_ingreso
   - activo (baja lógica)

2. **RegistroAsistencia**
   - id (PK)
   - legajo_empleado (FK)
   - fecha
   - hora_entrada, hora_salida
   - horas_trabajadas (calculado)

3. **DiaLaboral**
   - fecha (PK)
   - es_laboral
   - tipo_dia (LABORAL/FERIADO/FIN_DE_SEMANA)
   - descripcion

## 📅 Calendario Personalizado

El sistema utiliza períodos laborales que van del día 26 de un mes al día 25 del mes siguiente:

- **Período Octubre 2025**: 26/09/2025 - 25/10/2025
- **Período Noviembre 2025**: 26/10/2025 - 25/11/2025

### Lógica de Cálculo

```kotlin
fun calcularPeriodoActual(): Pair<LocalDate, LocalDate> {
    val hoy = LocalDate.now()
    val diaDelMes = hoy.dayOfMonth
    
    return if (diaDelMes >= 26) {
        // Período actual: 26 de este mes al 25 del próximo
        val inicio = hoy.withDayOfMonth(26)
        val fin = hoy.plusMonths(1).withDayOfMonth(25)
        Pair(inicio, fin)
    } else {
        // Período anterior: 26 del mes pasado al 25 de este mes
        val inicio = hoy.minusMonths(1).withDayOfMonth(26)
        val fin = hoy.withDayOfMonth(25)
        Pair(inicio, fin)
    }
}
```

## 🌐 API de Feriados

Integración con la API de feriados argentinos:
- **URL**: `https://nolaborables.com.ar/api/v2/feriados/{año}`
- **Sincronización**: Automática cada 30 días con WorkManager
- **Almacenamiento**: Local en tabla DiaLaboral

## 📊 Exportación

### Excel (.xlsx)
- Usando Apache POI
- Columnas: Legajo, Nombre, Apellido, Fecha, Hora Entrada, Hora Salida, Horas Trabajadas
- Formato profesional con headers

### CSV (.csv)
- Formato compatible con Excel
- Separador de comas
- Codificación UTF-8

## 🔧 Configuración

### Permisos Requeridos

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" 
    android:maxSdkVersion="28" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" 
    android:maxSdkVersion="32" />
```

### Versiones

- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Kotlin**: 1.9.22
- **Compose BOM**: 2024.02.00

## 🚀 Instalación

1. Clonar el repositorio
2. Abrir en Android Studio
3. Sincronizar dependencias
4. Ejecutar en dispositivo/emulador

## 📝 Uso

1. **Agregar Empleados**: Ir a "Empleados" → "Agregar Empleado"
2. **Registrar Asistencia**: "Inicio" → Ingresar legajo → "Entrada"/"Salida"
3. **Ver Calendario**: "Calendario" → Navegar períodos → Ver detalles
4. **Generar Reportes**: "Reportes" → Configurar rango → Exportar

## 🎨 Diseño

- **Material 3 Design**: Colores y componentes modernos
- **Navegación Inferior**: Acceso rápido a todas las pantallas
- **Tema Adaptativo**: Soporte para modo oscuro
- **Iconografía**: Material Icons para mejor UX

## 🔄 Flujo de Datos

1. **UI** → **ViewModel** → **UseCase** → **Repository** → **Database/API**
2. **StateFlow** para observación reactiva
3. **Coroutines** para operaciones asíncronas
4. **Hilt** para inyección de dependencias

## 📱 Funcionalidades Principales

### Registro de Asistencia
- Validación de empleado activo
- Prevención de entrada duplicada
- Cálculo automático de horas
- Manejo de errores

### Gestión de Empleados
- Validación de legajo único
- Búsqueda en tiempo real
- Formularios con validaciones
- Baja lógica (no eliminación física)

### Calendario
- Vista de grilla personalizada
- Indicadores visuales por estado
- Navegación entre períodos
- Detalles por fecha

### Reportes
- Filtros flexibles
- Estadísticas automáticas
- Exportación múltiple
- Vista previa de datos

## 🛡️ Validaciones

- Legajo único y obligatorio
- Fechas válidas (no futuras)
- Empleado activo para registros
- Entrada previa para salida
- Rango de fechas válido

## 📋 Pruebas manuales

Checklist de flujo de sincronización (device register → outbox → push submissions → pull approved) e instrucciones para ver logs en Android Studio: **[docs/MANUAL_TEST_CHECKLIST.md](docs/MANUAL_TEST_CHECKLIST.md)**.

## 🔧 Mantenimiento

- Base de datos optimizada con índices
- Limpieza automática de datos antiguos
- Sincronización periódica de feriados
- Logs de errores para debugging

## 📞 Soporte

Para consultas o reportar problemas, contactar al equipo de desarrollo.

---

**Desarrollado con ❤️ usando las mejores prácticas de Android**
