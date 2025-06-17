import javax.sound.sampled.*;
public class MusicPlayer {
    private Clip clip;

    // Konstruktor untuk memuat dan memutar suara
    public MusicPlayer(String musicPath) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResource(musicPath));
            clip = AudioSystem.getClip();
            clip.open(audioIn);
        } catch (Exception e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }

    // mulai sound
    public void play() {
        if (clip != null) {
            clip.setFramePosition(0);  // Memulai suara dari awal
            clip.start();
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }
    public void loop() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Looping
        }
    }
}
