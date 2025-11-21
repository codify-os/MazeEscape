package phase2.game.combat;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AttackData class
 */
public class AttackDataTest {

    @Test
    public void constructor_fullParameters_setsAllFields() {
        AttackData attack = new AttackData(
            "Fire Strike",
            50,
            5,
            AttackData.DamageType.FIRE,
            0.25,
            2.0,
            100
        );

        assertEquals("Fire Strike", attack.getAttackName());
        assertEquals(50, attack.getPower());
        assertEquals(5, attack.getRange());
        assertEquals(AttackData.DamageType.FIRE, attack.getDamageType());
        assertEquals(0.25, attack.getCritChance(), 0.001);
        assertEquals(2.0, attack.getCritMultiplier(), 0.001);
        assertEquals(100, attack.getCooldown());
    }

    @Test
    public void constructor_simpleParameters_usesDefaults() {
        AttackData attack = new AttackData("Basic Strike", 30, 2);

        assertEquals("Basic Strike", attack.getAttackName());
        assertEquals(30, attack.getPower());
        assertEquals(2, attack.getRange());
        assertEquals(AttackData.DamageType.PHYSICAL, attack.getDamageType());
        assertEquals(0.0, attack.getCritChance(), 0.001);
        assertEquals(1.5, attack.getCritMultiplier(), 0.001);
        assertEquals(0, attack.getCooldown());
    }

    @Test
    public void constructor_powerOnly_createsMeleeAttack() {
        AttackData attack = new AttackData(25);

        assertEquals("Basic Attack", attack.getAttackName());
        assertEquals(25, attack.getPower());
        assertEquals(1, attack.getRange());
        assertEquals(AttackData.DamageType.PHYSICAL, attack.getDamageType());
    }

    @Test
    public void critChance_exceedsOne_clampedToOne() {
        AttackData attack = new AttackData(
            "Always Crit",
            50,
            1,
            AttackData.DamageType.PHYSICAL,
            1.5, // More than 100%
            2.0,
            0
        );

        assertEquals(1.0, attack.getCritChance(), 0.001, "Crit chance should be clamped to 1.0");
    }

    @Test
    public void critChance_negative_clampedToZero() {
        AttackData attack = new AttackData(
            "Never Crit",
            50,
            1,
            AttackData.DamageType.PHYSICAL,
            -0.5, // Negative
            2.0,
            0
        );

        assertEquals(0.0, attack.getCritChance(), 0.001, "Crit chance should be clamped to 0.0");
    }

    @Test
    public void critMultiplier_lessThanOne_clampedToOne() {
        AttackData attack = new AttackData(
            "Weak Crit",
            50,
            1,
            AttackData.DamageType.PHYSICAL,
            0.5,
            0.5, // Less than 1.0
            0
        );

        assertEquals(1.0, attack.getCritMultiplier(), 0.001, "Crit multiplier should be at least 1.0");
    }

    @Test
    public void cooldown_negative_clampedToZero() {
        AttackData attack = new AttackData(
            "Instant",
            50,
            1,
            AttackData.DamageType.PHYSICAL,
            0.0,
            1.5,
            -100 // Negative cooldown
        );

        assertEquals(0, attack.getCooldown(), "Cooldown should be at least 0");
    }

    @Test
    public void isInRange_withinRange_returnsTrue() {
        AttackData attack = new AttackData("Ranged", 30, 5);

        assertTrue(attack.isInRange(3), "Distance 3 should be within range 5");
        assertTrue(attack.isInRange(5), "Distance 5 should be within range 5");
    }

    @Test
    public void isInRange_outOfRange_returnsFalse() {
        AttackData attack = new AttackData("Melee", 30, 1);

        assertFalse(attack.isInRange(2), "Distance 2 should be out of range 1");
        assertFalse(attack.isInRange(10), "Distance 10 should be out of range 1");
    }

    @Test
    public void isInRange_exactDistance_returnsTrue() {
        AttackData attack = new AttackData("Spear", 25, 2);

        assertTrue(attack.isInRange(2), "Exact range distance should be valid");
    }

