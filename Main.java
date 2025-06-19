import javax.swing.*;
import java.io.File;

import model.DatabaseManager;
import view.MainMenuView;

import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Azzam Love Game...");
        
        // Check if MySQL connector exists in lib folder
        File mysqlConnector = new File("lib/mysql-connector-j-9.2.0.jar");
        if (!mysqlConnector.exists()) {
            System.out.println("WARNING: MySQL connector JAR not found in lib folder!");
            System.out.println("Expected path: " + mysqlConnector.getAbsolutePath());
            
            // Check if it's in the classpath
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("MySQL driver found in classpath!");
            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null,
                    "MySQL JDBC driver not found!\n\n" +
                    "Please ensure the MySQL connector JAR is added to your project.\n" +
                    "Go to File > Project Structure > Modules > Dependencies\n" +
                    "and add mysql-connector-j-9.2.0.jar from the lib folder.",
                    "Database Driver Error",
                    JOptionPane.ERROR_MESSAGE);
                return; // Exit application
            }
        }
        
        // Initialize DatabaseManager early to ensure database is created
        try {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            if (dbManager != null) {
                dbManager.initializeDatabase();
                System.out.println("Database initialized in main application");
            }
        } catch (Exception e) {
            System.out.println("Error initializing database in main: " + e.getMessage());
        }
        
        // Start application on Swing Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("Launching main menu...");
                new MainMenuView();
            }
        });
    }
}
