package phase2.game.combat;

import java.util.ArrayList;
import java.util.List;

/**
 * Central manager for resolving combat actions and damage calculations
 */
public class CombatManager {
    private static final List<CombatListener> listeners = new ArrayList<>();

    /**
     * Register a listener for combat events
     * @param listener The listener to add
     */
    public static void addListener(CombatListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Remove a combat listener
     * @param listener The listener to remove
     */
    public static void removeListener(CombatListener listener) {
        listeners.remove(listener);
    }

    /**
     * Resolve an attack between an attacker and target
     * @param attacker The attacking entity
     * @param target The target entity
     * @param attackData The attack data to use
     * @return AttackResult containing damage dealt and outcome
     */
    public static AttackResult resolveAttack(Attacker attacker, Damageable target, AttackData attackData) {
        if (attacker == null || target == null || attackData == null) {
            return new AttackResult(0, false);
        }

        if (!target.isAlive()) {
            return new AttackResult(0, true);
        }

        // Calculate base damage
        int baseDamage = attackData.getPower();

        // Check for critical hit
        boolean isCritical = attackData.rollCritical();
        if (isCritical) {
            baseDamage = (int) (baseDamage * attackData.getCritMultiplier());
        }

        // Apply defense reduction (unless TRUE damage)
        int finalDamage = baseDamage;
        if (attackData.getDamageType() != AttackData.DamageType.TRUE) {
            int defense = target.getDefense();
            finalDamage = Math.max(1, baseDamage - defense); // Minimum 1 damage
        }

        // Apply damage to target
        DamageSource source = new DamageSource(attacker, attackData);
        target.takeDamage(finalDamage, source);

        // Create result
        AttackResult result = new AttackResult(
            finalDamage,
            isCritical,
            !target.isAlive(),
            target.getCurrentHealth()
        );

        // Notify listeners
        notifyAttack(attacker, target, result);

        return result;
    }

    /**
     * Calculate distance between two entities
     * @param x1 X position of entity 1
     * @param y1 Y position of entity 1
     * @param x2 X position of entity 2
     * @param y2 Y position of entity 2
     * @return Manhattan distance between entities
     */
    public static int calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * Check if attacker can hit target (range check)
     * @param attacker The attacker
     * @param target The target
     * @param attackData The attack data
     * @return true if target is in range
     */
    public static boolean canHitTarget(Attacker attacker, Damageable target, AttackData attackData) {
        if (attacker == null || target == null || attackData == null) {
            return false;
        }

        int distance = calculateDistance(
            attacker.getX(), attacker.getY(),
            target.getX(), target.getY()
        );

        return attackData.isInRange(distance);
    }

    // Listener notification methods
    private static void notifyAttack(Attacker attacker, Damageable target, AttackResult result) {
        for (CombatListener listener : listeners) {
            listener.onAttack(attacker, target, result);
        }
    }

    /**
     * Notify all listeners that damage was dealt
     * @param target The target that took damage
     * @param amount The amount of damage
     * @param source The source of the damage
     */
    public static void notifyDamage(Damageable target, int amount, DamageSource source) {
        for (CombatListener listener : listeners) {
            listener.onDamage(target, amount, source);
        }
    }

    /**
     * Notify all listeners that an entity died
     * @param target The entity that died
     * @param source The source of the killing blow
     */
    public static void notifyDeath(Damageable target, DamageSource source) {
        for (CombatListener listener : listeners) {
            listener.onDeath(target, source);
        }
    }

    /**
     * Notify all listeners that an entity was healed
     * @param target The entity that was healed
     * @param amount The amount healed
     */
    public static void notifyHeal(Damageable target, int amount) {
        for (CombatListener listener : listeners) {
            listener.onHeal(target, amount);
        }
    }
}
