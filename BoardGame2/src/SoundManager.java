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

    // Background music management
    private Clip currentBackgroundMusic;
    private String currentBgmName;
    private Clip previousBackgroundMusic;
    private String previousBgmName;

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

        // Sound effects
        loadSound("dice_roll", basePath + "dice_roll.wav");
        loadSound("move", basePath + "move.wav");
        loadSound("collect_points", basePath + "collect_points.wav");
        loadSound("double_turn", basePath + "double_turn.wav");
        loadSound("dijkstra", basePath + "dijkstra.wav");
        loadSound("exact_landing", basePath + "exact_landing.wav");
        loadSound("perfect_landing", basePath + "perfect_landing.wav");
        loadSound("game_over", basePath + "game_over.wav");
        loadSound("button_click", basePath + "button_click.wav");

        // Background music
        loadSound("bgm_main", basePath + "bgm_main.wav");
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

                // Convert linear volume (0.0 to 1.0) to decibels
                // Using a better curve for more natural volume control
                float min = volumeControl.getMinimum();
                float max = volumeControl.getMaximum();

                float gain;
                if (volume <= 0.0f) {
                    gain = min; // Mute
                } else {
                    // Logarithmic scale for more natural volume perception
                    // This makes the slider feel more linear to human ears
                    float range = max - min;
                    gain = min + (range * volume);
                }

                volumeControl.setValue(gain);
            } catch (Exception e) {
                System.err.println("Unable to set volume for clip");
            }
        }
    }

    public float getVolume() {
        return volume;
    }

    // ========== BACKGROUND MUSIC METHODS ==========

    /**
     * Start playing background music on loop
     */
    public void playBackgroundMusic(String musicName) {
        if (!soundEnabled) return;

        Clip musicClip = soundClips.get(musicName);
        if (musicClip != null) {
            // Stop current background music if any
            if (currentBackgroundMusic != null && currentBackgroundMusic.isRunning()) {
                currentBackgroundMusic.stop();
            }

            // Set new background music
            currentBackgroundMusic = musicClip;
            currentBgmName = musicName;

            // Reset and loop infinitely
            musicClip.setFramePosition(0);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);

            System.out.println("🎵 Playing background music: " + musicName);
        } else {
            System.err.println("Background music not found: " + musicName);
        }
    }

    /**
     * Temporarily switch to different background music (like scoreboard music)
     * Will remember the previous music to resume later
     */
    public void playTemporaryBackgroundMusic(String musicName) {
        if (!soundEnabled) return;

        // Save current background music info
        if (currentBackgroundMusic != null) {
            previousBackgroundMusic = currentBackgroundMusic;
            previousBgmName = currentBgmName;
            currentBackgroundMusic.stop();
        }

        // Play temporary music
        Clip tempMusic = soundClips.get(musicName);
        if (tempMusic != null) {
            currentBackgroundMusic = tempMusic;
            currentBgmName = musicName;

            tempMusic.setFramePosition(0);
            tempMusic.loop(Clip.LOOP_CONTINUOUSLY);

            System.out.println("🎵 Playing temporary background music: " + musicName);
        }
    }

    /**
     * Resume the previous background music (after scoreboard music ends)
     */
    public void resumePreviousBackgroundMusic() {
        if (!soundEnabled) return;

        if (previousBackgroundMusic != null) {
            // Stop current music
            if (currentBackgroundMusic != null && currentBackgroundMusic.isRunning()) {
                currentBackgroundMusic.stop();
            }

            // Resume previous music
            currentBackgroundMusic = previousBackgroundMusic;
            currentBgmName = previousBgmName;

            currentBackgroundMusic.setFramePosition(0);
            currentBackgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);

            System.out.println("🎵 Resumed background music: " + currentBgmName);

            // Clear previous reference
            previousBackgroundMusic = null;
            previousBgmName = null;
        }
    }

    /**
     * Stop all background music
     */
    public void stopBackgroundMusic() {
        if (currentBackgroundMusic != null && currentBackgroundMusic.isRunning()) {
            currentBackgroundMusic.stop();
            System.out.println("🎵 Stopped background music");
        }
    }

    /**
     * Play a sound that temporarily pauses background music
     * Background music will resume after the sound finishes
     */
    public void playSoundWithMusicPause(String soundName) {
        if (!soundEnabled) return;

        Clip clip = soundClips.get(soundName);
        if (clip != null) {
            // Save current background music
            if (currentBackgroundMusic != null && currentBackgroundMusic.isRunning()) {
                previousBackgroundMusic = currentBackgroundMusic;
                previousBgmName = currentBgmName;
                currentBackgroundMusic.stop();
            }

            // Play the sound
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();

            // Add listener to resume music when sound finishes
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    // Small delay before resuming music
                    javax.swing.Timer resumeTimer = new javax.swing.Timer(500, e -> {
                        resumePreviousBackgroundMusic();
                    });
                    resumeTimer.setRepeats(false);
                    resumeTimer.start();
                }
            });

            System.out.println("🔊 Playing sound with music pause: " + soundName);
        } else {
            System.err.println("Sound not found: " + soundName);
        }
    }

    /**
     * Check if background music is currently playing
     */
    public boolean isBackgroundMusicPlaying() {
        return currentBackgroundMusic != null && currentBackgroundMusic.isRunning();
    }

    public void cleanup() {
        stopAllSounds();
        stopBackgroundMusic();
        for (Clip clip : soundClips.values()) {
            clip.close();
        }
        soundClips.clear();
        currentBackgroundMusic = null;
        previousBackgroundMusic = null;
    }
}