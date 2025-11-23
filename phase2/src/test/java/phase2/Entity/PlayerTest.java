package test.java.phase2.Entity;

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

}