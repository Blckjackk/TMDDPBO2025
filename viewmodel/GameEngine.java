package viewmodel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import model.DatabaseManager;
import model.PlayerResult;

public class GameEngine {
    // Game constants
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private static final int PLAYER_SPEED = 5;
    private static final int HEART_SPEED_MIN = 1;
    private static final int HEART_SPEED_MAX = 3;
    private static final int GAME_DURATION_MS = 60000; // 1 minute in milliseconds
    
    // Game state
    private boolean isRunning;
    private String currentUsername;
    private int score;
    private int heartsCollected;
    private long startTime;
    private long timeRemaining;
    private boolean facingRight; // For character direction
    private boolean girlFacingRight; // Direction for girl character
    private int emotionState; // 0=normal, 1=happy, 2=excited, 3=laughing
    private boolean heartReachedGirl; // Flag for playing achievement sound
    
    // Game objects
    private Point playerPosition;
    private Point girlPosition;
    private ArrayList<Heart> hearts;
    private Lasso lasso;
    
    // Random generator
    private Random random;
    
    // Database manager
    private DatabaseManager databaseManager;
    
    // Constructor
    public GameEngine() {
        random = new Random();
        hearts = new ArrayList<>();
        
        // Initialize database manager
        try {
            databaseManager = DatabaseManager.getInstance();
            if (databaseManager == null) {
                System.out.println("Warning: Failed to initialize database manager in GameEngine");
            } else {
                System.out.println("Database manager initialized successfully in GameEngine");
            }
        } catch (Exception e) {
            System.out.println("Error initializing database manager in GameEngine: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Initialize game state
        reset();
    }
    
    // Reset game state
    public void reset() {
        isRunning = false;
        score = 0;
        heartsCollected = 0;
        timeRemaining = GAME_DURATION_MS;
        facingRight = true;
        girlFacingRight = true;
        emotionState = 0;
        heartReachedGirl = false;
        
        // Initialize player position (left side, middle)
        playerPosition = new Point(50, SCREEN_HEIGHT / 2);
        
        // Initialize girl position (center of screen)
        girlPosition = new Point(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
        
        // Clear hearts
        hearts.clear();
        
        // Reset lasso
        lasso = null;
    }
    
    // Start game with username
    public void startGame(String username) {
        if (username != null && !username.trim().isEmpty()) {
            currentUsername = username;            
            isRunning = true;
            startTime = System.currentTimeMillis();
            // Start with fewer hearts
            for (int i = 0; i < 3; i++) {
                spawnHeart();
            }
        }
    }
    
    // End game and save result
    public void endGame() {
        if (isRunning) {
            isRunning = false;
            System.out.println("\n========== GAME OVER ==========");
            System.out.println("Final Score: " + score);
            System.out.println("Hearts Collected: " + heartsCollected);
            
            // Save result to database
            if (currentUsername != null && !currentUsername.isEmpty()) {
                try {
                    System.out.println("Attempting to save game result to database");
                    System.out.println("Username: " + currentUsername);
                    System.out.println("Score: " + score);
                    System.out.println("Hearts: " + heartsCollected);
                    
                    // Always get a fresh instance of the database manager
                    databaseManager = DatabaseManager.getInstance();
                    
                    if (databaseManager != null) {
                        // Force initialize database to ensure table exists
                        databaseManager.initializeDatabase();
                        
                        // Create player result 
                        PlayerResult result = new PlayerResult(currentUsername, score, heartsCollected);
                        
                        // Debug the player result object
                        System.out.println("Created PlayerResult object:");
                        System.out.println(" - Username: " + result.getUsername());
                        System.out.println(" - Score: " + result.getSkor());
                        System.out.println(" - Hearts: " + result.getCount());
                        
                        // Save to database with better error handling
                        try {
                            System.out.println("Calling savePlayerResult...");
                            databaseManager.savePlayerResult(result);
                            System.out.println("savePlayerResult completed");
                        } catch (Exception dbEx) {
                            System.out.println("Critical error saving to database: " + dbEx.getMessage());
                            dbEx.printStackTrace();
                        }
                    } else {
                        System.out.println("CRITICAL ERROR: Database manager is null, can't save game result!");
                    }
                } catch (Exception e) {
                    System.out.println("Unexpected error in endGame: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("Cannot save result: username is empty or null");
            }
            
            System.out.println("============================\n");
            
            // Double-check that game state is properly ended
            isRunning = false;
        }
    }
    
    // Update game state (called in game loop)
    public void update() {
        if (!isRunning) return;
        
        // Update timer
        long currentTime = System.currentTimeMillis();
        timeRemaining = Math.max(0, GAME_DURATION_MS - (currentTime - startTime));
        
        // Check if time's up
        if (timeRemaining <= 0) {
            endGame();
            return;
        }
        
        // Update emotions based on score
        if (score >= 100) {
            emotionState = 3; // Azzam Tertawa
        } else if (score >= 50) {
            emotionState = 2; // Azzam Nah Ituu
        } else if (score >= 30) {
            emotionState = 1; // Azzam Senang
        } else {
            emotionState = 0; // Azzam Berjalan
        }
        
        // Reset heart reached flag
        heartReachedGirl = false;
        
        // Update girl facing direction - make her face towards player
        if (playerPosition.x < girlPosition.x) {
            girlFacingRight = false; // Player is to the left, girl faces left
        } else {
            girlFacingRight = true;  // Player is to the right, girl faces right
        }
        
        // Update hearts
        for (int i = hearts.size() - 1; i >= 0; i--) {
            Heart heart = hearts.get(i);
            heart.update();
            
            // Check if heart reached the girl
            if (heart.isCaught()) {
                double dx = girlPosition.x - heart.getPosition().x;
                double dy = girlPosition.y - heart.getPosition().y;
                double distance = Math.sqrt(dx * dx + dy * dy);
                
                if (distance <= 5) {
                    heartReachedGirl = true;
                }
            }
            
            // Remove hearts that go offscreen
            if ((heart.getPosition().x < -50) || (heart.getPosition().x > SCREEN_WIDTH + 50)) {
                hearts.remove(i);
                // Only spawn a new heart 50% of the time to reduce heart frequency
                if (random.nextInt(100) < 50) {
                    spawnHeart(); // Spawn a new one
                }
            }
        }
        
        // Update lasso if active
        if (lasso != null) {
            lasso.update();
            
            // Check if lasso caught any heart (only if it hasn't already caught one)
            if (!lasso.hasHeartCaught()) {
                for (Heart heart : hearts) {
                    if (!heart.isCaught() && lasso.checkCollision(heart.getPosition(), 30)) {                        // Mark this heart as caught
                        heart.setCaught(true);
                        
                        // Set the lasso reference for the heart
                        heart.setLasso(lasso);
                        
                        // Calculate points based on heart color
                        score += heart.getPoints();
                        heartsCollected++;
                        
                        // Make the lasso start retracting immediately
                        lasso.catchHeart();
                        
                        // Only catch one heart per throw
                        break;
                    }
                }
            }
            
            // Remove lasso if it's done
            if (lasso.isDone()) {
                lasso = null;
            }
        }
        
        // Randomly spawn new hearts (reduced frequency)
        if (random.nextInt(100) < 1 && hearts.size() < 7) {
            spawnHeart();
        }
    }
    
    // Spawn a new heart
    private void spawnHeart() {
        int type = random.nextInt(6); // 6 types of hearts
        
        // Determine spawn position and direction
        boolean fromTop = random.nextBoolean();
        int x, y, speedX;
        int middleAreaHeight = 200; // Height of middle area to avoid
        int middleAreaTop = (SCREEN_HEIGHT / 2) - (middleAreaHeight / 2);
        
        if (fromTop) {
            // From top: right to left
            x = SCREEN_WIDTH + 30;
            
            // Spawn in top third of screen, avoid middle area
            y = random.nextInt(middleAreaTop - 100) + 50; // Keep hearts higher up
            
            speedX = -random.nextInt(HEART_SPEED_MAX - HEART_SPEED_MIN + 1) - HEART_SPEED_MIN;
        } else {
            // From bottom: left to right
            x = -30;
            
            // Spawn in bottom third of screen, avoid middle area
            y = random.nextInt(middleAreaTop - 100) + middleAreaTop + middleAreaHeight; // Keep hearts lower down
            
            speedX = random.nextInt(HEART_SPEED_MAX - HEART_SPEED_MIN + 1) + HEART_SPEED_MIN;
        }
        
        // Create heart with appropriate points based on type
        int points;
        switch (type) {
            case 0: points = 3; break; // Blue
            case 1: points = 4; break; // Green
            case 2: points = 5; break; // Yellow
            case 3: points = 6; break; // Red
            case 4: points = 7; break; // Orange
            default: points = 2; break; // Purple
        }
        
        System.out.println("Spawning heart at position: (" + x + ", " + y + ")");
        hearts.add(new Heart(new Point(x, y), speedX, type, points));
    }
    
    // Throw lasso at target point
    public void throwLasso(Point target) {
        if (isRunning && lasso == null) {
            lasso = new Lasso(new Point(playerPosition.x, playerPosition.y), target);
        }
    }
    
    // Move player
    public void movePlayer(int dx, int dy) {
        if (isRunning) {
            // Update player position with bounds checking
            int newX = playerPosition.x + dx * PLAYER_SPEED;
            int newY = playerPosition.y + dy * PLAYER_SPEED;
            
            // Update facing direction
            if (dx > 0) {
                facingRight = true;
            } else if (dx < 0) {
                facingRight = false;
            }
            
            // Ensure player stays within screen bounds
            newX = Math.max(20, Math.min(SCREEN_WIDTH - 50, newX));
            newY = Math.max(20, Math.min(SCREEN_HEIGHT - 50, newY));
            
            playerPosition.x = newX;
            playerPosition.y = newY;
        }
    }
    
    // Setter methods for continuing a game with previous score
    public void setScore(int score) {
        System.out.println("Setting score to: " + score);
        this.score = score;
    }
    
    public void setHeartsCollected(int heartsCollected) {
        System.out.println("Setting hearts collected to: " + heartsCollected);
        this.heartsCollected = heartsCollected;
    }
    
    // Getters
    public boolean isRunning() {
        return isRunning;
    }
    
    public Point getPlayerPosition() {
        return playerPosition;
    }
    
    public Point getGirlPosition() {
        return girlPosition;
    }
    
    public ArrayList<Heart> getHearts() {
        return hearts;
    }
    
    public Lasso getLasso() {
        return lasso;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getHeartsCollected() {
        return heartsCollected;
    }
    
    public long getTimeRemaining() {
        return timeRemaining;
    }
    
    public boolean isFacingRight() {
        return facingRight;
    }
    
    public int getEmotionState() {
        return emotionState;
    }
    
    public boolean isHeartReachedGirl() {
        boolean result = heartReachedGirl;
        heartReachedGirl = false; // Reset after reading
        return result;
    }
    
    public boolean isGirlFacingRight() {
        return girlFacingRight;
    }
    
    // Inner class for Heart objects - changed from class-level to public visibility
    public class Heart {
        private Point position;
        private int type; // 0=blue, 1=green, 2=orange, 3=yellow, 4=purple, 5=red
        private int speedX;
        private int points;
        private boolean isCaught;
        private boolean returnedToPlayer; // Flag to track if heart has reached player
        private Lasso lasso; // Reference to the lasso that caught this heart
        
        public Heart(Point position, int speedX, int type, int points) {
            this.position = position;
            this.speedX = speedX;
            this.type = type;
            this.points = points;
            this.isCaught = false;
            this.returnedToPlayer = false;
        }
          public void update() {
            if (!isCaught) {
                // Normal movement
                position.x += speedX;
            } else if (!returnedToPlayer) {
                // If lasso is active and heart is caught, attach heart to lasso tip
                if (lasso != null) {
                    // Make heart follow the lasso tip exactly - no lag
                    position.x = lasso.getCurrentPosition().x;
                    position.y = lasso.getCurrentPosition().y;
                      // If lasso has returned to player position, set flag to move to girl
                    if (lasso.isDone()) {
                        returnedToPlayer = true;
                    }
                } else {
                    // If lasso is somehow null, move directly to player
                    double dx = playerPosition.x - position.x;
                    double dy = playerPosition.y - position.y;
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    
                    if (distance > 5) {
                        double ratio = 5 / distance;
                        position.x += dx * ratio;
                        position.y += dy * ratio;
                    } else {
                        // Heart reached player, now set flag to move to girl
                        returnedToPlayer = true;
                    }
                }
            } else {
                // After reaching player, now move toward girl
                double dx = girlPosition.x - position.x;
                double dy = girlPosition.y - position.y;
                double distance = Math.sqrt(dx * dx + dy * dy);
                
                if (distance > 5) {
                    double ratio = 5 / distance;
                    position.x += dx * ratio;
                    position.y += dy * ratio;
                } else {
                    // Heart reached girl, remove it
                    position.x = -100; // Will be removed in next update
                }
            }
        }
        
        public Point getPosition() {
            return position;
        }
        
        public int getType() {
            return type;
        }
        
        public int getPoints() {
            return points;
        }
        
        public boolean isCaught() {
            return isCaught;
        }
          public void setCaught(boolean caught) {
            isCaught = caught;
            if (caught && lasso == null) {
                // Store reference to the lasso that caught this heart
                this.lasso = GameEngine.this.lasso;
            }
        }
        
        public void setLasso(Lasso lasso) {
            this.lasso = lasso;
        }
    }
    
    // Inner class for Lasso object - changed from class-level to public visibility
    public class Lasso {
        private Point startPosition;
        private Point targetPosition;
        private Point currentPosition;
        private boolean extending;
        private boolean retracting;
        private boolean heartCaught; // Flag to track if a heart has been caught
        
        public Lasso(Point startPosition, Point targetPosition) {
            this.startPosition = new Point(startPosition);
            this.targetPosition = new Point(targetPosition);
            this.currentPosition = new Point(startPosition);
            this.extending = true;
            this.retracting = false;
            this.heartCaught = false;
        }
        
        public void update() {
            // Update start position to follow player
            startPosition.x = playerPosition.x;
            startPosition.y = playerPosition.y;
            
            if (extending) {
                // Move toward target position - FASTER SPEED
                double dx = targetPosition.x - currentPosition.x;
                double dy = targetPosition.y - currentPosition.y;
                double distance = Math.sqrt(dx * dx + dy * dy);
                
                // Increase speed from 10 to 15-20
                double speed = 18.0;
                
                if (distance > speed) {
                    double ratio = speed / distance;
                    currentPosition.x += dx * ratio;
                    currentPosition.y += dy * ratio;
                } else {
                    // Reached target, start retracting
                    extending = false;
                    retracting = true;
                }
            } else if (retracting) {
                // Move back to current player position (not original start position)
                double dx = startPosition.x - currentPosition.x;
                double dy = startPosition.y - currentPosition.y;
                double distance = Math.sqrt(dx * dx + dy * dy);
                
                // Increase return speed from 10 to 15-20
                double speed = 18.0;
                
                if (distance > speed) {
                    double ratio = speed / distance;
                    currentPosition.x += dx * ratio;
                    currentPosition.y += dy * ratio;
                } else {
                    // Back to start position, lasso is done
                    retracting = false;
                }
            }
        }
        
        public boolean checkCollision(Point point, int radius) {
            double dx = point.x - currentPosition.x;
            double dy = point.y - currentPosition.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            return distance < radius;
        }
        
        public boolean isDone() {
            return !extending && !retracting;
        }
        
        public Point getStartPosition() {
            return startPosition;
        }
        
        public Point getCurrentPosition() {
            return currentPosition;
        }
        
        public void catchHeart() {
            // Mark heart as caught and start retracting immediately
            this.heartCaught = true;
            this.extending = false;
            this.retracting = true;
        }
        
        public boolean hasHeartCaught() {
            return heartCaught;
        }
        
        public boolean isExtending() {
            return this.extending;
        }
        
        public boolean isRetracting() {
            return this.retracting;
        }
    }
}
