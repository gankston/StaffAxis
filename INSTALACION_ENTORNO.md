# Guía de Instalación del Entorno - StaffAxis

Todo lo que necesitas instalar y configurar para compilar y desarrollar StaffAxis correctamente.

---

## 1. Requisitos del Sistema

- **Sistema operativo**: Windows 10/11 (o macOS/Linux con comandos equivalentes)
- **RAM**: Mínimo 8 GB (recomendado 16 GB)
- **Disco**: ~10 GB libres para SDK, Gradle y builds
- **Pantalla**: Resolución mínima 1280x720

---

## 2. Software Obligatorio

### 2.1 Java Development Kit (JDK) 17

El proyecto usa **Java 17** (configurado en `app/build.gradle.kts`).

**Opción A - Con Android Studio** (recomendado):
- Android Studio incluye JDK embebido. No hace falta instalar JDK por separado si usas Android Studio.

**Opción B - Instalación manual**:
- Descarga: https://adoptium.net/temurin/releases/?version=17&os=windows
- O: https://www.oracle.com/java/technologies/downloads/#java17
- Instalar y agregar `JAVA_HOME` al entorno:
  - `JAVA_HOME` = ruta de instalación (ej: `C:\Program Files\Eclipse Adoptium\jdk-17.0.x`)

**Verificar**:
```powershell
java -version
# Debe mostrar: openjdk version "17.x.x" o similar
```

---

### 2.2 Android Studio

**Descarga**: https://developer.android.com/studio

**Durante la instalación**:
- Marcar "Android SDK"
- Marcar "Android SDK Platform"
- Marcar "Android Virtual Device" (opcional, para emulador)

**Componentes SDK requeridos** (instalar desde SDK Manager en Android Studio):
- **Android SDK Platform 34** (compileSdk del proyecto)
- **Android SDK Build-Tools** (última versión compatible)
- **Android SDK Platform-Tools** (para ADB)
- **Android Emulator** (opcional)
- **Google Play services** (opcional)

**Variables de entorno** (Android Studio suele configurarlas):
- `ANDROID_HOME` = `C:\Users\[tu_usuario]\AppData\Local\Android\Sdk`
- Agregar a PATH: `%ANDROID_HOME%\platform-tools` y `%ANDROID_HOME%\tools`

---

### 2.3 Git

**Descarga**: https://git-scm.com/download/win

**Durante la instalación**:
- Opción "Git from the command line and also from 3rd-party software"
- Opción "Use bundled OpenSSH"
- Opción "Checkout Windows-style, commit Unix-style line endings"

**Ruta típica**: `C:\Program Files\Git\bin\git.exe`

**Verificar**:
```powershell
git --version
```

**Configuración inicial** (una vez instalado):
```powershell
git config --global user.name "Tu Nombre"
git config --global user.email "tu@email.com"
```

---

## 3. Keystore para Firma (Release)

Para compilar APK de **release** firmados (producción), necesitas el keystore.

**Ubicación esperada**: `%USERPROFILE%\Desktop\staffaxis-release.keystore`

**Si ya tienes el keystore** (de otra máquina):
- Copiar `staffaxis-release.keystore` al Escritorio
- No modificar el archivo; debe ser el mismo en todas las compilaciones

**Si es la primera vez** (crear nuevo keystore):
```powershell
# Ejecutar en PowerShell (ajustar rutas si es necesario)
keytool -genkey -v -keystore "$env:USERPROFILE\Desktop\staffaxis-release.keystore" -alias staffaxis -keyalg RSA -keysize 2048 -validity 10000
```
- Contraseña del store: `staffaxis123` (o la que uses, pero actualizar en `app/build.gradle.kts`)
- Alias: `staffaxis`
- Contraseña de la key: `staffaxis123`

**Importante**: El keystore está en `.gitignore`. Nunca subirlo a GitHub.

---

## 4. Clonar el Repositorio

```powershell
git clone https://github.com/gankston/StaffAxis.git
cd StaffAxis
```

---

