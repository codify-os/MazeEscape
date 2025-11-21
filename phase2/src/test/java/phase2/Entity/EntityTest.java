package phase2.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import phase2.game.combat.*;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Entity abstract class
 * Uses a concrete test implementation to test Entity behavior
 */
public class EntityTest {

    private TestEntity entity;
    private MockDamageable mockTarget;

    @BeforeEach
    public void setUp() {
        entity = new TestEntity();
        mockTarget = new MockDamageable();
    }

    // =================== Constructor and Initialization Tests ===================

    @Test
    public void constructor_initializesHealthComponent() {
        assertNotNull(entity.health, "Health component should be initialized");
        assertEquals(100, entity.health.getCurrentHealth(), "Should start with full health");
        assertTrue(entity.health.isAlive(), "Entity should be alive initially");
    }

    @Test
    public void constructor_initializesStats() {
        assertNotNull(entity.stats, "Stats should be initialized");
        assertTrue(entity.stats.getDefense() >= 0, "Defense should be non-negative");
    }

    @Test
    public void constructor_initializesAttackData() {
        assertNotNull(entity.currentAttack, "Attack data should be initialized");
        assertTrue(entity.currentAttack.getPower() > 0, "Attack should have power");
    }

    // =================== Position Tests ===================

    @Test
    public void getWorldX_returnsCorrectPosition() {
        entity.worldX = 100;
        assertEquals(100, entity.getWorldX(), "Should return worldX value");
    }

    @Test
    public void getWorldY_returnsCorrectPosition() {
        entity.worldY = 200;
        assertEquals(200, entity.getWorldY(), "Should return worldY value");
    }

    @Test
    public void getX_returnsWorldX() {
        entity.worldX = 150;
        assertEquals(150, entity.getX(), "getX should return worldX");
    }

    @Test
    public void getY_returnsWorldY() {
        entity.worldY = 250;
        assertEquals(250, entity.getY(), "getY should return worldY");
    }

    // =================== Damageable Interface Tests ===================

    @Test
    public void takeDamage_reducesHealth() {
        DamageSource source = new DamageSource("Test Attack");
        int initialHealth = entity.getCurrentHealth();
        
        entity.takeDamage(20, source);
        
        assertEquals(initialHealth - 20, entity.getCurrentHealth(), "Health should be reduced by damage amount");
        assertTrue(entity.isAlive(), "Entity should still be alive");
    }

    @Test
    public void takeDamage_setsDamageFlashTimer() {
        DamageSource source = new DamageSource("Test Attack");
        
        entity.takeDamage(10, source);
        
        assertEquals(10, entity.damageFlashTimer, "Damage flash timer should be set to 10");
    }

    @Test
    public void takeDamage_setsDamageTextTimer() {
        DamageSource source = new DamageSource("Test Attack");
        
        entity.takeDamage(15, source);
        
        assertEquals(30, entity.damageTextTimer, "Damage text timer should be set to 30");
    }

    @Test
    public void takeDamage_storesPreviousDamageAmount() {
        DamageSource source = new DamageSource("Test Attack");
        
        entity.takeDamage(25, source);
        
        assertEquals(25, entity.previousDamageAmount, "Should store the damage amount");
    }

    @Test
    public void takeDamage_lethalDamage_callsOnDeath() {
        DamageSource source = new DamageSource("Killing Blow");
        
        entity.takeDamage(200, source);
        
        assertFalse(entity.isAlive(), "Entity should be dead");
        assertTrue(entity.onDeathCalled, "onDeath should have been called");
    }

    @Test
    public void takeDamage_nonLethalDamage_doesNotCallOnDeath() {
        DamageSource source = new DamageSource("Light Attack");
        
        entity.takeDamage(10, source);
        
        assertTrue(entity.isAlive(), "Entity should still be alive");
        assertFalse(entity.onDeathCalled, "onDeath should not be called for non-lethal damage");
    }

    @Test
    public void isAlive_returnsHealthComponentStatus() {
        assertTrue(entity.isAlive(), "Should be alive initially");
        
        entity.takeDamage(100, new DamageSource("Test"));
        assertFalse(entity.isAlive(), "Should be dead after lethal damage");
    }

    @Test
    public void getCurrentHealth_returnsHealthComponentValue() {
        assertEquals(100, entity.getCurrentHealth(), "Should return current health");
        
        entity.takeDamage(30, new DamageSource("Test"));
        assertEquals(70, entity.getCurrentHealth(), "Should return updated health");
    }

    @Test
    public void getMaxHealth_returnsHealthComponentValue() {
        assertEquals(100, entity.getMaxHealth(), "Should return max health");
    }

    @Test
    public void getDefense_returnsStatsDefense() {
        int defense = entity.getDefense();
        assertEquals(entity.stats.getDefense(), defense, "Should return stats defense value");
    }

    @Test
    public void heal_increasesHealth() {
        entity.takeDamage(40, new DamageSource("Test"));
        
        entity.heal(20);
        
        assertEquals(80, entity.getCurrentHealth(), "Health should increase");
    }

    // =================== Attacker Interface Tests ===================

    @Test
    public void getAttackData_returnsCurrentAttack() {
        AttackData attackData = entity.getAttackData();
        assertNotNull(attackData, "Should return attack data");
        assertEquals(entity.currentAttack, attackData, "Should return current attack");
    }

