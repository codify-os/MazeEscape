package phase2.Entity;

import java.awt.*;
import phase2.game.combat.*;
import phase2.game.stats.*;

public abstract class Entity implements Damageable, Attacker {
    /*
     * TO-DO:
     * Implement the enemy stuff, (I'll help with this stuff, but we should be able
     * to generate at least one enemy my monday),
     * I'm purposefully ignoring the inventory stuff for now, we will make the UI
     * for it, and make it trigger on button press
     * and stuff
     */

    // shared stats
    public int x, y;
    public int speed;
    public Image up, up2, down, down2, left, left2, right, right2;
    public String direction;

    // Combat components
    protected HealthComponent health;
    protected Stats stats;
    protected AttackData attackData;
    protected long lastAttackTime = 0;

    // shared methods
    public abstract void update();

    public abstract void draw(Graphics2D g2d);

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Damageable interface implementation
    @Override
    public void takeDamage(int amount, DamageSource source) {
        if (health != null) {
            health.takeDamage(amount, source);
        }
    }

    @Override
    public void heal(int amount) {
        if (health != null) {
            health.heal(amount);
        }
    }

    @Override
    public boolean isAlive() {
        return health != null && health.isAlive();
    }

    @Override
    public int getCurrentHealth() {
        return health != null ? health.getCurrentHealth() : 0;
    }

    @Override
    public int getMaxHealth() {
        return health != null ? health.getMaxHealth() : 0;
    }

    @Override
    public int getDefense() {
        return stats != null ? stats.getDefense() : 0;
    }

    @Override
    public void onDeath() {
        System.out.println(this.getClass().getSimpleName() + " has died!");
    }

    // Attacker interface implementation
    @Override
    public AttackResult attack(Damageable target) {
        if (!canAttack()) {
            return null;
        }
        
        lastAttackTime = System.currentTimeMillis();
        return CombatManager.resolveAttack(this, target, attackData);
    }

    @Override
    public boolean canAttack() {
        if (attackData == null) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastAttackTime) >= attackData.getCooldown();
    }

    @Override
    public boolean isInRange(Damageable target) {
        if (attackData == null || !(target instanceof Entity)) {
            return false;
        }
        Entity targetEntity = (Entity) target;
        int dx = this.x - targetEntity.x;
        int dy = this.y - targetEntity.y;
        int distance = (int) Math.sqrt(dx * dx + dy * dy);
        return attackData.isInRange(distance);
    }

    @Override
    public AttackData getAttackData() {
        return attackData;
    }

    @Override
    public void updateCooldown() {
        // Cooldown is automatically managed by lastAttackTime
    }
}
