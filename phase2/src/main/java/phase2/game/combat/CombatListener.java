package phase2.game.combat;

/**
 * Listener interface for combat events (for UI, analytics, etc.)
 */
public interface CombatListener {
    /**
     * Called when an entity takes damage
     * @param target The entity that took damage
     * @param amount The amount of damage taken
     * @param source The source of the damage
     */
    void onDamage(Damageable target, int amount, DamageSource source);

    /**
     * Called when an entity dies
     * @param target The entity that died
     * @param source The source that killed them
     */
    void onDeath(Damageable target, DamageSource source);

    /**
     * Called when an attack is performed
     * @param attacker The entity performing the attack
     * @param target The target of the attack
     * @param result The result of the attack
     */
    void onAttack(Attacker attacker, Damageable target, AttackResult result);

    /**
     * Called when an entity is healed
     * @param target The entity that was healed
     * @param amount The amount healed
     */
    void onHeal(Damageable target, int amount);
}
