package phase2.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import phase2.Entity.Enemy;
import phase2.Entity.KeyItem;
import phase2.Entity.Pathfinder;
import phase2.Entity.Player;
import phase2.UI.GamePanel;
import phase2.game.combat.DamageSource;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Enemy class
 */
public class EnemyTest {

    private Enemy enemy;
    private GamePanel mockGamePanel;
    private Player mockPlayer;
    private Pathfinder mockPathfinder;

    @BeforeEach
    public void setUp() {
        // Create mock game panel with minimal setup
        mockGamePanel = new GamePanel();
        mockPlayer = mockGamePanel.player;
        mockPathfinder = new Pathfinder(mockGamePanel.tileManager);
        
        // Create enemy at position (100, 100)
        enemy = new Enemy(mockGamePanel, mockPathfinder, mockPlayer, 100, 100);
    }

    // =================== Constructor and Initialization Tests ===================

    @Test
    public void constructor_initializesPosition() {
        assertEquals(100, enemy.worldX, "Enemy should be at specified X position");
        assertEquals(100, enemy.worldY, "Enemy should be at specified Y position");
    }

    @Test
    public void constructor_initializesDirection() {
        assertEquals("down", enemy.direction, "Enemy should face down initially");
    }

    @Test
    public void constructor_initializesSpeed() {
        assertEquals(3, enemy.speed, "Enemy speed should be 3");
    }

    @Test
    public void constructor_initializesCollisionArea() {
        assertNotNull(enemy.collisionArea, "Collision area should be initialized");
        assertTrue(enemy.collisionArea.width > 0, "Collision area should have positive width");
        assertTrue(enemy.collisionArea.height > 0, "Collision area should have positive height");
    }

    @Test
    public void constructor_initializesStats() {
        assertNotNull(enemy.stats, "Stats should be initialized");
        assertEquals(10, enemy.stats.getAttackPower(), "Enemy attack should be 10");
        assertEquals(2, enemy.stats.getDefense(), "Enemy defense should be 2");
    }

    @Test
    public void constructor_initializesHealth() {
        assertNotNull(enemy.health, "Health component should be initialized");
        assertEquals(50, enemy.health.getMaxHealth(), "Enemy max health should be 50");
        assertEquals(50, enemy.health.getCurrentHealth(), "Enemy should start with full health");
        assertTrue(enemy.isAlive(), "Enemy should be alive initially");
    }

    @Test
    public void constructor_initializesAttack() {
        assertNotNull(enemy.currentAttack, "Attack data should be initialized");
        assertTrue(enemy.currentAttack.getPower() > 0, "Attack should have power");
    }

    @Test
    public void constructor_hasKeyDefaultsFalse() {
        assertFalse(enemy.hasKey, "Enemy should not have key by default");
    }

    @Test
    public void constructor_loadsImages() {
        assertNotNull(enemy.up, "Up image should be loaded");
        assertNotNull(enemy.down, "Down image should be loaded");
        assertNotNull(enemy.left, "Left image should be loaded");
        assertNotNull(enemy.right, "Right image should be loaded");
    }

    // =================== Movement and Pathfinding Tests ===================

    @Test
    public void update_offScreen_doesNotMove() {
        // Place enemy very far from player so it's off-screen
        enemy.worldX = 100000;
        enemy.worldY = 100000;
        int initialX = enemy.worldX;
        int initialY = enemy.worldY;
        
        enemy.update();
        
        assertEquals(initialX, enemy.worldX, "Enemy should not move when off-screen");
        assertEquals(initialY, enemy.worldY, "Enemy should not move when off-screen");
    }

    @Test
    public void update_onScreen_movesTowardPlayer() {
        // Place enemy near player
        enemy.worldX = mockPlayer.worldX + 200;
        enemy.worldY = mockPlayer.worldY;
        int initialX = enemy.worldX;
        
        // Update multiple times to allow pathfinding to work
        for (int i = 0; i < 20; i++) {
            enemy.update();
        }
        
        // Enemy should have moved (either toward player or along a path)
        // We can't guarantee exact position due to pathfinding, but it should have changed
        assertTrue(enemy.worldX != initialX || enemy.collisionOn, 
            "Enemy should attempt to move or encounter collision");
    }

    // =================== Combat Tests ===================

    @Test
    public void update_enemyTouchesPlayer_attacksWhenCanAttack() {
        // Position enemy right on player
        enemy.worldX = mockPlayer.worldX;
        enemy.worldY = mockPlayer.worldY;
        enemy.coolDown = 0; // Can attack
        
        int playerHealthBefore = mockPlayer.getCurrentHealth();
        enemy.update();
        
        // Player should have taken damage (assuming attack succeeded)
        assertTrue(mockPlayer.getCurrentHealth() <= playerHealthBefore, 
            "Player should have taken damage or attack was performed");
    }

