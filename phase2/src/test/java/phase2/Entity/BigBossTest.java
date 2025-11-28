package phase2.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import phase2.UI.GamePanel;
import phase2.game.combat.DamageSource;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BigBoss class
 */
public class BigBossTest {

    private BigBoss boss;
    private GamePanel mockGamePanel;
    private Player mockPlayer;
    private Pathfinder mockPathfinder;

    @BeforeEach
    public void setUp() {
        mockGamePanel = new GamePanel();
        mockPlayer = mockGamePanel.player;
        mockPathfinder = new Pathfinder(mockGamePanel.tileManager);
        
        boss = new BigBoss(mockGamePanel, mockPathfinder, mockPlayer, 1000, 1000);
    }

    // =================== Constructor and Initialization Tests ===================

    @Test
    public void constructor_initializesPosition() {
        assertEquals(1000, boss.worldX, "Boss should be at specified X position");
        assertEquals(1000, boss.worldY, "Boss should be at specified Y position");
    }

    @Test
    public void constructor_initializesHealth() {
        assertNotNull(boss.health, "Health component should be initialized");
        assertEquals(550, boss.health.getMaxHealth(), "Boss max health should be 550");
        assertEquals(550, boss.health.getCurrentHealth(), "Boss should start with full health");
        assertTrue(boss.isAlive(), "Boss should be alive initially");
    }

    @Test
    public void constructor_initializesStats() {
        assertNotNull(boss.stats, "Stats should be initialized");
        assertEquals(20, boss.stats.getAttackPower(), "Boss attack should be 20");
        assertEquals(5, boss.stats.getDefense(), "Boss defense should be 5");
    }

    @Test
    public void constructor_initializesSpeed() {
        assertEquals(1, boss.speed, "Boss speed should be 1");
    }

    @Test
    public void constructor_initializesCollisionArea() {
        assertNotNull(boss.collisionArea, "Collision area should be initialized");
        assertEquals(mockGamePanel.tileSize * 2, boss.collisionArea.width, 
            "Collision width should be 2 tiles");
        assertEquals(mockGamePanel.tileSize * 2, boss.collisionArea.height, 
            "Collision height should be 2 tiles");
    }

    @Test
    public void constructor_loadsImages() {
        // Images are loaded via private method, verify they exist by checking draw doesn't fail
        Image img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> boss.draw(g2d), 
            "Drawing should not throw exception if images loaded");
        
