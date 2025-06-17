import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int frameWidth = 360;
    int frameHeight = 640;

    // image atribut
    Image backgroundImage;
    Image birdImage;
    Image lowerPipeImage;
    Image upperPipeImage;
    boolean isGameOver = false;
    boolean isGameStarted = false;

    JLabel scoreLabel;
    int score = 0;

    // Ini untuk player (burung)
    int playerStartPosX = frameWidth / 8;
    int playerStartPosY = frameHeight / 2;
    int playerWidth = 34;
    int playerHeight = 24;

    // ini untuk pipa
    int pipeStartPosX = frameWidth;
    int pipeStartPosY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;
    ArrayList<Pipe> pipes;

    // Game logic
    Player player;

    Timer gameLoop;
    Timer pipesCooldown;
    int gravity = 1;

    // Suara
    private Clip gameStartSound;
    private Clip gameOverSound;
    private Clip gameBonusSound;
    private Clip gameSoundtrack;

    // Fungsi untuk memutar suara
    private void playSound(String soundFile) {
        try {
            // Gunakan getClass().getResource untuk mengakses file suara
            File sound = new File(getClass().getResource("/sounds/" + soundFile).toURI());
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(sound);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    public FlappyBird(){
        setPreferredSize(new Dimension(frameWidth, frameHeight));
        setFocusable(true);
        addKeyListener(this);

        // Load gambar
        backgroundImage = new ImageIcon(getClass().getResource("assets/background.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("assets/bird.png")).getImage();
        lowerPipeImage = new ImageIcon(getClass().getResource("assets/lowerPipe.png")).getImage();
        upperPipeImage = new ImageIcon(getClass().getResource("assets/upperPipe.png")).getImage();

        // Set label skor
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(20, 10, 200, 30);
        this.setLayout(null);
        this.add(scoreLabel);

        // Player dan pipa
        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdImage);
        pipes = new ArrayList<Pipe>();

        // Timer untuk munculnya pipa
        pipesCooldown = new Timer(4000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("pipa");
                placePipe();
            }
        });

        pipesCooldown.start();

        // Timer utama untuk game loop
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();

        // Main soundtrack saat game berjalan
        playSound("game_soundtrack.wav");
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        g.drawImage(backgroundImage, 0,0 , frameWidth, frameHeight, null);
        g.drawImage(player.getImage(), player.getPosX(), player.getPosY(), player.getWidht(), player.getHeight(), null);

        for (int i = 0; i < pipes.size(); i++){
            Pipe pipe  = pipes.get(i);
            g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight(), null);
        }

        if (!isGameStarted) {
            g.setColor(new Color(0, 0, 0, 140));
            g.fillRoundRect(40, frameHeight / 2 - 70, 320, 120, 30, 30);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Segoe UI", Font.BOLD, 24));
            g.drawString("Welcome to Flappy Game!", 65, frameHeight / 2 - 25);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            g.drawString("Press SPACE to start", 110, frameHeight / 2 + 10);
        }

        if (isGameOver) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRoundRect(50, frameHeight / 2 - 80, 300, 140, 25, 25);
            g.setColor(new Color(255, 255, 255));
            g.setFont(new Font("Segoe UI", Font.BOLD, 26));
            g.drawString("Game Over", 120, frameHeight / 2 - 30);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            g.drawString("Score: " + score, 145, frameHeight / 2);
            g.drawString("Press R to Restart", 110, frameHeight / 2 + 35);
        }

    }

    public void move() {
        if (!isGameStarted || isGameOver) return;

        player.setPosY(player.getPosY() + player.getVelocityY());
        player.setVelocityY(player.getVelocityY() + 1); // gravity

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.setPosX(pipe.getPosX() + pipe.getVelocityX());

            // Deteksi tabrakan
            if (checkCollision(player, pipe)) {
                playSound("game_over.wav");
                gameOver();
                return;
            }

            // Tambah skor jika lolos dari pipa atas
            if (!pipe.isPassed() && pipe.getPosX() + pipe.getWidth() < player.getPosX() && pipe.getPosY() < frameHeight / 2) {
                pipe.setPassed(true);
                score++;
                updateScore();
                playSound("game_bonus.wav");
            }
        }
    }

    public boolean checkCollision(Player player, Pipe pipe) {
        Rectangle playerRect = new Rectangle(player.getPosX(), player.getPosY(), player.getWidht(), player.getHeight());
        Rectangle pipeRect = new Rectangle(pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight());
        return playerRect.intersects(pipeRect);
    }

    public void gameOver() {
        isGameOver = true;
        gameLoop.stop();
        pipesCooldown.stop();
        JOptionPane.showMessageDialog(this, "Game Over!");
    }

    public void updateScore() {
        scoreLabel.setText("Skor: " + score);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        move();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            player.setVelocityY(-10);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!isGameStarted) {
                isGameStarted = true;
                score = 0;
                pipes.clear();
                playSound("game_start.wav");
            }
            if (!isGameOver) {
                player.setVelocityY(-10);
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_R && isGameOver) {
            restartGame();
        }
    }

    public void restartGame() {
        player.setPosY(playerStartPosY);
        player.setVelocityY(0);
        pipes.clear();
        isGameOver = false;
        isGameStarted = false;
        score = 0;
        updateScore();
        gameLoop.start();
        pipesCooldown.start();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void placePipe(){
        int randomPosY = (int)(pipeStartPosY - pipeHeight/4 - Math.random() * (pipeHeight/2));
        int openingSpace = frameHeight/4;

        Pipe upperPipe = new Pipe(pipeStartPosX, randomPosY, pipeWidth, pipeHeight, upperPipeImage);
        pipes.add(upperPipe);

        Pipe lowerPipe = new Pipe(pipeStartPosX, (randomPosY + openingSpace + pipeHeight),pipeWidth, pipeHeight, lowerPipeImage);
        pipes.add(lowerPipe);
    }

}