## 5. Configuración del Proyecto

### 5.1 Abrir en Android Studio

1. File → Open → seleccionar carpeta `StaffAxis`
2. Esperar a que Gradle sincronice (descargará dependencias)
3. Si pide "Trust Project", aceptar

### 5.2 Archivo `local.properties` (automático)

Android Studio crea `local.properties` con la ruta del SDK. Si no existe:

Crear `local.properties` en la raíz del proyecto:
```properties
sdk.dir=C\:\\Users\\[tu_usuario]\\AppData\\Local\\Android\\Sdk
```
(Ajustar la ruta según tu instalación)

---

## 6. Compilar el Proyecto

### 6.1 Desde Android Studio

- **Debug**: Build → Make Project (Ctrl+F9) o Run (Shift+F10)
- **Release**: Build → Generate Signed Bundle / APK → APK → seleccionar keystore

### 6.2 Desde Terminal (PowerShell)

**Requisito**: Keystore en `%USERPROFILE%\Desktop\staffaxis-release.keystore`

```powershell
cd StaffAxis

# Compilar APK release
.\gradlew.bat assembleRelease
```

El APK se genera en: `app\build\outputs\apk\release\app-release.apk`

### 6.3 Script de actualización de versión

```powershell
.\update-version.ps1
```

El script:
- Incrementa `versionCode`
- Pide `versionName`
- Compila APK release
- Copia a Escritorio como `StaffAxis_v[version].apk`
- Opcionalmente genera JSON para GitHub Pages

---

## 7. Instalar en Dispositivo Físico

1. Activar **Opciones de desarrollador** en el Android
2. Activar **Depuración USB**
3. Conectar por USB
4. Ejecutar: `.\gradlew.bat installRelease`  
   O desde Android Studio: Run → seleccionar dispositivo

**ADB** (si está en PATH):
```powershell
adb devices
adb install -r app\build\outputs\apk\release\app-release.apk
```

---

## 8. Resumen de Versiones del Proyecto

| Componente | Versión |
|------------|---------|
| Gradle | 8.13 |
| Android Gradle Plugin | 8.13.0 |
| Kotlin | 2.0.0 |
| compileSdk | 34 |
| minSdk | 26 |
| targetSdk | 34 |
| Java | 17 |

---

## 9. Solución de Problemas

### "El keystore no se encuentra"
- Verificar que `staffaxis-release.keystore` está en el Escritorio
- Ruta esperada: `C:\Users\[tu_usuario]\Desktop\staffaxis-release.keystore`

### "git no se reconoce"
- Git no está en PATH. Usar ruta completa: `& "C:\Program Files\Git\bin\git.exe" [comando]`
- O agregar `C:\Program Files\Git\bin` al PATH del sistema

### Gradle sync falla
- File → Invalidate Caches → Invalidate and Restart
- Verificar conexión a internet (Gradle descarga dependencias)
- Verificar que `ANDROID_HOME` está configurado

### "SDK location not found"
- Crear `local.properties` con `sdk.dir` apuntando al Android SDK

### Build muy lento
- Aumentar memoria de Gradle en `gradle.properties`:  
  `org.gradle.jvmargs=-Xmx4096m`
- Usar SSD si es posible

### Errores de compilación con Hilt/Room
- Build → Clean Project
- Build → Rebuild Project
- Verificar que KSP está habilitado (el proyecto usa KSP, no KAPT para Room)

---

## 10. Checklist Pre-Compilación

- [ ] JDK 17 instalado (o Android Studio con JDK embebido)
- [ ] Android Studio instalado
- [ ] Android SDK Platform 34 instalado
- [ ] Git instalado
- [ ] Keystore `staffaxis-release.keystore` en el Escritorio
- [ ] Proyecto clonado y abierto en Android Studio
- [ ] Gradle sync completado sin errores
- [ ] `local.properties` existe (creado por Android Studio)

---

*Documento para configurar el entorno de desarrollo desde cero. Mantener actualizado si cambian versiones o requisitos.*
