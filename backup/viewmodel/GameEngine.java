package viewmodel;

import model.DatabaseManager;
import model.PlayerResult;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class GameEngine {
    // Game constants
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private static final int PLAYER_SPEED = 5;
    private static final int HEART_SPEED_MIN = 1;
    private static final int HEART_SPEED_MAX = 3;
    
    // Game state
    private boolean isRunning;
    private String currentUsername;
    private int score;
    private int heartsCollected;
    
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
        databaseManager = DatabaseManager.getInstance();
        
        // Initialize game state
        reset();
    }
    
    // Reset game state
    public void reset() {
        isRunning = false;
        score = 0;
        heartsCollected = 0;
        
        // Initialize player position (left side, middle)
        playerPosition = new Point(50, SCREEN_HEIGHT / 2);
        
        // Initialize girl position (top right corner)
        girlPosition = new Point(SCREEN_WIDTH - 100, 50);
        
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
            // Start with some hearts
            for (int i = 0; i < 5; i++) {
                spawnHeart();
            }
        }
    }
    
    // End game and save result
    public void endGame() {
        if (isRunning) {
            isRunning = false;
            // Save result to database
            if (currentUsername != null && !currentUsername.isEmpty()) {
                PlayerResult result = new PlayerResult(currentUsername, score, heartsCollected);
                databaseManager.savePlayerResult(result);
            }
        }
    }
    
    // Update game state (called in game loop)
    public void update() {
        if (!isRunning) return;
        
        // Update player position based on input controller
        // (will be handled by InputController)
        
        // Update hearts
        for (int i = hearts.size() - 1; i >= 0; i--) {
            Heart heart = hearts.get(i);
            heart.update();
            
            // Remove hearts that go offscreen
            if ((heart.getPosition().x < -50) || (heart.getPosition().x > SCREEN_WIDTH + 50)) {
                hearts.remove(i);
                spawnHeart(); // Spawn a new one
            }
        }
        
        // Update lasso if active
        if (lasso != null) {
            lasso.update();
            
            // Check if lasso caught any heart
            for (Heart heart : hearts) {
                if (!heart.isCaught() && lasso.checkCollision(heart.getPosition(), 30)) {
                    heart.setCaught(true);
                    // Calculate points based on heart color
                    score += heart.getPoints();
                    heartsCollected++;
                }
            }
            
            // Remove lasso if it's done
            if (lasso.isDone()) {
                lasso = null;
            }
        }
        
        // Randomly spawn new hearts
        if (random.nextInt(100) < 2 && hearts.size() < 10) {
            spawnHeart();
        }
    }
    
    // Spawn a new heart
    private void spawnHeart() {
        int type = random.nextInt(6); // 6 types of hearts
        
        // Determine spawn position and direction
        boolean fromTop = random.nextBoolean();
        int x, y, speedX;
        
        if (fromTop) {
            // From top: right to left
            x = SCREEN_WIDTH + 30;
            y = random.nextInt(SCREEN_HEIGHT / 2) + 50;
            speedX = -random.nextInt(HEART_SPEED_MAX - HEART_SPEED_MIN + 1) - HEART_SPEED_MIN;
        } else {
            // From bottom: left to right
            x = -30;
            y = random.nextInt(SCREEN_HEIGHT / 2) + SCREEN_HEIGHT / 2 - 50;
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
            
            // Ensure player stays within screen bounds
            newX = Math.max(20, Math.min(SCREEN_WIDTH / 2, newX));
            newY = Math.max(20, Math.min(SCREEN_HEIGHT - 50, newY));
            
            playerPosition.x = newX;
            playerPosition.y = newY;
        }
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
    
    // Inner class for Heart object
    public class Heart {
        private Point position;
        private int speedX;
        private int type; // 0-5 for different heart types
        private int points;
        private boolean isCaught;
        private Point targetPosition; // Position heart is moving to when caught
        
        public Heart(Point position, int speedX, int type, int points) {
            this.position = position;
            this.speedX = speedX;
            this.type = type;
            this.points = points;
            this.isCaught = false;
        }
        
        public void update() {
            if (!isCaught) {
                // Normal movement
                position.x += speedX;
            } else {
                // Move toward girl position when caught
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
        }
    }
    
    // Inner class for Lasso object
    public class Lasso {
        private Point startPosition;
        private Point targetPosition;
        private Point currentPosition;
        private boolean extending;
        private boolean retracting;
        private int lifetime;
        
        public Lasso(Point startPosition, Point targetPosition) {
            this.startPosition = new Point(startPosition);
            this.targetPosition = new Point(targetPosition);
            this.currentPosition = new Point(startPosition);
            this.extending = true;
            this.retracting = false;
            this.lifetime = 0;
        }
        
        public void update() {
            lifetime++;
            
            if (extending) {
                // Move toward target position
                double dx = targetPosition.x - currentPosition.x;
                double dy = targetPosition.y - currentPosition.y;
                double distance = Math.sqrt(dx * dx + dy * dy);
                
                if (distance > 10) {
                    double ratio = 10 / distance;
                    currentPosition.x += dx * ratio;
                    currentPosition.y += dy * ratio;
                } else {
                    // Reached target, start retracting
                    extending = false;
                    retracting = true;
                }
            } else if (retracting) {
                // Move back to start position
                double dx = startPosition.x - currentPosition.x;
                double dy = startPosition.y - currentPosition.y;
                double distance = Math.sqrt(dx * dx + dy * dy);
                
                if (distance > 10) {
                    double ratio = 10 / distance;
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
    }
}
