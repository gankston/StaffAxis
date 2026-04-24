# StaffAxis 📱

[Español](#español) | [English](#english)

---

## Español

Aplicación Android nativa para el control de asistencia de empleados, diseñada para ofrecer una experiencia moderna y eficiente en el registro de la jornada laboral.

### 🚀 Características
- **Control de Asistencia**: Registro de entrada y salida mediante legajo de empleado.
- **Calendario Personalizado**: Visualización de períodos laborales del 26 al 25 de cada mes.
- **Gestión de Empleados**: CRUD completo de empleados con validaciones en tiempo real.
- **Reportes**: Exportación de datos a Excel (.xlsx) y CSV.
- **Funcionamiento Offline**: Persistencia de datos local utilizando Room Database.
- **Sincronización de Feriados**: Integración con API de feriados para marcar días no laborables.
- **UI Moderna**: Diseñada con Jetpack Compose y Material 3.

### 🛠️ Stack Tecnológico
- **Lenguaje**: Kotlin
- **UI Framework**: Jetpack Compose
- **Arquitectura**: MVVM + Clean Architecture
- **Persistencia**: Room Database
- **Inyección de Dependencias**: Dagger Hilt
- **API**: Retrofit + Gson

### 📂 Estructura del Proyecto
- `app/src/main/java/.../data/`: Capa de datos (DB, Repositorios, API).
- `app/src/main/java/.../domain/`: Capa de dominio (Modelos, Casos de Uso).
- `app/src/main/java/.../presentation/`: Capa de interfaz (ViewModels, Screens).

---

## English

A native Android application for employee attendance control, designed to provide a modern and efficient experience in tracking workdays.

### 🚀 Key Features
- **Attendance Control**: Clock-in and clock-out registration using employee ID.
- **Custom Calendar**: Visualization of work periods from the 26th of one month to the 25th of the next.
- **Employee Management**: Full CRUD operations for employees with real-time validations.
- **Reporting**: Data export to Excel (.xlsx) and CSV formats.
- **Offline Functionality**: Local data persistence using Room Database.
- **Holiday Sync**: Integration with a holiday API to mark non-working days.
- **Modern UI**: Built with Jetpack Compose and Material 3.

### 🛠️ Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Persistence**: Room Database
- **Dependency Injection**: Dagger Hilt
- **API**: Retrofit + Gson

### 📂 Project Structure
- `app/src/main/java/.../data/`: Data layer (DB, Repositories, API).
- `app/src/main/java/.../domain/`: Domain layer (Models, Use Cases).
- `app/src/main/java/.../presentation/`: Interface layer (ViewModels, Screens).

---
Developed with ❤️ by gankston
