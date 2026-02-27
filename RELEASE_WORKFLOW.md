# Flujo de release - StaffAxis

**IMPORTANTE:** Seguir SIEMPRE estos pasos al generar una nueva versión.

## ⚠️ ANTES DE COMPILAR - OBLIGATORIO

**Primer paso SIEMPRE:** Actualizar `versionCode` y `versionName` en `app/build.gradle.kts`.
- versionCode: incrementar +1 (ej: 13 → 14)
- versionName: incrementar versión (ej: "1.7.6" → "1.7.7")
- Si no se hace esto, NO compilar. La versión anterior fue 1.7.6 (versionCode 13). Siguiente: 1.7.7 (14).

## Pasos obligatorios

1. **Actualizar versión** en `app/build.gradle.kts`:
   - Incrementar `versionCode` (+1 o más)
   - Actualizar `versionName` (ej: "1.7.5" → "1.7.6")

2. **Compilar APK release:**
   ```
   .\gradlew assembleRelease
   ```

3. **Copiar APK al Escritorio** con nombre en formato:
   - Origen: `app\build\outputs\apk\release\app-release.apk`
   - Destino: `C:\Users\pgast\Desktop\StaffAxis_v{versionName}.apk`
   - Ejemplo: `StaffAxis_v1.7.6.apk`

4. **Subir a GitHub:**
   - `git add` (archivos modificados + build.gradle.kts)
   - `git commit -m "v{versionName} (versionCode {X}) - descripción de cambios"`
   - `git push origin main`

## Resumen

- APK → Escritorio
- versionCode y versionName → actualizar siempre
- GitHub → push después de cada modificación
