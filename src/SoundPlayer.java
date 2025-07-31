import javax.sound.sampled.*;
import java.net.URL;

public class SoundPlayer {
    private Clip backgroundClip;
    private boolean soundEnabled = true;
    private boolean isMuted = false;
    private boolean isPlaying = false; // Track if music is currently playing

    public void playBackgroundMusic(String path) {
        if (!soundEnabled) return;

        // IMPORTANT: Stop any existing music before starting new one
        stopBackgroundMusic();

        try {
            // Try different path formats
            URL res = getClass().getResource("/" + path);
            if (res == null) {
                res = getClass().getResource("/music/" + path);
            }
            if (res == null) {
                res = getClass().getResource("/src/assets/" + path);
            }
            if (res == null) {
                System.err.println("❌ Could not find audio file: " + path);
                return;
            }

            System.out.println("🔍 Found music at: " + res);

            // Get audio input stream
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(res);
            AudioFormat originalFormat = audioIn.getFormat();
            System.out.println("🎵 Original audio format: " + originalFormat);

            // Convert to a supported PCM format
            AudioFormat targetFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    originalFormat.getSampleRate() == AudioSystem.NOT_SPECIFIED ? 44100 : originalFormat.getSampleRate(),
                    16, // 16-bit
                    originalFormat.getChannels() == AudioSystem.NOT_SPECIFIED ? 2 : originalFormat.getChannels(),
                    (originalFormat.getChannels() == AudioSystem.NOT_SPECIFIED ? 2 : originalFormat.getChannels()) * 2, // frame size
                    originalFormat.getSampleRate() == AudioSystem.NOT_SPECIFIED ? 44100 : originalFormat.getSampleRate(),
                    false // little endian
            );

            System.out.println("🎵 Target audio format: " + targetFormat);

            // Check if conversion is needed
            AudioInputStream convertedAudioIn = audioIn;
            if (!AudioSystem.isConversionSupported(targetFormat, originalFormat)) {
                System.err.println("❌ Audio conversion not supported");
                System.err.println("💡 Please convert your audio file to 16-bit PCM WAV format");
                return;
            }

            if (!originalFormat.matches(targetFormat)) {
                convertedAudioIn = AudioSystem.getAudioInputStream(targetFormat, audioIn);
                System.out.println("🔄 Converting audio format...");
            }

            // Create and start the clip
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(convertedAudioIn);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
            isPlaying = true; // Mark as playing
            System.out.println("✅ Background music started successfully");

        } catch (UnsupportedAudioFileException e) {
            System.err.println("❌ Unsupported audio format for: " + path);
            System.err.println("💡 Supported formats: " + getSupportedFormats());
            System.err.println("💡 Try converting to 16-bit PCM WAV format using Audacity or similar tool");
            soundEnabled = false;
        } catch (Exception e) {
            System.err.println("❌ Error playing background music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getSupportedFormats() {
        AudioFileFormat.Type[] types = AudioSystem.getAudioFileTypes();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < types.length; i++) {
            sb.append(types[i].toString());
            if (i < types.length - 1) sb.append(", ");
        }
        return sb.toString();
    }

    public void stopBackgroundMusic() {
        if (backgroundClip != null) {
            if (backgroundClip.isRunning()) {
                backgroundClip.stop();
                System.out.println("🛑 Background music stopped");
            }
            backgroundClip.close();
            backgroundClip = null; // Clear the reference
            isPlaying = false; // Mark as not playing
        }
    }

    // NEW METHOD: Check if music is currently playing
    public boolean isPlaying() {
        return isPlaying && backgroundClip != null && backgroundClip.isRunning();
    }

    // NEW METHOD: Resume music if it was playing
    public void resumeBackgroundMusic() {
        if (backgroundClip != null && !backgroundClip.isRunning() && isPlaying) {
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
            System.out.println("▶️ Background music resumed");
        }
    }

    // NEW METHOD: Pause music without stopping completely
    public void pauseBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            // Don't set isPlaying to false - we want to remember it was playing
            System.out.println("⏸️ Background music paused");
        }
    }

    public void playSoundEffect(String path) {
        if (!soundEnabled) return;

        try {
            URL res = getClass().getResource("/" + path);
            if (res == null) {
                res = getClass().getResource("/music/" + path);
            }
            if (res == null) {
                res = getClass().getResource("/src/assets/" + path);
            }
            if (res == null) {
                System.err.println("❌ Could not find sound effect: " + path);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(res);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();

            // Clean up after sound finishes
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });

        } catch (Exception e) {
            System.err.println("❌ Error playing sound effect: " + e.getMessage());
        }
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopBackgroundMusic();
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void toggleMute() {
        isMuted = !isMuted;
        if (backgroundClip != null && soundEnabled) {
            try {
                if (backgroundClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl volume = (FloatControl) backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);
                    if (isMuted) {
                        volume.setValue(volume.getMinimum()); // Mute
                    } else {
                        volume.setValue(0); // Reset to normal volume
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ Error controlling volume: " + e.getMessage());
            }
        }
    }

    public boolean isMuted() {
        return isMuted;
    }
}