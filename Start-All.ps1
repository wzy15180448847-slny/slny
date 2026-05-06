# House Rental Platform - Start All Services
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  House Rental Platform - Start All" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if MinIO exists
$minioPath = "D:\MinIO\minio.exe"
if (-not (Test-Path $minioPath)) {
    Write-Host "ERROR: MinIO not found at $minioPath" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host "[1/4] MinIO found..." -ForegroundColor Green

# Start MinIO
Write-Host ""
Write-Host "[2/4] Starting MinIO..." -ForegroundColor Yellow
Set-Location "D:\MinIO"
Start-Process cmd -ArgumentList "/k minio.exe server minio-data --console-address :9001" -WindowStyle Normal
Write-Host "  MinIO started!" -ForegroundColor Green
Write-Host "  Console: http://localhost:9001" -ForegroundColor Gray
Write-Host "  Username: minioadmin" -ForegroundColor Gray
Write-Host "  Password: minioadmin" -ForegroundColor Gray

# Wait a bit
Start-Sleep -Seconds 3

# Start Backend
Write-Host ""
Write-Host "[3/4] Starting Backend..." -ForegroundColor Yellow
$projectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location "$projectDir\house-rental-system"
Start-Process cmd -ArgumentList "/k mvn spring-boot:run" -WindowStyle Normal
Write-Host "  Backend started!" -ForegroundColor Green
Write-Host "  URL: http://localhost:8080" -ForegroundColor Gray

# Start Frontend
Write-Host ""
Write-Host "[4/4] Starting Frontend..." -ForegroundColor Yellow
Set-Location "$projectDir\frontend-vue"
Start-Process cmd -ArgumentList "/k npm run dev" -WindowStyle Normal
Write-Host "  Frontend started!" -ForegroundColor Green
Write-Host "  URL: http://localhost:3000" -ForegroundColor Gray

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  All services started!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Services:"
Write-Host "  - MinIO:   http://localhost:9001" -ForegroundColor White
Write-Host "  - Backend: http://localhost:8080" -ForegroundColor White
Write-Host "  - Frontend: http://localhost:3000" -ForegroundColor White
Write-Host ""
Write-Host "Tip: Close this window, it won't stop the services" -ForegroundColor Gray
Write-Host ""
Read-Host "Press Enter to close this window"
