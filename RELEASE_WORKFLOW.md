# Flujo de release - StaffAxis

**IMPORTANTE:** Seguir SIEMPRE estos pasos al generar una nueva versión.

## Pasos obligatorios

1. **Actualizar versión** en `app/build.gradle.kts`:
   - Incrementar `versionCode` (+1 o más)
   - Actualizar `versionName` (ej: "1.7.5" → "1.7.6")

2. **Compilar APK release:**
   ```
   .\gradlew assembleRelease
   ```

3. **Copiar APK al Escritorio** con nombre descriptivo:
   - Origen: `app\build\outputs\apk\release\app-release.apk`
   - Destino: `C:\Users\pgast\Desktop\StaffAxis-{versionName}.apk`
   - Ejemplo: `StaffAxis-1.7.6.apk`

4. **Subir a GitHub:**
   - `git add` (archivos modificados + build.gradle.kts)
   - `git commit -m "v{versionName} (versionCode {X}) - descripción de cambios"`
   - `git push origin main`

## Resumen

- APK → Escritorio
- versionCode y versionName → actualizar siempre
- GitHub → push después de cada modificación
