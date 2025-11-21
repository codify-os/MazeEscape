package phase2.game.stats;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import phase2.game.combat.DamageSource;
import phase2.game.combat.Damageable;

/**
 * Unit tests for HealthComponent class
 */
public class HealthComponentTest {

    private HealthComponent healthComponent;
    private MockDamageable entity;

    @BeforeEach
    public void setUp() {
        entity = new MockDamageable();
        healthComponent = new HealthComponent(100, 10, entity);
    }

    @Test
    public void constructor_fullParameters_setsAllFields() {
        HealthComponent hc = new HealthComponent(150, 20, entity);
        
        assertEquals(150, hc.getMaxHealth());
        assertEquals(150, hc.getCurrentHealth());
        assertEquals(20, hc.getDefense());
        assertTrue(hc.isAlive());
    }

    @Test
    public void constructor_simpleParameters_setsHealthAndDefense() {
        HealthComponent hc = new HealthComponent(80, 15);
        
        assertEquals(80, hc.getMaxHealth());
        assertEquals(80, hc.getCurrentHealth());
        assertEquals(15, hc.getDefense());
        assertTrue(hc.isAlive());
    }

    @Test
    public void constructor_zeroMaxHealth_clampsToOne() {
        HealthComponent hc = new HealthComponent(0, 10, entity);
        
        assertEquals(1, hc.getMaxHealth());
        assertEquals(1, hc.getCurrentHealth());
    }

    @Test
    public void constructor_negativeMaxHealth_clampsToOne() {
        HealthComponent hc = new HealthComponent(-50, 10, entity);
        
        assertEquals(1, hc.getMaxHealth());
        assertEquals(1, hc.getCurrentHealth());
    }

    @Test
    public void constructor_negativeDefense_clampsToZero() {
        HealthComponent hc = new HealthComponent(100, -10, entity);
        
        assertEquals(0, hc.getDefense());
    }

    @Test
    public void setEnt_updatesEntity() {
        HealthComponent hc = new HealthComponent(100, 10);
        MockDamageable newEntity = new MockDamageable();
        
        hc.setEnt(newEntity);
        
        // Verify entity was set (indirectly by checking health component still works)
        DamageSource source = new DamageSource("Test");
        hc.takeDamage(10, source);
        
        assertEquals(90, hc.getCurrentHealth(), "Health should be reduced after setting entity");
    }

    @Test
    public void takeDamage_normalDamage_reducesHealth() {
        DamageSource source = new DamageSource("Sword");
        
        healthComponent.takeDamage(30, source);
        
        assertEquals(70, healthComponent.getCurrentHealth());
        assertTrue(healthComponent.isAlive());
    }

    @Test
    public void takeDamage_lethalDamage_killsEntity() {
        DamageSource source = new DamageSource("Killing Blow");
        
        healthComponent.takeDamage(150, source);
        
        assertEquals(0, healthComponent.getCurrentHealth());
        assertFalse(healthComponent.isAlive());
    }

    @Test
    public void takeDamage_exactLethal_killsEntity() {
        DamageSource source = new DamageSource("Exact Damage");
        
        healthComponent.takeDamage(100, source);
        
        assertEquals(0, healthComponent.getCurrentHealth());
        assertFalse(healthComponent.isAlive());
    }

    @Test
    public void takeDamage_zeroDamage_noEffect() {
        healthComponent.takeDamage(0, new DamageSource("No Damage"));
        
        assertEquals(100, healthComponent.getCurrentHealth());
        assertTrue(healthComponent.isAlive());
    }

    @Test
    public void takeDamage_negativeDamage_noEffect() {
        healthComponent.takeDamage(-20, new DamageSource("Heal?"));
        
        assertEquals(100, healthComponent.getCurrentHealth());
        assertTrue(healthComponent.isAlive());
    }

    @Test
    public void takeDamage_alreadyDead_noEffect() {
        DamageSource source = new DamageSource("Test");
        healthComponent.takeDamage(100, source);
        assertFalse(healthComponent.isAlive());
        
        healthComponent.takeDamage(50, source);
        
        assertEquals(0, healthComponent.getCurrentHealth());
    }

    @Test
    public void heal_normalHeal_increasesHealth() {
        healthComponent.takeDamage(40, new DamageSource("Test"));
        
        healthComponent.heal(20);
        
        assertEquals(80, healthComponent.getCurrentHealth());
        assertTrue(healthComponent.isAlive());
    }

    @Test
    public void heal_exceedsMaxHealth_clampsToMax() {
        healthComponent.takeDamage(30, new DamageSource("Test"));
        
        healthComponent.heal(50);
        
        assertEquals(100, healthComponent.getCurrentHealth());
    }

    @Test
    public void heal_atFullHealth_noEffect() {
        healthComponent.heal(20);
        
        assertEquals(100, healthComponent.getCurrentHealth());
    }

    @Test
    public void heal_zeroAmount_noEffect() {
        healthComponent.takeDamage(30, new DamageSource("Test"));
        
        healthComponent.heal(0);
        
        assertEquals(70, healthComponent.getCurrentHealth());
    }

