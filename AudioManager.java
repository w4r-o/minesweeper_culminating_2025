import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.FloatControl;

/**
 * Manages audio playback for sound effects and background music in the game.
 * This class provides static methods to play, loop, and stop audio clips,
 * as well as control their volume.
 *
 * @author Aaron Jiang
 * @author Leo Tan
 * @version 2.1.0
 * @since 2025-06-14
 */
public class AudioManager {

    // Static field to hold the background music clip, allowing it to be controlled globally.
    private static Clip backgroundMusicClip;

    /**
     * Plays a sound effect once.
     * The sound is played on a new thread to avoid blocking the UI thread.
     *
     * @param filePath The path to the sound file (e.g., "assets/kaboom.wav").
     */
    public static void playSoundEffect(String filePath) {
        // Create a new thread for playing the sound to prevent UI freezing
        new Thread(() -> {
            try {
                // Create a File object from the given file path
                File soundFile = new File(filePath);
                // Check if the sound file exists
                if (!soundFile.exists()) {
                    System.err.println("Sound file not found: " + filePath);
                    return; // Exit if file not found
                }
                // Get an AudioInputStream from the sound file
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                // Get a Clip instance for playing the audio
                Clip clip = AudioSystem.getClip();
                // Open the audio stream with the clip
                clip.open(audioStream);
                // Set the volume for the sound effect (e.g., 150% volume)
                setVolume(clip, 1.5f);
                // Start playing the clip
                clip.start();
                // Add a LineListener to close the clip and audio stream when playback stops
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close(); // Close the clip
                        try {
                            audioStream.close(); // Close the audio stream
                        } catch (IOException e) {
                            e.printStackTrace(); // Print stack trace if closing fails
                        }
                    }
                });
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace(); // Print stack trace for audio-related exceptions
            }
        }).start(); // Start the new thread
    }

    /**
     * Plays background music, looping continuously.
     * If music is already playing, it will be stopped and closed before starting the new one.
     *
     * @param filePath The path to the music file (e.g., "assets/bg_music.wav").
     */
    public static void playBackgroundMusic(String filePath) {
        // Stop and close existing background music if it's running
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
            backgroundMusicClip.close();
        }
        try {
            // Create a File object from the given file path
            File musicFile = new File(filePath);
            // Check if the music file exists
            if (!musicFile.exists()) {
                System.err.println("Music file not found: " + filePath);
                return; // Exit if file not found
            }
            // Get an AudioInputStream from the music file
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            // Get a Clip instance for playing the audio
            backgroundMusicClip = AudioSystem.getClip();
            // Open the audio stream with the clip
            backgroundMusicClip.open(audioStream);
            // Set the volume for the background music (e.g., 50% volume)
            setVolume(backgroundMusicClip, 0.5f);
            // Loop the background music continuously
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            // Start playing the clip
            backgroundMusicClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace(); // Print stack trace for audio-related exceptions
        }
    }

    /**
     * Stops the currently playing background music.
     * If no music is playing, this method does nothing.
     */
    public static void stopBackgroundMusic() {
        // Check if background music clip exists and is running
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop(); // Stop the music
            backgroundMusicClip.close(); // Close the clip
        }
    }

    /**
     * Sets the volume of a given Clip.
     * This method adjusts the master gain control of the audio clip.
     *
     * @param clip The Clip to adjust volume for.
     * @param percentage The volume percentage (e.g., 0.5f for 50%, 1.5f for 150%).
     *                   Values greater than 1.0f can amplify the sound.
     */
    private static void setVolume(Clip clip, float percentage) {
        // Check if the clip supports the MASTER_GAIN control
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            // Get the FloatControl for MASTER_GAIN
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            // Calculate the range of the gain control
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            // Calculate the target gain value based on the percentage
            float gain = (range * percentage) + gainControl.getMinimum();
            // Set the gain control value, ensuring it does not exceed the maximum allowed gain
            gainControl.setValue(Math.min(gain, gainControl.getMaximum()));
        }
    }
}
