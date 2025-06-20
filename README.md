# AZZAM LOVE GAME

## Deskripsi Aplikasi
Azzam Love Game adalah permainan berbasis Java yang menggabungkan unsur keterampilan, refleks, dan strategi. Pemain mengendalikan karakter Azzam untuk menangkap berbagai macam hati dengan menggunakan lasso. Setiap warna hati memiliki nilai poin yang berbeda, dan tujuannya adalah mengumpulkan poin sebanyak mungkin dalam waktu 60 detik. Tantangan utamanya adalah menangkap hati yang memberikan poin positif sambil menghindari hati rusak (Hati Potek) yang mengurangi poin.

## Fitur Utama
- **Sistem Login & Database**: Menyimpan dan melacak skor tertinggi pemain
- **Grafis 2D Interaktif**: Karakter yang dapat dikendalikan dan animasi yang menarik
- **Efek Suara & Musik**: Musik latar yang dinamis dan efek suara saat aksi dilakukan
- **Sistem Peringkat**: Papan skor yang menampilkan peringkat pemain
- **Mekanisme Lasso**: Menangkap hati dengan lasso dan menariknya kembali
- **Emosi Karakter**: Ekspresi karakter berubah berdasarkan skor

## Cara Bermain

### Kontrol
- **Panah Atas/Bawah/Kiri/Kanan**: Mengendalikan pergerakan Azzam
- **Klik Mouse**: Melempar lasso ke arah kursor untuk menangkap hati
- **Spasi/Esc**: Mengakhiri permainan dan kembali ke menu utama

### Alur Permainan
1. Masukkan nama pengguna di menu utama
2. Tekan tombol "Play" untuk memulai permainan
3. Kenddalikan Azzam untuk menangkap hati yang bergerak
4. Kumpulkan poin sebanyak mungkin dalam waktu 60 detik
5. Hati yang tertangkap akan bergerak ke pemain terlebih dahulu, baru kemudian menuju karakter perempuan
6. Setelah permainan berakhir, skor akan disimpan ke database

### Jenis Hati & Poin
- **Hati Biru**: 3 poin
- **Hati Hijau**: 4 poin
- **Hati Kuning**: 5 poin
- **Hati Merah**: 6 poin
- **Hati Orange**: 7 poin
- **Hati Ungu**: 2 poin
- **Hati Potek**: -12 poin (mengurangi skor)

## Teknologi & Struktur Proyek

### Teknologi yang Digunakan
- **Java SE**: Bahasa pemrograman utama
- **Swing & AWT**: Antarmuka grafis
- **JDBC**: Koneksi dan operasi database
- **MySQL**: Penyimpanan data
- **JavaSound API**: Pemutaran audio

### Arsitektur MVVM
Proyek menggunakan arsitektur MVVM (Model-View-ViewModel):

#### Model (`model/`)
- **AudioPlayer.java**: Mengelola pemutaran musik dan efek suara
- **DatabaseManager.java**: Mengelola koneksi dan operasi database
- **PlayerResult.java**: Mewakili hasil pemain (nama pengguna, skor, jumlah hati)

#### View (`view/`)
- **MainMenuView.java**: Antarmuka menu utama
- **GamePanel.java**: Panel permainan dan pemrosesan grafis

#### ViewModel (`viewmodel/`)
- **GameEngine.java**: Logika utama permainan dan pengelolaan status
- **InputController.java**: Menangani input pengguna (keyboard dan mouse)

### File Pendukung
- **Main.java**: Titik masuk aplikasi
- **assets/**: Folder berisi gambar dan file suara
- **lib/**: Librari tambahan (MySQL connector, MP3 player)

## Persyaratan Sistem

### Perangkat Keras
- CPU: 1.5 GHz atau lebih tinggi
- RAM: 1 GB atau lebih
- Ruang Disk: 100 MB ruang kosong

### Perangkat Lunak
- Java Runtime Environment (JRE) 8 atau lebih tinggi
- MySQL Server 5.7 atau lebih tinggi

## Konfigurasi Database
Game ini menggunakan MySQL untuk menyimpan skor pemain. Database akan dibuat otomatis saat pertama kali aplikasi dijalankan.

### Pengaturan Default
- **Host**: localhost
- **Port**: 3306
- **Database**: azzam_love_db
- **Username**: root
- **Password**: (kosong)

## Fitur Teknis

### Mekanisme Permainan
1. **Spawn Hati**: Hati muncul secara acak dari sisi atas dan bawah layar
2. **Deteksi Tabrakan**: Sistem deteksi tabrakan antara lasso dan hati
3. **Pergerakan Hati**: Pergerakan hati yang tertangkap (menuju pemain, lalu menuju gadis)
4. **Sistem Poin**: Kalkulasi nilai berdasarkan jenis hati
5. **Perubahan Emosi**: Perubahan ekspresi karakter berdasarkan skor

### Mekanisme Lasso
- Lasso dilempar ke arah klik mouse
- Lasso hanya dapat menangkap satu hati per lemparan
- Setelah menangkap hati, lasso secara otomatis kembali ke pemain
- Hati yang tertangkap menempel pada ujung lasso saat ditarik kembali

### Sistem Suara
- **playSound()**: Memutar efek suara satu kali
- **playInGameMusic()**: Memutar musik latar yang akan otomatis restart saat selesai
- **stopAllSounds()**: Menghentikan semua suara saat permainan berakhir

## Pengembangan Lebih Lanjut

### Fitur Potensial
- Mode permainan tambahan
- Sistem level dengan tingkat kesulitan yang berbeda
- Power-up dan item khusus
- Mode multipemain
- Integrasi dengan layanan online untuk skor global

## Pemecahan Masalah

### Masalah Umum
1. **Database Error**:
   - Pastikan MySQL server berjalan
   - Periksa pengaturan koneksi di DatabaseManager.java

2. **Masalah Suara**:
   - Pastikan file audio ada di folder assets/
   - Periksa librari audio di folder lib/

3. **Kinerja Lambat**:
   - Kurangi jumlah hati di layar
   - Periksa penggunaan memori

## Tentang Pengembang
Game ini dibuat sebagai proyek pembelajaran DPBO (Desain dan Pemrograman Berorientasi Objek). Proyek ini mendemonstrasikan penerapan konsep OOP, penggunaan framework GUI, dan implementasi database dalam aplikasi permainan.

## Lisensi
© 2025 DPBO TMDD. Hak cipta dilindungi.
