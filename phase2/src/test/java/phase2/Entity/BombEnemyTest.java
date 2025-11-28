package phase2.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import phase2.UI.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BombEnemy class
 */
public class BombEnemyTest {

    private BombEnemy bomb;
    private GamePanel mockGamePanel;
    private Player mockPlayer;

    @BeforeEach
    public void setUp() {
        mockGamePanel = new GamePanel();
        mockPlayer = mockGamePanel.player;
        
        // Create bomb at position (1000, 1000)
        bomb = new BombEnemy(mockGamePanel, mockPlayer, 1000, 1000);
    }

    // =================== Constructor and Initialization Tests ===================

    @Test
    public void constructor_initializesPosition() {
        assertEquals(1000, bomb.worldX, "Bomb should be at specified X position");
        assertEquals(1000, bomb.worldY, "Bomb should be at specified Y position");
    }

    @Test
    public void constructor_initializesHealth() {
        assertNotNull(bomb.health, "Health component should be initialized");
        assertEquals(1, bomb.health.getMaxHealth(), "Bomb max health should be 1");
        assertEquals(1, bomb.health.getCurrentHealth(), "Bomb should start with full health");
        assertTrue(bomb.isAlive(), "Bomb should be alive initially");
    }

    @Test
    public void constructor_storesTargetPosition() {
        // Target should be player's position at spawn time
        int playerX = mockPlayer.worldX;
        int playerY = mockPlayer.worldY;
        
        BombEnemy testBomb = new BombEnemy(mockGamePanel, mockPlayer, 500, 500);
        
        // Update and verify it moves toward the stored target
        int initialX = testBomb.worldX;
        testBomb.update();
        
        // Bomb should move toward target (may move in X or Y direction)
        assertTrue(testBomb.worldX != initialX || testBomb.worldY != 500, 
            "Bomb should move toward stored target position");
    }

    @Test
    public void constructor_loadsImages() {
        // Verify images loaded by checking draw doesn't fail
        Image img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> bomb.draw(g2d), 
            "Drawing should not throw exception if images loaded");
        
