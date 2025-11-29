package phase2.Bonus;

import java.awt.*;

/**
 * Represents a collectible crystal placed in the world.
 * A crystal can be collected or despawn after a time delay.
 */
public class CrystalItem {

    /** The X coordinate of the crystal in the world. */
    public int worldX;

    /** The Y coordinate of the crystal in the world. */
    public int worldY;

    /** Whether the crystal has been collected by the player. */
    public boolean collected = false;

    /** Whether the crystal has despawned after the timer runs out. */
    public boolean despawned = false;

    /** The sprite used to render the crystal. */
    public Image sprite;

    /** Timestamp when the despawn timer started (in milliseconds). */
    private long spawnTime = 0;

    /** Time delay before despawn (15 seconds). */
    private static final long DESPAWN_DELAY = 15000;

    /**
     * Creates a new CrystalItem.
     *
     * @param worldX The X coordinate in the world.
     * @param worldY The Y coordinate in the world.
     * @param sprite The crystal's sprite.
     */
    public CrystalItem(int worldX, int worldY, Image sprite) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.sprite = sprite;
    }

    /**
     * Starts the despawn timer. Should be called when the game begins.
     */
    public void startTimer() {
        if (spawnTime == 0) {
            spawnTime = System.currentTimeMillis();
        }
    }

    /**
     * Updates the crystal's internal state and checks if it should despawn.
     * Will not despawn if collected or explicitly despawned already.
     */
    public void update() {
        if (collected || despawned) return;

        if (spawnTime == 0) return; // Timer not started yet

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - spawnTime;

        if (elapsedTime >= DESPAWN_DELAY) {
            despawned = true;
        }
    }

    /**
     * Returns whether the crystal is still available in the game world.
     *
     * @return true if not collected and not despawned.
     */
    public boolean isActive() {
        return !collected && !despawned;
    }

    /**
     * Gets the remaining time before the crystal despawns.
     *
     * @return Remaining time in seconds, or 0 if collected or already despawned.
     */
    public long getRemainingTime() {
        if (collected || despawned) return 0;

        long elapsed = System.currentTimeMillis() - spawnTime;
        long remaining = DESPAWN_DELAY - elapsed;

        return Math.max(0, remaining / 1000);
    }
}
