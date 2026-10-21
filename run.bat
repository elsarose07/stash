@echo off
echo ============================================================
echo Launching Employee Management System...
echo ============================================================

java -cp "bin;lib\mysql-connector-j-8.3.0.jar" com.ems.main.Main

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Application terminated with error code %ERRORLEVEL%.
    pause
)
