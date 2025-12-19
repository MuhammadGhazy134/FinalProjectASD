import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * SoundManager - Shared sound system for all board games
 * Place this file in a shared package that both games can access
 */
public class SoundManager {
    private static SoundManager instance;
    private Map<String, Clip> soundClips;
    private boolean soundEnabled = true;
    private float volume = 0.8f; // 0.0 to 1.0

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
        String basePath = System.getProperty("user.dir") + File.separator + "sounds" + File.separator;

        // Sound effects
        loadSound("dice_roll", basePath + "dice_roll.wav");
        loadSound("move", basePath + "move.wav");
        loadSound("collect_points", basePath + "collect_points.wav");
        loadSound("double_turn", basePath + "double_turn.wav");
        loadSound("dijkstra", basePath + "dijkstra.wav");
        loadSound("game_over", basePath + "game_over.wav");
        loadSound("button_click", basePath + "button_click.wav");

        // Background music
        loadSound("bgm_main", basePath + "bgm_main.wav");
    }

    private void loadSound(String name, String filePath) {
        try {
            URL soundURL = getClass().getClassLoader().getResource(filePath);
            AudioInputStream audioInputStream;

            if (soundURL != null) {
                audioInputStream = AudioSystem.getAudioInputStream(soundURL);
                System.out.println("✓ Loaded sound from resources: " + name);
            } else {
                File soundFile = new File(filePath);
                if (soundFile.exists()) {
                    audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                    System.out.println("✓ Loaded sound from file: " + name);
                } else {
                    File altPath1 = new File("sounds/" + name + ".wav");
                    if (altPath1.exists()) {
                        audioInputStream = AudioSystem.getAudioInputStream(altPath1);
                        System.out.println("✓ Loaded sound from alternative path: " + name);
                    } else {
                        System.err.println("✗ Sound file not found: " + filePath);
                        return;
                    }
                }
            }

            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            setClipVolume(clip, volume);
            soundClips.put(name, clip);

        } catch (Exception e) {
            System.err.println("Error loading sound: " + filePath);
            e.printStackTrace();
        }
    }

    public void playSound(String soundName) {
        if (!soundEnabled) return;

        Clip clip = soundClips.get(soundName);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
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

    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        for (Clip clip : soundClips.values()) {
            setClipVolume(clip, this.volume);
        }
    }

    private void setClipVolume(Clip clip, float volume) {
        if (clip != null) {
            try {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float min = volumeControl.getMinimum();
                float max = volumeControl.getMaximum();
                float gain = volume <= 0.0f ? min : min + ((max - min) * volume);
                volumeControl.setValue(gain);
            } catch (Exception e) {
                System.err.println("Unable to set volume for clip");
            }
        }
    }

    public void playBackgroundMusic(String musicName) {
        if (!soundEnabled) return;

        Clip musicClip = soundClips.get(musicName);
        if (musicClip != null) {
            if (currentBackgroundMusic != null && currentBackgroundMusic.isRunning()) {
                currentBackgroundMusic.stop();
            }

            currentBackgroundMusic = musicClip;
            currentBgmName = musicName;
            musicClip.setFramePosition(0);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopBackgroundMusic() {
        if (currentBackgroundMusic != null && currentBackgroundMusic.isRunning()) {
            currentBackgroundMusic.stop();
        }
    }

    public void cleanup() {
        stopAllSounds();
        stopBackgroundMusic();
        for (Clip clip : soundClips.values()) {
            clip.close();
        }
        soundClips.clear();
    }
}