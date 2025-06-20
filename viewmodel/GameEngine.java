package viewmodel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import model.DatabaseManager;
import model.PlayerResult;

public class GameEngine {    // Konstanta permainan
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private static final int PLAYER_SPEED = 5;
    private static final int HEART_SPEED_MIN = 1;
    private static final int HEART_SPEED_MAX = 3;
    private static final int GAME_DURATION_MS = 60000; // 1 menit dalam milidetik
      // Status permainan
    private boolean isRunning;
    private String currentUsername;
    private int score;
    private int heartsCollected;
    private long startTime;
    private long timeRemaining;
    private boolean facingRight; // Untuk arah karakter
    private boolean girlFacingRight; // Arah untuk karakter perempuan
    private int emotionState; // 0=normal, 1=senang, 2=bersemangat, 3=tertawa
    private boolean heartReachedGirl; // Bendera untuk memainkan suara pencapaian
      // Objek permainan
    private Point playerPosition;
    private Point girlPosition;
    private ArrayList<Heart> hearts;
    private Lasso lasso;
    
    // Generator acak
    private Random random;
    
    // Pengelola database
    private DatabaseManager databaseManager;
    
    // Konstruktor
    public GameEngine() {
        random = new Random();
        hearts = new ArrayList<>();
          // Inisialisasi pengelola database
        try {
            databaseManager = DatabaseManager.getInstance();
            if (databaseManager == null) {
                System.out.println("Peringatan: Gagal menginisialisasi pengelola database di GameEngine");
            } else {
                System.out.println("Pengelola database berhasil diinisialisasi di GameEngine");
            }
        } catch (Exception e) {
            System.out.println("Kesalahan inisialisasi pengelola database di GameEngine: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Initialize game state
        reset();
    }
      // Atur ulang status permainan
    public void reset() {
        isRunning = false;
        score = 0;
        heartsCollected = 0;
        timeRemaining = GAME_DURATION_MS;
        facingRight = true;
        girlFacingRight = true;
        emotionState = 0;
        heartReachedGirl = false;
          // Inisialisasi posisi pemain (sisi kiri, tengah)
        playerPosition = new Point(50, SCREEN_HEIGHT / 2);
        
        // Inisialisasi posisi perempuan (pusat layar)
        girlPosition = new Point(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
        
        // Bersihkan hati
        hearts.clear();
        
        // Atur ulang laso
        lasso = null;
    }
      // Mulai permainan dengan nama pengguna
    public void startGame(String username) {
        if (username != null && !username.trim().isEmpty()) {
            currentUsername = username;            
            isRunning = true;
            startTime = System.currentTimeMillis();
            // Mulai dengan lebih sedikit hati
            for (int i = 0; i < 3; i++) {
                spawnHeart();
            }
        }
    }
      // Akhiri permainan dan simpan hasilnya
    public void endGame() {
        if (isRunning) {
            isRunning = false;
            System.out.println("\n========== PERMAINAN BERAKHIR ==========");
            System.out.println("Skor Akhir: " + score);
            System.out.println("Hati yang Dikumpulkan: " + heartsCollected);
            
            // Hentikan semua suara saat permainan berakhir
            try {
                model.AudioPlayer audioPlayer = model.AudioPlayer.getInstance();
                if (audioPlayer != null) {
                    audioPlayer.stopAllSounds();
                    System.out.println("Semua suara dihentikan saat akhir permainan");
                }
            } catch (Exception e) {
                System.out.println("Kesalahan menghentikan suara: " + e.getMessage());
            }
              // Simpan hasil ke database
            if (currentUsername != null && !currentUsername.isEmpty()) {
                try {
                    System.out.println("Mencoba menyimpan hasil permainan ke database");
                    System.out.println("Nama pengguna: " + currentUsername);
                    System.out.println("Skor: " + score);
                    System.out.println("Hati: " + heartsCollected);
                    
                    // Selalu dapatkan instance pengelola database yang baru
                    databaseManager = DatabaseManager.getInstance();
                    
                    if (databaseManager != null) {
                        // Paksa inisialisasi database untuk memastikan tabel ada
                        databaseManager.initializeDatabase();
                        
                        // Create player result 
                        PlayerResult result = new PlayerResult(currentUsername, score, heartsCollected);
                          // Debug objek hasil pemain
                        System.out.println("Objek PlayerResult dibuat:");
                        System.out.println(" - Nama pengguna: " + result.getUsername());
                        System.out.println(" - Skor: " + result.getSkor());
                        System.out.println(" - Hati: " + result.getCount());
                        
                        // Simpan ke database dengan penanganan kesalahan yang lebih baik
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
              // Periksa kembali bahwa status permainan diakhiri dengan benar
            isRunning = false;
        }
    }
    
    // Perbarui status permainan (dipanggil dalam loop permainan)
    public void update() {
        if (!isRunning) return;
        
        // Perbarui timer
        long currentTime = System.currentTimeMillis();
        timeRemaining = Math.max(0, GAME_DURATION_MS - (currentTime - startTime));
        
        // Periksa jika waktunya habis
        if (timeRemaining <= 0) {
            endGame();
            return;
        }
          // Perbarui emosi berdasarkan skor
        if (score >= 100) {
            emotionState = 3; // Azzam Tertawa
        } else if (score >= 50) {
            emotionState = 2; // Azzam Nah Ituu
        } else if (score >= 30) {
            emotionState = 1; // Azzam Senang
        } else {
            emotionState = 0; // Azzam Berjalan
        }
        
        // Atur ulang bendera hati yang mencapai perempuan
        heartReachedGirl = false;
        
        // Update girl facing direction - make her face towards player
        if (playerPosition.x < girlPosition.x) {            girlFacingRight = false; // Pemain ada di sebelah kiri, perempuan menghadap kiri
        } else {
            girlFacingRight = true;  // Pemain ada di sebelah kanan, perempuan menghadap kanan
        }
        
        // Perbarui hati
        for (int i = hearts.size() - 1; i >= 0; i--) {
            Heart heart = hearts.get(i);
            heart.update();
            
            // Periksa jika hati mencapai perempuan
            if (heart.isCaught()) {
                double dx = girlPosition.x - heart.getPosition().x;
                double dy = girlPosition.y - heart.getPosition().y;
                double distance = Math.sqrt(dx * dx + dy * dy);
                
                if (distance <= 5) {
                    heartReachedGirl = true;
                }
            }
            
            // Hapus hati yang keluar layar
            if ((heart.getPosition().x < -50) || (heart.getPosition().x > SCREEN_WIDTH + 50)) {
                hearts.remove(i);
                // Hanya munculkan hati baru 50% dari waktu untuk mengurangi frekuensi hati
                if (random.nextInt(100) < 50) {
                    spawnHeart(); // Munculkan yang baru
                }
            }
        }
          // Perbarui laso jika aktif
        if (lasso != null) {
            lasso.update();
            
            // Periksa jika laso menangkap hati (hanya jika belum menangkap satu)
            if (!lasso.hasHeartCaught()) {
                for (Heart heart : hearts) {
                    if (!heart.isCaught() && lasso.checkCollision(heart.getPosition(), 30)) {                        // Tandai hati ini sebagai tertangkap
                        heart.setCaught(true);
                        
                        // Tetapkan referensi laso untuk hati
                        heart.setLasso(lasso);
                          // Hitung poin berdasarkan warna hati
                        int points = heart.getPoints();
                        score += points;
                        
                        // Hanya hitung jenis hati positif untuk hitungan heartsCollected 
                        if (points > 0) {
                            heartsCollected++;
                        } else {
                            // Untuk hati yang rusak, tampilkan pesan di konsol
                            System.out.println("Menangkap hati yang rusak! -12 poin!");
                        }
                        
                        // Buat laso mulai ditarik kembali segera
                        lasso.catchHeart();
                        
                        // Hanya tangkap satu hati per lemparan
                        break;
                    }
                }
            }
              // Hapus laso jika selesai
            if (lasso.isDone()) {
                lasso = null;
            }
        }
        
        // Secara acak munculkan hati baru (frekuensi dikurangi)
        if (random.nextInt(100) < 1 && hearts.size() < 7) {
            spawnHeart();
        }
    }    // Munculkan hati baru
    private void spawnHeart() {
        // Tentukan tipe hati - dengan sedikit kemungkinan untuk hati yang rusak
        int type;
        if (random.nextInt(100) < 15) { // 15% kemungkinan hati rusak (tidak terlalu sering)
            type = 6; // Hati yang rusak
        } else {
            type = random.nextInt(6); // Hati normal (0-5)
        }
          // Tentukan posisi kemunculan dan arah
        boolean fromTop = random.nextBoolean();
        int x, y, speedX;
        int middleAreaHeight = 200; // Tinggi area tengah untuk dihindari
        int middleAreaTop = (SCREEN_HEIGHT / 2) - (middleAreaHeight / 2);
        
        if (fromTop) {
            // Dari atas: kanan ke kiri
            x = SCREEN_WIDTH + 30;
            
            // Muncul di sepertiga atas layar, hindari area tengah
            y = random.nextInt(middleAreaTop - 100) + 50; // Jaga hati tetap lebih tinggi
            
            speedX = -random.nextInt(HEART_SPEED_MAX - HEART_SPEED_MIN + 1) - HEART_SPEED_MIN;
        } else {
            // Dari bawah: kiri ke kanan
            x = -30;
            
            // Muncul di sepertiga bawah layar, hindari area tengah
            y = random.nextInt(middleAreaTop - 100) + middleAreaTop + middleAreaHeight; // Jaga hati tetap lebih rendah
            
            speedX = random.nextInt(HEART_SPEED_MAX - HEART_SPEED_MIN + 1) + HEART_SPEED_MIN;
        }          // Buat hati dengan poin yang sesuai berdasarkan tipe
        int points;
        switch (type) {
            case 0: points = 3; break; // Biru
            case 1: points = 4; break; // Hijau
            case 2: points = 5; break; // Kuning
            case 3: points = 6; break; // Merah
            case 4: points = 7; break; // Oranye
            case 5: points = 2; break; // Ungu
            case 6: points = -12; break; // Hati rusak - poin negatif
            default: points = 2; break; // Default fallback
        }
        
        System.out.println("Memunculkan hati pada posisi: (" + x + ", " + y + ")");
        hearts.add(new Heart(new Point(x, y), speedX, type, points));
    }
      // Lempar laso ke titik target
    public void throwLasso(Point target) {
        if (isRunning && lasso == null) {
            lasso = new Lasso(new Point(playerPosition.x, playerPosition.y), target);
        }
    }
    
    // Pindahkan pemain
    public void movePlayer(int dx, int dy) {
        if (isRunning) {
            // Perbarui posisi pemain dengan pemeriksaan batas
            int newX = playerPosition.x + dx * PLAYER_SPEED;
            int newY = playerPosition.y + dy * PLAYER_SPEED;
            
            // Perbarui arah menghadap
            if (dx > 0) {
                facingRight = true;
            } else if (dx < 0) {
                facingRight = false;
            }
            
            // Pastikan pemain tetap dalam batas layar
            newX = Math.max(20, Math.min(SCREEN_WIDTH - 50, newX));
            newY = Math.max(20, Math.min(SCREEN_HEIGHT - 50, newY));
            
            playerPosition.x = newX;
            playerPosition.y = newY;
        }
    }
      // Metode setter untuk melanjutkan permainan dengan skor sebelumnya
    public void setScore(int score) {
        System.out.println("Mengatur skor ke: " + score);
        this.score = score;
    }
    
    public void setHeartsCollected(int heartsCollected) {
        System.out.println("Mengatur hati yang dikumpulkan ke: " + heartsCollected);
        this.heartsCollected = heartsCollected;
    }
    
    // Getter
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
      // Kelas dalam untuk objek Hati - diubah dari tingkat kelas ke visibilitas publik
    public class Heart {
        private Point position;
        private int type; // 0=biru, 1=hijau, 2=oranye, 3=kuning, 4=ungu, 5=merah
        private int speedX;
        private int points;
        private boolean isCaught;
        private boolean returnedToPlayer; // Bendera untuk melacak jika hati telah mencapai pemain
        private Lasso lasso; // Referensi ke laso yang menangkap hati ini
        
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
