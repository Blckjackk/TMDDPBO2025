package model;

import java.sql.*;
import java.util.ArrayList;
import java.io.File;

public class DatabaseManager {
    private Connection connection;
    private PreparedStatement statement;
    private static DatabaseManager instance;

    // Constructor - private for Singleton pattern
    private DatabaseManager() {
        try {
            // Check if the MySQL connector JAR exists
            File jarFile = new File("lib/mysql-connector-j-9.2.0.jar");
            if (!jarFile.exists()) {
                System.out.println("Warning: MySQL connector JAR not found at: " + jarFile.getAbsolutePath());
                System.out.println("Checking classpath...");
            }
            
            // Explicitly load the MySQL JDBC driver
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("MySQL JDBC driver loaded successfully!");
            } catch (ClassNotFoundException e) {
                System.out.println("ERROR: MySQL JDBC driver not found in classpath.");
                System.out.println("Please make sure mysql-connector-j-9.2.0.jar is properly added to your project dependencies.");
                e.printStackTrace();
                return; // Exit constructor since we can't continue without the driver
            }
            
            // Try to establish connection
            try {
                // First, check if server is reachable
                Connection serverConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");
                System.out.println("Connected to MySQL server successfully!");
                
                // Create database if it doesn't exist
                Statement stmt = serverConn.createStatement();
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS azzam_love_db");
                
                // Close server connection
                stmt.close();
                serverConn.close();
                
                // Connect to the specific database
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/azzam_love_db", "root", "");
                System.out.println("Database connection established successfully to azzam_love_db");
            } catch (SQLException e) {
                System.out.println("Database connection error: " + e.getMessage());
                System.out.println("Please ensure your MySQL server is running and accessible.");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Unexpected error in DatabaseManager: " + e.getMessage());
            e.printStackTrace();
        }
    }    // Singleton pattern to ensure only one database connection
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        } else {
            // Check if connection is still alive
            try {
                if (instance.connection == null || instance.connection.isClosed()) {
                    System.out.println("Database connection was closed, creating a new one");
                    instance = new DatabaseManager();
                }
            } catch (SQLException e) {
                System.out.println("Error checking database connection: " + e.getMessage());
                // Try to create a new instance
                instance = new DatabaseManager();
            }
        }
        return instance;
    }
    
    // Create table if not exists
    public void initializeDatabase() {
        if (connection == null) {
            System.out.println("Cannot initialize database - no connection available");
            return;
        }
        
        try {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS thasil (" +
                    "username VARCHAR(100) PRIMARY KEY," +
                    "skor INT NOT NULL," +
                    "count INT NOT NULL" +
                    ")";
            statement = connection.prepareStatement(createTableSQL);
            statement.executeUpdate();
            System.out.println("Database table initialized successfully");
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Get all player results
    public ArrayList<PlayerResult> getAllResults() {
        ArrayList<PlayerResult> results = new ArrayList<>();
        
        if (connection == null) {
            System.out.println("Cannot get results - no connection available");
            return results;
        }
        
        try {
            String query = "SELECT * FROM thasil ORDER BY skor DESC";
            statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            
            while (rs.next()) {
                String username = rs.getString("username");
                int skor = rs.getInt("skor");
                int count = rs.getInt("count");
                
                results.add(new PlayerResult(username, skor, count));
            }
        } catch (SQLException e) {
            System.out.println("Error getting results: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }    // Save player result (insert new or update if exists)
    public void savePlayerResult(PlayerResult playerResult) {
        if (connection == null) {
            System.out.println("Cannot save result - no connection available, trying to reconnect...");
            try {
                // Try to reconnect
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/azzam_love_db", "root", "");
                System.out.println("Reconnected to database successfully");
            } catch (SQLException reconnectEx) {
                System.out.println("Failed to reconnect to database: " + reconnectEx.getMessage());
                return;
            }
        }
        
        try {
            // Make sure the table exists
            initializeDatabase();
            
            // Print debugging information
            System.out.println("===== DATABASE SAVE OPERATION =====");
            System.out.println("Saving player: " + playerResult.getUsername());
            System.out.println("Score: " + playerResult.getSkor());
            System.out.println("Hearts: " + playerResult.getCount());
            
            // Check if username already exists
            String checkQuery = "SELECT * FROM thasil WHERE username = ?";
            statement = connection.prepareStatement(checkQuery);
            statement.setString(1, playerResult.getUsername());
            ResultSet rs = statement.executeQuery();
            
            if (rs.next()) {
                // Get existing score and hearts
                int existingSkor = rs.getInt("skor");
                int existingHearts = rs.getInt("count");
                
                System.out.println("Player already exists in database");
                System.out.println("Existing score: " + existingSkor);
                System.out.println("Existing hearts: " + existingHearts);
                
                // Only update if new score is higher
                if (playerResult.getSkor() > existingSkor) {
                    // Update existing record with higher score
                    String updateQuery = "UPDATE thasil SET skor = ?, count = ? WHERE username = ?";
                    statement = connection.prepareStatement(updateQuery);
                    statement.setInt(1, playerResult.getSkor());
                    statement.setInt(2, playerResult.getCount());
                    statement.setString(3, playerResult.getUsername());
                    
                    int rowsAffected = statement.executeUpdate();
                    System.out.println("Updated record! Rows affected: " + rowsAffected);
                    System.out.println("Updated player record: " + playerResult.getUsername() + 
                                    " with higher score: " + playerResult.getSkor() + 
                                    " (previous: " + existingSkor + ")");
                } else {
                    // No update needed
                    System.out.println("No update needed - current score is better");
                }
            } else {
                // Insert new record
                String insertQuery = "INSERT INTO thasil (username, skor, count) VALUES (?, ?, ?)";
                statement = connection.prepareStatement(insertQuery);
                statement.setString(1, playerResult.getUsername());
                statement.setInt(2, playerResult.getSkor());
                statement.setInt(3, playerResult.getCount());
                
                int rowsAffected = statement.executeUpdate();
                System.out.println("New record created! Rows affected: " + rowsAffected);
                System.out.println("Inserted new player record: " + playerResult.getUsername() + 
                                " with score: " + playerResult.getSkor());
            }
            System.out.println("===== DATABASE OPERATION COMPLETED =====");
        } catch (SQLException e) {
            System.out.println("Error saving player result: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Close connection on application exit
    public void closeConnection() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
