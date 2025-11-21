package phase2.game.combat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for CombatManager
 */
public class CombatManagerTest {

    private MockAttacker attacker;
    private MockDamageable target;

    @BeforeEach
    public void setUp() {
        // Create fresh mock entities for each test
        attacker = new MockAttacker(100, 100, 10);
        target = new MockDamageable(100, 100, 5);
        
        // Clear any listeners from previous tests
        CombatManager.removeListener(null);
    }

    @AfterEach
    public void tearDown() {
        // Clean up listeners after each test
        CombatManager.removeListener(null);
    }

    @Test
    public void resolveAttack_basicAttack_dealsDamage() {
        AttackData attack = new AttackData("Basic Attack", 20, 1);
        
        AttackResult result = CombatManager.resolveAttack(attacker, target, attack);
        
        assertNotNull(result, "Result should not be null");
        assertTrue(result.wasSuccessful(), "Attack should be successful");
        assertEquals(15, result.getDamageDealt(), "Damage should be 20 - 5 defense = 15");
        assertEquals(85, target.getCurrentHealth(), "Target health should be 85");
        assertFalse(result.targetKilled(), "Target should not be killed");
    }

    @Test
    public void resolveAttack_highDefense_dealsMinimumDamage() {
        target = new MockDamageable(100, 100, 50); // High defense
        AttackData attack = new AttackData("Weak Attack", 10, 1);
        
        AttackResult result = CombatManager.resolveAttack(attacker, target, attack);
        
        assertEquals(1, result.getDamageDealt(), "Should deal minimum 1 damage");
        assertEquals(99, target.getCurrentHealth(), "Target health should be 99");
    }

    @Test
    public void resolveAttack_trueDamage_ignoresDefense() {
        target = new MockDamageable(100, 100, 50);
        AttackData attack = new AttackData("True Strike", 20, 1, 
                                          AttackData.DamageType.TRUE, 0.0, 1.5, 0);
        
        AttackResult result = CombatManager.resolveAttack(attacker, target, attack);
        
        assertEquals(20, result.getDamageDealt(), "True damage should ignore defense");
        assertEquals(80, target.getCurrentHealth(), "Target health should be 80");
    }

    @Test
    public void resolveAttack_criticalHit_increasedDamage() {
        // Use 100% crit chance for deterministic testing
        AttackData attack = new AttackData("Critical Strike", 20, 1, 
                                          AttackData.DamageType.PHYSICAL, 1.0, 2.0, 0);
        
        AttackResult result = CombatManager.resolveAttack(attacker, target, attack);
        
        assertTrue(result.wasCritical(), "Should be a critical hit");
        assertEquals(35, result.getDamageDealt(), "Crit damage: (20 * 2.0) - 5 = 35");
        assertEquals(65, target.getCurrentHealth(), "Target health should be 65");
    }

    @Test
    public void resolveAttack_killsTarget_setsTargetKilled() {
        target = new MockDamageable(20, 100, 0); // Low health, no defense
        AttackData attack = new AttackData("Finishing Blow", 25, 1);
        
        AttackResult result = CombatManager.resolveAttack(attacker, target, attack);
        
        assertTrue(result.targetKilled(), "Target should be killed");
        assertEquals(0, target.getCurrentHealth(), "Target health should be 0");
        assertFalse(target.isAlive(), "Target should not be alive");
        assertEquals(0, result.getTargetRemainingHealth(), "Remaining health should be 0");
    }

    @Test
    public void resolveAttack_deadTarget_returnsZeroDamage() {
        target.takeDamage(200, new DamageSource("Test")); // Kill the target
        AttackData attack = new AttackData("Attack", 20, 1);
        
        AttackResult result = CombatManager.resolveAttack(attacker, target, attack);
        
        assertEquals(0, result.getDamageDealt(), "Should deal no damage to dead target");
        assertTrue(result.targetKilled(), "Target killed flag should be true");
    }

