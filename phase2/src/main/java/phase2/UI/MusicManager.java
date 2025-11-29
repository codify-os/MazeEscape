package phase2.UI;

import javazoom.jl.player.Player;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MusicManager handles background music playback for the game.
 * Supports play, stop, mute, and automatic looping of tracks.
 */
public class MusicManager {

    /** Array of track file paths to play. */
    private final String[] tracks;

    /** Index of the current track being played. */
    private int currentTrack = 0;

    /** Flag indicating if music is currently playing. */
    private boolean playing = false;

    /** Flag indicating if music is muted. */
    private boolean muted = false;

    /** JLayer Player instance for decoding and playing mp3 tracks. */
    private Player player;

    /** Executor for running music playback on a separate thread. */
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Constructs a MusicManager with the specified list of tracks.
     *
     * @param tracks array of track file paths (inside resources)
     */
    public MusicManager(String[] tracks) {
        this.tracks = tracks;
    }

    /**
     * Sets whether music is muted.
     * Stops playback if muted, or resumes if unmuted.
     *
     * @param mute true to mute, false to unmute
     */
    public void setMuted(boolean mute) {
        muted = mute;
        if (muted) stop();
        else play();
    }

    /**
     * Starts playing music tracks in a loop.
     * Will not start if muted or already playing.
     * Each track is loaded from resources and played sequentially.
     */
    public void play() {
        if (muted || playing) return;
        playing = true;

        executor.submit(() -> {
            try {
                while (playing && !muted) {

                    // Load from resources (inside jar)
                    InputStream is = getClass().getResourceAsStream(tracks[currentTrack]);

                    if (is == null) {
                        System.err.println("Music file not found: " + tracks[currentTrack]);
                        return;
                    }

                    player = new Player(is);
                    player.play(); // blocks until track ends

                    // Next track in loop
                    currentTrack = (currentTrack + 1) % tracks.length;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Stops music playback immediately.
     * Closes the current player if one is active.
     */
    public void stop() {
        playing = false;
        if (player != null) player.close();
    }
}
