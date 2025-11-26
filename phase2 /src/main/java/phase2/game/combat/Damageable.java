package phase2.game.combat;

/**
 * Interface for entities that can receive damage
 */
public interface Damageable {
    /**
     * Apply damage to this entity
     * @param amount The raw damage amount to apply
     * @param source The source of the damage (attacker, attack type, etc.)
     */
    void takeDamage(int amount, DamageSource source);

    /**
     * Check if this entity is still alive
     * @return true if alive (health > 0), false if dead
     */
    boolean isAlive();

    /**
     * Get the current health points
     * @return Current HP
     */
    int getCurrentHealth();

    /**
     * Get the maximum health points
     * @return Max HP
     */
    int getMaxHealth();

    /**
     * Get the defense value (reduces incoming damage)
     * @return Defense stat
     */
    int getDefense();

    /**
     * Heal this entity by a certain amount
     * @param amount Amount to heal (cannot exceed max health)
     */
    void heal(int amount);

    /**
     * Get the position X coordinate of this damageable entity
     * @return X position
     */
    int getX();

    /**
     * Get the position Y coordinate of this damageable entity
     * @return Y position
     */
    int getY();

    /**
     * Called when this entity dies
     */
    void onDeath();
}
