package phase2.game.stats;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import phase2.game.combat.AttackData;
import phase2.game.stats.Stats;

/**
 * Unit tests for Stats class
 */
public class StatsTest {

    @Test
    public void constructor_fullParameters_setsAllFields() {
        Stats stats = new Stats(25, 10, 5, 0.15, 2.0);
        
        assertEquals(25, stats.getAttackPower());
        assertEquals(10, stats.getDefense());
        assertEquals(5, stats.getSpeed());
        assertEquals(0.15, stats.getCritChance(), 0.001);
        assertEquals(2.0, stats.getCritMultiplier(), 0.001);
    }

    @Test
    public void constructor_simpleParameters_usesDefaults() {
        Stats stats = new Stats(30, 15);
        
        assertEquals(30, stats.getAttackPower());
        assertEquals(15, stats.getDefense());
        assertEquals(4, stats.getSpeed());
        assertEquals(0.05, stats.getCritChance(), 0.001);
        assertEquals(1.5, stats.getCritMultiplier(), 0.001);
    }

    @Test
    public void constructor_default_createsBalancedStats() {
        Stats stats = new Stats();
        
        assertEquals(10, stats.getAttackPower());
        assertEquals(5, stats.getDefense());
        assertEquals(4, stats.getSpeed());
        assertEquals(0.05, stats.getCritChance(), 0.001);
        assertEquals(1.5, stats.getCritMultiplier(), 0.001);
    }

    @Test
    public void constructor_negativeAttackPower_clampsToZero() {
        Stats stats = new Stats(-10, 5, 4, 0.1, 1.5);
        
        assertEquals(0, stats.getAttackPower());
    }

    @Test
    public void constructor_negativeDefense_clampsToZero() {
        Stats stats = new Stats(10, -5, 4, 0.1, 1.5);
        
        assertEquals(0, stats.getDefense());
    }

    @Test
    public void constructor_zeroSpeed_clampsToOne() {
        Stats stats = new Stats(10, 5, 0, 0.1, 1.5);
        
        assertEquals(1, stats.getSpeed());
    }

    @Test
    public void constructor_negativeSpeed_clampsToOne() {
        Stats stats = new Stats(10, 5, -3, 0.1, 1.5);
        
        assertEquals(1, stats.getSpeed());
    }

    @Test
    public void constructor_critChanceAboveOne_clampsToOne() {
        Stats stats = new Stats(10, 5, 4, 1.5, 2.0);
        
        assertEquals(1.0, stats.getCritChance(), 0.001);
    }

    @Test
    public void constructor_negativeCritChance_clampsToZero() {
        Stats stats = new Stats(10, 5, 4, -0.5, 2.0);
        
        assertEquals(0.0, stats.getCritChance(), 0.001);
    }

    @Test
    public void constructor_critMultiplierBelowOne_clampsToOne() {
        Stats stats = new Stats(10, 5, 4, 0.1, 0.5);
        
        assertEquals(1.0, stats.getCritMultiplier(), 0.001);
    }

    @Test
    public void setAttackPower_validValue_updates() {
        Stats stats = new Stats();
        stats.setAttackPower(50);
        
        assertEquals(50, stats.getAttackPower());
    }

    @Test
    public void setAttackPower_negativeValue_clampsToZero() {
        Stats stats = new Stats();
        stats.setAttackPower(-20);
        
        assertEquals(0, stats.getAttackPower());
    }

    @Test
    public void setDefense_validValue_updates() {
        Stats stats = new Stats();
        stats.setDefense(20);
        
        assertEquals(20, stats.getDefense());
    }

    @Test
    public void setDefense_negativeValue_clampsToZero() {
        Stats stats = new Stats();
        stats.setDefense(-10);
        
        assertEquals(0, stats.getDefense());
    }

    @Test
    public void setSpeed_validValue_updates() {
        Stats stats = new Stats();
        stats.setSpeed(8);
        
        assertEquals(8, stats.getSpeed());
    }

    @Test
    public void setSpeed_zeroValue_clampsToOne() {
        Stats stats = new Stats();
        stats.setSpeed(0);
        
        assertEquals(1, stats.getSpeed());
    }

    @Test
    public void setSpeed_negativeValue_clampsToOne() {
        Stats stats = new Stats();
        stats.setSpeed(-5);
        
        assertEquals(1, stats.getSpeed());
    }

    @Test
    public void setCritChance_validValue_updates() {
        Stats stats = new Stats();
        stats.setCritChance(0.25);
        
        assertEquals(0.25, stats.getCritChance(), 0.001);
    }

    @Test
    public void setCritChance_aboveOne_clampsToOne() {
        Stats stats = new Stats();
        stats.setCritChance(1.5);
        
        assertEquals(1.0, stats.getCritChance(), 0.001);
    }

