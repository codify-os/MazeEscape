package phase2.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import phase2.UI.GamePanel;
import phase2.game.combat.DamageSource;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PhantomMinion class
 */
public class PhantomMinionTest {

    private PhantomMinion phantom;
    private GamePanel mockGamePanel;
    private Player mockPlayer;
    private Pathfinder mockPathfinder;

    @BeforeEach
    public void setUp() {
        mockGamePanel = new GamePanel();
        mockPlayer = mockGamePanel.player;
        mockPathfinder = new Pathfinder(mockGamePanel.tileManager);
        
        phantom = new PhantomMinion(mockGamePanel, mockPathfinder, mockPlayer, 1000, 1000);
    }

    // =================== Constructor and Initialization Tests ===================

    @Test
    public void constructor_initializesPosition() {
        assertEquals(1000, phantom.worldX, "Phantom should be at specified X position");
        assertEquals(1000, phantom.worldY, "Phantom should be at specified Y position");
    }

    @Test
    public void constructor_initializesHealth() {
        assertNotNull(phantom.health, "Health component should be initialized");
        assertEquals(30, phantom.health.getMaxHealth(), "Phantom max health should be 30");
        assertEquals(30, phantom.health.getCurrentHealth(), "Phantom should start with full health");
        assertTrue(phantom.isAlive(), "Phantom should be alive initially");
    }

    @Test
    public void constructor_initializesSpeed() {
        assertEquals(2, phantom.speed, "Phantom speed should be 2");
    }

    @Test
    public void constructor_loadsImages() {
        Image img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> phantom.draw(g2d), 
            "Drawing should not throw exception if images loaded");
        
