import javax.sound.sampled.*;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private Map<String, Clip> soundMap;
    private boolean enabled;
    private float volume;

    public SoundManager() {
        soundMap = new HashMap<>();
        enabled = true;
        volume = 0.7f; // Default volume (70%)
        loadSounds();
    }

    private void loadSounds() {
        try {
            // Generate simple beep sounds
            soundMap.put("button_click", generateTone(440, 100, 0.3f)); // A4 note
            soundMap.put("maze_generated", generateTone(523, 300, 0.5f)); // C5 note
            soundMap.put("solution_found", generateChord(new int[]{659, 830, 988}, 500, 0.6f)); // E5 chord (success)
            soundMap.put("step", generateTone(392, 60, 0.2f)); // G4 note (short step)
            soundMap.put("wall_remove", generateTone(294, 80, 0.25f)); // D4 note
            soundMap.put("error", generateTone(220, 300, 0.4f)); // A3 note (lower, error)
            soundMap.put("reset", generateTone(349, 200, 0.35f)); // F4 note
            soundMap.put("hover", generateTone(330, 40, 0.15f)); // E4 note for hover

        } catch (Exception e) {
            System.err.println("Error loading sounds: " + e.getMessage());
            enabled = false;
        }
    }

    private Clip generateTone(int frequency, int duration, float amplitude)
            throws LineUnavailableException {
        AudioFormat af = new AudioFormat(44100, 16, 1, true, false);
        Clip clip = AudioSystem.getClip();

        int numSamples = duration * 44100 / 1000;
        byte[] buffer = new byte[numSamples * 2];

        for (int i = 0; i < numSamples; i++) {
            // Add envelope to avoid clicking
            double envelope;
            if (i < 100) {
                envelope = i / 100.0; // Fade in
            } else if (i > numSamples - 100) {
                envelope = (numSamples - i) / 100.0; // Fade out
            } else {
                envelope = 1.0;
            }

            double angle = 2.0 * Math.PI * i * frequency / 44100;
            short sample = (short) (envelope * amplitude * Short.MAX_VALUE * Math.sin(angle));

            buffer[2 * i] = (byte) (sample & 0xFF);
            buffer[2 * i + 1] = (byte) ((sample >> 8) & 0xFF);
        }

        // Use DataLine.Info instead of AudioInputStream
        clip.open(af, buffer, 0, buffer.length);
        return clip;
    }

    private Clip generateChord(int[] frequencies, int duration, float amplitude)
            throws LineUnavailableException {
        AudioFormat af = new AudioFormat(44100, 16, 1, true, false);
        Clip clip = AudioSystem.getClip();

        int numSamples = duration * 44100 / 1000;
        byte[] buffer = new byte[numSamples * 2];

        for (int i = 0; i < numSamples; i++) {
            // Add envelope
            double envelope;
            if (i < 200) {
                envelope = i / 200.0; // Fade in
            } else if (i > numSamples - 200) {
                envelope = (numSamples - i) / 200.0; // Fade out
            } else {
                envelope = 1.0;
            }

            double sampleValue = 0;
            for (int freq : frequencies) {
                double angle = 2.0 * Math.PI * i * freq / 44100;
                sampleValue += Math.sin(angle);
            }
            sampleValue /= frequencies.length; // Average the tones

            short sample = (short) (envelope * amplitude * Short.MAX_VALUE * sampleValue);

            buffer[2 * i] = (byte) (sample & 0xFF);
            buffer[2 * i + 1] = (byte) ((sample >> 8) & 0xFF);
        }

        clip.open(af, buffer, 0, buffer.length);
        return clip;
    }

    public void playSound(String soundName) {
        if (!enabled) return;

        Clip clip = soundMap.get(soundName);
        if (clip != null) {
            // Create a new thread for playback to avoid blocking
            new Thread(() -> {
                try {
                    // Stop if already playing
                    if (clip.isRunning()) {
                        clip.stop();
                    }
                    // Reset to beginning
                    clip.setFramePosition(0);

                    // Set volume
                    if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                        float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                        // Clamp dB value to valid range
                        dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));
                        gainControl.setValue(dB);
                    }

                    clip.start();

                    // Wait for the sound to finish if it's short
                    if (clip.getFrameLength() < 44100) { // Less than 1 second
                        Thread.sleep(clip.getFrameLength() / 44); // Approximate sleep
                    }

                } catch (Exception e) {
                    System.err.println("Error playing sound: " + e.getMessage());
                }
            }).start();
        }
    }

    public void playSoundAsync(String soundName) {
        if (!enabled) return;

        new Thread(() -> playSound(soundName)).start();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            stopAllSounds();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    public float getVolume() {
        return volume;
    }

    public void stopAllSounds() {
        for (Clip clip : soundMap.values()) {
            if (clip.isRunning()) {
                clip.stop();
            }
        }
    }

    public void close() {
        stopAllSounds();
        for (Clip clip : soundMap.values()) {
            clip.close();
        }
        soundMap.clear();
    }
}