    @Test
    public void setCritChance_negative_clampsToZero() {
        Stats stats = new Stats();
        stats.setCritChance(-0.3);
        
        assertEquals(0.0, stats.getCritChance(), 0.001);
    }

    @Test
    public void setCritMultiplier_validValue_updates() {
        Stats stats = new Stats();
        stats.setCritMultiplier(3.0);
        
        assertEquals(3.0, stats.getCritMultiplier(), 0.001);
    }

    @Test
    public void setCritMultiplier_belowOne_clampsToOne() {
        Stats stats = new Stats();
        stats.setCritMultiplier(0.5);
        
        assertEquals(1.0, stats.getCritMultiplier(), 0.001);
    }

    @Test
    public void modifyStat_attackPower_increases() {
        Stats stats = new Stats(20, 10);
        stats.modifyStat(Stats.StatType.ATTACK_POWER, 10);
        
        assertEquals(30, stats.getAttackPower());
    }

    @Test
    public void modifyStat_attackPower_decreases() {
        Stats stats = new Stats(20, 10);
        stats.modifyStat(Stats.StatType.ATTACK_POWER, -5);
        
        assertEquals(15, stats.getAttackPower());
    }

    @Test
    public void modifyStat_attackPower_negativeResult_clampsToZero() {
        Stats stats = new Stats(10, 5);
        stats.modifyStat(Stats.StatType.ATTACK_POWER, -20);
        
        assertEquals(0, stats.getAttackPower());
    }

    @Test
    public void modifyStat_defense_increases() {
        Stats stats = new Stats(20, 10);
        stats.modifyStat(Stats.StatType.DEFENSE, 5);
        
        assertEquals(15, stats.getDefense());
    }

    @Test
    public void modifyStat_defense_decreases() {
        Stats stats = new Stats(20, 10);
        stats.modifyStat(Stats.StatType.DEFENSE, -3);
        
        assertEquals(7, stats.getDefense());
    }

    @Test
    public void modifyStat_defense_negativeResult_clampsToZero() {
        Stats stats = new Stats(20, 5);
        stats.modifyStat(Stats.StatType.DEFENSE, -10);
        
        assertEquals(0, stats.getDefense());
    }

    @Test
    public void modifyStat_speed_increases() {
        Stats stats = new Stats(20, 10, 4, 0.1, 1.5);
        stats.modifyStat(Stats.StatType.SPEED, 2);
        
        assertEquals(6, stats.getSpeed());
    }

    @Test
    public void modifyStat_speed_decreases() {
        Stats stats = new Stats(20, 10, 5, 0.1, 1.5);
        stats.modifyStat(Stats.StatType.SPEED, -2);
        
        assertEquals(3, stats.getSpeed());
    }

    @Test
    public void modifyStat_speed_negativeResult_clampsToOne() {
        Stats stats = new Stats(20, 10, 3, 0.1, 1.5);
        stats.modifyStat(Stats.StatType.SPEED, -10);
        
        assertEquals(1, stats.getSpeed());
    }

    @Test
    public void modifyStat_critChance_increases() {
        Stats stats = new Stats(20, 10, 4, 0.1, 1.5);
        stats.modifyStat(Stats.StatType.CRIT_CHANCE, 10); // 10 = 10%
        
        assertEquals(0.2, stats.getCritChance(), 0.001);
    }

    @Test
    public void modifyStat_critChance_decreases() {
        Stats stats = new Stats(20, 10, 4, 0.2, 1.5);
        stats.modifyStat(Stats.StatType.CRIT_CHANCE, -5); // -5 = -5%
        
        assertEquals(0.15, stats.getCritChance(), 0.001);
    }

    @Test
    public void modifyStat_critChance_exceedsOne_clampsToOne() {
        Stats stats = new Stats(20, 10, 4, 0.5, 1.5);
        stats.modifyStat(Stats.StatType.CRIT_CHANCE, 100); // 100%
        
        assertEquals(1.0, stats.getCritChance(), 0.001);
    }

    @Test
    public void modifyStat_critChance_belowZero_clampsToZero() {
        Stats stats = new Stats(20, 10, 4, 0.1, 1.5);
        stats.modifyStat(Stats.StatType.CRIT_CHANCE, -50);
        
        assertEquals(0.0, stats.getCritChance(), 0.001);
    }

    @Test
    public void modifyStat_critMultiplier_increases() {
        Stats stats = new Stats(20, 10, 4, 0.1, 1.5);
        stats.modifyStat(Stats.StatType.CRIT_MULTIPLIER, 10); // 10 = 1.0x
        
        assertEquals(2.5, stats.getCritMultiplier(), 0.001);
    }

