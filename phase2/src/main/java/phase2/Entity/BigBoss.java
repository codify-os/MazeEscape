package phase2.Entity;

import phase2.UI.GamePanel;
import phase2.game.combat.DamageSource;
import phase2.game.stats.HealthComponent;
import phase2.game.stats.Stats;
import phase2.Entity.Entity;

import java.awt.*;
import java.util.Random;

public class BigBoss extends Enemy {

    private static final int BOSS_HP = 300;
    private static final int DETECTION_RANGE = 12 * 48;

    private static final int MINION_DELAY = 5000; //5s
    private static final int BOMB_DELAY = 5500; //5.5s

    private Image idleImg;
    private Image deathImg;

    private long lastMinionTime = 0;
    private long lastBombTime = 0;

    private boolean dying = false;
    private int deathTimer = 60;

    private final Random random = new Random();

    public BigBoss(GamePanel gp, Pathfinder pathfinder, Player player, int x, int y) {
        super(gp, pathfinder, player, x, y);

        // Override enemy stats
        this.health = new HealthComponent(BOSS_HP, 5, this);
        this.stats = new Stats(20, 5); // stronger attack & defense
        this.speed = 1;

        loadImages();
    }

    private void loadImages() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        idleImg = tk.getImage(gp.getResourceAsImage("Top_Down_Adventure_Pack_v.1.0/Enemies_Sprites/Pinkbat_Sprites/pinkbat_idle_left_anim.gif"));

        deathImg = tk.getImage(gp.getResourceAsImage("Top_Down_Adventure_Pack_v.1.0/Enemies_Sprites/Pinkbat_Sprites/pinkbat_death_anim_left.gif"));
        
    }

    @Override
    public void update() {
        if (dying) {
            deathTimer--;
            if (deathTimer <= 0) finishDeath();
            return;
        }

        // only chase when player is near portal
        if (distanceToPlayer() < DETECTION_RANGE) {
            chasePlayer();
            spawnMinion();
            spawnBomb();
        }

        super.update();
    }

    private double distanceToPlayer() {
        int dx = player.worldX - worldX;
        int dy = player.worldY - worldY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void chasePlayer() {
        if (player.worldX > worldX) worldX += speed;
        if (player.worldX < worldX) worldX -= speed;
        if (player.worldY > worldY) worldY += speed;
        if (player.worldY < worldY) worldY -= speed;
    }

    // ---------------- MINIONS ----------------
    private void spawnMinion() {
        long now = System.currentTimeMillis();
        if (now - lastMinionTime < MINION_DELAY) return;
        lastMinionTime = now;

        gp.enemies.add(new PhantomMinion(gp, gp.pathfinder, player, worldX, worldY));
    }

    // ---------------- BOMBS ----------------
    private void spawnBomb() {
        long now = System.currentTimeMillis();
        if (now - lastBombTime < BOMB_DELAY) return;
        lastBombTime = now;

        gp.enemies.add(new BombEnemy(gp, player, worldX, worldY));
    }

    // ---------------- TAKE DAMAGE ----------------
    @Override
    public void takeDamage(int amount, DamageSource src) {
        health.takeDamage(amount, src);

        if (!health.isAlive() && !dying) {
            dying = true;
        }
    }

    // ---------------- DEATH EXPLOSION ----------------
    private void finishDeath() {
        // Damage player if close
        Rectangle dmgArea = new Rectangle(
                worldX - gp.tileSize,
                worldY - gp.tileSize,
                gp.tileSize * 3,
                gp.tileSize * 3
        );

        Rectangle playerHitbox = new Rectangle(
                player.worldX,
                player.worldY,
                gp.tileSize,
                gp.tileSize
        );

        if (dmgArea.intersects(playerHitbox)) {
            player.takeDamage(30, new DamageSource("Boss Explosion"));
        }

        // Remove boss
        gp.enemies.remove(this);
    }

    @Override
    public void draw(Graphics2D g2) {
        int sx = worldX - player.worldX + player.screenX;
        int sy = worldY - player.worldY + player.screenY;

        Image img = dying ? deathImg : idleImg;

        g2.drawImage(img, sx, sy, gp.tileSize * 3, gp.tileSize * 3, gp);

        drawHealthBar(g2, sx, sy - 15, gp.tileSize * 3, 10);
    }

    @Override
    
public void drawHealthBar(Graphics2D g2, int x, int y, int w, int h) {
    double hp = (double) health.getCurrentHealth() / health.getMaxHealth();
    int barFill = (int)(w * hp);

    g2.setColor(Color.black);
    g2.fillRect(x, y, w, h);

    g2.setColor(Color.red);
    g2.fillRect(x, y, barFill, h);

    g2.setColor(Color.white);
    g2.drawRect(x, y, w, h);
}

}
