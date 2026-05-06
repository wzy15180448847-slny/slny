@echo off
chcp 65001 > nul
echo =============================================
echo     房屋租赁平台 - 停止所有服务
echo =============================================
echo.

echo 正在查找并停止相关进程...

REM 停止 MinIO
taskkill /FI "WINDOWTITLE eq MinIO-Server*" /T /F > nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ MinIO 服务已停止
) else (
    echo MinIO 服务未运行或已停止
)

REM 停止后端 (Java/Maven)
taskkill /FI "WINDOWTITLE eq Backend-Server*" /T /F > nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ 后端服务已停止
) else (
    echo 后端服务未运行或已停止
)

REM 停止前端 (Node.js/npm)
taskkill /FI "WINDOWTITLE eq Frontend-Server*" /T /F > nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ 前端服务已停止
) else (
    echo 前端服务未运行或已停止
)

echo.
echo =============================================
echo     所有服务已停止！
echo =============================================
echo.
pause
