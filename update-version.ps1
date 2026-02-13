# ============================================
# Script de Actualización de Versión - StaffAxis
# ============================================
# Este script automatiza el proceso de actualización de versión:
# 1. Incrementa el versionCode (obligatorio)
# 2. Actualiza el versionName (recomendado)
# 3. Compila el APK firmado con la misma keystore
# ============================================

param(
    [Parameter(Mandatory=$false)]
    [int]$NewVersionCode = 0,
    
    [Parameter(Mandatory=$false)]
    [string]$NewVersionName = ""
)

$buildGradlePath = "app\build.gradle.kts"
$ErrorActionPreference = "Stop"

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  Actualización de Versión - StaffAxis" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Verificar que el archivo existe
if (-not (Test-Path $buildGradlePath)) {
    Write-Host "ERROR: No se encuentra el archivo $buildGradlePath" -ForegroundColor Red
    exit 1
}

# Leer el archivo actual
$content = Get-Content $buildGradlePath -Raw

# Extraer versionCode y versionName actuales
$versionCodeMatch = [regex]::Match($content, 'versionCode\s*=\s*(\d+)')
$versionNameMatch = [regex]::Match($content, 'versionName\s*=\s*"([^"]+)"')

if (-not $versionCodeMatch.Success) {
    Write-Host "ERROR: No se pudo encontrar versionCode en $buildGradlePath" -ForegroundColor Red
    exit 1
}

$currentVersionCode = [int]$versionCodeMatch.Groups[1].Value
$currentVersionName = if ($versionNameMatch.Success) { $versionNameMatch.Groups[1].Value } else { "" }

Write-Host "Versión actual:" -ForegroundColor Yellow
Write-Host "  versionCode: $currentVersionCode" -ForegroundColor White
Write-Host "  versionName: $currentVersionName" -ForegroundColor White
Write-Host ""

# Determinar nuevo versionCode
if ($NewVersionCode -eq 0) {
    $NewVersionCode = $currentVersionCode + 1
    Write-Host "Nuevo versionCode (incrementado automáticamente): $NewVersionCode" -ForegroundColor Green
} else {
    if ($NewVersionCode -le $currentVersionCode) {
        Write-Host "ERROR: El nuevo versionCode ($NewVersionCode) debe ser mayor que el actual ($currentVersionCode)" -ForegroundColor Red
        exit 1
    }
    Write-Host "Nuevo versionCode (especificado): $NewVersionCode" -ForegroundColor Green
}

# Determinar nuevo versionName
if ([string]::IsNullOrWhiteSpace($NewVersionName)) {
    Write-Host ""
    Write-Host "Ingresa el nuevo versionName (o presiona Enter para mantener '$currentVersionName'):" -ForegroundColor Yellow
    $input = Read-Host
    if ([string]::IsNullOrWhiteSpace($input)) {
        $NewVersionName = $currentVersionName
        Write-Host "Manteniendo versionName: $NewVersionName" -ForegroundColor Yellow
    } else {
        $NewVersionName = $input
    }
}

Write-Host ""
Write-Host "Nueva versión:" -ForegroundColor Yellow
Write-Host "  versionCode: $NewVersionCode" -ForegroundColor White
Write-Host "  versionName: $NewVersionName" -ForegroundColor White
Write-Host ""

# Confirmar
Write-Host "¿Continuar con la actualización? (S/N):" -ForegroundColor Yellow
$confirm = Read-Host
if ($confirm -ne "S" -and $confirm -ne "s" -and $confirm -ne "Y" -and $confirm -ne "y") {
    Write-Host "Operación cancelada." -ForegroundColor Red
    exit 0
}

# Actualizar versionCode
$content = $content -replace "versionCode\s*=\s*\d+", "versionCode = $NewVersionCode"

