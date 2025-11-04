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

    public int getDamageDealt() {
        return damageDealt;
    }

    public boolean wasCritical() {
        return wasCritical;
    }

    public boolean targetKilled() {
        return targetKilled;
    }

    public int getTargetRemainingHealth() {
        return targetRemainingHealth;
    }

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
