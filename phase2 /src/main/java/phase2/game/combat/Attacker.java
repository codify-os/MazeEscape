package phase2.game.combat;

/**
 * Interface for entities that can attack other entities
 */
public interface Attacker {
    /**
     * Get the current attack data for this attacker
     * @return AttackData containing power, range, and other attack properties
     */
    AttackData getAttackData();

    /**
     * Perform an attack on a target
     * @param target The Damageable entity to attack
     * @return AttackResult containing damage dealt and outcome
     */
    AttackResult attack(Damageable target);

    /**
     * Check if this attacker can currently attack (not on cooldown)
     * @return true if attack is ready, false if on cooldown
     */
    boolean canAttack();

    /**
     * Check if a target is within attack range
     * @param target The target to check
     * @return true if target is in range, false otherwise
     */
    boolean isInRange(Damageable target);

    /**
     * Get the position X coordinate of this attacker
     * @return X position
     */
    int getX();

    /**
     * Get the position Y coordinate of this attacker
     * @return Y position
     */
    int getY();

    /**
     * Update attack cooldown (called each frame/update)
     */
    void updateCooldown();
}
