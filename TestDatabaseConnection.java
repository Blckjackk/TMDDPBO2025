import java.sql.*;

public class TestDatabaseConnection {
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        try {
            // Print the classpath to see if the MySQL connector is included
            String classpath = System.getProperty("java.class.path");
            System.out.println("Current classpath: " + classpath);
            
            // Try to load the MySQL driver explicitly
            System.out.println("Attempting to load MySQL JDBC driver...");
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("MySQL JDBC driver loaded successfully!");
            } catch (ClassNotFoundException e) {
                System.out.println("ERROR: MySQL JDBC driver not found in classpath!");
                System.out.println("Make sure mysql-connector-j-9.2.0.jar is added to your project dependencies.");
                e.printStackTrace();
                return;
            }
            
            // Try to connect to MySQL
            System.out.println("Attempting to connect to MySQL...");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");
            System.out.println("Connected to MySQL server successfully!");
            
            // Create database if it doesn't exist
            System.out.println("Creating database if it doesn't exist...");
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS azzam_love_db");
            System.out.println("Database created or already exists.");
            
            // Use the database
            System.out.println("Using azzam_love_db database...");
            stmt.executeUpdate("USE azzam_love_db");
            
            // Create table if it doesn't exist
            System.out.println("Creating table if it doesn't exist...");
            String createTable = "CREATE TABLE IF NOT EXISTS thasil (" +
                    "username VARCHAR(100) PRIMARY KEY," +
                    "skor INT NOT NULL," +
                    "count INT NOT NULL)";
            stmt.executeUpdate(createTable);
            System.out.println("Table created or already exists.");
            
            // Close resources
            stmt.close();
            conn.close();
            System.out.println("Database connection test completed successfully!");
            
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            System.out.println("Error details: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
