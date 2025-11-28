package phase2.game.combat;

/**
 * Data object representing the source of damage
 */
public class DamageSource {
    private final Object attacker;
    private final AttackData attackData;
    private final String sourceName;

    /**
     * Create a damage source from an attacker and their attack
     * @param attacker The entity that caused the damage
     * @param attackData The attack data used
     */
    public DamageSource(Object attacker, AttackData attackData) {
        this.attacker = attacker;
        this.attackData = attackData;
        this.sourceName = attackData != null ? attackData.getAttackName() : "Unknown";
    }

    /**
     * Create a damage source with just a name (for environmental damage, etc.)
     * @param sourceName Name of the damage source
     */
    public DamageSource(String sourceName) {
        this.attacker = null;
        this.attackData = null;
        this.sourceName = sourceName;
    }

    /**
     * Get the entity that caused the damage
     * @return Attacker object or null
     */
    public Object getAttacker() {
        return attacker;
    }

    /**
     * Get the attack data used for this damage
     * @return AttackData or null
     */
    public AttackData getAttackData() {
        return attackData;
    }

    /**
     * Get the name of the damage source
     * @return Source name
     */
    public String getSourceName() {
        return sourceName;
    }

    /**
     * Get the type of damage from this source
     * @return Damage type
     */
    public AttackData.DamageType getDamageType() {
        return attackData != null ? attackData.getDamageType() : AttackData.DamageType.PHYSICAL;
    }

    @Override
    public String toString() {
        return String.format("DamageSource{source='%s', type=%s}", 
                           sourceName, getDamageType());
    }
}
