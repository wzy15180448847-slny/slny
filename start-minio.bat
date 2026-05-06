@echo off
cd /d D:\MinIO
start "MinIO" cmd /k "minio.exe server minio-data --console-address :9001"
echo MinIO started at http://localhost:9001
pause