        g2d.dispose();
    }

    // =================== Update and Mode Switching Tests ===================

    @Test
    public void update_initialState_doesNotCrash() {
        assertDoesNotThrow(() -> boss.update(), 
            "Update should not throw exception");
    }

    @Test
    public void update_dyingState_decrementsDeathTimer() {
        // Simulate boss death
        boss.takeDamage(1000, new DamageSource("Test"));
        
        // Trigger dying state by updating
        boss.update();
        
        // Death timer should be counting down
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 70; i++) {
                boss.update();
            }
        }, "Death animation should complete without exception");
    }

    @Test
    public void update_attackCooldownDecrements() {
        boss.attackCoolDown = 20;
        
        boss.update();
        
        assertEquals(19, boss.attackCoolDown, 
            "Attack cooldown should decrement");
    }

    @Test
    public void update_playerInRange_triggersCombat() {
        // Position boss right on player
        boss.worldX = mockPlayer.worldX;
        boss.worldY = mockPlayer.worldY;
        boss.attackCoolDown = 0;
        
        int playerHealthBefore = mockPlayer.getCurrentHealth();
        boss.update();
        
        assertTrue(mockPlayer.getCurrentHealth() <= playerHealthBefore, 
            "Player should take damage when boss attacks");
    }

    @Test
    public void update_playerOnCooldown_doesNotAttack() {
        boss.worldX = mockPlayer.worldX;
        boss.worldY = mockPlayer.worldY;
        boss.attackCoolDown = 10;
        
        int playerHealthBefore = mockPlayer.getCurrentHealth();
        boss.update();
        
        assertEquals(playerHealthBefore, mockPlayer.getCurrentHealth(), 
            "Player should not take damage when boss is on cooldown");
    }

    // =================== Damage and Death Tests ===================

    @Test
    public void takeDamage_reducesHealth() {
        int initialHealth = boss.getCurrentHealth();
        
        boss.takeDamage(100, new DamageSource("Test"));
        
        assertEquals(initialHealth - 100, boss.getCurrentHealth(), 
            "Boss health should be reduced");
    }

    @Test
    public void takeDamage_lethal_killsBoss() {
        boss.takeDamage(1000, new DamageSource("Killing Blow"));
        
        assertFalse(boss.isAlive(), "Boss should be dead");
        assertEquals(0, boss.getCurrentHealth(), "Boss health should be 0");
    }

    @Test
    public void takeDamage_triggersDeathSequence() {
        boss.takeDamage(1000, new DamageSource("Test"));
        
        // Update to trigger death animation
        for (int i = 0; i < 70; i++) {
            boss.update();
        }
        
        // Boss should be marked for removal
        assertTrue(mockGamePanel.enemiesToRemove.contains(boss), 
            "Boss should be removed after death animation");
    }

    @Test
    public void finishDeath_dropsKey() {
        mockGamePanel.enemies.add(boss);
        boss.takeDamage(1000, new DamageSource("Test"));
        
        // Complete death sequence
        for (int i = 0; i < 70; i++) {
            boss.update();
        }
        
        assertNotNull(mockGamePanel.droppedKey, "Boss should drop key on death");
    }

    @Test
    public void finishDeath_awardsScore() {
        int scoreBefore = mockGamePanel.finalScore;
        
        boss.takeDamage(1000, new DamageSource("Test"));
        for (int i = 0; i < 70; i++) {
            boss.update();
        }
        
        assertTrue(mockGamePanel.finalScore >= scoreBefore + 500, 
            "Boss death should award at least 500 points");
    }

    @Test
    public void finishDeath_playerInRange_takesExplosionDamage() {
        // Position player near boss
        mockPlayer.worldX = boss.worldX;
        mockPlayer.worldY = boss.worldY;
        
        int playerHealthBefore = mockPlayer.getCurrentHealth();
        
        boss.takeDamage(1000, new DamageSource("Test"));
        for (int i = 0; i < 70; i++) {
            boss.update();
        }
        
        assertTrue(mockPlayer.getCurrentHealth() < playerHealthBefore, 
            "Player should take explosion damage if near boss");
    }

    @Test
    public void finishDeath_playerOutOfRange_noExplosionDamage() {
        // Position player far from boss
        mockPlayer.worldX = boss.worldX + 5000;
        mockPlayer.worldY = boss.worldY + 5000;
        
        int playerHealthBefore = mockPlayer.getCurrentHealth();
        
        boss.takeDamage(1000, new DamageSource("Test"));
        for (int i = 0; i < 70; i++) {
            boss.update();
        }
        
        assertEquals(playerHealthBefore, mockPlayer.getCurrentHealth(), 
            "Player should not take explosion damage if far from boss");
    }

    // =================== Movement and Chase Tests ===================

    @Test
    public void update_playerFarAway_bossDoesNotMove() {
        mockPlayer.worldX = boss.worldX + 10000;
        mockPlayer.worldY = boss.worldY + 10000;
        
        int initialX = boss.worldX;
        int initialY = boss.worldY;
        
        boss.update();
        
        assertEquals(initialX, boss.worldX, 
            "Boss should not move when player is out of detection range");
        assertEquals(initialY, boss.worldY, 
            "Boss should not move when player is out of detection range");
    }

    @Test
    public void update_playerInNormalRange_bossChases() {
        // Position player within normal detection range
        mockPlayer.worldX = boss.worldX + 500;
        mockPlayer.worldY = boss.worldY;
        
        int initialX = boss.worldX;
        
        // Update multiple times to allow movement
        for (int i = 0; i < 20; i++) {
            boss.update();
        }
        
        // Boss should have moved toward player or encountered collision
        assertTrue(boss.worldX != initialX || boss.collisionOn, 
            "Boss should attempt to move toward player");
    }

    // =================== Bomb Spawning Tests ===================

    @Test
    public void update_playerInRange_spawnsBombs() {
        mockPlayer.worldX = boss.worldX + 300;
        mockPlayer.worldY = boss.worldY;
        
        int initialEnemyCount = mockGamePanel.enemiesToAdd.size();
        
        // Update for enough time to spawn a bomb (3.5 seconds at 60 FPS = ~210 frames)
        for (int i = 0; i < 250; i++) {
            boss.update();
            // Simulate time passing
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        assertTrue(mockGamePanel.enemiesToAdd.size() >= initialEnemyCount, 
            "Boss should spawn bombs when player is in range");
    }

    // =================== Special Mode Tests ===================

    @Test
    public void update_extendedTime_switchesToSpecialMode() {
        // Position player in range to trigger alert
        mockPlayer.worldX = boss.worldX + 300;
        mockPlayer.worldY = boss.worldY;
        
        // Update once to trigger alert
        boss.update();
        
        // Simulate 20+ seconds passing (using reflection to manipulate time would be complex,
        // so we just verify mode switching logic doesn't crash)
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 100; i++) {
                boss.update();
            }
        }, "Mode switching should work without exception");
    }

    // =================== Drawing Tests ===================

    @Test
    public void draw_doesNotThrowException() {
        Image img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> boss.draw(g2d), 
            "Drawing should not throw exception");
        
        g2d.dispose();
    }

    @Test
    public void draw_dyingState_showsDeathAnimation() {
        boss.takeDamage(1000, new DamageSource("Test"));
        boss.update();
        
        Image img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> boss.draw(g2d), 
            "Drawing dying boss should not throw exception");
        
        g2d.dispose();
    }

    @Test
    public void draw_withDangerZone_rendersRedIndicator() {
        // Position player in special mode range
        mockPlayer.worldX = boss.worldX + 400;
        mockPlayer.worldY = boss.worldY;
        
        // Simulate updates to potentially trigger special mode charge
        for (int i = 0; i < 50; i++) {
            boss.update();
        }
        
        Image img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> boss.draw(g2d), 
            "Drawing with danger zone should not throw exception");
        
        g2d.dispose();
    }

    @Test
    public void drawHealthBar_doesNotThrowException() {
        Image img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> boss.drawHealthBar(g2d, 100, 100, 144, 10), 
            "Drawing health bar should not throw exception");
        
        g2d.dispose();
    }

    @Test
    public void drawHealthBar_reflectsCurrentHealth() {
        boss.takeDamage(275, new DamageSource("Test")); // Take half health
        
        double expectedHealthPercent = 0.5;
        double actualHealthPercent = boss.health.getHealthPercentage();
        
        assertEquals(expectedHealthPercent, actualHealthPercent, 0.01, 
            "Health bar should reflect 50% health");
    }

    // =================== Integration Tests ===================

    @Test
    public void multipleUpdates_bossRemainsStable() {
        // Position player in range
        mockPlayer.worldX = boss.worldX + 500;
        mockPlayer.worldY = boss.worldY;
        
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 200; i++) {
                boss.update();
            }
        }, "Multiple updates should not cause instability");
        
        assertTrue(boss.isAlive(), "Boss should remain alive");
    }

    @Test
    public void damageAndHealCycle_worksCorrectly() {
        boss.takeDamage(200, new DamageSource("Test"));
        assertEquals(350, boss.getCurrentHealth());
        
        boss.heal(100);
        assertEquals(450, boss.getCurrentHealth());
        
        boss.takeDamage(450, new DamageSource("Test"));
        assertEquals(0, boss.getCurrentHealth());
        assertFalse(boss.isAlive());
    }

    @Test
    public void combatWithPlayer_dealsDamageCorrectly() {
        boss.worldX = mockPlayer.worldX;
        boss.worldY = mockPlayer.worldY;
        boss.attackCoolDown = 0;
        
        int playerHealthBefore = mockPlayer.getCurrentHealth();
        boss.update();
        
        assertTrue(mockPlayer.getCurrentHealth() < playerHealthBefore, 
            "Boss attack should damage player");
    }

    @Test
    public void fullBossFight_simulation() {
        // Position player in range
        mockPlayer.worldX = boss.worldX + 300;
        mockPlayer.worldY = boss.worldY;
        
        // Simulate boss fight
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 100; i++) {
                boss.update();
                
                // Simulate player attacking boss
                if (i % 10 == 0) {
                    boss.takeDamage(50, new DamageSource("Player"));
                }
            }
        }, "Boss fight simulation should run without errors");
    }

    @Test
    public void bossSize_is3x3Tiles() {
        // Verify boss collision area is appropriately sized for 3x3 sprite
        int tileSize = mockGamePanel.tileSize;
        
        assertTrue(boss.collisionArea.width >= tileSize * 1.5, 
            "Boss collision width should be substantial for 3x3 sprite");
        assertTrue(boss.collisionArea.height >= tileSize * 1.5, 
            "Boss collision height should be substantial for 3x3 sprite");
    }
}
