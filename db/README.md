# StaffAxis – Base de datos Turso

Instrucciones para configurar la base de datos central en Turso (libSQL).

---

## 1. Instalar Turso CLI

### macOS (preferido: Homebrew)
```bash
brew install tursodatabase/tap/turso
```

### macOS / Linux (alternativa)
```bash
curl -sSfL https://get.tur.so/install.sh | bash
```

### Windows
Requiere WSL. En PowerShell:
```powershell
wsl
```
Luego dentro de WSL:
```bash
curl -sSfL https://get.tur.so/install.sh | bash
```

Abrir **una nueva terminal** y verificar:
```bash
turso --version
```

---

## 2. Autenticarse
```bash
turso auth login
```
(Abre el navegador para login con GitHub)

---

## 3. Crear la base de datos
```bash
turso db create staffaxis
```

---

## 4. Obtener DATABASE_URL y AUTH_TOKEN

### DATABASE_URL
```bash
turso db show staffaxis --url
```
Copiar la URL (formato `libsql://staffaxis-...turso.io`).

### AUTH_TOKEN
```bash
turso db tokens create staffaxis
```
Copiar el token. Para producción, considerar `--expiration never` o un plazo específico.

---

## 5. Ejecutar schema.sql y seed.sql

### Opción A (preferida): redirección de archivo
Desde la raíz del repo:
```bash
turso db shell staffaxis < db/schema.sql
turso db shell staffaxis < db/seed.sql
```

### Opción B: shell interactivo con .read
```bash
turso db shell staffaxis
```
Dentro del shell:
```sql
.read db/schema.sql
.read db/seed.sql
.quit
```
(Nota: `.read` usa rutas relativas al directorio actual; asegurarse de estar en la raíz del repo.)

---

## Variables de entorno para el backend

```
DATABASE_URL=libsql://staffaxis-xxxxx-xxxxx.turso.io
AUTH_TOKEN=eyJ...
```