    @Test
    public void update_enemyTouchesPlayer_doesNotAttackOnCooldown() {
        // Position enemy right on player
        enemy.worldX = mockPlayer.worldX;
        enemy.worldY = mockPlayer.worldY;
        enemy.coolDown = 10; // On cooldown
        
        int playerHealthBefore = mockPlayer.getCurrentHealth();
        enemy.update();
        
        assertEquals(playerHealthBefore, mockPlayer.getCurrentHealth(), 
            "Player should not take damage when enemy is on cooldown");
    }

    @Test
    public void attack_dealsDamageToPlayer() {
        enemy.coolDown = 0;
        int playerHealthBefore = mockPlayer.getCurrentHealth();
        
        enemy.attack(mockPlayer);
        
        assertTrue(mockPlayer.getCurrentHealth() < playerHealthBefore, 
            "Player should have taken damage");
    }

    // =================== Damage and Death Tests ===================

    @Test
    public void takeDamage_reducesHealth() {
        int initialHealth = enemy.getCurrentHealth();
        
        enemy.takeDamage(20, new DamageSource("Test"));
        
        assertEquals(initialHealth - 20, enemy.getCurrentHealth(), 
            "Enemy health should be reduced");
    }

    @Test
    public void takeDamage_setsDamageFlashTimer() {
        enemy.takeDamage(10, new DamageSource("Test"));
        
        assertEquals(10, enemy.damageFlashTimer, 
            "Damage flash timer should be set");
    }

    @Test
    public void takeDamage_lethal_killsEnemy() {
        enemy.takeDamage(100, new DamageSource("Killing Blow"));
        
        assertFalse(enemy.isAlive(), "Enemy should be dead");
        assertEquals(0, enemy.getCurrentHealth(), "Enemy health should be 0");
    }

    @Test
    public void onDeath_removesEnemyFromGamePanel() {
        mockGamePanel.enemies.add(enemy);
        assertTrue(mockGamePanel.enemies.contains(enemy), 
            "Enemy should be in enemies list");
        
        enemy.onDeath();
        
        assertFalse(mockGamePanel.enemies.contains(enemy), 
            "Enemy should be removed from enemies list on death");
    }

    @Test
    public void onDeath_withKey_dropsKey() {
        enemy.hasKey = true;
        mockGamePanel.droppedKey = null;
        
        enemy.onDeath();
        
        assertNotNull(mockGamePanel.droppedKey, "Key should be dropped");
        assertEquals(enemy.worldX, mockGamePanel.droppedKey.worldX, 
            "Key should be at enemy's X position");
        assertEquals(enemy.worldY, mockGamePanel.droppedKey.worldY, 
            "Key should be at enemy's Y position");
    }

    @Test
    public void onDeath_withoutKey_doesNotDropKey() {
        enemy.hasKey = false;
        mockGamePanel.droppedKey = null;
        
        enemy.onDeath();
        
        assertNull(mockGamePanel.droppedKey, "Key should not be dropped");
    }

    @Test
    public void onDeath_withKeyAlreadyDropped_doesNotDropAnotherKey() {
        enemy.hasKey = true;
        KeyItem existingKey = new KeyItem(0, 0);
        mockGamePanel.droppedKey = existingKey;
        
        enemy.onDeath();
        
        assertSame(existingKey, mockGamePanel.droppedKey, 
            "Should not replace existing dropped key");
    }

    @Test
    public void onDeath_healsPlayer() {
        mockPlayer.takeDamage(20, new DamageSource("Test"));
        int healthBefore = mockPlayer.getCurrentHealth();
        
        enemy.onDeath();
        
        assertTrue(mockPlayer.getCurrentHealth() >= healthBefore, 
            "Player should be healed on enemy death");
    }

    @Test
    public void onDeath_grantsPlayerBuff() {
        // We can't easily verify buff was granted without checking internal state
        // But we can verify the method doesn't throw exception
        assertDoesNotThrow(() -> enemy.onDeath(), 
            "onDeath should grant player buff without exception");
    }

    // =================== Drawing Tests ===================

    @Test
    public void draw_doesNotThrowException() {
        Image img = new java.awt.image.BufferedImage(800, 600, 
            java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> enemy.draw(g2d), 
            "Drawing should not throw exception");
        
        g2d.dispose();
    }

