package view;

import model.DatabaseManager;
import model.PlayerResult;
import viewmodel.GameEngine;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
    
    // Images
    private BufferedImage backgroundImage;
    private BufferedImage azzamImage;
    private BufferedImage girlImage;
    private BufferedImage[] heartImages;
    
    // Database manager
    private DatabaseManager databaseManager;
    
    // Background Panel with image
    class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                // Draw background image scaled to fit panel
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            }
            
            // Draw character images
            if (azzamImage != null) {
                g.drawImage(azzamImage, 40, getHeight() - 180, 120, 120, null);
            }
            
            if (girlImage != null) {
                g.drawImage(girlImage, getWidth() - 160, getHeight() - 180, 120, 120, null);
            }
            
            // Draw hearts in a decorative pattern
            if (heartImages != null) {
                // Draw hearts around the top of the frame
                for (int i = 0; i < 5; i++) {
                    BufferedImage heart = heartImages[i % heartImages.length];
                    int x = 100 + i * 100;
                    int y = 20 + (i % 2) * 20;  // Alternate height for visual interest
                    g.drawImage(heart, x, y, 40, 40, null);
                }
            }
        }
    }
    
    public MainMenuView() {
        // Set up the frame
        setTitle("Azzam Love - Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);  // Larger size for more visual impact
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Load images
        loadImages();
        
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
    
    private void loadImages() {
        try {
            // Load background image
            backgroundImage = ImageIO.read(new File("assets/background taman.png"));
            
            // Load character images
            azzamImage = ImageIO.read(new File("assets/Azzam Senang.png"));
            girlImage = ImageIO.read(new File("assets/Perempuan Cinta.png"));
            
            // Load heart images
            heartImages = new BufferedImage[6];
            heartImages[0] = ImageIO.read(new File("assets/Hati Biru.png"));
            heartImages[1] = ImageIO.read(new File("assets/Hati Hijau.png"));
            heartImages[2] = ImageIO.read(new File("assets/Hati Kuning.png"));
            heartImages[3] = ImageIO.read(new File("assets/Hati Merah.png"));
            heartImages[4] = ImageIO.read(new File("assets/Hati Orange.png"));
            heartImages[5] = ImageIO.read(new File("assets/Hati Ungu.png"));
            
        } catch (IOException e) {
            System.out.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }
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
        // Create background panel with custom painting
        mainPanel = new BackgroundPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(80, 30, 30, 30));
        
        // Create a semi-transparent panel for content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(15, 15));
        contentPanel.setBackground(new Color(255, 255, 255, 180));
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 105, 180), 3, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Title panel with decorative elements
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        
        titleLabel = new JLabel("♥ Azzam Love Game ♥");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 32));
        titleLabel.setForeground(new Color(220, 20, 60)); // Crimson red
        
        // Add drop shadow effect to title
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        titlePanel.add(titleLabel);
        
        // Input panel for username with styled components
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setOpaque(false);
        usernameLabel = new JLabel("Enter Your Name:");
        usernameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        usernameLabel.setForeground(new Color(75, 0, 130)); // Indigo
        
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setMargin(new Insets(5, 7, 5, 7));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 105, 180), 2, true),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        
        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);
        
        // Button panel with styled buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        buttonPanel.setOpaque(false);
        
        // Styled Play button
        playButton = new JButton("Play Game");
        playButton.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        playButton.setForeground(Color.WHITE);
        playButton.setBackground(new Color(50, 205, 50)); // Lime Green
        playButton.setFocusPainted(false);
        playButton.setBorder(BorderFactory.createRaisedBevelBorder());
        playButton.setPreferredSize(new Dimension(120, 40));
        
        // Add heart icon to play button
        try {
            BufferedImage heartIcon = ImageIO.read(new File("assets/Hati Merah.png"));
            Image scaledHeart = heartIcon.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            playButton.setIcon(new ImageIcon(scaledHeart));
            playButton.setIconTextGap(10);
        } catch (Exception e) {
            System.out.println("Could not load heart icon for button");
        }
        
        // Styled Quit button
        quitButton = new JButton("Quit");
        quitButton.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        quitButton.setForeground(Color.WHITE);
        quitButton.setBackground(new Color(178, 34, 34)); // Firebrick
        quitButton.setFocusPainted(false);
        quitButton.setBorder(BorderFactory.createRaisedBevelBorder());
        quitButton.setPreferredSize(new Dimension(120, 40));
        
        buttonPanel.add(playButton);
        buttonPanel.add(quitButton);
        
        // Table panel for scores with styled components
        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setOpaque(false);
        
        scoreLabel = new JLabel("♥ Top Scores ♥");
        scoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        scoreLabel.setForeground(new Color(128, 0, 128)); // Purple
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        // Create table model with column names
        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[][]{}, 
                new String[]{"Player Name", "Score", "Hearts Collected"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        scoreTable = new JTable(tableModel);
        scoreTable.setFillsViewportHeight(true);
        scoreTable.setRowHeight(25);
        scoreTable.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Style the table header
        JTableHeader header = scoreTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(255, 182, 193)); // Light pink
        header.setForeground(new Color(128, 0, 128)); // Purple
        
        // Style the table appearance
        scoreTable.setSelectionBackground(new Color(255, 182, 193, 180)); // Light pink with transparency
        scoreTable.setSelectionForeground(new Color(75, 0, 130)); // Indigo
        scoreTable.setGridColor(new Color(255, 182, 193)); // Light pink
        
        // Set column widths
        scoreTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        scoreTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        scoreTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        
        // Add scroll pane with styled border
        tableScrollPane = new JScrollPane(scoreTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 200));
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 105, 180), 2, true));
        
        tablePanel.add(scoreLabel, BorderLayout.NORTH);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Build the top content panel
        JPanel topContentPanel = new JPanel(new GridLayout(3, 1, 5, 15));
        topContentPanel.setOpaque(false);
        topContentPanel.add(titlePanel);
        topContentPanel.add(inputPanel);
        topContentPanel.add(buttonPanel);
        
        // Add components to content panel
        contentPanel.add(topContentPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Add content panel to main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
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
        
        // Button hover effects
        playButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                playButton.setBackground(new Color(60, 240, 60));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                playButton.setBackground(new Color(50, 205, 50));
            }
        });
        
        quitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                quitButton.setBackground(new Color(220, 40, 40));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                quitButton.setBackground(new Color(178, 34, 34));
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
