# How to Install MySQL JDBC Driver

If you're seeing "No suitable driver found" or other JDBC-related errors when running the game, follow these steps to install the MySQL JDBC driver correctly.

## Option 1: Using IntelliJ IDEA

1. Open Project Structure (File > Project Structure or Ctrl+Alt+Shift+S)
2. Select "Modules" on the left panel
3. Go to the "Dependencies" tab
4. Click the "+" button and select "JARs or directories..."
5. Navigate to the "lib" folder in your project
6. Select "mysql-connector-j-9.2.0.jar"
7. Click "OK" and ensure it appears in the dependency list
8. Apply changes and close the Project Structure dialog

## Option 2: From the Command Line

When running the game from the command line, include the MySQL connector in the classpath:

```bash
# Compile with the connector in the classpath
javac -cp ".;lib/mysql-connector-j-9.2.0.jar" Main.java

# Run with the connector in the classpath
java -cp ".;lib/mysql-connector-j-9.2.0.jar" Main
```

Note: On Linux/Mac, use ":" instead of ";" as the classpath separator:

```bash
javac -cp ".:lib/mysql-connector-j-9.2.0.jar" Main.java
java -cp ".:lib/mysql-connector-j-9.2.0.jar" Main
```

## Option 3: Download the Driver Directly

If the MySQL connector JAR is missing from the lib folder, you can download it:

1. Visit the official MySQL website: https://dev.mysql.com/downloads/connector/j/
2. Download the platform-independent version
3. Extract the .jar file from the download
4. Place it in the "lib" folder of your project

## Option 4: Use the Helper Script

We've included a helper script to check your MySQL setup:

1. Run the "setup_mysql.bat" file in the project directory
2. Follow the instructions to diagnose any issues

## Common Issues

- **MySQL Server not running**: Make sure your MySQL server is started
- **Wrong credentials**: The game uses "root" with no password by default
- **Database doesn't exist**: The game will try to create "azzam_love_db" if it doesn't exist
- **Classpath issues**: Make sure the MySQL connector is properly added to your project's classpath

If you continue to have issues, please consult the "JDBC_SETUP_GUIDE.md" file for more detailed instructions.
