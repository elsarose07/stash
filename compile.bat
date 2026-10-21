@echo off
echo ============================================================
echo Compiling Employee Management System...
echo ============================================================

if not exist bin mkdir bin

javac -encoding UTF-8 -d bin -cp "lib\mysql-connector-j-8.3.0.jar;src" src\com\ems\model\*.java src\com\ems\exception\*.java src\com\ems\util\*.java src\com\ems\dao\*.java src\com\ems\ui\*.java src\com\ems\ui\admin\*.java src\com\ems\ui\employee\*.java src\com\ems\main\*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCCESS] Compilation finished successfully!
    echo Classes compiled to 'bin' directory.
    echo Run the application using: run.bat
) else (
    echo.
    echo [ERROR] Compilation failed. Please check the error messages above.
)
pause
