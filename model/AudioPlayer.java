package model;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;

public class AudioPlayer {
    private static AudioPlayer instance;
    private HashMap<String, Clip> clips;
    private boolean soundEnabled = true;

    // Private constructor for singleton pattern
    private AudioPlayer() {
        clips = new HashMap<>();
        loadSounds();
    }

    // Singleton instance getter
    public static synchronized AudioPlayer getInstance() {
        if (instance == null) {
            instance = new AudioPlayer();
        }
        return instance;
    }    // Load all game sounds
    private void loadSounds() {
        try {
            // Try both MP3 and WAV formats for each sound
            // Game start sound
            boolean gameStartLoaded = false;
            try {
                loadSound("game_start", "assets/sound game start.mp3");
                gameStartLoaded = true;
            } catch (Exception e) {
                System.out.println("Couldn't load MP3, trying WAV: " + e.getMessage());
                try {
                    loadSound("game_start", "sounds/game_start.wav");
                    gameStartLoaded = true;
                } catch (Exception e2) {
                    System.out.println("Couldn't load WAV either: " + e2.getMessage());
                }
            }
            if (!gameStartLoaded) {
                System.out.println("WARNING: game_start sound could not be loaded in any format");
            }
            
            // In-game music
            boolean inGameLoaded = false;
            try {
                loadSound("ingame", "assets/sound ingame.mp3");
                inGameLoaded = true;
            } catch (Exception e) {
                System.out.println("Couldn't load MP3, trying WAV: " + e.getMessage());
                try {
                    loadSound("ingame", "sounds/game_soundtrack.wav");
                    inGameLoaded = true;
                } catch (Exception e2) {
                    System.out.println("Couldn't load WAV either: " + e2.getMessage());
                }
            }
            if (!inGameLoaded) {
                System.out.println("WARNING: ingame sound could not be loaded in any format");
            }
            
            // Achievement sound
            boolean achievementLoaded = false;
            try {
                loadSound("achievement", "assets/sound achivement.mp3");
                achievementLoaded = true;
            } catch (Exception e) {
                System.out.println("Couldn't load MP3, trying WAV: " + e.getMessage());
                try {
                    loadSound("achievement", "sounds/game_bonus.wav");
                    achievementLoaded = true;
                } catch (Exception e2) {
                    System.out.println("Couldn't load WAV either: " + e2.getMessage());
                }
            }
            if (!achievementLoaded) {
                System.out.println("WARNING: achievement sound could not be loaded in any format");
            }
            
            // Character change sound
            boolean characterChangeLoaded = false;
            try {
                loadSound("character_change", "assets/sound berubah.mp3");
                characterChangeLoaded = true;
            } catch (Exception e) {
                System.out.println("Couldn't load MP3, trying WAV: " + e.getMessage());
                try {
                    loadSound("character_change", "sounds/game_over.wav");
                    characterChangeLoaded = true;
                } catch (Exception e2) {
                    System.out.println("Couldn't load WAV either: " + e2.getMessage());
                }
            }
            if (!characterChangeLoaded) {
                System.out.println("WARNING: character_change sound could not be loaded in any format");
            }
            
            System.out.println("Audio initialization completed");
        } catch (Exception e) {
            System.out.println("Error in sound initialization: " + e.getMessage());
            e.printStackTrace();
            soundEnabled = false;
        }
    }

    // Load individual sound
    private void loadSound(String name, String path) {
        try {
            File soundFile = new File(path);
            if (!soundFile.exists()) {
                System.out.println("Warning: Sound file not found: " + path);
                return;
            }
            
            // Print debug info
            System.out.println("Attempting to load sound: " + path);
            
            // Try to get AudioInputStream - this is where MP3 loading might fail
            AudioInputStream audioIn = null;
            try {
                audioIn = AudioSystem.getAudioInputStream(soundFile);
                System.out.println("AudioInputStream created for: " + path);
            } catch (UnsupportedAudioFileException e) {
                System.out.println("Unsupported audio format for: " + path + ". Error: " + e.getMessage());
                return;
            }
            
            // Get clip and open the stream
            try {
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clips.put(name, clip);
                System.out.println("Successfully loaded sound: " + name + " from " + path);
            } catch (Exception e) {
                System.out.println("Failed to create or open clip for: " + path + ". Error: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Error in loadSound for " + name + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Play sound once
    public void playSound(String name) {
        if (!soundEnabled) return;
        
        try {
            Clip clip = clips.get(name);
            if (clip != null) {
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.setFramePosition(0);
                clip.start();
            } else {
                System.out.println("Sound not found: " + name);
            }
        } catch (Exception e) {
            System.out.println("Error playing sound " + name + ": " + e.getMessage());
        }
    }

    // Loop sound continuously
    public void loopSound(String name) {
        if (!soundEnabled) return;
        
        try {
            Clip clip = clips.get(name);
            if (clip != null) {
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.setFramePosition(0);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                System.out.println("Sound not found: " + name);
            }
        } catch (Exception e) {
            System.out.println("Error looping sound " + name + ": " + e.getMessage());
        }
    }

    // Stop specific sound
    public void stopSound(String name) {
        try {
            Clip clip = clips.get(name);
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
        } catch (Exception e) {
            System.out.println("Error stopping sound " + name + ": " + e.getMessage());
        }
    }

    // Stop all sounds
    public void stopAllSounds() {
        for (Clip clip : clips.values()) {
            if (clip.isRunning()) {
                clip.stop();
            }
        }
    }
    
    // Enable/disable sounds
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopAllSounds();
        }
    }
    
    // Get sound enabled state
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
}
