package phase2.UI;

import javazoom.jl.player.Player;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicManager {
    private final String[] tracks;
    private int currentTrack = 0;
    private boolean playing = false;
    private boolean muted = false;

    private Player player;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public MusicManager(String[] tracks) {
        this.tracks = tracks;
    }

    public void setMuted(boolean mute) {
        muted = mute;
        if (muted) stop();
        else play();
    }

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

    public void stop() {
        playing = false;
        if (player != null) player.close();
    }
}