    @Test
    public void modifyStat_critMultiplier_decreases() {
        Stats stats = new Stats(20, 10, 4, 0.1, 2.0);
        stats.modifyStat(Stats.StatType.CRIT_MULTIPLIER, -5); // -5 = -0.5x
        
        assertEquals(1.5, stats.getCritMultiplier(), 0.001);
    }

    @Test
    public void modifyStat_critMultiplier_belowOne_clampsToOne() {
        Stats stats = new Stats(20, 10, 4, 0.1, 1.5);
        stats.modifyStat(Stats.StatType.CRIT_MULTIPLIER, -20);
        
        assertEquals(1.0, stats.getCritMultiplier(), 0.001);
    }

    @Test
    public void createAttack_customParameters_createsCorrectAttackData() {
        Stats stats = new Stats(50, 10, 4, 0.2, 2.5);
        
        AttackData attack = stats.createAttack("Fire Strike", 3, AttackData.DamageType.FIRE);
        
        assertEquals("Fire Strike", attack.getAttackName());
        assertEquals(50, attack.getPower());
        assertEquals(3, attack.getRange());
        assertEquals(AttackData.DamageType.FIRE, attack.getDamageType());
        assertEquals(0.2, attack.getCritChance(), 0.001);
        assertEquals(2.5, attack.getCritMultiplier(), 0.001);
        assertEquals(20, attack.getCooldown());
    }

    @Test
    public void createAttack_allDamageTypes_createsCorrectly() {
        Stats stats = new Stats(30, 5);
        
        for (AttackData.DamageType type : AttackData.DamageType.values()) {
            AttackData attack = stats.createAttack("Test", 2, type);
            assertEquals(type, attack.getDamageType());
        }
    }

    @Test
    public void createBasicAttack_usesStatsValues() {
        Stats stats = new Stats(40, 10, 4, 0.15, 2.0);
        
        AttackData attack = stats.createBasicAttack();
        
        assertEquals("Basic Attack", attack.getAttackName());
        assertEquals(40, attack.getPower());
        assertEquals(1, attack.getRange());
        assertEquals(AttackData.DamageType.PHYSICAL, attack.getDamageType());
        assertEquals(0.15, attack.getCritChance(), 0.001);
        assertEquals(2.0, attack.getCritMultiplier(), 0.001);
    }

    @Test
    public void toString_containsAllStats() {
        Stats stats = new Stats(25, 12, 6, 0.18, 2.2);
        
        String str = stats.toString();
        
        assertTrue(str.contains("25"), "Should contain attack power");
        assertTrue(str.contains("12"), "Should contain defense");
        assertTrue(str.contains("6"), "Should contain speed");
        assertTrue(str.contains("18"), "Should contain crit chance as percentage");
        assertTrue(str.contains("2.2"), "Should contain crit multiplier");
    }

    @Test
    public void allStatTypes_canBeModified() {
        Stats stats = new Stats(20, 10, 4, 0.1, 1.5);
        
        // Test all stat types can be modified
        stats.modifyStat(Stats.StatType.ATTACK_POWER, 5);
        stats.modifyStat(Stats.StatType.DEFENSE, 3);
        stats.modifyStat(Stats.StatType.SPEED, 1);
        stats.modifyStat(Stats.StatType.CRIT_CHANCE, 5);
        stats.modifyStat(Stats.StatType.CRIT_MULTIPLIER, 5);
        
        assertEquals(25, stats.getAttackPower());
        assertEquals(13, stats.getDefense());
        assertEquals(5, stats.getSpeed());
        assertEquals(0.15, stats.getCritChance(), 0.001);
        assertEquals(2.0, stats.getCritMultiplier(), 0.001);
    }

    @Test
    public void statModification_sequentialBuffsAndDebuffs() {
        Stats stats = new Stats(50, 20, 5, 0.1, 1.5);
        
        // Apply buff
        stats.modifyStat(Stats.StatType.ATTACK_POWER, 20);
        assertEquals(70, stats.getAttackPower());
        
        // Apply debuff
        stats.modifyStat(Stats.StatType.ATTACK_POWER, -30);
        assertEquals(40, stats.getAttackPower());
        
        // Apply another buff
        stats.modifyStat(Stats.StatType.ATTACK_POWER, 10);
        assertEquals(50, stats.getAttackPower());
    }

    @Test
    public void extremeValues_handledCorrectly() {
        Stats stats = new Stats(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1.0, 10.0);
        
        assertEquals(Integer.MAX_VALUE, stats.getAttackPower());
        assertEquals(Integer.MAX_VALUE, stats.getDefense());
        assertEquals(Integer.MAX_VALUE, stats.getSpeed());
        assertEquals(1.0, stats.getCritChance(), 0.001);
        assertEquals(10.0, stats.getCritMultiplier(), 0.001);
    }
}
