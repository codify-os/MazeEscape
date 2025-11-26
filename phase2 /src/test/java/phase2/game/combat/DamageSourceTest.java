package phase2.game.combat;

import org.junit.jupiter.api.Test;
import phase2.game.combat.AttackData;
import phase2.game.combat.DamageSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DamageSource class
 */
public class DamageSourceTest {

    @Test
    public void constructor_withAttackerAndAttackData_setsAllFields() {
        Object attacker = new Object();
        AttackData attackData = new AttackData("Sword Slash", 30, 1);
        
        DamageSource source = new DamageSource(attacker, attackData);
        
        assertEquals(attacker, source.getAttacker());
        assertEquals(attackData, source.getAttackData());
        assertEquals("Sword Slash", source.getSourceName());
    }

    @Test
    public void constructor_withNullAttackData_handlesGracefully() {
        Object attacker = new Object();
        
        DamageSource source = new DamageSource(attacker, null);
        
        assertEquals(attacker, source.getAttacker());
        assertNull(source.getAttackData());
        assertEquals("Unknown", source.getSourceName());
    }

    @Test
    public void constructor_withNameOnly_setsNameAndNullsOthers() {
        DamageSource source = new DamageSource("Lava");
        
        assertNull(source.getAttacker());
        assertNull(source.getAttackData());
        assertEquals("Lava", source.getSourceName());
    }

    @Test
    public void getDamageType_withAttackData_returnsAttackDataType() {
        AttackData attackData = new AttackData(
            "Fire Strike",
            40,
            2,
            AttackData.DamageType.FIRE,
            0.1,
            1.5,
            0
        );
        
        DamageSource source = new DamageSource(new Object(), attackData);
        
        assertEquals(AttackData.DamageType.FIRE, source.getDamageType());
    }

    @Test
    public void getDamageType_withoutAttackData_returnsPhysical() {
        DamageSource source = new DamageSource("Fall Damage");
        
        assertEquals(AttackData.DamageType.PHYSICAL, source.getDamageType());
    }

    @Test
    public void getDamageType_nullAttackData_returnsPhysical() {
        DamageSource source = new DamageSource(new Object(), null);
        
        assertEquals(AttackData.DamageType.PHYSICAL, source.getDamageType());
    }

    @Test
    public void toString_withAttackData_containsSourceInfo() {
        AttackData attackData = new AttackData(
            "Thunder Strike",
            50,
            3,
            AttackData.DamageType.MAGICAL,
            0.2,
            2.0,
            30
        );
        
        DamageSource source = new DamageSource(new Object(), attackData);
        String str = source.toString();
        
        assertTrue(str.contains("Thunder Strike"), "Should contain source name");
        assertTrue(str.contains("MAGICAL"), "Should contain damage type");
    }

    @Test
    public void toString_environmentalDamage_containsName() {
        DamageSource source = new DamageSource("Poison Cloud");
        String str = source.toString();
        
        assertTrue(str.contains("Poison Cloud"), "Should contain source name");
    }

    @Test
    public void sourceName_fromAttackData_matchesAttackName() {
        AttackData attackData = new AttackData("Heavy Strike", 60, 1);
        DamageSource source = new DamageSource(new Object(), attackData);
        
        assertEquals("Heavy Strike", source.getSourceName());
        assertEquals(attackData.getAttackName(), source.getSourceName());
    }

    @Test
    public void attacker_canBeAnyObject_storedCorrectly() {
        String stringAttacker = "TestAttacker";
        DamageSource source1 = new DamageSource(stringAttacker, new AttackData(10));
        
        Integer integerAttacker = 42;
        DamageSource source2 = new DamageSource(integerAttacker, new AttackData(20));
        
        assertEquals(stringAttacker, source1.getAttacker());
        assertEquals(integerAttacker, source2.getAttacker());
    }

    @Test
    public void environmentalDamage_commonSources_createdCorrectly() {
        DamageSource lava = new DamageSource("Lava");
        DamageSource spikes = new DamageSource("Spikes");
        DamageSource poison = new DamageSource("Poison");
        DamageSource fall = new DamageSource("Fall Damage");
        
        assertEquals("Lava", lava.getSourceName());
        assertEquals("Spikes", spikes.getSourceName());
        assertEquals("Poison", poison.getSourceName());
        assertEquals("Fall Damage", fall.getSourceName());
        
        assertNull(lava.getAttacker());
        assertNull(spikes.getAttacker());
        assertNull(poison.getAttacker());
        assertNull(fall.getAttacker());
    }

    @Test
    public void damageType_allTypes_handledCorrectly() {
        for (AttackData.DamageType type : AttackData.DamageType.values()) {
            AttackData attackData = new AttackData("Test", 10, 1, type, 0.0, 1.0, 0);
            DamageSource source = new DamageSource(new Object(), attackData);
            
            assertEquals(type, source.getDamageType(), "Should handle " + type);
        }
    }

    @Test
    public void trueDamage_identifiableFromSource() {
        AttackData trueAttack = new AttackData(
            "Execute",
            100,
            1,
            AttackData.DamageType.TRUE,
            0.0,
            1.0,
            0
        );
        
        DamageSource source = new DamageSource(new Object(), trueAttack);
        
        assertEquals(AttackData.DamageType.TRUE, source.getDamageType());
        assertEquals("Execute", source.getSourceName());
    }

    @Test
    public void elementalDamage_identifiableFromSource() {
        AttackData fireAttack = new AttackData(
            "Fireball",
            45,
            4,
            AttackData.DamageType.FIRE,
            0.15,
            1.5,
            25
        );
        
        DamageSource source = new DamageSource(new Object(), fireAttack);
        
        assertEquals(AttackData.DamageType.FIRE, source.getDamageType());
        assertEquals("Fireball", source.getSourceName());
    }
}
