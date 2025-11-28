package phase2.Entity;
import java.awt.*;

import jdk.jfr.DataAmount;
import phase2.UI.GamePanel;
import phase2.game.combat.*;
import phase2.game.stats.*;

/**
 * Base class for all game entities (player, enemies, etc.)
 */
public abstract class Entity implements Damageable, Attacker {
    /** Duration in frames for damage flash effect */
    protected static final int DAMAGE_FLASH_DURATION = 10;
    /** Duration in frames for damage text display */
    protected static final int DAMAGE_TEXT_DURATION = 30;
  
    /** World X coordinate */
    public int worldX, worldY;
    /** Movement speed */
    public int speed;
    /** Direction sprites */
    public Image up, down, left, right;
    /** Current direction */
    public String direction;
    /** Reference to game panel */
    public GamePanel gp;

    /** Update entity state each frame */
    public abstract void update();

    /**
     * Draw the entity
     * @param g2d Graphics context
     */
    public abstract void draw(Graphics2D g2d);

    /**
     * Get world X coordinate
     * @return X position
     */
    public int getWorldX() {
        return worldX;
    }

    /**
     * Get world Y coordinate
     * @return Y position
     */
    public int getWorldY() {
        return worldY;
    }
    /** Collision hitbox */
    public Rectangle collisionArea;
    /** Whether collision is detected */
    public boolean collisionOn = false;

    /** Health component managing HP and damage */
    public HealthComponent health;
    /** Stats component for attack and defense */
    public Stats stats;

    /** Current attack being used */
    public AttackData currentAttack;
    /** Attack cooldown timer in frames */
    public int coolDown;

    /** Timer for damage flash effect */
    public int damageFlashTimer = 0;
    /** Timer for damage text display */
    public int damageTextTimer = 0;
    /** Last damage amount for display */
    public int previousDamageAmount = 0;
    /** Whether last attack was critical */
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
     * @throws IllegalArgumentException if stats is null or maxHealth is less than or equal to 0
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

    /**
     * Draw health bar above entity
     * @param g2d Graphics context
     * @param screenX Screen X position
     * @param screeny Screen Y position
     * @param width Bar width
     * @param height Bar height
     */
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
        currentAttack = new AttackData("Basic Attack",stats.getAttackPower(),
                1,
                AttackData.DamageType.PHYSICAL,
                stats.getCritChance(),
                stats.getCritMultiplier(),
                0);
        return currentAttack;
    }
    @Override
    public AttackResult attack(Damageable target) {
        refreshAttackFromStats();

        System.out.println("---- ATTACK DEBUG ----");
        System.out.println("Stats.attackPower = " + stats.getAttackPower());
        System.out.println("Stats.critChance = " + stats.getCritChance());
        System.out.println("Stats.critMultiplier = " + stats.getCritMultiplier());
        System.out.println("AttackData.power = " + currentAttack.getPower());
        System.out.println("AttackData.critChance = " + currentAttack.getCritChance());
        System.out.println("AttackData.critMultiplier = " + currentAttack.getCritMultiplier());
        System.out.println("-----------------------");

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

    protected void refreshAttackFromStats() {
        currentAttack = new AttackData("Basic Attack",stats.getAttackPower(),
                1,
                AttackData.DamageType.PHYSICAL,
                stats.getCritChance(),
                stats.getCritMultiplier(),
                0);
    }

}