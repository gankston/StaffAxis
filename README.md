# StaffAxis 📱

> [!IMPORTANT]
> **StaffAxis** y **[StaffAdmin](https://github.com/gankston/StaffAdmin)** son aplicaciones conectadas que funcionan en conjunto. StaffAxis es la interfaz móvil para la recolección de datos en tiempo real, mientras que StaffAdmin es el panel de gestión centralizado. Ambos se sincronizan mediante una infraestructura Cloud compartida.

[Español](#español) | [English](#english)

---

## Español

Aplicación Android nativa para el control de asistencia de empleados, diseñada para ofrecer una experiencia moderna y eficiente en el registro de la jornada laboral.

### 🌐 Pilares del Ecosistema
- **API Personalizada**: Desarrollada íntegramente en **Cloudflare Workers**, gestiona el flujo de datos y la lógica de negocio en la nube.
- **Base de Datos Cloud**: Utiliza **Turso (libSQL)** como motor de base de datos distribuida, garantizando que cada registro sea visible instantáneamente en todas las plataformas.
- **Sincronización Total**: Conexión directa con el panel de administración **StaffAdmin** para una gestión de recursos humanos unificada.

### 🚀 Características
- **Sincronización en Tiempo Real**: Los registros se suben al instante a la nube.
- **Control de Asistencia**: Registro de entrada y salida mediante legajo de empleado.
- **Calendario Personalizado**: Visualización de períodos laborales del 26 al 25 de cada mes.
- **Gestión de Empleados**: CRUD completo de empleados con validaciones en tiempo real.
- **Reportes**: Exportación de datos a Excel (.xlsx) y CSV.
- **Sincronización de Feriados**: Integración con API de feriados para marcar días no laborables.
- **UI Moderna**: Diseñada con Jetpack Compose y Material 3.

### 🛠️ Stack Tecnológico
- **Lenguaje**: Kotlin
- **UI Framework**: Jetpack Compose
- **Arquitectura**: MVVM + Clean Architecture
- **Persistencia**: Room Database + Turso (libSQL)
- **API**: Retrofit + Cloudflare Workers

---

## English

A native Android application for employee attendance control, designed to provide a modern and efficient experience in tracking workdays.

### 🌐 Ecosystem Pillars
- **Custom API**: Entirely developed on **Cloudflare Workers**, managing data flow and business logic in the cloud.
- **Cloud Database**: Powered by **Turso (libSQL)** as a distributed database engine, ensuring every record is instantly visible across all platforms.
- **Full Synchronization**: Direct connection with the **StaffAdmin** dashboard for unified human resource management.

### 🚀 Key Features
- **Real-Time Sync**: Records are instantly uploaded to the cloud.
- **Attendance Control**: Clock-in and clock-out registration using employee ID.
- **Custom Calendar**: Visualization of work periods from the 26th of one month to the 25th of the next.
- **Employee Management**: Full CRUD operations for employees with real-time validations.
- **Reporting**: Data export to Excel (.xlsx) and CSV formats.
- **Holiday Sync**: Integration with a holiday API to mark non-working days.
- **Modern UI**: Built with Jetpack Compose and Material 3.

### 🛠️ Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Persistence**: Room Database + Turso (libSQL)
- **API**: Retrofit + Cloudflare Workers

---
Developed with ❤️ by gankston
