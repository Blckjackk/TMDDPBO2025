@echo off
echo Compiling AzzamLova Game...
javac -cp ".;lib/mysql-connector-j-9.2.0.jar" Main.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful! Starting game...
    java -cp ".;lib/mysql-connector-j-9.2.0.jar" Main
) else (
    echo Compilation failed.
    pause
)
