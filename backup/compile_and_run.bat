@echo off
echo ===== Compiling AzzamLova Game =====

REM Create build directory if it doesn't exist
if not exist "build\classes" mkdir build\classes

REM Compile all Java files
echo Compiling all Java files...
javac -cp ".;lib\mysql-connector-j-9.2.0.jar" -d "build\classes" Main.java model\*.java view\*.java viewmodel\*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo.
    echo ===== Running AzzamLova Game =====
    java -cp "build\classes;lib\mysql-connector-j-9.2.0.jar" Main
) else (
    echo Compilation failed.
    pause
)
