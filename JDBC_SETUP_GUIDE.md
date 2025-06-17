# JDBC Driver Setup Guide for IntelliJ IDEA

If you're experiencing issues with the MySQL JDBC driver, follow these steps to properly set it up in IntelliJ IDEA:

## Step 1: Check the MySQL Connector JAR

First, verify that the MySQL connector JAR file exists in the `lib` folder:
- Look for `mysql-connector-j-9.2.0.jar` in the `lib` directory
- If it's missing, you can download it from the [MySQL website](https://dev.mysql.com/downloads/connector/j/)

## Step 2: Add the JAR to Project Dependencies

1. Open your project in IntelliJ IDEA
2. Go to **File → Project Structure** (or press `Ctrl+Alt+Shift+S`)
3. Select **Modules** in the left panel
4. Go to the **Dependencies** tab
5. Click the **+** button and select **JARs or directories**
6. Navigate to the `lib` folder in your project and select `mysql-connector-j-9.2.0.jar`
7. Click **OK** to add it to your project dependencies
8. Make sure the scope is set to **Compile** or **Runtime**
9. Click **Apply** and then **OK**

## Step 3: Verify MySQL Server Connection

1. Make sure your MySQL server is installed and running
2. By default, this application tries to connect with username **root** and no password
3. If your MySQL setup uses different credentials, update the code in `DatabaseManager.java`

## Step 4: Run the Setup Helper

1. Double-click the `setup_mysql.bat` file in the project directory
2. This will test your MySQL connection and create the necessary database and tables

## Step 5: Run the Application Again

After completing the above steps, try running the game again:
1. In IntelliJ IDEA, right-click on `Main.java`
2. Select **Run 'Main.main()'**

## Troubleshooting

If you still encounter issues:

1. **Check MySQL Server**: 
   - Make sure MySQL service is running
   - Try connecting with a MySQL client like MySQL Workbench

2. **Classpath Issues**:
   - Run the application with explicit classpath: 
     ```
     java -cp ".;lib/mysql-connector-j-9.2.0.jar" Main
     ```

3. **Database Creation**:
   - Try manually creating the database:
     ```sql
     CREATE DATABASE IF NOT EXISTS azzam_love_db;
     USE azzam_love_db;
     CREATE TABLE thasil (
       username VARCHAR(100) PRIMARY KEY,
       skor INT NOT NULL,
       count INT NOT NULL
     );
     ```

4. **Driver Version Issues**:
   - If using a different version of MySQL, make sure the connector version is compatible

5. **Error Logging**:
   - Review the console output for specific error messages
   - Check the MySQL error log for server-side issues
