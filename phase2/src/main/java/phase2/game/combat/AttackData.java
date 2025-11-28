package phase2.game.combat;

/**
 * Data object representing an attack action (damage, range, type, etc.)
 */
public class AttackData {
    private int power;
    private final int range;
    private final DamageType damageType;
    private double critChance;
    private double critMultiplier;
    private final int cooldown; // in frames or milliseconds
    private final String attackName;

    /**
     * Full constructor for attack data with validation
     * @param attackName Name of the attack
     * @param power Base damage power (will be clamped to 0 minimum)
     * @param range Maximum range in tiles (will be clamped to 0 minimum)
     * @param damageType Type of damage (PHYSICAL, MAGICAL, etc.)
     * @param critChance Critical hit chance 0.0-1.0 (will be clamped)
     * @param critMultiplier Critical damage multiplier (will be clamped to 1.0 minimum)
     * @param cooldown Cooldown duration in frames (will be clamped to 0 minimum)
     * @throws IllegalArgumentException if attackName is null/empty or damageType is null
     */
    public AttackData(String attackName, int power, int range, DamageType damageType, 
                     double critChance, double critMultiplier, int cooldown) {
        if (attackName == null || attackName.trim().isEmpty()) {
            throw new IllegalArgumentException("Attack name cannot be null or empty");
        }
        if (damageType == null) {
            throw new IllegalArgumentException("Damage type cannot be null");
        }
        this.attackName = attackName;
        this.power = Math.max(0, power);
        this.range = Math.max(0, range);
        this.damageType = damageType;
        this.critChance = Math.max(0.0, Math.min(1.0, critChance)); // Clamp 0-1
        this.critMultiplier = Math.max(1.0, critMultiplier); // Min 1.0x
        this.cooldown = Math.max(0, cooldown);
    }

    /**
     * Simple constructor with defaults
     * @param attackName Name of the attack
     * @param power Base damage power
     * @param range Maximum range in tiles
     */
    public AttackData(String attackName, int power, int range) {
        this(attackName, power, range, DamageType.PHYSICAL, 0.0, 1.5, 0);
    }

    /**
     * Basic melee attack constructor
     * @param power Base damage power
     */
    public AttackData(int power) {
        this("Basic Attack", power, 1, DamageType.PHYSICAL, 0.0, 1.5, 0);
    }

    // Getters
    /**
     * Get the name of this attack
     * @return Attack name
     */
    public String getAttackName() {
        return attackName;
    }

    /**
     * Get the base damage power
     * @return Damage power
     */
    public int getPower() {
        return power;
    }

    /**
     * Get the maximum range in tiles
     * @return Attack range
     */
    public int getRange() {
        return range;
    }

    /**
     * Get the type of damage this attack deals
     * @return Damage type
     */
    public DamageType getDamageType() {
        return damageType;
    }

    /**
     * Get the critical hit chance (0.0 to 1.0)
     * @return Critical chance
     */
    public double getCritChance() {
        return critChance;
    }

    /**
     * Get the critical hit damage multiplier
     * @return Critical multiplier
     */
    public double getCritMultiplier() {
        return critMultiplier;
    }

    /**
     * Get the cooldown duration in frames
     * @return Cooldown duration
     */
    public int getCooldown() {
        return cooldown;
    }

    /**
     * Check if this attack can reach the target based on distance
     * @param distance Distance to target in tiles
     * @return True if target is within range
     */
    public boolean isInRange(int distance) {
        return distance <= range;
    }

    /**
     * Calculate if this attack critically hits based on crit chance
     * @return True if the attack is a critical hit
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
        /** Physical damage type */
        PHYSICAL,
        /** Magical damage type */
        MAGICAL,
        /** True damage - ignores defense */
        TRUE,
        /** Fire damage type */
        FIRE,
        /** Ice damage type */
        ICE,
        /** Poison damage type */
        POISON
    }
}
