package phase2.Bonus;

import java.awt.*;

public class CrystalItem {

    public int worldX, worldY;
    public boolean collected = false;
    public boolean despawned = false;

    public Image sprite;
    
    // Despawn mechanics
    private long spawnTime = 0;
    private static final long DESPAWN_DELAY = 15000; // 15 seconds in milliseconds

    public CrystalItem(int worldX, int worldY, Image sprite) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.sprite = sprite;
    }
    
    /**
     * Starts the despawn timer (called when game actually starts)
     */
    public void startTimer() {
        if (spawnTime == 0) {
            spawnTime = System.currentTimeMillis();
        }
    }
    
    /**
     * Updates the crystal state and checks if it should despawn
     */
    public void update() {
        if (collected || despawned) return;
        
        // Don't despawn if timer hasn't started
        if (spawnTime == 0) return;
        
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - spawnTime;
        
        if (elapsedTime >= DESPAWN_DELAY) {
            despawned = true;
        }
    }
    
    /**
     * Checks if the crystal is still active (not collected and not despawned)
     */
    public boolean isActive() {
        return !collected && !despawned;
    }
    
    /**
     * Gets the remaining time before despawn in seconds
     */
    public long getRemainingTime() {
        if (collected || despawned) return 0;
        long elapsed = System.currentTimeMillis() - spawnTime;
        long remaining = DESPAWN_DELAY - elapsed;
        return Math.max(0, remaining / 1000);
    }
}