    @Test
    public void resolveAttack_nullAttacker_returnsFailedResult() {
        AttackData attack = new AttackData("Attack", 20, 1);
        
        AttackResult result = CombatManager.resolveAttack(null, target, attack);
        
        assertEquals(0, result.getDamageDealt(), "Should deal no damage");
        assertFalse(result.wasSuccessful(), "Attack should not be successful");
    }

    @Test
    public void resolveAttack_nullTarget_returnsFailedResult() {
        AttackData attack = new AttackData("Attack", 20, 1);
        
        AttackResult result = CombatManager.resolveAttack(attacker, null, attack);
        
        assertEquals(0, result.getDamageDealt(), "Should deal no damage");
        assertFalse(result.wasSuccessful(), "Attack should not be successful");
    }

    @Test
    public void resolveAttack_nullAttackData_returnsFailedResult() {
        AttackResult result = CombatManager.resolveAttack(attacker, target, null);
        
        assertEquals(0, result.getDamageDealt(), "Should deal no damage");
        assertFalse(result.wasSuccessful(), "Attack should not be successful");
    }

    @Test
    public void calculateDistance_samePosition_returnsZero() {
        int distance = CombatManager.calculateDistance(0, 0, 0, 0);
        assertEquals(0, distance, "Distance should be 0");
    }

    @Test
    public void calculateDistance_horizontalMovement_correctDistance() {
        int distance = CombatManager.calculateDistance(0, 0, 5, 0);
        assertEquals(5, distance, "Horizontal distance should be 5");
    }

    @Test
    public void calculateDistance_verticalMovement_correctDistance() {
        int distance = CombatManager.calculateDistance(0, 0, 0, 3);
        assertEquals(3, distance, "Vertical distance should be 3");
    }

    @Test
    public void calculateDistance_diagonalMovement_manhattanDistance() {
        int distance = CombatManager.calculateDistance(0, 0, 3, 4);
        assertEquals(7, distance, "Manhattan distance should be 3 + 4 = 7");
    }

    @Test
    public void canHitTarget_withinRange_returnsTrue() {
        attacker.setPosition(0, 0);
        target.setPosition(2, 0);
        AttackData attack = new AttackData("Ranged Attack", 10, 3);
        
        boolean canHit = CombatManager.canHitTarget(attacker, target, attack);
        
        assertTrue(canHit, "Should be able to hit target within range");
    }

    @Test
    public void canHitTarget_outOfRange_returnsFalse() {
        attacker.setPosition(0, 0);
        target.setPosition(5, 0);
        AttackData attack = new AttackData("Melee Attack", 10, 1);
        
        boolean canHit = CombatManager.canHitTarget(attacker, target, attack);
        
        assertFalse(canHit, "Should not be able to hit target out of range");
    }

    @Test
    public void canHitTarget_exactRange_returnsTrue() {
        attacker.setPosition(0, 0);
        target.setPosition(3, 0);
        AttackData attack = new AttackData("Attack", 10, 3);
        
        boolean canHit = CombatManager.canHitTarget(attacker, target, attack);
        
        assertTrue(canHit, "Should be able to hit target at exact range");
    }

    @Test
    public void canHitTarget_nullAttacker_returnsFalse() {
        AttackData attack = new AttackData("Attack", 10, 3);
        
        boolean canHit = CombatManager.canHitTarget(null, target, attack);
        
        assertFalse(canHit, "Should return false for null attacker");
    }

    @Test
    public void canHitTarget_nullTarget_returnsFalse() {
        AttackData attack = new AttackData("Attack", 10, 3);
        
        boolean canHit = CombatManager.canHitTarget(attacker, null, attack);
        
        assertFalse(canHit, "Should return false for null target");
    }

    @Test
    public void canHitTarget_nullAttackData_returnsFalse() {
        boolean canHit = CombatManager.canHitTarget(attacker, target, null);
        
        assertFalse(canHit, "Should return false for null attack data");
    }

    @Test
    public void combatListener_receivesAttackNotification() {
        MockCombatListener listener = new MockCombatListener();
        CombatManager.addListener(listener);
        
        AttackData attack = new AttackData("Test Attack", 20, 1);
        CombatManager.resolveAttack(attacker, target, attack);
        
        assertTrue(listener.wasAttackCalled(), "Listener should receive attack notification");
        assertEquals(1, listener.getAttackCount(), "Should have received 1 attack notification");
    }

