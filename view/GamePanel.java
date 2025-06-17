package view;

import viewmodel.GameEngine;
import viewmodel.InputController;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import javax.sound.sampled.*;
import java.io.File;

public class GamePanel extends JPanel implements ActionListener {
    // Constants
    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;
    private static final int FPS = 60;
    
    // References
    private GameEngine gameEngine;
    private InputController inputController;
    private MainMenuView mainMenuView;
    
    // Timer for game loop
    private Timer gameTimer;
    
    // Images
    private BufferedImage backgroundImage;
    private BufferedImage[] playerImages; // Different emotion states
    private BufferedImage girlImage;
    private HashMap<Integer, BufferedImage> heartImages;
    private BufferedImage ropeImage;
    
    // Sound effects
    private Clip backgroundMusic;
    private Clip achievementSound;
    private Clip gameOverSound;
    private Clip gameStartSound;
    private Clip level30Sound;
    private Clip level50Sound;
    private Clip level100Sound;
    private int lastEmotionState = -1;
    
    // Flag to track if achievement sound was recently played
    private long lastAchievementSoundTime = 0;
    
    public GamePanel(GameEngine gameEngine, MainMenuView mainMenuView) {
        this.gameEngine = gameEngine;
        this.mainMenuView = mainMenuView;
        
        // Set up panel
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setFocusable(true);
        
        // Load assets
        loadImages();
        loadSounds();
        
        // Set up input controller
        inputController = new InputController(gameEngine);
        addKeyListener(inputController);
        addMouseListener(inputController);
        
        // Create game frame
        JFrame gameFrame = new JFrame("Azzam Love - Game");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setResizable(false);
        gameFrame.add(this);
        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
        
        // Start game timer
        gameTimer = new Timer(1000 / FPS, this);
        gameTimer.start();
        
        // Play start sound and background music
        playSound(gameStartSound);
        playBackgroundMusic();
    }
    
