@echo off
echo ===== Compiling AzzamLova Game =====

REM Create build directory if it doesn't exist
if not exist "build\classes" mkdir build\classes

REM Compile all Java files
echo Compiling all Java files...

REM First find all Java files and save them to a temporary file
dir /s /b *.java > java_files.txt

REM Then compile them all at once with the correct classpath
javac -cp ".;lib\mysql-connector-j-9.2.0.jar" -d "build\classes" @java_files.txt

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    REM Remove the temporary file
    del java_files.txt
    echo.
    echo To run the game, type: run_game.bat
) else (
    echo Compilation failed.
    del java_files.txt
)

pause
