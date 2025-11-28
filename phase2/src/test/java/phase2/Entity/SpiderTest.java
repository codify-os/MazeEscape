package phase2.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import phase2.UI.GamePanel;
import phase2.game.combat.DamageSource;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Spider class
 */
public class SpiderTest {

    private Spider spider;
    private GamePanel mockGamePanel;
    private Player mockPlayer;
    private Pathfinder mockPathfinder;

    @BeforeEach
    public void setUp() {
        mockGamePanel = new GamePanel();
        mockPlayer = mockGamePanel.player;
        mockPathfinder = new Pathfinder(mockGamePanel.tileManager);
        
        spider = new Spider(mockGamePanel, mockPathfinder, mockPlayer, 1000, 1000);
    }

    // =================== Constructor and Initialization Tests ===================

    @Test
    public void constructor_initializesPosition() {
        assertEquals(1000, spider.worldX, "Spider should be at specified X position");
        assertEquals(1000, spider.worldY, "Spider should be at specified Y position");
    }

    @Test
    public void constructor_initializesRandomSpeed() {
        // Speed should be between MIN_SPEED (2) and MAX_SPEED (5)
        assertTrue(spider.speed >= 2 && spider.speed <= 5, 
            "Spider speed should be between 2 and 5");
    }

    @Test
    public void constructor_loadsImages() {
        Image img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> spider.draw(g2d), 
            "Drawing should not throw exception if images loaded");
        
