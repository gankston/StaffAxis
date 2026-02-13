# Cómo subir este proyecto a GitHub

## 1. Instalar Git

Si no tienes Git instalado:

- Descarga: https://git-scm.com/download/win
- Instala con las opciones por defecto
- Reinicia la terminal después de instalar

## 2. Crear el repositorio en GitHub

1. Entra a https://github.com/new
2. Nombre del repo: por ejemplo `staffaxis` o `registro-empleados`
3. Elige **Público** o **Privado**
4. **No** marques "Add a README" (el proyecto ya tiene contenido)
5. Clic en **Create repository**

## 3. Subir el código desde tu PC

Abre PowerShell o CMD en la carpeta del proyecto (`c:\Registro empleados`) y ejecuta:

```powershell
cd "c:\Registro empleados"

# Inicializar Git (si aún no está inicializado)
git init

# Añadir todos los archivos (el .gitignore excluye build, .gradle, etc.)
git add .

# Primer commit
git commit -m "Initial commit: StaffAxis v1.7.2"

# Conectar con tu repositorio de GitHub (cambia TU_USUARIO y TU_REPO por los tuyos)
git remote add origin https://github.com/TU_USUARIO/TU_REPO.git

# Subir la rama main
git branch -M main
git push -u origin main
```

## 4. Autenticación con GitHub

Git te pedirá autenticación. Opciones:

- **Token personal**: GitHub → Settings → Developer settings → Personal access tokens → Generar token con permiso `repo`
- **GitHub CLI**: `winget install GitHub.cli` y luego `gh auth login`

## Importante

- El archivo **staffaxis-release.keystore** está en tu Escritorio, fuera del proyecto. No lo subas nunca a GitHub.
- El `.gitignore` evita que se suban carpetas `build/`, `.gradle/`, `local.properties` y archivos sensibles.
