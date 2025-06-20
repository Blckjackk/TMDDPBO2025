package model;

import java.sql.*;
import java.util.ArrayList;
import java.io.File;

public class DatabaseManager {
    private Connection connection;
    private PreparedStatement statement;
    private static DatabaseManager instance;    // Konstruktor - private untuk pola Singleton
    private DatabaseManager() {
        try {            // Memeriksa apakah file JAR MySQL connector ada
            File jarFile = new File("lib/mysql-connector-j-9.2.0.jar");
            if (!jarFile.exists()) {
                System.out.println("Peringatan: MySQL connector JAR tidak ditemukan di: " + jarFile.getAbsolutePath());
                System.out.println("Memeriksa classpath...");
            }
            
            // Memuat JDBC driver MySQL secara eksplisit
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("MySQL JDBC driver berhasil dimuat!");
            } catch (ClassNotFoundException e) {
                System.out.println("ERROR: MySQL JDBC driver tidak ditemukan dalam classpath.");
                System.out.println("Pastikan mysql-connector-j-9.2.0.jar telah ditambahkan dengan benar ke dependensi proyek Anda.");
                e.printStackTrace();
                return; // Keluar dari konstruktor karena tidak dapat melanjutkan tanpa driver
            }
              // Mencoba menjalin koneksi
            try {
                // Pertama, periksa apakah server dapat dijangkau
                Connection serverConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");
                System.out.println("Berhasil terhubung ke server MySQL!");
                
                // Buat database jika belum ada
                Statement stmt = serverConn.createStatement();
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS azzam_love_db");
                
                // Tutup koneksi server
                stmt.close();
                serverConn.close();
                
                // Hubungkan ke database tertentu
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/azzam_love_db", "root", "");
                System.out.println("Koneksi database berhasil dibuat ke azzam_love_db");
            } catch (SQLException e) {
                System.out.println("Kesalahan koneksi database: " + e.getMessage());
                System.out.println("Pastikan server MySQL Anda berjalan dan dapat diakses.");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Unexpected error in DatabaseManager: " + e.getMessage());
            e.printStackTrace();
        }
    }    // Pola Singleton untuk memastikan hanya ada satu koneksi database
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        } else {
            // Periksa apakah koneksi masih aktif
            try {
                if (instance.connection == null || instance.connection.isClosed()) {
                    System.out.println("Koneksi database telah ditutup, membuat yang baru");
                    instance = new DatabaseManager();
                }
            } catch (SQLException e) {
                System.out.println("Kesalahan memeriksa koneksi database: " + e.getMessage());
                // Coba buat instance baru
                instance = new DatabaseManager();
            }
        }
        return instance;
    }
      // Buat tabel jika belum ada
    public void initializeDatabase() {
        if (connection == null) {
            System.out.println("Tidak dapat menginisialisasi database - tidak ada koneksi tersedia");
            return;
        }
        
        try {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS thasil (" +
                    "username VARCHAR(100) PRIMARY KEY," +
                    "skor INT NOT NULL," +
                    "count INT NOT NULL" +
                    ")";
            statement = connection.prepareStatement(createTableSQL);
            statement.executeUpdate();
            System.out.println("Tabel database berhasil diinisialisasi");
        } catch (SQLException e) {
            System.out.println("Kesalahan inisialisasi database: " + e.getMessage());
            e.printStackTrace();
        }
    }    // Dapatkan semua hasil pemain
    public ArrayList<PlayerResult> getAllResults() {
        ArrayList<PlayerResult> results = new ArrayList<>();
        
        if (connection == null) {
            System.out.println("Tidak dapat mendapatkan hasil - tidak ada koneksi tersedia");
            return results;
        }
        
        try {
            String query = "SELECT * FROM thasil ORDER BY skor DESC";
            statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            
            while (rs.next()) {
                String username = rs.getString("username");
                int skor = rs.getInt("skor");
                int count = rs.getInt("count");
                
                results.add(new PlayerResult(username, skor, count));
            }
        } catch (SQLException e) {
            System.out.println("Error getting results: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }    // Simpan hasil pemain (masukkan yang baru atau perbarui jika sudah ada)
    public void savePlayerResult(PlayerResult playerResult) {
        if (connection == null) {
            System.out.println("Tidak dapat menyimpan hasil - tidak ada koneksi tersedia, mencoba untuk terhubung kembali...");
            try {
                // Coba terhubung kembali
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/azzam_love_db", "root", "");
                System.out.println("Berhasil terhubung kembali ke database");
            } catch (SQLException reconnectEx) {
                System.out.println("Gagal terhubung kembali ke database: " + reconnectEx.getMessage());
                return;
            }
        }
        
        try {
            // Make sure the table exists
            initializeDatabase();
              // Cetak informasi debugging
            System.out.println("===== OPERASI PENYIMPANAN DATABASE =====");
            System.out.println("Menyimpan pemain: " + playerResult.getUsername());
            System.out.println("Skor: " + playerResult.getSkor());
            System.out.println("Hati: " + playerResult.getCount());
              // Periksa apakah username sudah ada
            String checkQuery = "SELECT * FROM thasil WHERE username = ?";
            statement = connection.prepareStatement(checkQuery);
            statement.setString(1, playerResult.getUsername());
            ResultSet rs = statement.executeQuery();
            
            if (rs.next()) {
                // Dapatkan skor dan hati yang ada
                int existingSkor = rs.getInt("skor");
                int existingHearts = rs.getInt("count");
                  System.out.println("Pemain sudah ada dalam database");
                System.out.println("Skor yang ada: " + existingSkor);
                System.out.println("Hati yang ada: " + existingHearts);
                
                // Hanya perbarui jika skor baru lebih tinggi
                if (playerResult.getSkor() > existingSkor) {
                    // Perbarui catatan yang ada dengan skor yang lebih tinggi
                    String updateQuery = "UPDATE thasil SET skor = ?, count = ? WHERE username = ?";
                    statement = connection.prepareStatement(updateQuery);
                    statement.setInt(1, playerResult.getSkor());
                    statement.setInt(2, playerResult.getCount());
                    statement.setString(3, playerResult.getUsername());
                    
                    int rowsAffected = statement.executeUpdate();
                    System.out.println("Catatan diperbarui! Baris yang terpengaruh: " + rowsAffected);
                    System.out.println("Memperbarui catatan pemain: " + playerResult.getUsername() + 
                                    " dengan skor lebih tinggi: " + playerResult.getSkor() + 
                                    " (sebelumnya: " + existingSkor + ")");
                } else {
                    // Tidak perlu pembaruan
                    System.out.println("Tidak perlu pembaruan - skor saat ini lebih baik");
                }            } else {
                // Masukkan catatan baru
                String insertQuery = "INSERT INTO thasil (username, skor, count) VALUES (?, ?, ?)";
                statement = connection.prepareStatement(insertQuery);
                statement.setString(1, playerResult.getUsername());
                statement.setInt(2, playerResult.getSkor());
                statement.setInt(3, playerResult.getCount());
                
                int rowsAffected = statement.executeUpdate();
                System.out.println("Catatan baru dibuat! Baris yang terpengaruh: " + rowsAffected);
                System.out.println("Memasukkan catatan pemain baru: " + playerResult.getUsername() + 
                                " dengan skor: " + playerResult.getSkor());
            }
            System.out.println("===== OPERASI DATABASE SELESAI =====");
        } catch (SQLException e) {
            System.out.println("Error saving player result: " + e.getMessage());
            e.printStackTrace();
        }
    }
      // Tutup koneksi saat aplikasi keluar
    public void closeConnection() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
                System.out.println("Koneksi database ditutup");
            }
        } catch (SQLException e) {
            System.out.println("Kesalahan menutup koneksi: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
