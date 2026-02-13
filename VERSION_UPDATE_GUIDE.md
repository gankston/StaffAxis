# Guía de Actualización de Versión - StaffAxis

## Proceso de Actualización

Antes de generar una nueva versión de la app, **SIEMPRE** debes seguir estos pasos:

### 1. ✅ Incrementar `versionCode` (OBLIGATORIO)
- El `versionCode` **DEBE** ser mayor que la versión anterior
- Android usa esto para determinar si hay una actualización disponible
- **Ejemplo**: Si la versión actual es `5`, la nueva debe ser `6` o mayor

### 2. 📝 Actualizar `versionName` (RECOMENDADO)
- Es la versión visible para el usuario
- **Ejemplo**: `"1.4"` → `"1.5"`
- Sigue un formato semántico (ej: `1.4.1`, `1.5.0`, `2.0.0`)

### 3. 🔐 Generar APK firmado
- El APK se firmará **automáticamente** con la misma keystore configurada
- La keystore está en: `C:\Users\[tu_usuario]\Desktop\staffaxis-release.keystore`
- **IMPORTANTE**: Usar siempre la misma keystore para mantener la misma firma

---

## Método 1: Script Automatizado (Recomendado)

Usa el script `update-version.ps1` que automatiza todo el proceso:

```powershell
# Ejecutar el script (te pedirá el nuevo versionName)
.\update-version.ps1

# O especificar ambos valores directamente
.\update-version.ps1 -NewVersionCode 6 -NewVersionName "1.5"
```

El script:
- ✅ Incrementa automáticamente el `versionCode` (+1)
- ✅ Te permite ingresar el nuevo `versionName`
- ✅ Actualiza `app/build.gradle.kts`
- ✅ Compila el APK release firmado
- ✅ Copia el APK al escritorio con el nombre `StaffAxis_v[versionName].apk`

---

## Método 2: Manual

Si prefieres hacerlo manualmente:

### Paso 1: Editar `app/build.gradle.kts`

Busca estas líneas en `defaultConfig`:

```kotlin
versionCode = 5        // ← Incrementar este número
versionName = "1.4"    // ← Actualizar este string
```

**Ejemplo de actualización:**
```kotlin
versionCode = 6        // 5 → 6 (incrementado)
versionName = "1.5"    // "1.4" → "1.5" (actualizado)
```

### Paso 2: Compilar APK Release

Ejecuta en la terminal:

```powershell
# Limpiar build anterior
.\gradlew.bat clean

# Compilar APK release firmado
.\gradlew.bat assembleRelease
```

### Paso 3: Copiar APK al Escritorio

El APK se genera en: `app\build\outputs\apk\release\app-release.apk`

Cópialo al escritorio con un nombre descriptivo:
```powershell
Copy-Item "app\build\outputs\apk\release\app-release.apk" "$env:USERPROFILE\Desktop\StaffAxis_v1.5.apk" -Force
```

---

## Verificación

Después de compilar, verifica que:

1. ✅ El `versionCode` fue incrementado correctamente
2. ✅ El `versionName` fue actualizado
3. ✅ El APK está firmado con la misma keystore (verificar con `jarsigner` si es necesario)
4. ✅ El APK está en el escritorio con el nombre correcto

---

## Notas Importantes

- ⚠️ **NUNCA** compiles con un `versionCode` menor o igual al anterior
- ⚠️ **SIEMPRE** usa la misma keystore para mantener la misma firma
- ⚠️ La misma firma es **crítica** para que el sistema de actualizaciones internas funcione
- ⚠️ Si cambias la keystore, los usuarios no podrán actualizar la app sin desinstalarla primero

---

## Ubicación de Archivos

- **Keystore**: `C:\Users\[tu_usuario]\Desktop\staffaxis-release.keystore`
- **APK generado**: `app\build\outputs\apk\release\app-release.apk`
- **APK copiado**: `C:\Users\[tu_usuario]\Desktop\StaffAxis_v[version].apk`
- **Configuración**: `app\build.gradle.kts`
