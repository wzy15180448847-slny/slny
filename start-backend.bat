@echo off
echo =============================================
echo 房屋租赁平台 - 后端服务启动脚本
echo =============================================
echo.

REM 设置JVM内存参数
set JAVA_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200

echo JVM配置: %JAVA_OPTS%
echo.

cd house-rental-system

REM 编译并启动服务
echo 正在编译项目...
mvn compile -q
if %errorlevel% neq 0 (
    echo 编译失败!
    pause
    exit /b 1
)

echo 编译成功!
echo.
echo 正在启动后端服务...
echo.

mvn spring-boot:run -Dspring-boot.run.jvmArguments="%JAVA_OPTS%"