    @Test
    public void combatListener_receivesDamageNotification() {
        MockCombatListener listener = new MockCombatListener();
        CombatManager.addListener(listener);
        
        DamageSource source = new DamageSource("Test");
        CombatManager.notifyDamage(target, 10, source);
        
        assertTrue(listener.wasDamageCalled(), "Listener should receive damage notification");
    }

    @Test
    public void combatListener_removedListener_doesNotReceiveNotifications() {
        MockCombatListener listener = new MockCombatListener();
        CombatManager.addListener(listener);
        CombatManager.removeListener(listener);
        
        AttackData attack = new AttackData("Test Attack", 20, 1);
        CombatManager.resolveAttack(attacker, target, attack);
        
        assertFalse(listener.wasAttackCalled(), "Removed listener should not receive notifications");
    }

    @Test
    public void addListener_duplicateListener_onlyAddedOnce() {
        MockCombatListener listener = new MockCombatListener();
        CombatManager.addListener(listener);
        CombatManager.addListener(listener); // Add same listener twice
        
        AttackData attack = new AttackData("Test Attack", 20, 1);
        CombatManager.resolveAttack(attacker, target, attack);
        
        assertEquals(1, listener.getAttackCount(), "Duplicate listener should only be notified once");
        CombatManager.removeListener(listener);
    }

    @Test
    public void notifyDamage_directCall_notifiesListeners() {
        MockCombatListener listener = new MockCombatListener();
        CombatManager.addListener(listener);
        
        DamageSource source = new DamageSource("Test");
        CombatManager.notifyDamage(target, 15, source);
        
        assertTrue(listener.wasDamageCalled(), "Listener should be notified of damage");
        CombatManager.removeListener(listener);
    }

    @Test
    public void notifyDeath_directCall_notifiesListeners() {
        MockCombatListener listener = new MockCombatListener();
        CombatManager.addListener(listener);
        
        DamageSource source = new DamageSource("Test");
        CombatManager.notifyDeath(target, source);
        
        assertTrue(listener.wasDeathCalled(), "Listener should be notified of death");
        CombatManager.removeListener(listener);
    }

    @Test
    public void notifyHeal_directCall_notifiesListeners() {
        MockCombatListener listener = new MockCombatListener();
        CombatManager.addListener(listener);
        
        CombatManager.notifyHeal(target, 30);
        
        assertTrue(listener.wasHealCalled(), "Listener should be notified of heal");
        CombatManager.removeListener(listener);
    }

    @Test
    public void multipleListeners_allReceiveNotifications() {
        MockCombatListener listener1 = new MockCombatListener();
        MockCombatListener listener2 = new MockCombatListener();
        MockCombatListener listener3 = new MockCombatListener();
        
        CombatManager.addListener(listener1);
        CombatManager.addListener(listener2);
        CombatManager.addListener(listener3);
        
        AttackData attack = new AttackData("Test Attack", 20, 1);
        CombatManager.resolveAttack(attacker, target, attack);
        
        assertTrue(listener1.wasAttackCalled(), "Listener 1 should be notified");
        assertTrue(listener2.wasAttackCalled(), "Listener 2 should be notified");
        assertTrue(listener3.wasAttackCalled(), "Listener 3 should be notified");
        
        CombatManager.removeListener(listener1);
        CombatManager.removeListener(listener2);
        CombatManager.removeListener(listener3);
    }

