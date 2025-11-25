package phase2.game.combat;

import org.junit.jupiter.api.Test;
import phase2.game.combat.AttackResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AttackResult class
 */
public class AttackResultTest {

    @Test
    public void constructor_fullParameters_setsAllFields() {
        AttackResult result = new AttackResult(50, true, false, 30);

        assertEquals(50, result.getDamageDealt());
        assertTrue(result.wasCritical());
        assertFalse(result.targetKilled());
        assertEquals(30, result.getTargetRemainingHealth());
    }

    @Test
    public void constructor_simpleParameters_setsBasicFields() {
        AttackResult result = new AttackResult(25, true);

        assertEquals(25, result.getDamageDealt());
        assertTrue(result.targetKilled());
        assertFalse(result.wasCritical());
        assertEquals(0, result.getTargetRemainingHealth());
    }

    @Test
    public void wasSuccessful_positiveDamage_returnsTrue() {
        AttackResult result = new AttackResult(1, false, false, 99);

        assertTrue(result.wasSuccessful(), "Attack with positive damage should be successful");
    }

    @Test
    public void wasSuccessful_zeroDamage_returnsFalse() {
        AttackResult result = new AttackResult(0, false, false, 100);

        assertFalse(result.wasSuccessful(), "Attack with zero damage should not be successful");
    }

    @Test
    public void wasSuccessful_negativeDamage_returnsFalse() {
        AttackResult result = new AttackResult(-5, false, false, 100);

        assertFalse(result.wasSuccessful(), "Attack with negative damage should not be successful");
    }

    @Test
    public void targetKilled_whenTrue_indicatesDeath() {
        AttackResult result = new AttackResult(100, false, true, 0);

        assertTrue(result.targetKilled());
        assertEquals(0, result.getTargetRemainingHealth());
    }

    @Test
    public void targetKilled_whenFalse_targetSurvived() {
        AttackResult result = new AttackResult(30, false, false, 20);

        assertFalse(result.targetKilled());
        assertTrue(result.getTargetRemainingHealth() > 0);
    }

    @Test
    public void wasCritical_whenTrue_indicatesCritHit() {
        AttackResult result = new AttackResult(75, true, false, 25);

        assertTrue(result.wasCritical());
    }

    @Test
    public void wasCritical_whenFalse_normalHit() {
        AttackResult result = new AttackResult(40, false, false, 60);

        assertFalse(result.wasCritical());
    }

    @Test
    public void toString_normalAttack_containsBasicInfo() {
        AttackResult result = new AttackResult(35, false, false, 65);

        String str = result.toString();

        assertTrue(str.contains("35"), "Should contain damage amount");
        assertTrue(str.contains("65"), "Should contain remaining health");
        assertFalse(str.contains("CRIT"), "Should not contain crit indicator");
        assertFalse(str.contains("KILLED"), "Should not contain killed indicator");
    }

    @Test
    public void toString_criticalAttack_containsCritIndicator() {
        AttackResult result = new AttackResult(60, true, false, 40);

        String str = result.toString();

        assertTrue(str.contains("60"), "Should contain damage amount");
        assertTrue(str.contains("CRIT"), "Should contain crit indicator");
        assertFalse(str.contains("KILLED"), "Should not contain killed indicator");
    }

    @Test
    public void toString_killingBlow_containsKilledIndicator() {
        AttackResult result = new AttackResult(80, false, true, 0);

        String str = result.toString();

        assertTrue(str.contains("80"), "Should contain damage amount");
        assertTrue(str.contains("KILLED"), "Should contain killed indicator");
        assertTrue(str.contains("0"), "Should show 0 remaining health");
    }

    @Test
    public void toString_criticalKill_containsBothIndicators() {
        AttackResult result = new AttackResult(150, true, true, 0);

        String str = result.toString();

        assertTrue(str.contains("150"), "Should contain damage amount");
        assertTrue(str.contains("CRIT"), "Should contain crit indicator");
        assertTrue(str.contains("KILLED"), "Should contain killed indicator");
    }

    @Test
    public void zeroDamageResult_unsuccessful() {
        AttackResult result = new AttackResult(0, false);

        assertFalse(result.wasSuccessful());
        assertEquals(0, result.getDamageDealt());
    }

    @Test
    public void highDamageAttack_properlyRecorded() {
        AttackResult result = new AttackResult(9999, true, true, 0);

        assertEquals(9999, result.getDamageDealt());
        assertTrue(result.wasCritical());
        assertTrue(result.targetKilled());
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void overkillDamage_recordedAccurately() {
        // Target had 50 HP, dealt 100 damage
        AttackResult result = new AttackResult(100, false, true, 0);

        assertEquals(100, result.getDamageDealt());
        assertTrue(result.targetKilled());
        assertEquals(0, result.getTargetRemainingHealth());
    }

    @Test
    public void minimumDamageAttack_stillSuccessful() {
        AttackResult result = new AttackResult(1, false, false, 99);

        assertTrue(result.wasSuccessful());
        assertEquals(1, result.getDamageDealt());
        assertFalse(result.targetKilled());
    }

    @Test
    public void exactLethalDamage_killsTarget() {
        // Target had exactly 50 HP, dealt exactly 50 damage
        AttackResult result = new AttackResult(50, false, true, 0);

        assertTrue(result.targetKilled());
        assertEquals(0, result.getTargetRemainingHealth());
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void allFlagsTrue_validResult() {
        AttackResult result = new AttackResult(200, true, true, 0);

        assertTrue(result.wasCritical());
        assertTrue(result.targetKilled());
        assertTrue(result.wasSuccessful());
        assertEquals(200, result.getDamageDealt());
        assertEquals(0, result.getTargetRemainingHealth());
    }

    @Test
    public void allFlagsFalse_missedAttack() {
        AttackResult result = new AttackResult(0, false, false, 100);

        assertFalse(result.wasCritical());
        assertFalse(result.targetKilled());
        assertFalse(result.wasSuccessful());
        assertEquals(0, result.getDamageDealt());
        assertEquals(100, result.getTargetRemainingHealth());
    }

    @Test
    public void remainingHealth_variousValues_storedCorrectly() {
        AttackResult result1 = new AttackResult(10, false, false, 90);
        AttackResult result2 = new AttackResult(50, false, false, 50);
        AttackResult result3 = new AttackResult(99, false, false, 1);

        assertEquals(90, result1.getTargetRemainingHealth());
        assertEquals(50, result2.getTargetRemainingHealth());
        assertEquals(1, result3.getTargetRemainingHealth());
    }
}