    @Test
    public void attack_whenCanAttack_returnsResult() {
        entity.coolDown = 0;
        mockTarget.setAlive(true);
        
        AttackResult result = entity.attack(mockTarget);
        
        assertNotNull(result, "Should return attack result");
    }

    @Test
    public void attack_whenOnCooldown_returnsNull() {
        entity.coolDown = 5;
        
        AttackResult result = entity.attack(mockTarget);
        
        assertNull(result, "Should return null when on cooldown");
    }

    @Test
    public void attack_setsCooldown() {
        entity.coolDown = 0;
        mockTarget.setAlive(true);
        int expectedCooldown = entity.currentAttack.getCooldown();
        
        entity.attack(mockTarget);
        
        assertEquals(expectedCooldown, entity.coolDown, "Should set cooldown after attack");
    }

    @Test
    public void attack_targetIsEntity_updatesDamageDisplay() {
        entity.coolDown = 0;
        TestEntity targetEntity = new TestEntity();
        
        entity.attack(targetEntity);
        
        assertTrue(targetEntity.damageTextTimer > 0, "Target should have damage text timer set");
        assertTrue(targetEntity.previousDamageAmount >= 0, "Target should have damage amount stored");
    }

    @Test
    public void canAttack_whenCooldownZero_returnsTrue() {
        entity.coolDown = 0;
        assertTrue(entity.canAttack(), "Should be able to attack when cooldown is 0");
    }

    @Test
    public void canAttack_whenCooldownPositive_returnsFalse() {
        entity.coolDown = 3;
        assertFalse(entity.canAttack(), "Should not be able to attack when on cooldown");
    }

    @Test
    public void updateCooldown_reducesWhenPositive() {
        entity.coolDown = 5;
        
        entity.updateCooldown();
        
        assertEquals(4, entity.coolDown, "Cooldown should decrease by 1");
    }

    @Test
    public void updateCooldown_staysAtZero() {
        entity.coolDown = 0;
        
        entity.updateCooldown();
        
        assertEquals(0, entity.coolDown, "Cooldown should stay at 0");
    }

    @Test
    public void updateCooldown_multipleCalls_decreasesCorrectly() {
        entity.coolDown = 10;
        
        for (int i = 0; i < 5; i++) {
            entity.updateCooldown();
        }
        
        assertEquals(5, entity.coolDown, "Cooldown should decrease correctly over multiple updates");
    }

    @Test
    public void isInRange_targetInRange_returnsTrue() {
        entity.worldX = 0;
        entity.worldY = 0;
        mockTarget.setPosition(48, 0); // 1 tile away (48 pixels = 1 tile at default size)
        
        // Assuming basic attack has range >= 1
        boolean inRange = entity.isInRange(mockTarget);
        
        assertTrue(inRange || entity.currentAttack.getRange() < 1, 
            "Target should be in range or attack has range < 1");
    }

    @Test
    public void isInRange_targetFarAway_returnsFalse() {
        entity.worldX = 0;
        entity.worldY = 0;
        mockTarget.setPosition(10000, 10000); // Very far away
        
        boolean inRange = entity.isInRange(mockTarget);
        
        assertFalse(inRange, "Target should be out of range");
    }

    // =================== Health Bar Drawing Tests ===================

    @Test
    public void drawHealthBar_doesNotThrowException() {
        // Create a mock graphics context
        Image img = new java.awt.image.BufferedImage(100, 100, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        
        assertDoesNotThrow(() -> {
            entity.drawHealthBar(g2d, 10, 10, 50, 5);
        }, "Drawing health bar should not throw exception");
        
        g2d.dispose();
    }

    // =================== Concrete Test Entity Implementation ===================

    /**
     * Concrete implementation of Entity for testing
     */
    private static class TestEntity extends Entity {
        public boolean onDeathCalled = false;
        public int updateCallCount = 0;
        public int drawCallCount = 0;

        public TestEntity() {
            super();
            this.worldX = 0;
            this.worldY = 0;
            this.speed = 4;
            this.direction = "down";
            this.collisionArea = new Rectangle(0, 0, 48, 48);
        }

        @Override
        public void update() {
            updateCallCount++;
        }

        @Override
        public void draw(Graphics2D g2d) {
            drawCallCount++;
        }

        @Override
        public void onDeath() {
            super.onDeath();
            onDeathCalled = true;
        }
    }

    /**
     * Mock Damageable implementation for testing
     */
    private static class MockDamageable implements Damageable {
        private int health = 100;
        private boolean alive = true;
        private int x = 0;
        private int y = 0;

        public void setAlive(boolean alive) {
            this.alive = alive;
            if (!alive) {
                health = 0;
            }
        }

        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void takeDamage(int amount, DamageSource source) {
            health = Math.max(0, health - amount);
            if (health == 0) {
                alive = false;
            }
        }

        @Override
        public boolean isAlive() {
            return alive;
        }

        @Override
        public int getCurrentHealth() {
            return health;
        }

        @Override
        public int getMaxHealth() {
            return 100;
        }

        @Override
        public int getDefense() {
            return 0;
        }

        @Override
        public void heal(int amount) {
            health = Math.min(100, health + amount);
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public void onDeath() {
            alive = false;
        }
    }
}