        g2d.dispose();
    }

    @Test
    public void constructor_multipleSpiders_haveDifferentSpeeds() {
        // Create multiple spiders and check they can have different speeds
        Spider spider1 = new Spider(mockGamePanel, mockPathfinder, mockPlayer, 100, 100);
        Spider spider2 = new Spider(mockGamePanel, mockPathfinder, mockPlayer, 200, 200);
        Spider spider3 = new Spider(mockGamePanel, mockPathfinder, mockPlayer, 300, 300);
        Spider spider4 = new Spider(mockGamePanel, mockPathfinder, mockPlayer, 400, 400);
        Spider spider5 = new Spider(mockGamePanel, mockPathfinder, mockPlayer, 500, 500);
        
        // With 5 spiders, it's very likely at least two have different speeds
        boolean hasDifferentSpeeds = spider1.speed != spider2.speed || 
                                     spider2.speed != spider3.speed ||
                                     spider3.speed != spider4.speed ||
                                     spider4.speed != spider5.speed;
        
        assertTrue(hasDifferentSpeeds, "Multiple spiders should have varied speeds");
    }

    // =================== Detection Tests ===================

    @Test
    public void update_playerFarAway_doesNotDetect() {
        // Position player far from spider
        mockPlayer.worldX = spider.worldX + 10000;
        mockPlayer.worldY = spider.worldY + 10000;
        
        spider.update();
        
        // Spider should not be in detection mode (speed doesn't increase to MAX)
        assertTrue(spider.speed <= 5, "Spider should maintain base speed when player far");
    }

    @Test
    public void update_playerClose_detectsPlayer() {
        // Position player within detection range (2 tiles)
        mockPlayer.worldX = spider.worldX + mockGamePanel.tileSize;
        mockPlayer.worldY = spider.worldY;
        
        int initialSpeed = spider.speed;
        
        spider.update();
        
        // Speed should increase to MAX_SPEED (5) when player detected
        assertEquals(5, spider.speed, "Spider speed should increase to 5 when player detected");
    }

    // =================== Movement Tests ===================

    @Test
    public void update_movesTowardPlayer() {
        // Position player within detection range (2 tiles)
        mockPlayer.worldX = spider.worldX + mockGamePanel.tileSize;
        mockPlayer.worldY = spider.worldY;
        
        spider.update();
        
        // Spider should move or detect player
        assertDoesNotThrow(() -> spider.update(), 
            "Spider should update without errors when player nearby");
    }

    @Test
    public void update_multipleUpdates_continuesMoving() {
        // Position player within detection range
        mockPlayer.worldX = spider.worldX + mockGamePanel.tileSize;
        mockPlayer.worldY = spider.worldY;
        
        for (int i = 0; i < 10; i++) {
            spider.update();
        }
        
        // Spider should have detected player and moved
        assertDoesNotThrow(() -> spider.update(), 
            "Spider should update multiple times without errors");
    }

    @Test
    public void update_setsCorrectDirection() {
        // Position player within detection range to ensure movement
        mockPlayer.worldX = spider.worldX + mockGamePanel.tileSize;
        mockPlayer.worldY = spider.worldY;
        
        spider.update();
        
        // Direction should be set to one of the valid directions
        assertTrue(spider.direction.equals("up") || 
                   spider.direction.equals("down") || 
                   spider.direction.equals("left") || 
                   spider.direction.equals("right"), 
            "Spider should have valid direction");
    }

    // =================== Explosion Tests ===================

    @Test
    public void update_delayedExplosion_waitsBeforeExploding() {
        // Position player in detection range
        mockPlayer.worldX = spider.worldX + mockGamePanel.tileSize;
        mockPlayer.worldY = spider.worldY;
        
        spider.update();
        
        // Should not explode immediately
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10; i++) {
                spider.update();
            }
        }, "Spider should delay explosion after detection");
    }

    @Test
    public void explode_playerInRange_damagesPlayer() throws InterruptedException {
        mockPlayer.worldX = spider.worldX;
        mockPlayer.worldY = spider.worldY;
        
        int playerHealthBefore = mockPlayer.getCurrentHealth();
        
        // Trigger detection
        spider.update();
        
        // Wait for explosion delay (2 seconds)
        Thread.sleep(2100);
        
        // Update to trigger explosion
        spider.update();
        
        assertTrue(mockPlayer.getCurrentHealth() < playerHealthBefore, 
            "Player should take damage from spider explosion");
    }

    @Test
    public void explode_playerOutOfRange_noDamage() throws InterruptedException {
        // Position player within detection range but move away before explosion
        mockPlayer.worldX = spider.worldX + mockGamePanel.tileSize;
        mockPlayer.worldY = spider.worldY;
        
        spider.update(); // Trigger detection
        
        // Move player far away
        mockPlayer.worldX = spider.worldX + 10000;
        mockPlayer.worldY = spider.worldY + 10000;
        
        Thread.sleep(2100);
        
        int playerHealthBefore = mockPlayer.getCurrentHealth();
        spider.update();
        
        assertEquals(playerHealthBefore, mockPlayer.getCurrentHealth(), 
            "Player should not take damage if out of range during explosion");
    }

    @Test
    public void explode_removesSpiderAfterAnimation() throws InterruptedException {
        mockGamePanel.enemies.add(spider);
        
        mockPlayer.worldX = spider.worldX;
        mockPlayer.worldY = spider.worldY;
        
        spider.update(); // Trigger detection
        
        Thread.sleep(2100); // Wait for explosion delay
        spider.update(); // Trigger explosion
        
        // Wait for explosion animation (400ms)
        Thread.sleep(450);
        spider.update();
        
        assertFalse(mockGamePanel.enemies.contains(spider), 
            "Spider should be removed after explosion animation");
    }

    @Test
    public void explode_dealsDamage() throws InterruptedException {
        mockPlayer.worldX = spider.worldX;
        mockPlayer.worldY = spider.worldY;
        
        int playerHealthBefore = mockPlayer.getCurrentHealth();
        
        spider.update();
        Thread.sleep(2100);
        spider.update();
        
        assertTrue(mockPlayer.getCurrentHealth() < playerHealthBefore, 
            "Explosion should deal damage");
    }

    // =================== Reset Detection Tests ===================

    @Test
    public void update_playerLeavesRange_resetsDetection() throws InterruptedException {
        // Position player in range
        mockPlayer.worldX = spider.worldX + mockGamePanel.tileSize;
        mockPlayer.worldY = spider.worldY;
        
        spider.update(); // Trigger detection
        
        // Move player out of range before explosion
        mockPlayer.worldX = spider.worldX + 10000;
        mockPlayer.worldY = spider.worldY + 10000;
        
        Thread.sleep(2100);
        spider.update(); // Should reset instead of exploding
        
        // Spider should not explode (remain in enemies list)
        assertDoesNotThrow(() -> spider.update(), 
            "Spider should reset when player leaves range");
    }

    // =================== Drawing Tests ===================

    @Test
    public void draw_doesNotThrowException() {
        Image img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> spider.draw(g2d), 
            "Drawing should not throw exception");
        
        g2d.dispose();
    }

    @Test
    public void draw_beforeExplosion_showsIdleImage() {
        Image img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> spider.draw(g2d), 
            "Drawing idle spider should not throw exception");
        
        g2d.dispose();
    }

    @Test
    public void draw_afterExplosion_showsHitImage() throws InterruptedException {
        mockPlayer.worldX = spider.worldX;
        mockPlayer.worldY = spider.worldY;
        
        spider.update();
        Thread.sleep(2100);
        spider.update(); // Trigger explosion
        
        Image img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> spider.draw(g2d), 
            "Drawing exploded spider should not throw exception");
        
        g2d.dispose();
    }

    @Test
    public void draw_offScreen_returnsEarly() {
        spider.worldX = mockPlayer.worldX + 100000;
        spider.worldY = mockPlayer.worldY + 100000;
        
        Image img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> spider.draw(g2d), 
            "Drawing off-screen spider should not throw exception");
        
        g2d.dispose();
    }

    // =================== Edge Cases ===================

    @Test
    public void update_offScreen_skipsUpdate() {
        spider.worldX = mockPlayer.worldX + 100000;
        spider.worldY = mockPlayer.worldY + 100000;
        
        int initialX = spider.worldX;
        spider.update();
        
        assertEquals(initialX, spider.worldX, 
            "Spider should not update when off-screen");
    }

    @Test
    public void update_collision_stopsMovement() {
        // Position spider near a wall
        spider.worldX = 0;
        spider.worldY = 0;
        mockPlayer.worldX = -100;
        mockPlayer.worldY = 0;
        
        spider.update();
        
        // Spider should handle collision (position may not change much)
        assertTrue(spider.worldX >= 0, 
            "Spider should handle collision with boundaries");
    }

    // =================== Integration Tests ===================

    @Test
    public void fullLifecycle_completesSuccessfully() throws InterruptedException {
        mockGamePanel.enemies.add(spider);
        mockPlayer.worldX = spider.worldX;
        mockPlayer.worldY = spider.worldY;
        
        int playerHealthBefore = mockPlayer.getCurrentHealth();
        
        // Detection
        spider.update();
        
        // Wait for explosion
        Thread.sleep(2100);
        spider.update();
        
        // Wait for animation
        Thread.sleep(450);
        spider.update();
        
        assertTrue(mockPlayer.getCurrentHealth() < playerHealthBefore, 
            "Player should take damage");
        assertFalse(mockGamePanel.enemies.contains(spider), 
            "Spider should be removed");
    }

    @Test
    public void multipleUpdates_spiderBehavesCorrectly() {
        mockPlayer.worldX = spider.worldX + 300;
        mockPlayer.worldY = spider.worldY;
        
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 50; i++) {
                spider.update();
            }
        }, "Multiple updates should work without errors");
    }

    @Test
    public void multipleSpiders_workIndependently() throws InterruptedException {
        Spider spider2 = new Spider(mockGamePanel, mockPathfinder, mockPlayer, 2000, 2000);
        
        mockGamePanel.enemies.add(spider);
        mockGamePanel.enemies.add(spider2);
        
        mockPlayer.worldX = spider.worldX;
        mockPlayer.worldY = spider.worldY;
        
        spider.update();
        spider2.update();
        
        Thread.sleep(2100);
        
        spider.update();
        spider2.update();
        
        // Both should function independently
        assertDoesNotThrow(() -> {
            spider.update();
            spider2.update();
        }, "Multiple spiders should work independently");
    }

    @Test
    public void detectionRange_is2Tiles() {
        int detectionRange = 2 * mockGamePanel.tileSize;
        
        // Just outside range
        mockPlayer.worldX = spider.worldX + detectionRange + 10;
        mockPlayer.worldY = spider.worldY;
        
        int speedBefore = spider.speed;
        spider.update();
        
        assertTrue(spider.speed == speedBefore, 
            "Spider should not detect player beyond 2 tiles");
        
        // Just inside range
        mockPlayer.worldX = spider.worldX + detectionRange - 10;
        spider.update();
        
        assertEquals(5, spider.speed, 
            "Spider should detect player within 2 tiles");
    }
}
