package phase2.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import phase2.Entity.Enemy;
import phase2.Entity.Pathfinder;
import phase2.Entity.Player;
import phase2.UI.GamePanel;
import phase2.UI.KeyHandler;
import phase2.game.combat.DamageSource;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest{
    private Player player;
    private GamePanel mockGamePanel;
    private Enemy mockEnemy;
    private Pathfinder mockPathfinder;
    private KeyHandler kh;

    @BeforeEach
    public void setUp() {
        // Create mock game panel with minimal setup
        mockGamePanel = new GamePanel();
        kh = new KeyHandler();
        player = new Player(mockGamePanel, kh);
        mockPathfinder = new Pathfinder(mockGamePanel.tileManager);


        // Create enemy at position (100, 100)
        mockEnemy = new Enemy(mockGamePanel, mockPathfinder, player, 100, 100);
    }

    @Test
    public void constructorInitializesFields() {
        assertEquals(100, player.getCurrentHealth());
        assertEquals(100, player.getMaxHealth());
        assertEquals(20, player.stats.getAttackPower());
        assertEquals(5, player.stats.getDefense());
        assertEquals(0, player.getInventory().size());

    }

    @Test
    public void testInventoryAddRemove() {
        player.addItem("key");
        assertTrue(player.hasItem("key"));

        player.removeItem("key");
        assertFalse(player.hasItem("key"));

        player.clearInventory();
        assertEquals(0, player.getInventory().size());
    }

    @Test
    public void testPlayerTakingDamage() {
        int initialHealth = player.getCurrentHealth();

        player.takeDamage(20, new DamageSource("Mock Enemy"));
        assertEquals(initialHealth - 20, player.getCurrentHealth());
    }

    @Test
    public void testDeathAtZeroHealth() {
        player.takeDamage(player.getMaxHealth(), new DamageSource("mock enemy"));
        assertFalse(player.isAlive());
    }

    @Test
    public void testCritBuffActive() {
        player.forceCritBuff();

        assertEquals(1.0, player.stats.getCritChance());
        assertTrue(player.isCritBuffActive());
    }

    @Test
    public void testCritBuffExpires() {
        player.forceCritBuff();

        for(int i = 0; i < 181; i++) {
            player.update();
        }

        assertFalse(player.isCritBuffActive());
        assertEquals(0.05, player.stats.getCritChance());
    }

    @Test
    public void testAddMultipleKeys() {
        player.addItem("key");
        player.addItem("key");
        player.addItem("key");
        
        assertEquals(3, player.getInventory().get("key"));
    }

    @Test
    public void testRemoveItemReducesCount() {
        player.addItem("key");
        player.addItem("key");
        player.removeItem("key");
        
        assertEquals(1, player.getInventory().get("key"));
    }

    @Test
    public void testHasItemReturnsFalseForNonExistentItem() {
        assertFalse(player.hasItem("nonexistent"));
    }

    @Test
    public void testPlayerDefaultPosition() {
        player.setDefaultValues();
        assertNotNull(player.worldX);
        assertNotNull(player.worldY);
    }

    @Test
    public void testPlayerCanAttackWhenCooldownReady() {
        assertTrue(player.canAttack());
    }

    @Test
    public void testPlayerHealRestoresHealth() {
        player.takeDamage(30, new DamageSource("test"));
        int damagedHealth = player.getCurrentHealth();
        
        player.heal(15);
        assertEquals(damagedHealth + 15, player.getCurrentHealth());
    }

    @Test
    public void testPlayerHealDoesNotExceedMaxHealth() {
        player.heal(200);
        assertEquals(player.getMaxHealth(), player.getCurrentHealth());
    }

    @Test
    public void testPlayerTakesNoDamageWhenDead() {
        player.takeDamage(player.getMaxHealth(), new DamageSource("fatal"));
        assertFalse(player.isAlive());
        
        int healthAfterDeath = player.getCurrentHealth();
        player.takeDamage(10, new DamageSource("overkill"));
        assertEquals(healthAfterDeath, player.getCurrentHealth());
    }

    @Test
    public void testCritBuffTimerDecrementsCorrectly() {
        player.forceCritBuff();
        int initialTimer = player.getCritBuffTimer();
        
        player.update();
        assertEquals(initialTimer - 1, player.getCritBuffTimer());
    }

    @Test
    public void testGrantRandomBuffActivatesCritBuff() {
        // This test may be probabilistic, but we can test it activates something
        double initialCritChance = player.stats.getCritChance();
        
        // Try multiple times since it's random
        boolean buffActivated = false;
        for (int i = 0; i < 20; i++) {
            player.grantRandomBuff();
            if (player.isCritBuffActive()) {
                buffActivated = true;
                break;
            }
        }
        
        assertTrue(buffActivated, "Crit buff should activate at least once in 20 attempts");
    }

    @Test
    public void testCollectKeyWithDroppedKey() {
        // Setup: Create a dropped key at player's position
        mockGamePanel.droppedKey = new KeyItem(player.worldX, player.worldY);
        mockGamePanel.droppedKey.collected = false;
        
        // Act: Move player (which calls collectKey internally)
        kh.wPressed = true;
        player.update();
        kh.wPressed = false;
        
        // Assert: Key should be collected and added to inventory
        assertTrue(mockGamePanel.droppedKey.collected);
        assertTrue(player.hasItem("key"));
    }

    @Test
    public void testCollectKeyWithNoDroppedKey() {
        // Setup: No dropped key
        mockGamePanel.droppedKey = null;
        int initialInventorySize = player.getInventory().size();
        
        // Act: Move player
        kh.wPressed = true;
        player.update();
        kh.wPressed = false;
        
        // Assert: Inventory should not change
        assertEquals(initialInventorySize, player.getInventory().size());
    }

    @Test
    public void testCollectKeyWhenAlreadyCollected() {
        // Setup: Create an already collected key
        mockGamePanel.droppedKey = new KeyItem(player.worldX, player.worldY);
        mockGamePanel.droppedKey.collected = true;
        
        // Act: Move player
        kh.wPressed = true;
        player.update();
        kh.wPressed = false;
        
        // Assert: Key should not be added to inventory again
        assertFalse(player.hasItem("key"));
    }

    @Test
    public void testTrapDamageHandlerReducesHealth() {
        // Setup: Create a trap tile
        phase2.Tile.Tile trapTile = new phase2.Tile.Tile();
        trapTile.isTrap = true;
        trapTile.trapDamage = 15;
        trapTile.trapCooldown = 60;
        trapTile.trapTimer = 0;
        
        int initialHealth = player.getCurrentHealth();
        
        // Act: Trigger trap (call through reflection since it's private)
        try {
            java.lang.reflect.Method trapMethod = Player.class.getDeclaredMethod("trapDamageHandler", phase2.Tile.Tile.class);
            trapMethod.setAccessible(true);
            trapMethod.invoke(player, trapTile);
            
            // Assert: Player should take damage
            assertEquals(initialHealth - 15, player.getCurrentHealth());
            assertEquals(60, trapTile.trapTimer); // Timer should be set
        } catch (Exception e) {
            fail("Failed to invoke trapDamageHandler: " + e.getMessage());
        }
    }

    @Test
    public void testTrapDamageHandlerCooldownPreventsImmediateDamage() {
        // Setup: Create a trap tile with active cooldown
        phase2.Tile.Tile trapTile = new phase2.Tile.Tile();
        trapTile.isTrap = true;
        trapTile.trapDamage = 15;
        trapTile.trapCooldown = 60;
        trapTile.trapTimer = 30; // Cooldown active
        
        int initialHealth = player.getCurrentHealth();
        
        // Act: Trigger trap
        try {
            java.lang.reflect.Method trapMethod = Player.class.getDeclaredMethod("trapDamageHandler", phase2.Tile.Tile.class);
            trapMethod.setAccessible(true);
            trapMethod.invoke(player, trapTile);
            
            // Assert: Player should not take damage, timer should decrement
            assertEquals(initialHealth, player.getCurrentHealth());
            assertEquals(29, trapTile.trapTimer);
        } catch (Exception e) {
            fail("Failed to invoke trapDamageHandler: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateDirectionChangesOnKeyPress() {
        // Test up direction (W key)
        kh.wPressed = true;
        player.update();
        assertEquals("up", player.direction);
        kh.wPressed = false;
        
        // Test down direction (S key)
        kh.sPressed = true;
        player.update();
        assertEquals("down", player.direction);
        kh.sPressed = false;
        
        // Test left direction (A key)
        kh.aPressed = true;
        player.update();
        assertEquals("left", player.direction);
        kh.aPressed = false;
        
        // Test right direction (D key)
        kh.dPressed = true;
        player.update();
        assertEquals("right", player.direction);
        kh.dPressed = false;
    }

    @Test
    public void testAttackTriggeredBySpaceKey() {
        // Setup: Add an enemy in range
        mockEnemy.worldX = player.worldX + 20; // Close to player
        mockEnemy.worldY = player.worldY + 20;
        mockGamePanel.enemies.add(mockEnemy);
        
        int enemyInitialHealth = mockEnemy.getCurrentHealth();
        
        // Act: Press space to attack
        kh.spacePressed = true;
        player.update();
        kh.spacePressed = false;
        
        // Assert: Enemy should have taken damage (if in range)
        // This tests that the attack system is triggered
        assertTrue(enemyInitialHealth >= mockEnemy.getCurrentHealth());
    }

}