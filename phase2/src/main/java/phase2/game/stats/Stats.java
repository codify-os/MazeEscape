package phase2.game.stats;

import phase2.game.combat.AttackData;

/**
 * Component for managing entity combat statistics (attack, defense, resistances, etc.)
 */
public class Stats {
    private int attackPower;
    private int defense;
    private int speed;
    private double critChance;
    private double critMultiplier;

    /**
     * Full constructor for stats with validation
     * @param attackPower Base attack power (will be clamped to 0 minimum)
     * @param defense Defense value (will be clamped to 0 minimum)
     * @param speed Movement/action speed (will be clamped to 1 minimum)
     * @param critChance Critical hit chance (will be clamped to 0.0-1.0)
     * @param critMultiplier Critical hit damage multiplier (will be clamped to 1.0 minimum)
     */
    public Stats(int attackPower, int defense, int speed, double critChance, double critMultiplier) {
        this.attackPower = Math.max(0, attackPower);
        this.defense = Math.max(0, defense);
        this.speed = Math.max(1, speed);
        this.critChance = Math.max(0.0, Math.min(1.0, critChance));
        this.critMultiplier = Math.max(1.0, critMultiplier);
    }

    /**
     * Simple constructor with basic stats
     * @param attackPower Base attack power
     * @param defense Defense value
     */
    public Stats(int attackPower, int defense) {
        this(attackPower, defense, 4, 0.05, 1.5);
    }

    /**
     * Default constructor with balanced stats
     */
    public Stats() {
        this(10, 5, 4, 0.05, 1.5);
    }

    // Getters
    public int getAttackPower() {
        return attackPower;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
    }

    public double getCritChance() {
        return critChance;
    }

    public double getCritMultiplier() {
        return critMultiplier;
    }

    // Setters
    public void setAttackPower(int attackPower) {
        this.attackPower = Math.max(0, attackPower);
    }

    public void setDefense(int defense) {
        this.defense = Math.max(0, defense);
    }

    public void setSpeed(int speed) {
        this.speed = Math.max(1, speed);
    }

    public void setCritChance(double critChance) {
        this.critChance = Math.max(0.0, Math.min(1.0, critChance));
    }

    public void setCritMultiplier(double critMultiplier) {
        this.critMultiplier = Math.max(1.0, critMultiplier);
    }

    /**
     * Modify a stat by a certain amount (buff/debuff)
     * @param statType The type of stat to modify
     * @param amount The amount to add (can be negative for int stats, or set directly for doubles)
     */
    public void modifyStat(StatType statType, int amount) {
        switch (statType) {
            case ATTACK_POWER:
                setAttackPower(attackPower + amount);
                break;
            case DEFENSE:
                setDefense(defense + amount);
                break;
            case SPEED:
                setSpeed(speed + amount);
                break;
            case CRIT_CHANCE:
                setCritChance(critChance + (amount / 100.0)); // Convert to percentage
                break;
            case CRIT_MULTIPLIER:
                setCritMultiplier(critMultiplier + (amount / 10.0)); // 10 = 1.0x multiplier
                break;
        }
    }

    /**
     * Create an AttackData object based on these stats
     * @param attackName Name of the attack
     * @param range Attack range
     * @param damageType Type of damage
     * @return AttackData with stats applied
     */
    public AttackData createAttack(String attackName, int range, AttackData.DamageType damageType) {
        return new AttackData(attackName, attackPower, range, damageType, critChance, critMultiplier, 20);
    }

    /**
     * Create a basic melee attack with these stats
     * @return Basic melee AttackData
     */
    public AttackData createBasicAttack() {
        return createAttack("Basic Attack", 1, AttackData.DamageType.PHYSICAL);
    }

    @Override
    public String toString() {
        return String.format("Stats{ATK=%d, DEF=%d, SPD=%d, CRIT=%.1f%%x%.1f}", 
                           attackPower, defense, speed, critChance * 100, critMultiplier);
    }

    /**
     * Enum for stat types (for buffs/debuffs)
     */
    public enum StatType {
        ATTACK_POWER,
        DEFENSE,
        SPEED,
        CRIT_CHANCE,
        CRIT_MULTIPLIER
    }
}
