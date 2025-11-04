package phase2.game.combat;

/**
 * Simple console logger for combat events
 * Attach this to CombatManager to see combat activity
 */
public class CombatLogger implements CombatListener {
    private boolean verbose;

    public CombatLogger(boolean verbose) {
        this.verbose = verbose;
    }

    public CombatLogger() {
        this(true);
    }

    @Override
    public void onDamage(Damageable target, int amount, DamageSource source) {
        if (verbose && target != null) {
            System.out.printf("[DAMAGE] %s took %d damage from %s (HP: %d/%d)%n",
                getEntityName(target), amount, source.getSourceName(),
                target.getCurrentHealth(), target.getMaxHealth());
        }
    }

    @Override
    public void onDeath(Damageable target, DamageSource source) {
        if (target != null) {
            System.out.printf("[DEATH] %s was killed by %s!%n",
                getEntityName(target), source.getSourceName());
        }
    }

    @Override
    public void onAttack(Attacker attacker, Damageable target, AttackResult result) {
        if (result != null) {
            String critText = result.wasCritical() ? " CRITICAL HIT!" : "";
            System.out.printf("[ATTACK] %s attacked %s for %d damage%s%n",
                getEntityName(attacker), getEntityName(target),
                result.getDamageDealt(), critText);
        }
    }

    @Override
    public void onHeal(Damageable target, int amount) {
        if (verbose && target != null) {
            System.out.printf("[HEAL] %s healed for %d HP (HP: %d/%d)%n",
                getEntityName(target), amount,
                target.getCurrentHealth(), target.getMaxHealth());
        }
    }

    private String getEntityName(Object entity) {
        if (entity == null) return "Unknown";
        String className = entity.getClass().getSimpleName();
        return className.isEmpty() ? "Entity" : className;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
