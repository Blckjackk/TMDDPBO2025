@echo off
echo ====================================================
echo   Azzam Love Game - MySQL Driver Setup Helper
echo ====================================================
echo.
echo This script will help you set up the MySQL JDBC driver.
echo.

REM Check if the MySQL connector JAR exists
set JDBC_JAR=lib\mysql-connector-j-9.2.0.jar
if exist %JDBC_JAR% (
    echo [OK] MySQL connector found at %JDBC_JAR%
) else (
    echo [ERROR] MySQL connector JAR not found!
    echo Expected location: %JDBC_JAR%
    echo.
    echo Please make sure the connector JAR is in the lib folder.
    echo You can download it from https://dev.mysql.com/downloads/connector/j/
    echo.
    echo Press any key to exit...
    pause > nul
    exit /b 1
)

REM Check if MySQL server is installed and running
echo.
echo Checking MySQL connection...
echo.

REM Compile and run a test program
javac -cp ".;%JDBC_JAR%" TestDatabaseConnection.java
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed. Make sure Java is installed correctly.
    echo.
    echo Press any key to exit...
    pause > nul
    exit /b 1
)

java -cp ".;%JDBC_JAR%" TestDatabaseConnection

echo.
echo If you saw "Database connection test completed successfully!" above,
echo then your MySQL setup is working correctly!
echo.
echo Otherwise, please check that:
echo 1. MySQL server is installed and running
echo 2. The username (root) and password (empty) are correct
echo 3. The MySQL connector JAR is in the lib folder
echo.
echo Press any key to exit...
pause > nul
