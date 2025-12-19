import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

// ============= SoundManager.java =============
public class SoundManager {
    private static SoundManager instance;
    private Map<String, Clip> soundClips;
    private boolean soundEnabled = true;
    private float volume = 0.8f; // 0.0 to 1.0

    private SoundManager() {
        soundClips = new HashMap<>();
        loadSounds();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void loadSounds() {
        // Load all sound files
        // Use absolute path for sounds folder at project root
        String basePath = System.getProperty("user.dir") + File.separator + "sounds" + File.separator;

        loadSound("dice_roll", basePath + "dice_roll.wav");
        loadSound("move", basePath + "move.wav");
        loadSound("collect_points", basePath + "collect_points.wav");
        loadSound("double_turn", basePath + "double_turn.wav");
        loadSound("dijkstra", basePath + "dijkstra.wav");
        loadSound("exact_landing", basePath + "exact_landing.wav");
        loadSound("perfect_landing", basePath + "perfect_landing.wav");
        loadSound("game_over", basePath + "game_over.wav");
        loadSound("button_click", basePath + "button_click.wav");
    }

    private void loadSound(String name, String filePath) {
        try {
            // Try loading from resources first (for files in src folder)
            URL soundURL = getClass().getClassLoader().getResource(filePath);

            AudioInputStream audioInputStream;
            if (soundURL != null) {
                audioInputStream = AudioSystem.getAudioInputStream(soundURL);
                System.out.println("✓ Loaded sound from resources: " + name);
            } else {
                // Try loading from file system (absolute path)
                File soundFile = new File(filePath);
                if (soundFile.exists()) {
                    audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                    System.out.println("✓ Loaded sound from file: " + name + " at " + soundFile.getAbsolutePath());
                } else {
                    // Try alternative paths
                    File altPath1 = new File("BoardGame2/sounds/" + name + ".wav");
                    File altPath2 = new File("../sounds/" + name + ".wav");

                    if (altPath1.exists()) {
                        audioInputStream = AudioSystem.getAudioInputStream(altPath1);
                        System.out.println("✓ Loaded sound from alternative path: " + name);
                    } else if (altPath2.exists()) {
                        audioInputStream = AudioSystem.getAudioInputStream(altPath2);
                        System.out.println("✓ Loaded sound from alternative path 2: " + name);
                    } else {
                        System.err.println("✗ Sound file not found in any location: " + filePath);
                        System.err.println("  Tried: " + soundFile.getAbsolutePath());
                        System.err.println("  Tried: " + altPath1.getAbsolutePath());
                        System.err.println("  Tried: " + altPath2.getAbsolutePath());
                        return;
                    }
                }
            }

            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            // Set volume
            setClipVolume(clip, volume);

            soundClips.put(name, clip);

        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio format for: " + filePath);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error loading sound file: " + filePath);
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable for: " + filePath);
            e.printStackTrace();
        }
    }

    public void playSound(String soundName) {
        if (!soundEnabled) return;

        Clip clip = soundClips.get(soundName);
        if (clip != null) {
            // Stop and reset clip if it's already playing
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        } else {
            System.err.println("Sound not found: " + soundName);
        }
    }

    public void playSoundWithDelay(String soundName, int delayMillis) {
        javax.swing.Timer timer = new javax.swing.Timer(delayMillis, e -> {
            playSound(soundName);
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void stopSound(String soundName) {
        Clip clip = soundClips.get(soundName);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void stopAllSounds() {
        for (Clip clip : soundClips.values()) {
            if (clip.isRunning()) {
                clip.stop();
            }
        }
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopAllSounds();
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setVolume(float volume) {
        // Clamp volume between 0.0 and 1.0
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));

        // Update volume for all loaded clips
        for (Clip clip : soundClips.values()) {
            setClipVolume(clip, this.volume);
        }
    }

    private void setClipVolume(Clip clip, float volume) {
        if (clip != null) {
            try {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float range = volumeControl.getMaximum() - volumeControl.getMinimum();
                float gain = (range * volume) + volumeControl.getMinimum();
                volumeControl.setValue(gain);
            } catch (Exception e) {
                System.err.println("Unable to set volume for clip");
            }
        }
    }

    public float getVolume() {
        return volume;
    }

    public void cleanup() {
        stopAllSounds();
        for (Clip clip : soundClips.values()) {
            clip.close();
        }
        soundClips.clear();
    }
}