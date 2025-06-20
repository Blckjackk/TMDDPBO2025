package view;

import model.AudioPlayer;
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
import java.io.File;
import java.lang.reflect.Method;

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
    
    // Last emotion state for sound effects
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
        
        // Call this method to verify GameEngine methods are available
        ensureGameEngineMethods();
        
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
        playSound("game_start");
        playInGameMusic();
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
    }      private void loadSounds() {
        try {
            // Dapatkan instance AudioPlayer dan pastikan suara diinisialisasi dengan benar
            AudioPlayer audioPlayer = AudioPlayer.getInstance();
            
            // Output debug untuk memastikan sistem audio berjalan
            System.out.println("\n=== GAME SOUNDS INITIALIZATION ===");
            System.out.println("AudioPlayer instance: " + (audioPlayer != null ? "OK" : "NULL"));
            System.out.println("Sound enabled: " + audioPlayer.isSoundEnabled());
            
            // Verifikasi library MP3
            verifyMP3Libraries();
            
            // Verifikasi keberadaan file audio
            verifyAudioFiles();
            
            System.out.println("=================================\n");
        } catch (Exception e) {
            System.out.println("Error initializing audio system: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Method untuk memeriksa apakah library MP3 tersedia
    private void verifyMP3Libraries() {
        System.out.println("Checking MP3 libraries...");
        
        boolean mp3LibsFound = false;
        
        // Check for MP3SPI in classpath
        try {
            Class.forName("javazoom.spi.mpeg.sampled.file.MpegAudioFileReader");
            System.out.println("MP3SPI library found - MP3 support should be available");
            mp3LibsFound = true;
        } catch (ClassNotFoundException e) {
            System.out.println("MP3SPI library not found in classpath");
        }
        
        // Check for JLayer in classpath
        try {
            Class.forName("javazoom.jl.decoder.Decoder");
            System.out.println("JLayer library found");
        } catch (ClassNotFoundException e) {
            System.out.println("JLayer library not found in classpath");
        }
        
        if (!mp3LibsFound) {
            System.out.println("WARNING: MP3 support libraries not found!");
            System.out.println("To enable MP3 support, run download_mp3_libs.bat and then recompile the game");
            System.out.println("Alternatively, run convert_mp3_to_wav.bat to create WAV versions of the sounds");
        }
    }
      // Method untuk memeriksa keberadaan file audio
    private void verifyAudioFiles() {
        System.out.println("Verifying audio files...");
        
        // Check MP3 files in assets folder
        boolean mp3Found = checkAudioFile("assets/sound game start.mp3", "Game Start Sound (MP3)");
        mp3Found &= checkAudioFile("assets/sound ingame.mp3", "In-Game Music (MP3)");
        mp3Found &= checkAudioFile("assets/sound achivement.mp3", "Achievement Sound (MP3)");
        mp3Found &= checkAudioFile("assets/sound berubah.mp3", "Character Change Sound (MP3)");
        
        // If MP3 files are missing or if we want to check WAV fallbacks
        if (!mp3Found) {
            System.out.println("\nChecking WAV fallbacks in sounds folder...");
            boolean wavFound = checkAudioFile("sounds/sound game start.wav", "Game Start Sound (WAV)");
            wavFound &= checkAudioFile("sounds/sound ingame.wav", "In-Game Music (WAV)");
            wavFound &= checkAudioFile("sounds/sound achivement.wav", "Achievement Sound (WAV)");
            wavFound &= checkAudioFile("sounds/sound berubah.wav", "Character Change Sound (WAV)");
            
            if (!wavFound) {
                System.out.println("\nWARNING: Neither MP3 nor WAV files were found completely!");
                System.out.println("Run convert_mp3_to_wav.bat to convert your MP3 files to WAV format");
                System.out.println("or download_mp3_libs.bat to add MP3 support libraries");
            }
        }
        
        System.out.println("Audio file verification complete");
    }
    
    // Helper method untuk memeriksa keberadaan file audio
    private boolean checkAudioFile(String path, String description) {
        File file = new File(path);
        if (file.exists()) {
            System.out.println(description + " found: " + path + " (" + file.length() + " bytes)");
            return true;
        } else {
            System.out.println("WARNING: " + description + " not found at: " + path);
            return false;
        }
    }// Helper method to play a sound once
    private void playSound(String name) {
        try {
            System.out.println("GamePanel: Playing sound: " + name);
            AudioPlayer audioPlayer = AudioPlayer.getInstance();
            if (audioPlayer != null) {
                audioPlayer.playSound(name);
            } else {
                System.out.println("ERROR: AudioPlayer instance is null");
            }
        } catch (Exception e) {
            System.out.println("Error in playSound '" + name + "': " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unused")
    // Helper method to loop a sound continuously
    private void loopSound(String name) {
        try {
            System.out.println("GamePanel: Looping sound: " + name);
            AudioPlayer audioPlayer = AudioPlayer.getInstance();
            if (audioPlayer != null) {
                audioPlayer.loopSound(name);
            } else {
                System.out.println("ERROR: AudioPlayer instance is null");
            }
        } catch (Exception e) {
            System.out.println("Error in loopSound '" + name + "': " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unused")
    // Helper method to stop a specific sound
    private void stopSound(String name) {
        try {
            AudioPlayer audioPlayer = AudioPlayer.getInstance();
            audioPlayer.stopSound(name);
        } catch (Exception e) {
            System.out.println("Error in stopSound: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unused")
    // Helper method to stop all sounds
    private void stopAllSounds() {
        try {
            AudioPlayer audioPlayer = AudioPlayer.getInstance();
            audioPlayer.stopAllSounds();
        } catch (Exception e) {
            System.out.println("Error in stopAllSounds: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unused")
    private void playBackgroundMusic() {
        // Use AudioPlayer to play the background music (play once and auto-restart)
        playInGameMusic();
    }
    
    private void stopBackgroundMusic() {
        // Use AudioPlayer to stop the background music
        AudioPlayer.getInstance().stopSound("ingame");
    }
    
    // Helper method to ensure GameEngine methods are accessible
    private void ensureGameEngineMethods() {
        try {
            // Use reflection to check if methods exist
            Class<?> engineClass = gameEngine.getClass();
            
            // Check if all required methods exist (variables not used, but method existence is checked)
            engineClass.getMethod("isGirlFacingRight");
            engineClass.getMethod("isHeartReachedGirl");
            engineClass.getMethod("getEmotionState");
            engineClass.getMethod("isFacingRight");
            engineClass.getMethod("getTimeRemaining");
            
            System.out.println("All required GameEngine methods are accessible");
        } catch (Exception e) {
            System.out.println("Error checking GameEngine methods: " + e.getMessage());
        }
    }
    
    // Wrapper methods to access GameEngine methods via reflection
    private boolean isGirlFacingRight() {
        try {
            Method method = gameEngine.getClass().getMethod("isGirlFacingRight");
            return (boolean) method.invoke(gameEngine);
        } catch (Exception e) {
            System.out.println("Error in isGirlFacingRight: " + e.getMessage());
            return true; // Default value
        }
    }
    
    private boolean isHeartReachedGirl() {
        try {
            Method method = gameEngine.getClass().getMethod("isHeartReachedGirl");
            return (boolean) method.invoke(gameEngine);
        } catch (Exception e) {
            System.out.println("Error in isHeartReachedGirl: " + e.getMessage());
            return false; // Default value
        }
    }
    
    private int getEmotionState() {
        try {
            Method method = gameEngine.getClass().getMethod("getEmotionState");
            return (int) method.invoke(gameEngine);
        } catch (Exception e) {
            System.out.println("Error in getEmotionState: " + e.getMessage());
            return 0; // Default value
        }
    }
    
    private boolean isFacingRight() {
        try {
            Method method = gameEngine.getClass().getMethod("isFacingRight");
            return (boolean) method.invoke(gameEngine);
        } catch (Exception e) {
            System.out.println("Error in isFacingRight: " + e.getMessage());
            return true; // Default value
        }
    }
    
    private long getTimeRemaining() {
        try {
            Method method = gameEngine.getClass().getMethod("getTimeRemaining");
            return (long) method.invoke(gameEngine);
        } catch (Exception e) {
            System.out.println("Error in getTimeRemaining: " + e.getMessage());
            return 0; // Default value
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
                // Check girl facing direction
                if (!isGirlFacingRight()) {
                    // Draw mirrored image for girl facing left
                    g2d.translate(girlPos.x, girlPos.y);
                    g2d.scale(-1, 1);
                    g2d.drawImage(girlImage, -40, -50, 80, 100, null);
                    g2d.scale(-1, 1);
                    g2d.translate(-girlPos.x, -girlPos.y);
                } else {
                    // Draw normal image
                    g2d.drawImage(girlImage, girlPos.x - 40, girlPos.y - 50, 80, 100, null);
                }
            } else {
                // Fallback girl drawing
                g2d.setColor(Color.PINK);
                g2d.fillRect(girlPos.x - 20, girlPos.y - 30, 40, 60);
            }
            
            // Draw player (Azzam) with correct emotion and direction
            Point playerPos = gameEngine.getPlayerPosition();
            if (playerImages != null) {
                int emotionState = getEmotionState();
                BufferedImage playerImage = playerImages[emotionState];
                
                if (playerImage != null) {
                    // Check if we need to mirror the image
                    if (!isFacingRight()) {
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
            long timeRemaining = getTimeRemaining();
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
            int currentEmotionState = getEmotionState();
            if (currentEmotionState != lastEmotionState) {
                // Play sound effects for reaching score milestones
                if (currentEmotionState > lastEmotionState) {
                    playSound("character_change"); // Use the "sound berubah.mp3" when emotion changes
                }
                lastEmotionState = currentEmotionState;
            }
            
            // Check if a heart has reached the girl (for achievement sound)
            if (isHeartReachedGirl()) {
                // Don't play achievement sounds too close together
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastAchievementSoundTime > 500) { // 500ms cooldown
                    playSound("achievement");
                    lastAchievementSoundTime = currentTime;
                }
            }
            
            // Repaint
            repaint();
        } else {
            // Game is over, stop timer and stop background music
            stopBackgroundMusic();
            playSound("character_change"); // Use as game over sound
            gameTimer.stop();
            
            // Game ended - automatically save the results to database
            // This happens automatically in GameEngine.endGame(), which was called when time ran out
            
            // Show final score before returning to menu
            JFrame gameFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            JOptionPane.showMessageDialog(gameFrame, 
                "Time's up! Your final score: " + gameEngine.getScore() + 
                "\nHearts collected: " + gameEngine.getHeartsCollected() +
                "\n\nYour result has been saved to database!",
                "Game Over", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Close game window
            gameFrame.dispose();
            
            // Refresh scores and show menu
            mainMenuView.refreshScores();
            mainMenuView.setVisible(true);
        }
    }
    
    // Helper method to play in-game music (play once and restart when finished)
    private void playInGameMusic() {
        try {
            System.out.println("GamePanel: Playing in-game music with auto-restart");
            AudioPlayer audioPlayer = AudioPlayer.getInstance();
            if (audioPlayer != null) {
                audioPlayer.playInGameMusic();
            } else {
                System.out.println("ERROR: AudioPlayer instance is null");
            }
        } catch (Exception e) {
            System.out.println("Error in playInGameMusic: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
