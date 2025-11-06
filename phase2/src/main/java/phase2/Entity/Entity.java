package phase2.Entity;
import java.awt.*;

import phase2.UI.GamePanel;
import phase2.game.combat.*;
import phase2.game.stats.*;

public abstract class Entity implements Damageable, Attacker {
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
    protected Stats stats;

    //Attack data
    protected AttackData currentAttack;
    protected int coolDown; //attacks are only at set times not every frame

    protected int damageFlashTimer = 0;

    public Entity() {
        stats = new Stats();
        health = new HealthComponent(100, stats.getDefense(), this);
        currentAttack = stats.createBasicAttack();
    }

    protected void drawHealthBar(Graphics2D g2d, int screenX, int screeny, int width, int height) {
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
        damageFlashTimer = 10;
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
        System.out.println(getClass().getSimpleName() + " was killed");
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
        return CombatManager.resolveAttack(this, target, currentAttack);
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