    private void loadImages() {
        try {
            // Load background
            backgroundImage = ImageIO.read(new File("assets/background taman.png"));
            
            // Load player images for different emotions
            playerImages = new BufferedImage[4];
            playerImages[0] = ImageIO.read(new File("assets/Azzam Berjalan.png")); // Normal
            playerImages[1] = ImageIO.read(new File("assets/Azzam Senang.png"));   // Happy (score >= 30)
            playerImages[2] = ImageIO.read(new File("assets/Azzam Nahh Ituu.png")); // Excited (score >= 50)
            playerImages[3] = ImageIO.read(new File("assets/Azzam Tertawa.png"));   // Laughing (score >= 100)
            
            // Load girl
            girlImage = ImageIO.read(new File("assets/Perempuan Senang.png"));
            
            // Load heart images
            heartImages = new HashMap<>();
            heartImages.put(0, ImageIO.read(new File("assets/Hati Biru.png"))); // Blue - 3 points
            heartImages.put(1, ImageIO.read(new File("assets/Hati Hijau.png"))); // Green - 4 points
            heartImages.put(2, ImageIO.read(new File("assets/Hati Kuning.png"))); // Yellow - 5 points
            heartImages.put(3, ImageIO.read(new File("assets/Hati Merah.png"))); // Red - 6 points
            heartImages.put(4, ImageIO.read(new File("assets/Hati Orange.png"))); // Orange - 7 points
            heartImages.put(5, ImageIO.read(new File("assets/Hati Ungu.png"))); // Purple - 2 points
            
            // Load rope image for lasso
            ropeImage = ImageIO.read(new File("assets/tali cinta.png"));
            
        } catch (IOException e) {
            System.out.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadSounds() {
        try {
            // Load background music
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("sounds/game_soundtrack.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            
            // Load achievement sound (heart reaching girl)
            audioStream = AudioSystem.getAudioInputStream(new File("sounds/game_bonus.wav"));
            achievementSound = AudioSystem.getClip();
            achievementSound.open(audioStream);
            
            // Load game over sound
            audioStream = AudioSystem.getAudioInputStream(new File("sounds/game_over.wav"));
            gameOverSound = AudioSystem.getClip();
            gameOverSound.open(audioStream);
            
            // Load game start sound
            audioStream = AudioSystem.getAudioInputStream(new File("sounds/game_start.wav"));
            gameStartSound = AudioSystem.getClip();
            gameStartSound.open(audioStream);
            
        } catch (Exception e) {
            System.out.println("Error loading sounds: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void playSound(Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }
    
    private void playBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    
    private void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Create graphics object for better rendering
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw background
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, null);
        } else {
            // Fallback background
            g2d.setColor(new Color(230, 255, 230));
            g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        }
        
        // Draw game objects if game is running
        if (gameEngine.isRunning()) {
            // Draw lasso if active
            GameEngine.Lasso lasso = gameEngine.getLasso();
            if (lasso != null) {
                Point start = lasso.getStartPosition();
                Point end = lasso.getCurrentPosition();
                
                // Draw rope line (with some thickness)
                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(Color.RED);
                g2d.drawLine(start.x, start.y, end.x, end.y);
                
                // Draw rope image at the end
                if (ropeImage != null) {
                    g2d.drawImage(ropeImage, end.x - 15, end.y - 15, 30, 30, null);
                }
            }
            
            // Draw hearts
            for (GameEngine.Heart heart : gameEngine.getHearts()) {
                Point pos = heart.getPosition();
                BufferedImage heartImage = heartImages.get(heart.getType());
                
                if (heartImage != null) {
                    g2d.drawImage(heartImage, pos.x - 25, pos.y - 25, 50, 50, null);
                } else {
                    // Fallback heart drawing
                    g2d.setColor(Color.RED);
                    g2d.fillOval(pos.x - 15, pos.y - 15, 30, 30);
                }
            }
            
            // Draw girl (target for hearts)
            Point girlPos = gameEngine.getGirlPosition();
            if (girlImage != null) {
                g2d.drawImage(girlImage, girlPos.x - 40, girlPos.y - 50, 80, 100, null);
            } else {
                // Fallback girl drawing
                g2d.setColor(Color.PINK);
                g2d.fillRect(girlPos.x - 20, girlPos.y - 30, 40, 60);
            }
            
            // Draw player (Azzam) with correct emotion and direction
            Point playerPos = gameEngine.getPlayerPosition();
            if (playerImages != null) {
                int emotionState = gameEngine.getEmotionState();
                BufferedImage playerImage = playerImages[emotionState];
                
                if (playerImage != null) {
                    // Check if we need to mirror the image
                    if (!gameEngine.isFacingRight()) {
                        // Draw mirrored image
                        g2d.translate(playerPos.x, playerPos.y);
                        g2d.scale(-1, 1);
                        g2d.drawImage(playerImage, -40, -50, 80, 100, null);
                        g2d.scale(-1, 1);
                        g2d.translate(-playerPos.x, -playerPos.y);
                    } else {
                        // Draw normal image
                        g2d.drawImage(playerImage, playerPos.x - 40, playerPos.y - 50, 80, 100, null);
                    }
                }
            } else {
                // Fallback player drawing
                g2d.setColor(Color.BLUE);
                g2d.fillRect(playerPos.x - 20, playerPos.y - 30, 40, 60);
            }
            
            // Draw timer
            long timeRemaining = gameEngine.getTimeRemaining();
            int seconds = (int)(timeRemaining / 1000);
            int milliseconds = (int)(timeRemaining % 1000 / 10);
            
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.drawString(String.format("Time: %02d:%02d", seconds, milliseconds), PANEL_WIDTH - 150, 30);
            
            // Draw score and hearts collected
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("Score: " + gameEngine.getScore(), 20, 30);
            g2d.drawString("Hearts: " + gameEngine.getHeartsCollected(), 20, 60);
            
            // Draw instructions
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            g2d.drawString("Use arrow keys to move", 20, PANEL_HEIGHT - 40);
            g2d.drawString("Click to throw lasso", 20, PANEL_HEIGHT - 20);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // Check if game is still running
        if (gameEngine.isRunning()) {
            // Process input
            inputController.processInput();
            
            // Update game state
            gameEngine.update();
            
            // Check for emotion state change for sound effects
            int currentEmotionState = gameEngine.getEmotionState();
            if (currentEmotionState != lastEmotionState) {
                // Play sound effects for reaching score milestones
                switch (currentEmotionState) {
                    case 1: // Score >= 30
                        // Could play special sound here if available
                        break;
                    case 2: // Score >= 50
                        // Could play special sound here if available
                        break;
                    case 3: // Score >= 100
                        // Could play special sound here if available
                        break;
                }
                lastEmotionState = currentEmotionState;
            }
            
            // Check if a heart has reached the girl (for achievement sound)
            if (gameEngine.isHeartReachedGirl()) {
                // Don't play achievement sounds too close together
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastAchievementSoundTime > 500) { // 500ms cooldown
                    playSound(achievementSound);
                    lastAchievementSoundTime = currentTime;
                }
            }
            
            // Repaint
            repaint();
        } else {
            // Game is over, stop timer and return to menu
            stopBackgroundMusic();
            playSound(gameOverSound);
            gameTimer.stop();
            
            // Show final score before returning to menu
            JFrame gameFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            JOptionPane.showMessageDialog(gameFrame, 
                "Time's up! Your final score: " + gameEngine.getScore() + 
                "\nHearts collected: " + gameEngine.getHeartsCollected(),
                "Game Over", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Close game window
            gameFrame.dispose();
            
            // Refresh scores and show menu
            mainMenuView.refreshScores();
            mainMenuView.setVisible(true);
        }
    }
}
