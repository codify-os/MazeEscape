package phase2.game.combat;

/**
 * Data object representing an attack action (damage, range, type, etc.)
 */
public class AttackData {
    private final int power;
    private final int range;
    private final DamageType damageType;
    private final double critChance;
    private final double critMultiplier;
    private final int cooldown; // in frames or milliseconds
    private final String attackName;

    /**
     * Full constructor for attack data
     */
    public AttackData(String attackName, int power, int range, DamageType damageType, 
                     double critChance, double critMultiplier, int cooldown) {
        this.attackName = attackName;
        this.power = power;
        this.range = range;
        this.damageType = damageType;
        this.critChance = Math.max(0.0, Math.min(1.0, critChance)); // Clamp 0-1
        this.critMultiplier = Math.max(1.0, critMultiplier); // Min 1.0x
        this.cooldown = Math.max(0, cooldown);
    }

    /**
     * Simple constructor with defaults
     */
    public AttackData(String attackName, int power, int range) {
        this(attackName, power, range, DamageType.PHYSICAL, 0.0, 1.5, 0);
    }

    /**
     * Basic melee attack constructor
     */
    public AttackData(int power) {
        this("Basic Attack", power, 1, DamageType.PHYSICAL, 0.0, 1.5, 0);
    }

    // Getters
    public String getAttackName() {
        return attackName;
    }

    public int getPower() {
        return power;
    }

    public int getRange() {
        return range;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public double getCritChance() {
        return critChance;
    }

    public double getCritMultiplier() {
        return critMultiplier;
    }

    public int getCooldown() {
        return cooldown;
    }

    /**
     * Check if this attack can reach the target based on distance
     */
    public boolean isInRange(int distance) {
        return distance <= range;
    }

    /**
     * Calculate if this attack critically hits based on crit chance
     */
    public boolean rollCritical() {
        return Math.random() < critChance;
    }

    @Override
    public String toString() {
        return String.format("AttackData{name='%s', power=%d, range=%d, type=%s, crit=%.1f%%}", 
                           attackName, power, range, damageType, critChance * 100);
    }

    /**
     * Enum for damage types (can affect resistance calculations)
     * Keep if we're still doing different weapons
     */
    public enum DamageType {
        PHYSICAL,
        MAGICAL,
        TRUE, // Ignores defense
        FIRE,
        ICE,
        POISON
    }
}
