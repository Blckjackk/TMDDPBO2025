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
    // Konstanta
    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;
    private static final int FPS = 60;
    
    // Referensi
    private GameEngine gameEngine;
    private InputController inputController;
    private MainMenuView mainMenuView;
    
    // Timer untuk putaran permainan
    private Timer gameTimer;
    
    // Gambar
    private BufferedImage backgroundImage;
    private BufferedImage[] playerImages; // Berbagai kondisi emosi
    private BufferedImage girlImage;
    private HashMap<Integer, BufferedImage> heartImages;
    private BufferedImage ropeImage;
      // Status emosi terakhir untuk efek suara
    private int lastEmotionState = -1;
    
    // Penanda untuk melacak apakah suara prestasi baru saja diputar
    private long lastAchievementSoundTime = 0;      public GamePanel(GameEngine gameEngine, MainMenuView mainMenuView) {
        this.gameEngine = gameEngine;
        this.mainMenuView = mainMenuView;
        
        // Mengatur panel
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setFocusable(true);
        
        // Memuat aset
        loadImages();
        loadSounds();
        
        // Memanggil metode ini untuk memverifikasi metode GameEngine tersedia
        ensureGameEngineMethods();
        
        // Mengatur pengontrol input
        inputController = new InputController(gameEngine);
        addKeyListener(inputController);
        addMouseListener(inputController);
        
        // Membuat frame permainan
        JFrame gameFrame = new JFrame("Azzam Love - Game");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setResizable(false);
        gameFrame.add(this);
        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
        
        // Memulai timer permainan
        gameTimer = new Timer(1000 / FPS, this);
        gameTimer.start();
          // Memutar suara mulai dan musik latar
        playSound("game_start");
        playInGameMusic();
    }
      private void loadImages() {
        try {
            // Memuat latar belakang
            backgroundImage = ImageIO.read(new File("assets/background taman.png"));
            
            // Memuat gambar pemain untuk berbagai emosi
            playerImages = new BufferedImage[4];
            playerImages[0] = ImageIO.read(new File("assets/Azzam Berjalan.png")); // Normal
            playerImages[1] = ImageIO.read(new File("assets/Azzam Senang.png"));   // Senang (skor >= 30)
            playerImages[2] = ImageIO.read(new File("assets/Azzam Nahh Ituu.png")); // Bersemangat (skor >= 50)
            playerImages[3] = ImageIO.read(new File("assets/Azzam Tertawa.png"));   // Tertawa (skor >= 100)
            
            // Memuat karakter perempuan
            girlImage = ImageIO.read(new File("assets/Perempuan Senang.png"));
            
            // Memuat gambar hati
            heartImages = new HashMap<>();
            heartImages.put(0, ImageIO.read(new File("assets/Hati Biru.png"))); // Biru - 3 poin
            heartImages.put(1, ImageIO.read(new File("assets/Hati Hijau.png"))); // Hijau - 4 poin
            heartImages.put(2, ImageIO.read(new File("assets/Hati Kuning.png"))); // Kuning - 5 poin
            heartImages.put(3, ImageIO.read(new File("assets/Hati Merah.png"))); // Merah - 6 poin
            heartImages.put(4, ImageIO.read(new File("assets/Hati Orange.png"))); // Oranye - 7 poin
            heartImages.put(5, ImageIO.read(new File("assets/Hati Ungu.png"))); // Ungu - 2 poin
            heartImages.put(6, ImageIO.read(new File("assets/Hati Potek.png"))); // Rusak - -12 poin
            
            // Memuat gambar tali untuk lasso
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
    }// Metode pembantu untuk memainkan suara sekali
    private void playSound(String name) {
        try {
            System.out.println("GamePanel: Memainkan suara: " + name);
            AudioPlayer audioPlayer = AudioPlayer.getInstance();
            if (audioPlayer != null) {
                audioPlayer.playSound(name);
            } else {
                System.out.println("ERROR: Instance AudioPlayer adalah null");
            }
        } catch (Exception e) {
            System.out.println("Error pada playSound '" + name + "': " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unused")
    // Metode pembantu untuk mengulang suara terus-menerus
    private void loopSound(String name) {
        try {
            System.out.println("GamePanel: Mengulang suara: " + name);
            AudioPlayer audioPlayer = AudioPlayer.getInstance();
            if (audioPlayer != null) {
                audioPlayer.loopSound(name);
            } else {
                System.out.println("ERROR: Instance AudioPlayer adalah null");
            }
        } catch (Exception e) {
            System.out.println("Error pada loopSound '" + name + "': " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unused")
    // Metode pembantu untuk menghentikan suara tertentu
    private void stopSound(String name) {
        try {
            AudioPlayer audioPlayer = AudioPlayer.getInstance();
            audioPlayer.stopSound(name);
        } catch (Exception e) {
            System.out.println("Error pada stopSound: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unused")
    // Metode pembantu untuk menghentikan semua suara
    private void stopAllSounds() {
        try {
            AudioPlayer audioPlayer = AudioPlayer.getInstance();
            audioPlayer.stopAllSounds();
        } catch (Exception e) {
            System.out.println("Error pada stopAllSounds: " + e.getMessage());
        }
    }
      @SuppressWarnings("unused")
    private void playBackgroundMusic() {
        // Menggunakan AudioPlayer untuk memutar musik latar (putar sekali dan mulai ulang otomatis)
        playInGameMusic();
    }
      private void stopBackgroundMusic() {
        // Menghentikan semua suara termasuk musik latar belakang saat permainan berakhir
        try {
            AudioPlayer audioPlayer = AudioPlayer.getInstance();
            if (audioPlayer != null) {
                audioPlayer.stopAllSounds();
                System.out.println("Semua suara dihentikan di GamePanel");
            }
        } catch (Exception e) {
            System.out.println("Error menghentikan suara di GamePanel: " + e.getMessage());
        }
    }
    
    // Metode pembantu untuk memastikan metode GameEngine dapat diakses
    private void ensureGameEngineMethods() {
        try {
            // Menggunakan reflection untuk memeriksa apakah metode ada
            Class<?> engineClass = gameEngine.getClass();
            
            // Memeriksa apakah semua metode yang diperlukan ada (variabel tidak digunakan, tetapi keberadaan metode diperiksa)
            engineClass.getMethod("isGirlFacingRight");
            engineClass.getMethod("isHeartReachedGirl");
            engineClass.getMethod("getEmotionState");
            engineClass.getMethod("isFacingRight");
            engineClass.getMethod("getTimeRemaining");
            
            System.out.println("Semua metode GameEngine yang diperlukan dapat diakses");
        } catch (Exception e) {
            System.out.println("Error memeriksa metode GameEngine: " + e.getMessage());
        }
    }
      // Metode pembungkus untuk mengakses metode GameEngine melalui reflection
    private boolean isGirlFacingRight() {
        try {
            Method method = gameEngine.getClass().getMethod("isGirlFacingRight");
            return (boolean) method.invoke(gameEngine);
        } catch (Exception e) {
            System.out.println("Error pada isGirlFacingRight: " + e.getMessage());
            return true; // Nilai default
        }
    }
    
    private boolean isHeartReachedGirl() {
        try {
            Method method = gameEngine.getClass().getMethod("isHeartReachedGirl");
            return (boolean) method.invoke(gameEngine);
        } catch (Exception e) {
            System.out.println("Error pada isHeartReachedGirl: " + e.getMessage());
            return false; // Nilai default
        }
    }
    
    private int getEmotionState() {
        try {
            Method method = gameEngine.getClass().getMethod("getEmotionState");
            return (int) method.invoke(gameEngine);
        } catch (Exception e) {
            System.out.println("Error pada getEmotionState: " + e.getMessage());
            return 0; // Nilai default
        }
    }
    
    private boolean isFacingRight() {
        try {
            Method method = gameEngine.getClass().getMethod("isFacingRight");
            return (boolean) method.invoke(gameEngine);
        } catch (Exception e) {
            System.out.println("Error pada isFacingRight: " + e.getMessage());
            return true; // Nilai default
        }
    }
    
    private long getTimeRemaining() {
        try {
            Method method = gameEngine.getClass().getMethod("getTimeRemaining");
            return (long) method.invoke(gameEngine);
        } catch (Exception e) {
            System.out.println("Error pada getTimeRemaining: " + e.getMessage());
            return 0; // Nilai default
        }
    }
      @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Membuat objek grafik untuk rendering yang lebih baik
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Menggambar latar belakang
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, null);
        } else {
            // Latar belakang cadangan
            g2d.setColor(new Color(230, 255, 230));
            g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        }
          // Menggambar objek permainan jika permainan sedang berjalan
        if (gameEngine.isRunning()) {
            // Menggambar lasso jika aktif
            GameEngine.Lasso lasso = gameEngine.getLasso();
            if (lasso != null) {
                Point start = lasso.getStartPosition();
                Point end = lasso.getCurrentPosition();
                
                // Menggambar garis tali (dengan ketebalan tertentu)
                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(Color.RED);
                g2d.drawLine(start.x, start.y, end.x, end.y);
                
                // Menggambar gambar tali di ujung
                if (ropeImage != null) {
                    g2d.drawImage(ropeImage, end.x - 15, end.y - 15, 30, 30, null);
                }
            }
            
            // Menggambar hati
            for (GameEngine.Heart heart : gameEngine.getHearts()) {
                Point pos = heart.getPosition();
                BufferedImage heartImage = heartImages.get(heart.getType());
                
                if (heartImage != null) {
                    g2d.drawImage(heartImage, pos.x - 25, pos.y - 25, 50, 50, null);
                } else {
                    // Gambar hati cadangan
                    g2d.setColor(Color.RED);
                    g2d.fillOval(pos.x - 15, pos.y - 15, 30, 30);
                }
            }
              // Menggambar perempuan (target untuk hati)
            Point girlPos = gameEngine.getGirlPosition();
            if (girlImage != null) {
                // Memeriksa arah hadap perempuan
                if (!isGirlFacingRight()) {
                    // Menggambar gambar yang dicerminkan untuk perempuan menghadap ke kiri
                    g2d.translate(girlPos.x, girlPos.y);
                    g2d.scale(-1, 1);
                    g2d.drawImage(girlImage, -40, -50, 80, 100, null);
                    g2d.scale(-1, 1);
                    g2d.translate(-girlPos.x, -girlPos.y);
                } else {
                    // Menggambar gambar normal
                    g2d.drawImage(girlImage, girlPos.x - 40, girlPos.y - 50, 80, 100, null);
                }
            } else {
                // Gambar perempuan cadangan
                g2d.setColor(Color.PINK);
                g2d.fillRect(girlPos.x - 20, girlPos.y - 30, 40, 60);
            }
            
            // Menggambar pemain (Azzam) dengan emosi dan arah yang benar
            Point playerPos = gameEngine.getPlayerPosition();
            if (playerImages != null) {
                int emotionState = getEmotionState();
                BufferedImage playerImage = playerImages[emotionState];
                
                if (playerImage != null) {
                    // Memeriksa apakah kita perlu mencerminkan gambar
                    if (!isFacingRight()) {
                        // Menggambar gambar yang dicerminkan
                        g2d.translate(playerPos.x, playerPos.y);
                        g2d.scale(-1, 1);
                        g2d.drawImage(playerImage, -40, -50, 80, 100, null);
                        g2d.scale(-1, 1);
                        g2d.translate(-playerPos.x, -playerPos.y);
                    } else {
                        // Menggambar gambar normal
                        g2d.drawImage(playerImage, playerPos.x - 40, playerPos.y - 50, 80, 100, null);
                    }
                }
            } else {
                // Gambar pemain cadangan
                g2d.setColor(Color.BLUE);
                g2d.fillRect(playerPos.x - 20, playerPos.y - 30, 40, 60);
            }
              // Menggambar timer
            long timeRemaining = getTimeRemaining();
            int seconds = (int)(timeRemaining / 1000);
            int milliseconds = (int)(timeRemaining % 1000 / 10);
            
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.drawString(String.format("Waktu: %02d:%02d", seconds, milliseconds), PANEL_WIDTH - 150, 30);
            
            // Menggambar skor dan hati yang dikumpulkan
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("Skor: " + gameEngine.getScore(), 20, 30);
            g2d.drawString("Hati: " + gameEngine.getHeartsCollected(), 20, 60);
            
            // Menggambar instruksi
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            g2d.drawString("Gunakan tombol panah untuk bergerak", 20, PANEL_HEIGHT - 40);
            g2d.drawString("Klik untuk melempar lasso", 20, PANEL_HEIGHT - 20);
        }
    }
      @Override
    public void actionPerformed(ActionEvent e) {
        // Memeriksa apakah permainan masih berjalan
        if (gameEngine.isRunning()) {
            // Memproses input
            inputController.processInput();
            
            // Memperbarui status permainan
            gameEngine.update();
            
            // Memeriksa perubahan status emosi untuk efek suara
            int currentEmotionState = getEmotionState();
            if (currentEmotionState != lastEmotionState) {
                // Memutar efek suara untuk mencapai tonggak skor
                if (currentEmotionState > lastEmotionState) {
                    playSound("character_change"); // Gunakan "sound berubah.mp3" ketika emosi berubah
                }
                lastEmotionState = currentEmotionState;
            }
            
            // Memeriksa apakah hati telah mencapai perempuan (untuk suara pencapaian)
            if (isHeartReachedGirl()) {
                // Jangan memainkan suara pencapaian terlalu dekat bersama-sama
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastAchievementSoundTime > 500) { // 500ms cooldown
                    playSound("achievement");
                    lastAchievementSoundTime = currentTime;
                }
            }
            
            // Menggambar ulang
            repaint();        } else {
            // Permainan berakhir, hentikan timer
            gameTimer.stop();
            
            // Putar efek suara game over, kemudian hentikan semua musik latar belakang
            playSound("character_change"); // Gunakan sebagai suara game over            // Tunggu sebentar untuk membiarkan efek suara bermain, kemudian hentikan semua suara
            javax.swing.Timer soundStopTimer = new javax.swing.Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    stopBackgroundMusic();
                }
            });
            soundStopTimer.setRepeats(false);
            soundStopTimer.start();
            
            // Permainan berakhir - otomatis menyimpan hasil ke database
            // Ini terjadi secara otomatis di GameEngine.endGame(), yang dipanggil ketika waktu habis
            
            // Tampilkan skor akhir sebelum kembali ke menu
            JFrame gameFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            JOptionPane.showMessageDialog(gameFrame, 
                "Waktu habis! Skor akhir Anda: " + gameEngine.getScore() + 
                "\nHati yang dikumpulkan: " + gameEngine.getHeartsCollected() +
                "\n\nHasil Anda telah disimpan ke database!",
                "Game Over", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Tutup jendela permainan
            gameFrame.dispose();
            
            // Perbarui skor dan tampilkan menu
            mainMenuView.refreshScores();
            mainMenuView.setVisible(true);
        }
    }
      // Metode pembantu untuk memutar musik dalam permainan (putar sekali dan mulai ulang ketika selesai)
    private void playInGameMusic() {
        try {
            System.out.println("GamePanel: Memutar musik dalam permainan dengan auto-restart");
            AudioPlayer audioPlayer = AudioPlayer.getInstance();
            if (audioPlayer != null) {
                audioPlayer.playInGameMusic();
            } else {
                System.out.println("ERROR: Instance AudioPlayer adalah null");
            }
        } catch (Exception e) {
            System.out.println("Error pada playInGameMusic: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