    @Test
    public void resolveAttack_variousDamageTypes_calculatesCorrectly() {
        // Physical damage
        AttackData physical = new AttackData("Physical", 30, 1, 
                                            AttackData.DamageType.PHYSICAL, 0.0, 1.0, 0);
        AttackResult result1 = CombatManager.resolveAttack(attacker, target, physical);
        assertEquals(25, result1.getDamageDealt()); // 30 - 5 defense
        
        // Magical damage (still affected by defense)
        AttackData magical = new AttackData("Magical", 30, 1, 
                                           AttackData.DamageType.MAGICAL, 0.0, 1.0, 0);
        AttackResult result2 = CombatManager.resolveAttack(attacker, target, magical);
        assertEquals(25, result2.getDamageDealt()); // 30 - 5 defense
        
        // Fire damage (still affected by defense)
        AttackData fire = new AttackData("Fire", 30, 1, 
                                        AttackData.DamageType.FIRE, 0.0, 1.0, 0);
        AttackResult result3 = CombatManager.resolveAttack(attacker, target, fire);
        assertEquals(25, result3.getDamageDealt()); // 30 - 5 defense
    }

    @Test
    public void calculateDistance_negativeCoordinates_calculatesCorrectly() {
        int distance = CombatManager.calculateDistance(-5, -5, 5, 5);
        assertEquals(20, distance, "Distance should handle negative coordinates");
    }

    @Test
    public void canHitTarget_diagonalDistance_checksCorrectly() {
        attacker.setPosition(0, 0);
        target.setPosition(2, 2);
        AttackData attack = new AttackData("Ranged", 10, 4);
        
        boolean canHit = CombatManager.canHitTarget(attacker, target, attack);
        
        assertTrue(canHit, "Manhattan distance is 4, should be within range 4");
    }

    // Mock classes for testing

    private static class MockAttacker implements Attacker {
        private int x, y;
        private int power;
        private int range;

        public MockAttacker(int x, int y, int power) {
            this.x = x;
            this.y = y;
            this.power = power;
            this.range = 1;
        }

        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public AttackData getAttackData() {
            return new AttackData("Mock Attack", power, range);
        }

        @Override
        public AttackResult attack(Damageable target) {
            return CombatManager.resolveAttack(this, target, getAttackData());
        }

        @Override
        public boolean canAttack() {
            return true;
        }

        @Override
        public boolean isInRange(Damageable target) {
            return CombatManager.canHitTarget(this, target, getAttackData());
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
        public void updateCooldown() {
            // Mock implementation
        }
    }

    private static class MockDamageable implements Damageable {
        private int currentHealth;
        private int maxHealth;
        private int defense;
        private int x, y;

        public MockDamageable(int health, int maxHealth, int defense) {
            this.currentHealth = health;
            this.maxHealth = maxHealth;
            this.defense = defense;
            this.x = 0;
            this.y = 0;
        }

        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void takeDamage(int amount, DamageSource source) {
            currentHealth = Math.max(0, currentHealth - amount);
            if (!isAlive()) {
                CombatManager.notifyDeath(this, source);
            }
        }

        @Override
        public boolean isAlive() {
            return currentHealth > 0;
        }

        @Override
        public int getCurrentHealth() {
            return currentHealth;
        }

        @Override
        public int getMaxHealth() {
            return maxHealth;
        }

        @Override
        public int getDefense() {
            return defense;
        }

        @Override
        public void heal(int amount) {
            currentHealth = Math.min(maxHealth, currentHealth + amount);
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
            // Mock implementation
        }
    }

    private static class MockCombatListener implements CombatListener {
        private boolean attackCalled = false;
        private boolean damageCalled = false;
        private boolean deathCalled = false;
        private boolean healCalled = false;
        private int attackCount = 0;

        @Override
        public void onAttack(Attacker attacker, Damageable target, AttackResult result) {
            attackCalled = true;
            attackCount++;
        }

        @Override
        public void onDamage(Damageable target, int amount, DamageSource source) {
            damageCalled = true;
        }

        @Override
        public void onDeath(Damageable target, DamageSource source) {
            deathCalled = true;
        }

        @Override
        public void onHeal(Damageable target, int amount) {
            healCalled = true;
        }

        public boolean wasAttackCalled() {
            return attackCalled;
        }

        public boolean wasDamageCalled() {
            return damageCalled;
        }

        public boolean wasDeathCalled() {
            return deathCalled;
        }

        public boolean wasHealCalled() {
            return healCalled;
        }

        public int getAttackCount() {
            return attackCount;
        }
    }
}
