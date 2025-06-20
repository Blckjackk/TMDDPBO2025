package model;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.*;

/**
 * AudioPlayer yang mendukung pemutaran file MP3 dan WAV
 * dengan implementasi sederhana.
 */
public class AudioPlayer {
    private static AudioPlayer instance;
    private Map<String, Sound> sounds;
    private boolean soundEnabled = true;
    
    // Mapping nama file suara sesuai kebutuhan game
    private static final String GAME_START_SOUND = "game_start";
    private static final String INGAME_SOUND = "ingame";
    private static final String ACHIEVEMENT_SOUND = "achievement";
    private static final String CHARACTER_CHANGE_SOUND = "character_change";

    // Private constructor untuk singleton pattern
    private AudioPlayer() {
        sounds = new HashMap<>();
        loadSounds();
    }

    // Singleton instance getter
    public static synchronized AudioPlayer getInstance() {
        if (instance == null) {
            instance = new AudioPlayer();
        }
        return instance;
    }
    
    // Load semua suara game
    private void loadSounds() {
        try {
            System.out.println("===== LOADING GAME SOUNDS =====");
            
            // Suara game start
            loadSound(GAME_START_SOUND, "assets/sound game start.wav");
            
            // Suara ingame (background music)
            loadSound(INGAME_SOUND, "assets/sound ingame.wav");
            
            // Suara achievement
            loadSound(ACHIEVEMENT_SOUND, "assets/sound achivement.wav");
            
            // Suara perubahan karakter
            loadSound(CHARACTER_CHANGE_SOUND, "assets/sound berubah.wav");
            
            System.out.println("===== SOUND LOADING COMPLETED =====");
        } catch (Exception e) {
            System.out.println("Error initializing sounds: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Load satu suara berdasarkan nama dan path
    private void loadSound(String name, String path) {
        try {
            File soundFile = new File(path);
            if (!soundFile.exists()) {
                System.out.println("File tidak ditemukan: " + path);
                return;
            }
            
            System.out.println("Loading sound: " + name + " from " + path);
            
            // Buat objek Sound baru dan tambahkan ke map
            Sound sound = new Sound(path);
            sounds.put(name, sound);
            
            System.out.println("Sound loaded successfully: " + name);
        } catch (Exception e) {
            System.out.println("Error loading sound " + name + ": " + e.getMessage());
        }
    }
    
    // Putar suara sekali
    public void playSound(String name) {
        if (!soundEnabled) return;
        
        try {
            Sound sound = sounds.get(name);
            if (sound != null) {
                sound.play();
                System.out.println("Playing sound: " + name);
            } else {
                System.out.println("Sound not found: " + name);
            }
        } catch (Exception e) {
            System.out.println("Error playing sound " + name + ": " + e.getMessage());
        }
    }
    
    // Loop suara terus menerus
    public void loopSound(String name) {
        if (!soundEnabled) return;
        
        try {
            Sound sound = sounds.get(name);
            if (sound != null) {
                sound.loop();
                System.out.println("Looping sound: " + name);
            } else {
                System.out.println("Sound not found: " + name);
            }
        } catch (Exception e) {
            System.out.println("Error looping sound " + name + ": " + e.getMessage());
        }
    }
    
    // Hentikan suara tertentu
    public void stopSound(String name) {
        try {
            Sound sound = sounds.get(name);
            if (sound != null) {
                sound.stop();
                System.out.println("Stopping sound: " + name);
            } else {
                System.out.println("Sound not found: " + name);
            }
        } catch (Exception e) {
            System.out.println("Error stopping sound " + name + ": " + e.getMessage());
        }
    }
    
    // Hentikan semua suara
    public void stopAllSounds() {
        for (Sound sound : sounds.values()) {
            sound.stop();
        }
        System.out.println("All sounds stopped");
    }
    
    // Aktifkan/nonaktifkan suara
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopAllSounds();
        }
    }
    
    // Dapatkan status pengaktifan suara
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    /**
     * Inner class untuk mengelola pemutaran suara MP3 secara individual
     */
    private class Sound {
        private String filePath;
        private Thread playThread;
        private boolean isPlaying = false;
        private boolean isLooping = false;
        
        public Sound(String filePath) {
            this.filePath = filePath;
        }
        
        // Putar suara sekali
        public void play() {
            // Hentikan pemutaran yang sedang berjalan
            stop();
            
            isPlaying = true;
            isLooping = false;
            
            playThread = new Thread(() -> {
                try {
                    playFile(filePath, false);
                } catch (Exception e) {
                    System.out.println("Error playing sound: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            playThread.start();
        }
        
        // Loop suara terus menerus
        public void loop() {
            // Hentikan pemutaran yang sedang berjalan
            stop();
            
            isPlaying = true;
            isLooping = true;
            
            playThread = new Thread(() -> {
                try {
                    while (isPlaying && isLooping) {
                        playFile(filePath, true);
                    }
                } catch (Exception e) {
                    System.out.println("Error looping sound: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            playThread.start();
        }
        
        // Hentikan pemutaran suara
        public void stop() {
            isPlaying = false;
            isLooping = false;
            
            if (playThread != null) {
                try {
                    playThread.interrupt();
                    playThread = null;
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
        
        // Metode untuk memutar file suara
        private void playFile(String filePath, boolean loop) {
            try {
                if (filePath.toLowerCase().endsWith(".mp3")) {
                    playMP3(filePath, loop);
                } else if (filePath.toLowerCase().endsWith(".wav")) {
                    playWAV(filePath, loop);
                }
            } catch (Exception e) {
                System.out.println("Error playing sound file: " + e.getMessage());
            }
        }
        
        // Putar file MP3
        private void playMP3(String filePath, boolean loop) {
            try {
                // Coba gunakan JavaSound dengan library MP3SPI
                System.out.println("Attempting to play MP3 file: " + filePath);
                
                File file = new File(filePath);
                if (!file.exists()) {
                    System.out.println("File tidak ditemukan: " + filePath);
                    return;
                }
                
                try {
                    // Menggunakan MP3SPI (akan bekerja jika JAR sudah ditambahkan ke classpath)
                    System.out.println("Using MP3SPI/JLayer to play MP3");
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                    AudioFormat baseFormat = audioInputStream.getFormat();
                    
                    System.out.println("Audio format: " + baseFormat);
                    System.out.println("Sample rate: " + baseFormat.getSampleRate());
                    System.out.println("Channels: " + baseFormat.getChannels());
                    
                    AudioFormat decodedFormat = new AudioFormat(
                            AudioFormat.Encoding.PCM_SIGNED,
                            baseFormat.getSampleRate(),
                            16,
                            baseFormat.getChannels(),
                            baseFormat.getChannels() * 2,
                            baseFormat.getSampleRate(),
                            false);
                    
                    AudioInputStream decodedInputStream = AudioSystem.getAudioInputStream(decodedFormat, audioInputStream);
                    Clip clip = AudioSystem.getClip();
                    clip.open(decodedInputStream);
                    
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                        }
                    });
                    
                    if (loop) {
                        clip.loop(Clip.LOOP_CONTINUOUSLY);
                        System.out.println("MP3 playing in loop mode");
                    } else {
                        clip.start();
                        System.out.println("MP3 playing once");
                    }
                    
                    System.out.println("MP3 playback started successfully");
                    return;
                } catch (Exception e) {
                    System.out.println("MP3 playback with MP3SPI failed: " + e.getMessage());
                    e.printStackTrace();
                }
                
                // Jika MP3 gagal diputar, coba cari file WAV dengan nama yang sama di folder sounds
                String wavPath = "sounds/" + new File(filePath).getName().replaceAll("\\.mp3$", ".wav");
                File wavFile = new File(wavPath);
                
                if (wavFile.exists()) {
                    System.out.println("Fallback to WAV file: " + wavPath);
                    playWAV(wavPath, loop);
                } else {
                    System.out.println("No WAV fallback found at: " + wavPath);
                }
            } catch (Exception e) {
                System.out.println("MP3 playback error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Putar file WAV
        private void playWAV(String filePath, boolean loop) {
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
                
                if (loop) {
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                } else {
                    clip.start();
                }
            } catch (Exception e) {
                System.out.println("WAV playback error: " + e.getMessage());
            }
        }
        
        /**
         * Khusus untuk musik in-game, putar sekali dan otomatis replay ketika selesai
         */
        public void playAndRestart() {
            // Hentikan pemutaran yang sedang berjalan
            stop();
            
            isPlaying = true;
            isLooping = false; // Not true looping, we'll control restart manually
            
            playThread = new Thread(() -> {
                try {
                    // Keep playing the file as long as isPlaying is true
                    while (isPlaying) {
                        playFileWithAutoRestart(filePath);
                    }
                } catch (Exception e) {
                    System.out.println("Error in playAndRestart: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            playThread.start();
        }
        
        // Putar file WAV dengan auto-restart ketika selesai
        private void playFileWithAutoRestart(String filePath) {
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                
                // Use a semaphore to wait for the clip to finish
                Object lock = new Object();
                
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        synchronized (lock) {
                            lock.notify(); // Notify that clip has stopped
                        }
                        clip.close();
                    }
                });
                
                // Start playing
                clip.start();
                System.out.println("Playing in-game music once...");
                
                // Wait for the clip to finish
                synchronized (lock) {
                    try {
                        lock.wait(); // Wait for notification from LineListener
                    } catch (InterruptedException e) {
                        System.out.println("Playback interrupted");
                        return; // Exit if interrupted
                    }
                }
                
                // If we get here, the clip has finished naturally
                System.out.println("Music finished, restarting...");
                
                // Small delay to prevent potential issues
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // Ignore
                }
                
                // The while loop in playAndRestart will call this method again
            } catch (Exception e) {
                System.out.println("Error in playFileWithAutoRestart: " + e.getMessage());
            }
        }
    }
    
    /**
     * Khusus untuk musik in-game, putar sekali dan otomatis replay ketika selesai
     */
    public void playInGameMusic() {
        if (!soundEnabled) return;
        
        try {
            Sound sound = sounds.get(INGAME_SOUND);
            if (sound != null) {
                sound.playAndRestart();
                System.out.println("Playing in-game music with auto-restart");
            } else {
                System.out.println("In-game sound not found");
            }
        } catch (Exception e) {
            System.out.println("Error playing in-game music: " + e.getMessage());
        }
    }
}
