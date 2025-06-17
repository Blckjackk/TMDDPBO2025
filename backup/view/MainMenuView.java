package view;

import model.DatabaseManager;
import model.PlayerResult;
import viewmodel.GameEngine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainMenuView extends JFrame {
    
    // Components
    private JPanel mainPanel;
    private JTextField usernameField;
    private JButton playButton;
    private JButton quitButton;
    private JTable scoreTable;
    private JScrollPane tableScrollPane;
    private JLabel titleLabel;
    private JLabel usernameLabel;
    private JLabel scoreLabel;
    
    // Database manager
    private DatabaseManager databaseManager;
      public MainMenuView() {
        // Set up the frame
        setTitle("Azzam Love - Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Initialize database
        try {
            databaseManager = DatabaseManager.getInstance();
            if (databaseManager != null) {
                databaseManager.initializeDatabase();
            } else {
                showDatabaseError("Failed to initialize database connection.");
            }
        } catch (Exception e) {
            showDatabaseError("Error connecting to database: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Set up UI components
        initializeComponents();
        
        // Load scores
        loadScores();
        
        // Display the frame
        setVisible(true);
    }
    
    private void showDatabaseError(String message) {
        JOptionPane.showMessageDialog(this,
            message + "\n\n" +
            "The application will continue to run, but scores won't be saved.\n" +
            "Please check the JDBC_SETUP_GUIDE.md file for setup instructions.",
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
    
    private void initializeComponents() {
        // Main panel with border layout
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 230, 230)); // Light pink background
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titleLabel = new JLabel("♥ Azzam Love Game ♥");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(220, 20, 60)); // Crimson red
        titlePanel.add(titleLabel);
        
        // Input panel for username
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setOpaque(false);
        usernameLabel = new JLabel("Enter Username:");
        usernameField = new JTextField(15);
        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        playButton = new JButton("Play");
        quitButton = new JButton("Quit");
        
        // Style buttons
        playButton.setBackground(new Color(50, 205, 50)); // Lime Green
        playButton.setForeground(Color.WHITE);
        playButton.setFocusPainted(false);
        
        quitButton.setBackground(new Color(178, 34, 34)); // Firebrick
        quitButton.setForeground(Color.WHITE);
        quitButton.setFocusPainted(false);
        
        buttonPanel.add(playButton);
        buttonPanel.add(quitButton);
        
        // Table panel for scores
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        scoreLabel = new JLabel("High Scores:");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Create table model with column names
        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[][]{}, 
                new String[]{"Username", "Score", "Hearts Collected"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        scoreTable = new JTable(tableModel);
        scoreTable.setFillsViewportHeight(true);
        
        // Set column widths
        scoreTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        scoreTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        scoreTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        
        // Add scroll pane
        tableScrollPane = new JScrollPane(scoreTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 200));
        
        tablePanel.add(scoreLabel, BorderLayout.NORTH);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Add components to main panel
        JPanel topPanel = new JPanel(new GridLayout(3, 1, 5, 10));
        topPanel.setOpaque(false);
        topPanel.add(titlePanel);
        topPanel.add(inputPanel);
        topPanel.add(buttonPanel);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Add action listeners
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        // Add main panel to frame
        setContentPane(mainPanel);
    }
      private void loadScores() {
        try {
            if (databaseManager == null) {
                System.out.println("Warning: Database manager is null, can't load scores");
                return;
            }
            
            ArrayList<PlayerResult> results = databaseManager.getAllResults();
            if (results == null) {
                System.out.println("Warning: No results returned from database");
                return;
            }
            
            DefaultTableModel tableModel = (DefaultTableModel) scoreTable.getModel();
            
            // Clear the table
            tableModel.setRowCount(0);
            
            // Add each result to the table
            for (PlayerResult result : results) {
                tableModel.addRow(new Object[]{
                        result.getUsername(),
                        result.getSkor(),
                        result.getCount()
                });
            }
            
            System.out.println("Loaded " + results.size() + " scores from database");
        } catch (Exception e) {
            System.out.println("Error loading scores: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void startGame() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a username!",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Hide this frame
        setVisible(false);
        
        // Create and show game panel
        GameEngine gameEngine = new GameEngine();
        GamePanel gamePanel = new GamePanel(gameEngine, this);
        
        // Start game with the username
        gameEngine.startGame(username);
    }
    
    // Method to refresh scores when returning from game
    public void refreshScores() {
        loadScores();
    }
}
