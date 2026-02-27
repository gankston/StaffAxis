# Checklist de prueba manual - StaffAxis

Flujo de sincronización: device register → outbox → push submissions → pull approved.

---

## 1. Registrar dispositivo y guardar token

- [ ] Instalar la app (build debug).
- [ ] En la pantalla de bienvenida, elegir sector y encargado.
- [ ] Verificar en logs: `register device OK`.
- [ ] Token guardado en DataStore (`device_prefs`).

**Si falla:** `register device FAIL` indica sector no resuelto, error HTTP o excepción. Revisar que el backend tenga sectores cargados y que `BuildConfig.BASE_URL` apunte al servidor correcto.

---

## 2. Cargar asistencia → crear outbox pendiente

- [ ] Ir al Dashboard.
- [ ] Registrar asistencia para un empleado (4h, 8h o 12h).
- [ ] Verificar en base de datos Room: tabla `outbox_submissions` tiene un registro con `status = 'pending'`.

**Consulta SQL (Device File Explorer → `data/data/com.registro.empleados/databases/`):**
```sql
SELECT * FROM outbox_submissions WHERE status = 'pending';
```

---

## 3. Push outbox → enviar submissions

- [ ] Con internet activo, esperar hasta 15 min (o reiniciar app para disparar el OneTimeWork).
- [ ] Verificar en logs: `push outbox OK: N enviados`.
- [ ] Confirmar que el registro en `outbox_submissions` pasó a `status = 'sent'`.

**Si falla:** `push outbox FAIL (retrying)` → revisar que el backend reciba POST /submissions con header `X-Device-Token`, que el token sea válido y que el backend esté en marcha.

---

## 4. Pull approved → guardar en Room

- [ ] Con internet y backend aprobando submissions (flujo admin: approve).
- [ ] Esperar hasta 15 min o reiniciar app.
- [ ] Verificar en logs: `pull approved: N recibidos` (N > 0 si el backend devolvió attendances).
- [ ] Consultar la tabla espejo: `approved_attendances` tiene registros.

**Consulta SQL:**
```sql
SELECT * FROM approved_attendances WHERE is_deleted = 0;
```

---

## Ver logs en Android Studio

1. **Logcat**
   - Menú: View → Tool Windows → Logcat.
   - Filtrar por tag: `DeviceIdentityManager`, `PushOutboxWorker`, `PullApprovedWorker`.
   - O buscar texto: `register device`, `push outbox`, `pull approved`.

2. **Filtro por tag**
   ```
   tag:DeviceIdentityManager | tag:PushOutboxWorker | tag:PullApprovedWorker
   ```

3. **Filtro por paquete**
   - En Logcat, elegir el dropdown de proceso y seleccionar `com.registro.empleados`.

4. **Nivel de log**
   - Los mensajes usan `Log.d` y `Log.w`. Asegurarse de que el nivel sea "Debug" o inferior (Verbose, Debug, Info, Warn, Error).

5. **Build debug**
   - Los logs solo aparecen en builds debug (`BuildConfig.DEBUG = true`). En release no se imprimen.