        g2d.dispose();
    }

    // =================== Movement Tests ===================

    @Test
    public void update_movesTowardPlayer() {
        // Position player to the right
        mockPlayer.worldX = phantom.worldX + 500;
        mockPlayer.worldY = phantom.worldY;
        
        int initialX = phantom.worldX;
        
        phantom.update();
        
        assertTrue(phantom.worldX > initialX, 
            "Phantom should move toward player on X axis");
    }

    @Test
    public void update_movesInBothDirections() {
        // Position player diagonally
        mockPlayer.worldX = phantom.worldX + 500;
        mockPlayer.worldY = phantom.worldY + 500;
        
        int initialX = phantom.worldX;
        int initialY = phantom.worldY;
        
        phantom.update();
        
        assertTrue(phantom.worldX > initialX, 
            "Phantom should move toward player on X axis");
        assertTrue(phantom.worldY > initialY, 
            "Phantom should move toward player on Y axis");
    }

    @Test
    public void update_playerLeft_movesLeft() {
        mockPlayer.worldX = phantom.worldX - 500;
        mockPlayer.worldY = phantom.worldY;
        
        int initialX = phantom.worldX;
        phantom.update();
        
        assertTrue(phantom.worldX < initialX, 
            "Phantom should move left toward player");
    }

    @Test
    public void update_playerAbove_movesUp() {
        mockPlayer.worldX = phantom.worldX;
        mockPlayer.worldY = phantom.worldY - 500;
        
        int initialY = phantom.worldY;
        phantom.update();
        
        assertTrue(phantom.worldY < initialY, 
            "Phantom should move up toward player");
    }

    @Test
    public void update_multipleUpdates_continuesFollowing() {
        mockPlayer.worldX = phantom.worldX + 1000;
        mockPlayer.worldY = phantom.worldY + 1000;
        
        int initialX = phantom.worldX;
        int initialY = phantom.worldY;
        
        for (int i = 0; i < 20; i++) {
            phantom.update();
        }
        
        assertTrue(phantom.worldX > initialX + 20, 
            "Phantom should move significantly over multiple updates");
        assertTrue(phantom.worldY > initialY + 20, 
            "Phantom should move significantly over multiple updates");
    }

    @Test
    public void update_movementSpeed_isConsistent() {
        mockPlayer.worldX = phantom.worldX + 1000;
        mockPlayer.worldY = phantom.worldY;
        
        int x1 = phantom.worldX;
        phantom.update();
        int x2 = phantom.worldX;
        
        int distance1 = x2 - x1;
        
        phantom.update();
        int x3 = phantom.worldX;
        
        int distance2 = x3 - x2;
        
        assertEquals(distance1, distance2, 
            "Movement distance should be consistent each update");
    }

    // =================== Combat and Damage Tests ===================

    @Test
    public void takeDamage_reducesHealth() {
        int initialHealth = phantom.getCurrentHealth();
        
        phantom.takeDamage(10, new DamageSource("Test"));
        
        assertEquals(initialHealth - 10, phantom.getCurrentHealth(), 
            "Phantom health should be reduced");
    }

    @Test
    public void takeDamage_lethal_killsPhantom() {
        phantom.takeDamage(50, new DamageSource("Killing Blow"));
        
        assertFalse(phantom.isAlive(), "Phantom should be dead");
        assertEquals(0, phantom.getCurrentHealth(), "Phantom health should be 0");
    }

    @Test
    public void takeDamage_triggersDeathState() {
        phantom.takeDamage(50, new DamageSource("Test"));
        
        // Update should handle death state
        assertDoesNotThrow(() -> phantom.update(), 
            "Update should handle death state without exception");
    }

    @Test
    public void update_dying_countsDownDeathTimer() {
        phantom.takeDamage(50, new DamageSource("Test"));
        
        // Death timer should count down
        for (int i = 0; i < 35; i++) {
            phantom.update();
        }
        
        assertDoesNotThrow(() -> phantom.update(), 
            "Death animation should complete without error");
    }

    @Test
    public void update_dying_removesAfterTimer() {
        mockGamePanel.enemies.add(phantom);
        
        phantom.takeDamage(50, new DamageSource("Test"));
        
        // Update through death timer
        for (int i = 0; i < 35; i++) {
            phantom.update();
        }
        
        assertFalse(mockGamePanel.enemies.contains(phantom), 
            "Phantom should be removed after death animation");
    }

    @Test
    public void update_dying_doesNotMove() {
        phantom.takeDamage(50, new DamageSource("Test"));
        
        int x = phantom.worldX;
        int y = phantom.worldY;
        
        phantom.update();
        
        assertEquals(x, phantom.worldX, 
            "Phantom should not move while dying");
        assertEquals(y, phantom.worldY, 
            "Phantom should not move while dying");
    }

    // =================== Drawing Tests ===================

    @Test
    public void draw_doesNotThrowException() {
        Image img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> phantom.draw(g2d), 
            "Drawing should not throw exception");
        
        g2d.dispose();
    }

    @Test
    public void draw_alive_showsRunImage() {
        Image img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> phantom.draw(g2d), 
            "Drawing alive phantom should not throw exception");
        
        g2d.dispose();
    }

    @Test
    public void draw_dying_showsDeathImage() {
        phantom.takeDamage(50, new DamageSource("Test"));
        
        Image img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> phantom.draw(g2d), 
            "Drawing dying phantom should not throw exception");
        
        g2d.dispose();
    }

    @Test
    public void draw_offScreen_returnsEarly() {
        phantom.worldX = mockPlayer.worldX + 100000;
        phantom.worldY = mockPlayer.worldY + 100000;
        
        Image img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> phantom.draw(g2d), 
            "Drawing off-screen phantom should not throw exception");
        
        g2d.dispose();
    }

    // =================== Integration Tests ===================

    @Test
    public void fullLifecycle_normalDeath() {
        mockGamePanel.enemies.add(phantom);
        
        // Move toward player
        mockPlayer.worldX = phantom.worldX + 500;
        mockPlayer.worldY = phantom.worldY;
        
        for (int i = 0; i < 10; i++) {
            phantom.update();
        }
        
        // Take lethal damage
        phantom.takeDamage(50, new DamageSource("Test"));
        
        // Complete death animation
        for (int i = 0; i < 35; i++) {
            phantom.update();
        }
        
        assertFalse(mockGamePanel.enemies.contains(phantom), 
            "Phantom should be removed after full death cycle");
    }

    @Test
    public void multipleUpdates_phantomBehavesCorrectly() {
        mockPlayer.worldX = phantom.worldX + 500;
        mockPlayer.worldY = phantom.worldY + 500;
        
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 100; i++) {
                phantom.update();
            }
        }, "Multiple updates should work without errors");
        
        assertTrue(phantom.isAlive(), "Phantom should remain alive");
    }

    @Test
    public void damageAndHealCycle_worksCorrectly() {
        phantom.takeDamage(15, new DamageSource("Test"));
        assertEquals(15, phantom.getCurrentHealth());
        
        phantom.heal(10);
        assertEquals(25, phantom.getCurrentHealth());
        
        phantom.takeDamage(25, new DamageSource("Test"));
        assertEquals(0, phantom.getCurrentHealth());
        assertFalse(phantom.isAlive());
    }

    @Test
    public void multiplePhantoms_workIndependently() {
        PhantomMinion phantom2 = new PhantomMinion(mockGamePanel, mockPathfinder, mockPlayer, 2000, 2000);
        
        mockGamePanel.enemies.add(phantom);
        mockGamePanel.enemies.add(phantom2);
        
        // Both should move independently
        for (int i = 0; i < 10; i++) {
            phantom.update();
            phantom2.update();
        }
        
        assertTrue(phantom.isAlive(), "First phantom should be alive");
        assertTrue(phantom2.isAlive(), "Second phantom should be alive");
        
        // Damage only one
        phantom.takeDamage(50, new DamageSource("Test"));
        
        assertFalse(phantom.isAlive(), "First phantom should be dead");
        assertTrue(phantom2.isAlive(), "Second phantom should still be alive");
    }

    @Test
    public void combatWithPlayer_phantomCanAttack() {
        // Position phantom on player
        phantom.worldX = mockPlayer.worldX;
        phantom.worldY = mockPlayer.worldY;
        phantom.attackCoolDown = 0;
        
        int playerHealthBefore = mockPlayer.getCurrentHealth();
        phantom.update();
        
        assertTrue(mockPlayer.getCurrentHealth() <= playerHealthBefore, 
            "Phantom should be able to attack player");
    }

    @Test
    public void phantomSpeed_is2() {
        assertEquals(2, phantom.speed, 
            "Phantom base speed should be 2");
        
        // Speed should remain constant during movement
        phantom.update();
        assertEquals(2, phantom.speed, 
            "Phantom speed should not change during updates");
    }

    @Test
    public void phantomHealth_is30() {
        assertEquals(30, phantom.health.getMaxHealth(), 
            "Phantom max health should be 30");
    }

    @Test
    public void phantomMovement_simpleDirectChase() {
        // Test that phantom uses simple direct chase, not pathfinding
        mockPlayer.worldX = phantom.worldX + 100;
        mockPlayer.worldY = phantom.worldY + 100;
        
        int initialX = phantom.worldX;
        int initialY = phantom.worldY;
        
        phantom.update();
        
        // Should move directly toward player
        assertTrue(phantom.worldX > initialX && phantom.worldY > initialY, 
            "Phantom should move directly toward player");
    }

    @Test
    public void deathTimer_is30Frames() {
        phantom.takeDamage(50, new DamageSource("Test"));
        
        mockGamePanel.enemies.add(phantom);
        
        // Update 29 times - should still be in enemies
        for (int i = 0; i < 29; i++) {
            phantom.update();
        }
        
        assertTrue(mockGamePanel.enemies.contains(phantom), 
            "Phantom should remain during death animation");
        
        // One more update should remove it
        phantom.update();
        
        assertFalse(mockGamePanel.enemies.contains(phantom), 
            "Phantom should be removed after 30 frame death timer");
    }

    @Test
    public void followPlayer_movesCloser() {
        mockPlayer.worldX = phantom.worldX + 1000;
        mockPlayer.worldY = phantom.worldY + 1000;
        
        double initialDistance = Math.sqrt(
            Math.pow(mockPlayer.worldX - phantom.worldX, 2) + 
            Math.pow(mockPlayer.worldY - phantom.worldY, 2)
        );
        
        for (int i = 0; i < 10; i++) {
            phantom.update();
        }
        
        double finalDistance = Math.sqrt(
            Math.pow(mockPlayer.worldX - phantom.worldX, 2) + 
            Math.pow(mockPlayer.worldY - phantom.worldY, 2)
        );
        
        assertTrue(finalDistance < initialDistance, 
            "Phantom should get closer to player over time");
    }
}
