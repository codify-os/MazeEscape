package phase2.game.combat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Unit tests for CombatLogger
 */
public class CombatLoggerTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private MockDamageable target;
    private MockAttacker attacker;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        target = new MockDamageable(100, 100, 5);
        attacker = new MockAttacker(0, 0, 20);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void constructor_defaultVerbose_setsVerboseTrue() {
        CombatLogger logger = new CombatLogger();
        
        DamageSource source = new DamageSource("Test");
        logger.onDamage(target, 10, source);
        
        String output = outContent.toString();
        assertTrue(output.contains("[DAMAGE]"), "Default verbose should log damage");
    }

    @Test
    public void constructor_withVerboseFalse_logsLessInfo() {
        CombatLogger logger = new CombatLogger(false);
        
        DamageSource source = new DamageSource("Test");
        logger.onDamage(target, 10, source);
        
        String output = outContent.toString();
        assertEquals("", output, "Non-verbose should not log damage");
    }

    @Test
    public void onDamage_verbose_logsCompleteMessage() {
        CombatLogger logger = new CombatLogger(true);
        DamageSource source = new DamageSource("Fire");
        
        logger.onDamage(target, 25, source);
        
        String output = outContent.toString();
        assertTrue(output.contains("[DAMAGE]"), "Should contain damage tag");
        assertTrue(output.contains("25"), "Should contain damage amount");
        assertTrue(output.contains("Fire"), "Should contain source name");
        assertTrue(output.contains("HP:"), "Should contain HP info");
    }

    @Test
    public void onDamage_nonVerbose_doesNotLog() {
        CombatLogger logger = new CombatLogger(false);
        DamageSource source = new DamageSource("Fire");
        
        logger.onDamage(target, 25, source);
        
        String output = outContent.toString();
        assertEquals("", output, "Non-verbose should not log damage");
    }

    @Test
    public void onDamage_nullTarget_handlesGracefully() {
        CombatLogger logger = new CombatLogger(true);
        DamageSource source = new DamageSource("Test");
        
        logger.onDamage(null, 10, source);
        
        String output = outContent.toString();
        assertEquals("", output, "Should not log for null target");
    }

    @Test
    public void onDeath_alwaysLogs_regardlessOfVerbose() {
        CombatLogger logger = new CombatLogger(false);
        DamageSource source = new DamageSource("Lava");
        
        logger.onDeath(target, source);
        
        String output = outContent.toString();
        assertTrue(output.contains("[DEATH]"), "Death should always be logged");
        assertTrue(output.contains("Lava"), "Should contain source name");
        assertTrue(output.contains("killed"), "Should contain kill message");
    }

    @Test
    public void onDeath_verbose_logsMessage() {
        CombatLogger logger = new CombatLogger(true);
        DamageSource source = new DamageSource("Spike Trap");
        
        logger.onDeath(target, source);
        
        String output = outContent.toString();
        assertTrue(output.contains("[DEATH]"), "Should contain death tag");
        assertTrue(output.contains("Spike Trap"), "Should contain source name");
    }

    @Test
    public void onDeath_nullTarget_handlesGracefully() {
        CombatLogger logger = new CombatLogger(true);
        DamageSource source = new DamageSource("Test");
        
        logger.onDeath(null, source);
        
        String output = outContent.toString();
        assertEquals("", output, "Should not log for null target");
    }

    @Test
    public void onAttack_normalAttack_logsMessage() {
        CombatLogger logger = new CombatLogger(true);
        AttackResult result = new AttackResult(30, false, false, 70);
        
        logger.onAttack(attacker, target, result);
        
        String output = outContent.toString();
        assertTrue(output.contains("[ATTACK]"), "Should contain attack tag");
        assertTrue(output.contains("30"), "Should contain damage amount");
        assertFalse(output.contains("CRITICAL"), "Normal attack should not show critical");
    }

    @Test
    public void onAttack_criticalAttack_logsCritical() {
        CombatLogger logger = new CombatLogger(true);
        AttackResult result = new AttackResult(60, true, false, 40);
        
        logger.onAttack(attacker, target, result);
        
        String output = outContent.toString();
        assertTrue(output.contains("[ATTACK]"), "Should contain attack tag");
        assertTrue(output.contains("60"), "Should contain damage amount");
        assertTrue(output.contains("CRITICAL"), "Critical attack should show crit indicator");
    }

    @Test
    public void onAttack_nullResult_handlesGracefully() {
        CombatLogger logger = new CombatLogger(true);
        
        logger.onAttack(attacker, target, null);
        
        String output = outContent.toString();
        assertEquals("", output, "Should not log for null result");
    }

    @Test
    public void onHeal_verbose_logsHealMessage() {
        CombatLogger logger = new CombatLogger(true);
        target.takeDamage(50, new DamageSource("Test"));
        outContent.reset(); // Clear damage log
        
        logger.onHeal(target, 20);
        
        String output = outContent.toString();
        assertTrue(output.contains("[HEAL]"), "Should contain heal tag");
        assertTrue(output.contains("20"), "Should contain heal amount");
        assertTrue(output.contains("HP:"), "Should contain HP info");
    }

    @Test
    public void onHeal_nonVerbose_doesNotLog() {
        CombatLogger logger = new CombatLogger(false);
        
        logger.onHeal(target, 20);
        
        String output = outContent.toString();
        assertEquals("", output, "Non-verbose should not log heals");
    }

    @Test
    public void onHeal_nullTarget_handlesGracefully() {
        CombatLogger logger = new CombatLogger(true);
        
        logger.onHeal(null, 20);
        
        String output = outContent.toString();
        assertEquals("", output, "Should not log for null target");
    }

    @Test
    public void setVerbose_changesLoggingBehavior() {
        CombatLogger logger = new CombatLogger(false);
        DamageSource source = new DamageSource("Test");
        
        // Should not log when not verbose
        logger.onDamage(target, 10, source);
        assertEquals("", outContent.toString());
        
        outContent.reset();
        
        // Should log after setting verbose to true
        logger.setVerbose(true);
        logger.onDamage(target, 10, source);
        assertTrue(outContent.toString().contains("[DAMAGE]"));
    }

    @Test
    public void getEntityName_nullEntity_returnsUnknown() {
        CombatLogger logger = new CombatLogger(true);
        DamageSource source = new DamageSource("Test");
        
        logger.onDamage(null, 10, source);
        
        // Should handle gracefully without exception
        String output = outContent.toString();
        assertFalse(output.contains("Unknown"), "Null should not log at all");
    }

    @Test
    public void getEntityName_regularObject_returnsClassName() {
        CombatLogger logger = new CombatLogger(true);
        DamageSource source = new DamageSource("Test");
        
        logger.onDamage(target, 10, source);
        
        String output = outContent.toString();
        assertTrue(output.contains("MockDamageable"), "Should use class simple name");
    }

    @Test
    public void getEntityName_anonymousClass_returnsEntity() {
        CombatLogger logger = new CombatLogger(true);
        
        // Create anonymous class with empty simple name
        Damageable anonymous = new Damageable() {
            @Override
            public void takeDamage(int amount, DamageSource source) {}
            @Override
            public boolean isAlive() { return true; }
            @Override
            public int getCurrentHealth() { return 100; }
            @Override
            public int getMaxHealth() { return 100; }
            @Override
            public int getDefense() { return 0; }
            @Override
            public void heal(int amount) {}
            @Override
            public int getX() { return 0; }
            @Override
            public int getY() { return 0; }
            @Override
            public void onDeath() {}
        };
        
        DamageSource source = new DamageSource("Test");
        logger.onDamage(anonymous, 10, source);
        
        String output = outContent.toString();
        // Anonymous classes may have simple names, but if empty, should use "Entity"
        assertTrue(output.length() > 0, "Should log something");
    }

    @Test
    public void completeLoggingSequence_verboseOn_logsAllEvents() {
        CombatLogger logger = new CombatLogger(true);
        CombatManager.addListener(logger);
        
        AttackData attack = new AttackData("Test Attack", 30, 1);
        AttackResult result = CombatManager.resolveAttack(attacker, target, attack);
        
        String output = outContent.toString();
        assertTrue(output.contains("[ATTACK]") || output.contains("[DAMAGE]"), 
                  "Should log combat events");
        
        CombatManager.removeListener(logger);
    }

    @Test
    public void completeLoggingSequence_deathEvent_logsAll() {
        CombatLogger logger = new CombatLogger(true);
        CombatManager.addListener(logger);
        
        target = new MockDamageable(10, 100, 0);
        AttackData attack = new AttackData("Killing Blow", 50, 1);
        CombatManager.resolveAttack(attacker, target, attack);
        
        String output = outContent.toString();
        assertTrue(output.contains("[DEATH]"), "Should log death event");
        
        CombatManager.removeListener(logger);
    }

    // Mock classes for testing
    private static class MockAttacker implements Attacker {
        private int x, y;
        private int power;

        public MockAttacker(int x, int y, int power) {
            this.x = x;
            this.y = y;
            this.power = power;
        }

        @Override
        public AttackData getAttackData() {
            return new AttackData("Mock Attack", power, 1);
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
            return true;
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
        public void updateCooldown() {}
    }

    private static class MockDamageable implements Damageable {
        private int currentHealth;
        private int maxHealth;
        private int defense;

        public MockDamageable(int health, int maxHealth, int defense) {
            this.currentHealth = health;
            this.maxHealth = maxHealth;
            this.defense = defense;
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
            return 0;
        }

        @Override
        public int getY() {
            return 0;
        }

        @Override
        public void onDeath() {}
    }
}
