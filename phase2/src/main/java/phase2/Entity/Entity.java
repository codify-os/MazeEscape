package phase2.Entity;
import java.awt.*;

import jdk.jfr.DataAmount;
import phase2.UI.GamePanel;
import phase2.game.combat.*;
import phase2.game.stats.*;

public abstract class Entity implements Damageable, Attacker {
    // Constants for display timers
    protected static final int DAMAGE_FLASH_DURATION = 10;
    protected static final int DAMAGE_TEXT_DURATION = 30;
    
    // shared stats
    public int worldX, worldY;
    public int speed;
    public Image up, down, left, right;
    public String direction;
    public GamePanel gp;

    // shared methods
    public abstract void update();

    public abstract void draw(Graphics2D g2d);

    public int getWorldX() {
        return worldX;
    }

    public int getWorldY() {
        return worldY;
    }
    public Rectangle collisionArea;
    public boolean collisionOn = false;

    //Entity Health and stats
    public HealthComponent health;
    public Stats stats;

    //Attack data
    public AttackData currentAttack;
    public int coolDown; //attacks are only at set times not every frame

    //Display Text data
    public int damageFlashTimer = 0;
    public int damageTextTimer = 0;
    public int previousDamageAmount = 0;
    public boolean lastCrit = false;

    /**
     * Default constructor with standard stats
     */
    public Entity() {
        this(100, new Stats());
    }
    
    /**
     * Constructor with custom max health and stats
     * @param maxHealth Maximum health for this entity
     * @param stats Stats component for this entity
     * @throws IllegalArgumentException if stats is null or maxHealth <= 0
     */
    protected Entity(int maxHealth, Stats stats) {
        if (stats == null) {
            throw new IllegalArgumentException("Stats cannot be null");
        }
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("Max health must be positive");
        }
        this.stats = stats;
        this.health = new HealthComponent(maxHealth, stats.getDefense(), this);
        this.currentAttack = stats.createBasicAttack();
    }

    public void drawHealthBar(Graphics2D g2d, int screenX, int screeny, int width, int height) {
        double hpPercent = health.getHealthPercentage();
        g2d.setColor(Color.black);
        g2d.drawRect(screenX-1, screeny-1, width + 2, height + 2);

        g2d.setColor(Color.red);
        int barFill = (int) (width * hpPercent);
        g2d.fillRect(screenX, screeny, barFill, height);
    }


    //Damageable interface

    @Override
    public void takeDamage(int amount, DamageSource source) {
        health.takeDamage(amount, source);
        damageFlashTimer = DAMAGE_FLASH_DURATION;

        previousDamageAmount = amount;
        damageTextTimer = DAMAGE_TEXT_DURATION;
        if(!health.isAlive()) {
            onDeath();
        }
    }
    @Override
    public boolean isAlive() {
        return health.isAlive();
    }
    @Override
    public int getCurrentHealth() {
        return health.getCurrentHealth();
    }
    @Override
    public int getMaxHealth() {
        return health.getMaxHealth();
    }
    @Override
    public int getDefense() {
        return stats.getDefense();
    }
    @Override
    public void heal(int amount) {
        health.heal(amount);
    }
    @Override
    public int getX() {
        return getWorldX();
    }

    @Override
    public int getY() {
        return getWorldY();
    }
    @Override
    public void onDeath() {
        System.out.println("[DEBUG] Enemy.onDeath() (entity class) executed");

        if (this instanceof Enemy enemy) {
            enemy.handleDeath();
        } else if (this instanceof Player player) {
            player.handleDeath();
        }
//        System.out.println(getClass().getSimpleName() + " was killed");
    }

    //Attacker interface

    @Override
    public AttackData getAttackData() {
        return currentAttack;
    }
    @Override
    public AttackResult attack(Damageable target) {
        if(!canAttack()) {
            return null;
        }
        coolDown = currentAttack.getCooldown();
        AttackResult atkResult = CombatManager.resolveAttack(this, target, currentAttack);

        if(atkResult.getDamageDealt() > 0) {
            if (target instanceof Entity targetEnt) {
                targetEnt.lastCrit = atkResult.wasCritical();
                targetEnt.previousDamageAmount = atkResult.getDamageDealt();
                targetEnt.damageTextTimer = 30;
            }
        }
        return atkResult;
    }
    @Override
    public boolean canAttack() {
        return coolDown <= 0;
    }
    @Override
    public boolean isInRange(Damageable target) {
        int dist = CombatManager.calculateDistance(getX(), getY(), target.getX(), target.getY());
        return currentAttack.isInRange(dist/48);
    }
    @Override
    public void updateCooldown() {
        if (coolDown > 0) {
            coolDown--;
        }
    }

}