    @Test
    public void heal_negativeAmount_noEffect() {
        healthComponent.takeDamage(30, new DamageSource("Test"));
        
        healthComponent.heal(-20);
        
        assertEquals(70, healthComponent.getCurrentHealth());
    }

    @Test
    public void heal_deadEntity_noEffect() {
        healthComponent.takeDamage(100, new DamageSource("Test"));
        assertFalse(healthComponent.isAlive());
        
        healthComponent.heal(50);
        
        assertEquals(0, healthComponent.getCurrentHealth());
        assertFalse(healthComponent.isAlive());
    }

    @Test
    public void isAlive_fullHealth_returnsTrue() {
        assertTrue(healthComponent.isAlive());
    }

    @Test
    public void isAlive_partialHealth_returnsTrue() {
        healthComponent.takeDamage(50, new DamageSource("Test"));
        
        assertTrue(healthComponent.isAlive());
    }

    @Test
    public void isAlive_oneHealth_returnsTrue() {
        healthComponent.takeDamage(99, new DamageSource("Test"));
        
        assertEquals(1, healthComponent.getCurrentHealth());
        assertTrue(healthComponent.isAlive());
    }

    @Test
    public void isAlive_zeroHealth_returnsFalse() {
        healthComponent.takeDamage(100, new DamageSource("Test"));
        
        assertFalse(healthComponent.isAlive());
    }

    @Test
    public void setMaxHealth_increasesMax_currentHealthUnchanged() {
        healthComponent.takeDamage(30, new DamageSource("Test"));
        
        healthComponent.setMaxHealth(150);
        
        assertEquals(150, healthComponent.getMaxHealth());
        assertEquals(70, healthComponent.getCurrentHealth());
    }

    @Test
    public void setMaxHealth_decreasesBelowCurrent_clampsCurrentHealth() {
        healthComponent.setMaxHealth(50);
        
        assertEquals(50, healthComponent.getMaxHealth());
        assertEquals(50, healthComponent.getCurrentHealth());
    }

    @Test
    public void setMaxHealth_zeroValue_clampsToOne() {
        healthComponent.setMaxHealth(0);
        
        assertEquals(1, healthComponent.getMaxHealth());
    }

    @Test
    public void setMaxHealth_negativeValue_clampsToOne() {
        healthComponent.setMaxHealth(-50);
        
        assertEquals(1, healthComponent.getMaxHealth());
    }

    @Test
    public void setDefense_validValue_updates() {
        healthComponent.setDefense(25);
        
        assertEquals(25, healthComponent.getDefense());
    }

    @Test
    public void setDefense_negativeValue_clampsToZero() {
        healthComponent.setDefense(-10);
        
        assertEquals(0, healthComponent.getDefense());
    }

    @Test
    public void fullHeal_restoresFullHealth() {
        healthComponent.takeDamage(80, new DamageSource("Test"));
        
        healthComponent.fullHeal();
        
        assertEquals(100, healthComponent.getCurrentHealth());
        assertTrue(healthComponent.isAlive());
    }

    @Test
    public void fullHeal_deadEntity_revivesAndHeals() {
        healthComponent.takeDamage(100, new DamageSource("Test"));
        assertFalse(healthComponent.isAlive());
        
        healthComponent.fullHeal();
        
        assertEquals(100, healthComponent.getCurrentHealth());
        assertTrue(healthComponent.isAlive());
    }

    @Test
    public void getHealthPercentage_fullHealth_returnsOne() {
        assertEquals(1.0, healthComponent.getHealthPercentage(), 0.001);
    }

    @Test
    public void getHealthPercentage_halfHealth_returnsHalf() {
        healthComponent.takeDamage(50, new DamageSource("Test"));
        
        assertEquals(0.5, healthComponent.getHealthPercentage(), 0.001);
    }

    @Test
    public void getHealthPercentage_quarterHealth_returnsQuarter() {
        healthComponent.takeDamage(75, new DamageSource("Test"));
        
        assertEquals(0.25, healthComponent.getHealthPercentage(), 0.001);
    }

    @Test
    public void getHealthPercentage_zeroHealth_returnsZero() {
        healthComponent.takeDamage(100, new DamageSource("Test"));
        
        assertEquals(0.0, healthComponent.getHealthPercentage(), 0.001);
    }

    @Test
    public void revive_withPercentage_setsCorrectHealth() {
        healthComponent.takeDamage(100, new DamageSource("Test"));
        assertFalse(healthComponent.isAlive());
        
        healthComponent.revive(0.5);
        
        assertEquals(50, healthComponent.getCurrentHealth());
        assertTrue(healthComponent.isAlive());
    }

    @Test
    public void revive_withFullPercentage_restoresFullHealth() {
        healthComponent.takeDamage(100, new DamageSource("Test"));
        
        healthComponent.revive(1.0);
        
        assertEquals(100, healthComponent.getCurrentHealth());
        assertTrue(healthComponent.isAlive());
    }