# Actualizar versionName
$content = $content -replace 'versionName\s*=\s*"[^"]*"', "versionName = `"$NewVersionName`""

# Guardar el archivo
Set-Content -Path $buildGradlePath -Value $content -NoNewline

Write-Host ""
Write-Host "✓ Archivo $buildGradlePath actualizado" -ForegroundColor Green
Write-Host ""

# Compilar APK release
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  Compilando APK Release..." -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Limpiar build anterior
Write-Host "Limpiando build anterior..." -ForegroundColor Yellow
& .\gradlew.bat clean
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Falló la limpieza del build" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Compilando APK release firmado..." -ForegroundColor Yellow
& .\gradlew.bat assembleRelease
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Falló la compilación del APK" -ForegroundColor Red
    exit 1
}

# Copiar APK al escritorio
$apkPath = "app\build\outputs\apk\release\app-release.apk"
$desktopApk = "$env:USERPROFILE\Desktop\StaffAxis_v$NewVersionName.apk"

if (Test-Path $apkPath) {
    Copy-Item $apkPath $desktopApk -Force
    Write-Host ""
    Write-Host "============================================" -ForegroundColor Green
    Write-Host "  ✓ APK generado exitosamente" -ForegroundColor Green
    Write-Host "============================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "APK copiado a: $desktopApk" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Resumen:" -ForegroundColor Yellow
    Write-Host "  versionCode: $currentVersionCode → $NewVersionCode" -ForegroundColor White
    Write-Host "  versionName: $currentVersionName → $NewVersionName" -ForegroundColor White
    Write-Host "  APK: $desktopApk" -ForegroundColor White
    Write-Host ""
    
    # Generar JSON para GitHub Pages
    Write-Host "============================================" -ForegroundColor Cyan
    Write-Host "  Generar JSON para GitHub Pages" -ForegroundColor Cyan
    Write-Host "============================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "¿Generar JSON para actualizar en GitHub Pages? (S/N):" -ForegroundColor Yellow
    $generateJson = Read-Host
    if ($generateJson -eq "S" -or $generateJson -eq "s" -or $generateJson -eq "Y" -or $generateJson -eq "y") {
        
        # Preguntar por mandatory
        Write-Host ""
        Write-Host "¿La actualización es obligatoria? (S/N, default: N):" -ForegroundColor Yellow
        $mandatoryInput = Read-Host
        $mandatory = ($mandatoryInput -eq "S" -or $mandatoryInput -eq "s" -or $mandatoryInput -eq "Y" -or $mandatoryInput -eq "y")
        
        # Preguntar por notes
        Write-Host ""
        Write-Host "Ingresa las notas de la actualización (o presiona Enter para dejar vacío):" -ForegroundColor Yellow
        $notesInput = Read-Host
        $notes = if ([string]::IsNullOrWhiteSpace($notesInput)) { "" } else { $notesInput }
        
        # Generar nombre del APK para la URL
        $apkFileName = "staffaxis-$NewVersionName.apk"
        $apkUrl = "https://gankston.github.io/staffaxis-updates/$apkFileName"
        
        # Crear objeto JSON
        $jsonObject = @{
            versionCode = $NewVersionCode
            versionName = $NewVersionName
            apkUrl = $apkUrl
            mandatory = $mandatory
            notes = $notes
        } | ConvertTo-Json -Depth 10
        
        Write-Host ""
        Write-Host "============================================" -ForegroundColor Green
        Write-Host "  JSON para GitHub Pages" -ForegroundColor Green
        Write-Host "============================================" -ForegroundColor Green
        Write-Host ""
        Write-Host "Copia este JSON y actualízalo en GitHub Pages:" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "----------------------------------------" -ForegroundColor Gray
        Write-Host $jsonObject -ForegroundColor White
        Write-Host "----------------------------------------" -ForegroundColor Gray
        Write-Host ""
        Write-Host "Instrucciones:" -ForegroundColor Yellow
        Write-Host "1. Sube el APK '$apkFileName' a: https://gankston.github.io/staffaxis-updates/" -ForegroundColor White
        Write-Host "2. Reemplaza el contenido de 'version.json' en GitHub Pages con el JSON de arriba" -ForegroundColor White
        Write-Host ""
    }
} else {
    Write-Host "ERROR: No se encontró el APK compilado en $apkPath" -ForegroundColor Red
    exit 1
}
