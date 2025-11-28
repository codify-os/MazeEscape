package phase2.game.combat;

/**
 * Data object representing the result of an attack
 */
public class AttackResult {
    private final int damageDealt;
    private final boolean wasCritical;
    private final boolean targetKilled;
    private final int targetRemainingHealth;

    /**
     * Create an attack result
     * @param damageDealt The final damage dealt after all calculations
     * @param wasCritical Whether the attack was a critical hit
     * @param targetKilled Whether the target died from this attack
     * @param targetRemainingHealth The target's remaining health after damage
     */
    public AttackResult(int damageDealt, boolean wasCritical, boolean targetKilled, int targetRemainingHealth) {
        this.damageDealt = damageDealt;
        this.wasCritical = wasCritical;
        this.targetKilled = targetKilled;
        this.targetRemainingHealth = targetRemainingHealth;
    }

    /**
     * Simple constructor for basic attacks
     * @param damageDealt Damage dealt
     * @param targetKilled Whether target died
     */
    public AttackResult(int damageDealt, boolean targetKilled) {
        this(damageDealt, false, targetKilled, 0);
    }

    /**
     * Get the amount of damage dealt
     * @return Damage dealt
     */
    public int getDamageDealt() {
        return damageDealt;
    }

    /**
     * Check if the attack was a critical hit
     * @return True if critical hit
     */
    public boolean wasCritical() {
        return wasCritical;
    }

    /**
     * Check if the target was killed by this attack
     * @return True if target was killed
     */
    public boolean targetKilled() {
        return targetKilled;
    }

    /**
     * Get the target's remaining health after the attack
     * @return Remaining health
     */
    public int getTargetRemainingHealth() {
        return targetRemainingHealth;
    }

    /**
     * Check if the attack successfully dealt damage
     * @return True if damage was dealt
     */
    public boolean wasSuccessful() {
        return damageDealt > 0;
    }

    @Override
    public String toString() {
        String result = String.format("AttackResult{damage=%d", damageDealt);
        if (wasCritical) result += " CRIT!";
        if (targetKilled) result += " KILLED";
        result += String.format(", remaining=%d}", targetRemainingHealth);
        return result;
    }
}