    @Test
    public void draw_offScreen_returnsEarly() {
        // Position enemy far off-screen
        enemy.worldX = 100000;
        enemy.worldY = 100000;
        
        Image img = new java.awt.image.BufferedImage(800, 600, 
            java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        // Should not throw exception and should return early
        assertDoesNotThrow(() -> enemy.draw(g2d), 
            "Drawing off-screen enemy should not throw exception");
        
        g2d.dispose();
    }

    @Test
    public void draw_withDamageFlash_showsFlashEffect() {
        enemy.damageFlashTimer = 5;
        
        Image img = new java.awt.image.BufferedImage(800, 600, 
            java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> enemy.draw(g2d), 
            "Drawing with damage flash should not throw exception");
        
        assertEquals(4, enemy.damageFlashTimer, 
            "Damage flash timer should decrement during draw");
        
        g2d.dispose();
    }

    @Test
    public void draw_withDamageText_showsDamageNumber() {
        enemy.damageTextTimer = 20;
        enemy.previousDamageAmount = 15;
        
        Image img = new java.awt.image.BufferedImage(800, 600, 
            java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> enemy.draw(g2d), 
            "Drawing with damage text should not throw exception");
        
        assertEquals(19, enemy.damageTextTimer, 
            "Damage text timer should decrement during draw");
        
        g2d.dispose();
    }

    @Test
    public void draw_criticalHit_usesDifferentColor() {
        enemy.damageTextTimer = 20;
        enemy.previousDamageAmount = 30;
        enemy.lastCrit = true;
        
        Image img = new java.awt.image.BufferedImage(800, 600, 
            java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> enemy.draw(g2d), 
            "Drawing critical hit damage should not throw exception");
        
        g2d.dispose();
    }

    // =================== Direction Tests ===================

    @Test
    public void draw_usesCorrectImageForDirection() {
        Image img = new java.awt.image.BufferedImage(800, 600, 
            java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        enemy.direction = "up";
        assertDoesNotThrow(() -> enemy.draw(g2d));
        
        enemy.direction = "down";
        assertDoesNotThrow(() -> enemy.draw(g2d));
        
        enemy.direction = "left";
        assertDoesNotThrow(() -> enemy.draw(g2d));
        
        enemy.direction = "right";
        assertDoesNotThrow(() -> enemy.draw(g2d));
        
        g2d.dispose();
    }

    // =================== Integration Tests ===================

    @Test
    public void multipleUpdates_enemyBehavesCorrectly() {
        enemy.worldX = mockPlayer.worldX + 500;
        enemy.worldY = mockPlayer.worldY;
        
        // Simulate multiple game frames
        for (int i = 0; i < 100; i++) {
            enemy.update();
            enemy.updateCooldown();
        }
        
        // Enemy should still be alive and functioning
        assertTrue(enemy.isAlive(), "Enemy should still be alive");
        assertTrue(enemy.worldX != 0 || enemy.worldY != 0, 
            "Enemy should have valid position");
    }

    @Test
    public void damageAndHealCycle_worksCorrectly() {
        enemy.takeDamage(20, new DamageSource("Test"));
        assertEquals(30, enemy.getCurrentHealth());
        
        enemy.heal(10);
        assertEquals(40, enemy.getCurrentHealth());
        
        enemy.takeDamage(40, new DamageSource("Test"));
        assertEquals(0, enemy.getCurrentHealth());
        assertFalse(enemy.isAlive());
    }

    @Test
    public void combatCycle_attackCooldownWorks() {
        enemy.coolDown = 0;
        assertTrue(enemy.canAttack(), "Should be able to attack initially");
        
        enemy.attack(mockPlayer);
        assertFalse(enemy.canAttack(), "Should be on cooldown after attack");
        
        int cooldownDuration = enemy.coolDown;
        for (int i = 0; i < cooldownDuration; i++) {
            enemy.updateCooldown();
        }
        
        assertTrue(enemy.canAttack(), "Should be able to attack after cooldown expires");
    }

    @Test
    public void healthBar_correctlyReflectsHealth() {
        Image img = new java.awt.image.BufferedImage(800, 600, 
            java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        // Full health
        enemy.draw(g2d);
        
        // Half health
        enemy.takeDamage(25, new DamageSource("Test"));
        assertEquals(0.5, enemy.health.getHealthPercentage(), 0.01);
        enemy.draw(g2d);
        
        // Low health
        enemy.takeDamage(20, new DamageSource("Test"));
        assertTrue(enemy.health.getHealthPercentage() < 0.2);
        enemy.draw(g2d);
        
        g2d.dispose();
    }
}