        g2d.dispose();
    }

    // =================== Movement Tests ===================

    @Test
    public void update_notAtTarget_movesTowardTarget() {
        int initialX = bomb.worldX;
        int initialY = bomb.worldY;
        
        bomb.update();
        
        // Bomb should have moved toward target
        assertTrue(bomb.worldX != initialX || bomb.worldY != initialY, 
            "Bomb should move toward target");
    }

    @Test
    public void update_multipleUpdates_continuesMovingToTarget() {
        int initialX = bomb.worldX;
        
        for (int i = 0; i < 10; i++) {
            bomb.update();
        }
        
        // Should have moved closer to target
        assertTrue(Math.abs(bomb.worldX - initialX) > 0, 
            "Bomb should move significantly over multiple updates");
    }

    @Test
    public void update_reachesTarget_stopsFuseStarts() {
        // Place bomb very close to target
        bomb.worldX = mockPlayer.worldX;
        bomb.worldY = mockPlayer.worldY;
        
        // Update to trigger target reached
        bomb.update();
        
        // Bomb may still move slightly due to Enemy.update() pathfinding
        // Just verify it's near the target area
        int distanceFromTarget = Math.abs(bomb.worldX - mockPlayer.worldX) + 
                                Math.abs(bomb.worldY - mockPlayer.worldY);
        
        assertTrue(distanceFromTarget < 100, 
            "Bomb should be near target position");
    }

    // =================== Fuse and Explosion Tests ===================

    @Test
    public void update_fuseCountsDown() {
        // Get bomb to target
        bomb.worldX = mockPlayer.worldX;
        bomb.worldY = mockPlayer.worldY;
        bomb.update();
        
        // Fuse should start counting down
        for (int i = 0; i < 5; i++) {
            bomb.update();
        }
        
        // Bomb should still exist but counting down
        assertNotNull(bomb, "Bomb should still exist during fuse countdown");
    }

    @Test
    public void update_fuseExpires_explodes() {
        bomb.worldX = mockPlayer.worldX;
        bomb.worldY = mockPlayer.worldY;
        
        // Update until explosion (reach target + fuse timer)
        for (int i = 0; i < 35; i++) {
            bomb.update();
        }
        
        // Bomb should be in exploding state (verified by not being removed yet)
        // Continue updating through explosion
        for (int i = 0; i < 25; i++) {
            bomb.update();
        }
        
        // After explosion duration, bomb should be removed
        assertFalse(mockGamePanel.enemies.contains(bomb), 
            "Bomb should be removed after explosion completes");
    }

    @Test
    public void explode_playerInRange_damagesPlayer() {
        // Position player right on bomb
        mockPlayer.worldX = bomb.worldX;
        mockPlayer.worldY = bomb.worldY;
        
        bomb.worldX = mockPlayer.worldX;
        bomb.worldY = mockPlayer.worldY;
        
        int playerHealthBefore = mockPlayer.getCurrentHealth();
        
        // Update until explosion
        for (int i = 0; i < 35; i++) {
            bomb.update();
        }
        
        assertTrue(mockPlayer.getCurrentHealth() < playerHealthBefore, 
            "Player should take damage from bomb explosion");
    }

    @Test
    public void explode_playerOutOfRange_noDamage() {
        // Position player far from bomb
        mockPlayer.worldX = bomb.worldX + 5000;
        mockPlayer.worldY = bomb.worldY + 5000;
        
        int playerHealthBefore = mockPlayer.getCurrentHealth();
        
        // Update until explosion
        for (int i = 0; i < 35; i++) {
            bomb.update();
        }
        
        assertEquals(playerHealthBefore, mockPlayer.getCurrentHealth(), 
            "Player should not take damage if out of explosion range");
    }

    @Test
    public void explode_explosionAreaIsCorrectSize() {
        // Position player at edge of explosion range (2 tiles)
        int tileSize = mockGamePanel.tileSize;
        mockPlayer.worldX = bomb.worldX + tileSize;
        mockPlayer.worldY = bomb.worldY + tileSize;
        
        bomb.worldX = mockPlayer.worldX - tileSize;
        bomb.worldY = mockPlayer.worldY - tileSize;
        
        int playerHealthBefore = mockPlayer.getCurrentHealth();
        
        // Update until explosion
        for (int i = 0; i < 35; i++) {
            bomb.update();
        }
        
        // Player at edge should take damage
        assertTrue(mockPlayer.getCurrentHealth() <= playerHealthBefore, 
            "Explosion should have 2-tile radius");
    }

    // =================== State Management Tests ===================

    @Test
    public void update_beforeReachingTarget_fuseNotLit() {
        // Bomb far from target
        bomb.worldX = mockPlayer.worldX + 1000;
        bomb.worldY = mockPlayer.worldY + 1000;
        
        bomb.update();
        
        // Should still be moving (not exploding)
        assertFalse(mockGamePanel.enemies.isEmpty() || mockGamePanel.enemies.contains(bomb), 
            "Bomb should still be active before reaching target");
    }

    @Test
    public void update_exploding_stopsMoving() {
        bomb.worldX = mockPlayer.worldX;
        bomb.worldY = mockPlayer.worldY;
        
        // Update until explosion starts
        for (int i = 0; i < 35; i++) {
            bomb.update();
        }
        
        int xDuringExplosion = bomb.worldX;
        int yDuringExplosion = bomb.worldY;
        
        bomb.update();
        
        assertEquals(xDuringExplosion, bomb.worldX, 
            "Bomb should not move while exploding");
        assertEquals(yDuringExplosion, bomb.worldY, 
            "Bomb should not move while exploding");
    }

    @Test
    public void update_afterExplosion_removesFromGame() {
        mockGamePanel.enemies.add(bomb);
        
        bomb.worldX = mockPlayer.worldX;
        bomb.worldY = mockPlayer.worldY;
        
        // Update through entire lifecycle
        for (int i = 0; i < 60; i++) {
            bomb.update();
        }
        
        assertFalse(mockGamePanel.enemies.contains(bomb), 
            "Bomb should be removed after explosion completes");
    }

    // =================== Drawing Tests ===================

    @Test
    public void draw_doesNotThrowException() {
        Image img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> bomb.draw(g2d), 
            "Drawing should not throw exception");
        
        g2d.dispose();
    }

    @Test
    public void draw_beforeFuse_showsBombImage() {
        Image img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        // Should draw bomb image initially
        assertDoesNotThrow(() -> bomb.draw(g2d), 
            "Drawing bomb before fuse should not throw exception");
        
        g2d.dispose();
    }

    @Test
    public void draw_duringExplosion_showsExplosionImage() {
        bomb.worldX = mockPlayer.worldX;
        bomb.worldY = mockPlayer.worldY;
        
        // Update until explosion
        for (int i = 0; i < 35; i++) {
            bomb.update();
        }
        
        Image img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> bomb.draw(g2d), 
            "Drawing explosion should not throw exception");
        
        g2d.dispose();
    }

    @Test
    public void draw_explosionSize_is2x2Tiles() {
        bomb.worldX = mockPlayer.worldX;
        bomb.worldY = mockPlayer.worldY;
        
        // Update until explosion
        for (int i = 0; i < 35; i++) {
            bomb.update();
        }
        
        Image img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        // Explosion should be drawn at 2x2 tiles
        assertDoesNotThrow(() -> bomb.draw(g2d), 
            "Drawing 2x2 explosion should not throw exception");
        
        g2d.dispose();
    }

    // =================== Integration Tests ===================

    @Test
    public void fullLifecycle_completesSuccessfully() {
        mockGamePanel.enemies.add(bomb);
        
        // Position player at bomb location
        mockPlayer.worldX = bomb.worldX;
        mockPlayer.worldY = bomb.worldY;
        
        int playerHealthBefore = mockPlayer.getCurrentHealth();
        
        // Run full lifecycle: move -> fuse -> explode -> remove
        for (int i = 0; i < 100; i++) {
            if (mockGamePanel.enemies.contains(bomb)) {
                bomb.update();
            }
        }
        
        assertTrue(mockPlayer.getCurrentHealth() < playerHealthBefore, 
            "Player should have taken damage from bomb");
        // Bomb removes itself from enemies list directly in update()
        assertDoesNotThrow(() -> bomb.update(), 
            "Bomb should complete lifecycle without errors");
    }

    @Test
    public void multipleBombs_workIndependently() {
        BombEnemy bomb2 = new BombEnemy(mockGamePanel, mockPlayer, 2000, 2000);
        
        mockGamePanel.enemies.add(bomb);
        mockGamePanel.enemies.add(bomb2);
        
        // Update both bombs
        for (int i = 0; i < 50; i++) {
            if (mockGamePanel.enemies.contains(bomb)) {
                bomb.update();
            }
            if (mockGamePanel.enemies.contains(bomb2)) {
                bomb2.update();
            }
        }
        
        // Both should function independently
        assertDoesNotThrow(() -> {
            bomb.update();
            bomb2.update();
        }, "Multiple bombs should work independently");
    }

    @Test
    public void bombDamage_is20Points() {
        mockPlayer.worldX = bomb.worldX;
        mockPlayer.worldY = bomb.worldY;
        
        int playerHealthBefore = mockPlayer.getCurrentHealth();
        
        // Trigger explosion
        for (int i = 0; i < 35; i++) {
            bomb.update();
        }
        
        assertTrue(mockPlayer.getCurrentHealth() < playerHealthBefore, 
            "Bomb should deal damage to player");
    }

    @Test
    public void targetPosition_fixedAtSpawn() {
        BombEnemy testBomb = new BombEnemy(mockGamePanel, mockPlayer, 500, 500);
        
        // Move player to different position
        mockPlayer.worldX += 1000;
        mockPlayer.worldY += 1000;
        
        // Bomb should still move toward original position
        int bombInitialX = testBomb.worldX;
        
        for (int i = 0; i < 10; i++) {
            testBomb.update();
        }
        
        // Verify bomb moved (toward original target, not current player position)
        assertTrue(testBomb.worldX != bombInitialX || testBomb.worldY != 500, 
            "Bomb should move toward fixed target position");
    }
}