    @Test
    public void isInRange_zeroDistance_returnsTrue() {
        AttackData attack = new AttackData("Melee", 30, 1);

        assertTrue(attack.isInRange(0), "Zero distance should always be in range");
    }

    @Test
    public void rollCritical_zeroCritChance_alwaysFalse() {
        AttackData attack = new AttackData(
            "No Crit",
            50,
            1,
            AttackData.DamageType.PHYSICAL,
            0.0,
            2.0,
            0
        );

        // Test multiple times to ensure it's consistently false
        for (int i = 0; i < 100; i++) {
            assertFalse(attack.rollCritical(), "Should never crit with 0% chance");
        }
    }

    @Test
    public void rollCritical_fullCritChance_alwaysTrue() {
        AttackData attack = new AttackData(
            "Always Crit",
            50,
            1,
            AttackData.DamageType.PHYSICAL,
            1.0,
            2.0,
            0
        );

        // Test multiple times to ensure it's consistently true
        for (int i = 0; i < 100; i++) {
            assertTrue(attack.rollCritical(), "Should always crit with 100% chance");
        }
    }

    @Test
    public void rollCritical_partialCritChance_sometimesCrits() {
        AttackData attack = new AttackData(
            "Sometimes Crit",
            50,
            1,
            AttackData.DamageType.PHYSICAL,
            0.5,
            2.0,
            0
        );

        int critCount = 0;
        int trials = 1000;

        for (int i = 0; i < trials; i++) {
            if (attack.rollCritical()) {
                critCount++;
            }
        }

        // With 50% crit chance, expect roughly 500 crits out of 1000
        // Allow some variance (between 40% and 60%)
        assertTrue(critCount > 400 && critCount < 600, 
                  "Crit count should be roughly 50% (was " + critCount + "/1000)");
    }

    @Test
    public void damageType_allTypes_storedCorrectly() {
        for (AttackData.DamageType type : AttackData.DamageType.values()) {
            AttackData attack = new AttackData("Test", 10, 1, type, 0.0, 1.5, 0);
            assertEquals(type, attack.getDamageType(), "Damage type should match");
        }
    }

    @Test
    public void toString_containsKeyInformation() {
        AttackData attack = new AttackData(
            "Thunder Strike",
            75,
            3,
            AttackData.DamageType.MAGICAL,
            0.15,
            2.5,
            50
        );

        String str = attack.toString();

        assertTrue(str.contains("Thunder Strike"), "Should contain attack name");
        assertTrue(str.contains("75"), "Should contain power");
        assertTrue(str.contains("3"), "Should contain range");
        assertTrue(str.contains("MAGICAL"), "Should contain damage type");
    }

    @Test
    public void equals_samePowerAndRange_canBeDifferent() {
        AttackData attack1 = new AttackData("Attack A", 50, 2);
        AttackData attack2 = new AttackData("Attack B", 50, 2);

        // These are different objects even with same stats
        assertNotSame(attack1, attack2, "Should be different instances");
    }

    @Test
    public void damageType_trueDamage_identifiable() {
        AttackData trueAttack = new AttackData(
            "True Strike",
            100,
            1,
            AttackData.DamageType.TRUE,
            0.0,
            1.0,
            0
        );

        assertEquals(AttackData.DamageType.TRUE, trueAttack.getDamageType());
    }

    @Test
    public void damageType_elementalDamage_identifiable() {
        AttackData fireAttack = new AttackData("Fireball", 50, 5, 
                                               AttackData.DamageType.FIRE, 0.1, 1.5, 30);
        AttackData iceAttack = new AttackData("Ice Shard", 40, 4, 
                                              AttackData.DamageType.ICE, 0.2, 1.5, 25);
        AttackData poisonAttack = new AttackData("Poison Dart", 30, 3, 
                                                 AttackData.DamageType.POISON, 0.05, 1.0, 0);

        assertEquals(AttackData.DamageType.FIRE, fireAttack.getDamageType());
        assertEquals(AttackData.DamageType.ICE, iceAttack.getDamageType());
        assertEquals(AttackData.DamageType.POISON, poisonAttack.getDamageType());
    }
}