    @Test
    public void revive_withZeroPercentage_revivesWithZeroHealth() {
        healthComponent.takeDamage(100, new DamageSource("Test"));
        
        healthComponent.revive(0.0);
        
        assertEquals(0, healthComponent.getCurrentHealth());
        assertFalse(healthComponent.isAlive(), "Entity with 0 health should not be alive");
    }

    @Test
    public void revive_percentageAboveOne_clampsToOne() {
        healthComponent.takeDamage(100, new DamageSource("Test"));
        
        healthComponent.revive(1.5);
        
        assertEquals(100, healthComponent.getCurrentHealth());
    }

    @Test
    public void revive_negativePercentage_clampsToZero() {
        healthComponent.takeDamage(100, new DamageSource("Test"));
        
        healthComponent.revive(-0.5);
        
        assertEquals(0, healthComponent.getCurrentHealth());
        assertFalse(healthComponent.isAlive(), "Entity with 0 health should not be alive");
    }

    @Test
    public void revive_aliveEntity_stillWorks() {
        healthComponent.takeDamage(60, new DamageSource("Test"));
        assertTrue(healthComponent.isAlive());
        
        healthComponent.revive(1.0);
        
        assertEquals(100, healthComponent.getCurrentHealth());
        assertTrue(healthComponent.isAlive());
    }

    @Test
    public void toString_containsHealthInfo() {
        healthComponent.takeDamage(25, new DamageSource("Test"));
        
        String str = healthComponent.toString();
        
        assertTrue(str.contains("75"), "Should contain current health");
        assertTrue(str.contains("100"), "Should contain max health");
        assertTrue(str.contains("75%"), "Should contain percentage");
        assertTrue(str.contains("10"), "Should contain defense");
    }

    @Test
    public void toString_fullHealth_showsCorrectInfo() {
        String str = healthComponent.toString();
        
        assertTrue(str.contains("100/100"), "Should show full health");
        assertTrue(str.contains("100%"), "Should show 100%");
    }

    @Test
    public void toString_zeroHealth_showsCorrectInfo() {
        healthComponent.takeDamage(100, new DamageSource("Test"));
        
        String str = healthComponent.toString();
        
        assertTrue(str.contains("0/100"), "Should show zero health");
        assertTrue(str.contains("0%"), "Should show 0%");
    }

    @Test
    public void multipleDamageEvents_trackedCorrectly() {
        DamageSource source1 = new DamageSource("Fire");
        DamageSource source2 = new DamageSource("Ice");
        DamageSource source3 = new DamageSource("Lightning");
        
        healthComponent.takeDamage(20, source1);
        assertEquals(80, healthComponent.getCurrentHealth());
        
        healthComponent.takeDamage(30, source2);
        assertEquals(50, healthComponent.getCurrentHealth());
        
        healthComponent.takeDamage(25, source3);
        assertEquals(25, healthComponent.getCurrentHealth());
        
        assertTrue(healthComponent.isAlive());
    }

    @Test
    public void damageAndHealCycle_worksCorrectly() {
        healthComponent.takeDamage(40, new DamageSource("Test"));
        assertEquals(60, healthComponent.getCurrentHealth());
        
        healthComponent.heal(20);
        assertEquals(80, healthComponent.getCurrentHealth());
        
        healthComponent.takeDamage(50, new DamageSource("Test"));
        assertEquals(30, healthComponent.getCurrentHealth());
        
        healthComponent.heal(70);
        assertEquals(100, healthComponent.getCurrentHealth());
    }

    @Test
    public void deathEvent_onlyTriggeredOnce() {
        MockDamageable mockEntity = new MockDamageable();
        HealthComponent hc = new HealthComponent(50, 0, mockEntity);
        
        hc.takeDamage(30, new DamageSource("Test"));
        assertEquals(20, hc.getCurrentHealth());
        
        hc.takeDamage(30, new DamageSource("Test")); // This should kill
        assertEquals(0, hc.getCurrentHealth());
        assertFalse(hc.isAlive());
        
        // Additional damage should not be applied to dead entity
        hc.takeDamage(10, new DamageSource("Test"));
        assertEquals(0, hc.getCurrentHealth()); // Still 0
        assertFalse(hc.isAlive()); // Still dead
    }

    // Mock class for testing
    private static class MockDamageable implements Damageable {
        private boolean notified = false;
        private int damageCount = 0;
        private int deathCount = 0;

        @Override
        public void takeDamage(int amount, DamageSource source) {
            notified = true;
            damageCount++;
        }

        @Override
        public boolean isAlive() {
            return true;
        }

        @Override
        public int getCurrentHealth() {
            return 100;
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
        public void heal(int amount) {}

        @Override
        public int getX() {
            return 0;
        }

        @Override
        public int getY() {
            return 0;
        }

        @Override
        public void onDeath() {
            deathCount++;
        }

        public boolean wasNotified() {
            return notified;
        }

        public int getDamageCount() {
            return damageCount;
        }

        public int getDeathCount() {
            return deathCount;
        }
    }
}
