@echo off
chcp 65001 > nul
echo =============================================
echo     房屋租赁平台 - 全服务启动脚本
echo =============================================
echo.

REM 设置工作目录
set PROJECT_DIR=%~dp0
set MINIO_PATH=D:\MinIO\minio.exe
set MINIO_DATA=D:\MinIO\minio-data

echo [1/4] 检查 MinIO...
if not exist "%MINIO_PATH%" (
    echo 错误: 找不到 MinIO 可执行文件: %MINIO_PATH%
    echo 请确认 MinIO 已正确安装在 D:\MinIO 目录
    pause
    exit /b 1
)
echo ✓ MinIO 可执行文件已找到

echo.
echo [2/4] 启动 MinIO...
cd /d D:\MinIO
start "MinIO-Server" cmd /k "%MINIO_PATH% server %MINIO_DATA% --console-address :9001"

echo ✓ MinIO 已启动
echo   - 访问地址: http://localhost:9000
echo   - 控制台: http://localhost:9001
echo   - 用户名: minioadmin
echo   - 密码: minioadmin

echo.
echo 等待 3 秒让 MinIO 完全启动...
timeout /t 3 /nobreak > nul

echo.
echo [3/4] 启动后端服务...
cd /d "%PROJECT_DIR%house-rental-system"
start "Backend-Server" cmd /k "mvn spring-boot:run"

echo ✓ 后端服务已启动
echo   - 访问地址: http://localhost:8080

echo.
echo [4/4] 启动前端服务...
cd /d "%PROJECT_DIR%frontend-vue"
start "Frontend-Server" cmd /k "npm run dev"

echo ✓ 前端服务已启动
echo   - 访问地址: http://localhost:3000

echo.
echo =============================================
echo     所有服务启动完成！
echo =============================================
echo.
echo 服务列表:
echo   ✓ MinIO          - http://localhost:9001
echo   ✓ 后端服务       - http://localhost:8080
echo   ✓ 前端服务       - http://localhost:3000
echo.
echo 提示: 关闭此窗口不会影响正在运行的服务
echo       如需停止，请分别关闭各个服务的窗口
echo.
pause
