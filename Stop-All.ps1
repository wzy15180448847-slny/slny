# House Rental Platform - Stop All Services
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  House Rental Platform - Stop All" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Stopping services..." -ForegroundColor Yellow

# Stop MinIO, Java, Node processes
$stopped = @()
Get-Process | Where-Object {$_.ProcessName -like "*minio*" -or $_.ProcessName -eq "java" -or $_.ProcessName -eq "node"} | ForEach-Object {
    Stop-Process -Id $_.Id -Force -ErrorAction SilentlyContinue
    $stopped += "$($_.ProcessName) (PID: $($_.Id))"
}

if ($stopped.Count -gt 0) {
    Write-Host ""
    Write-Host "Stopped processes:" -ForegroundColor Green
    $stopped | ForEach-Object { Write-Host "  - $_" -ForegroundColor White }
} else {
    Write-Host "No matching processes found or already stopped." -ForegroundColor Gray
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Done!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Read-Host "Press Enter to exit"
