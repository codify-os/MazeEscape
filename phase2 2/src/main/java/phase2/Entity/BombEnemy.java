package main.java.phase2.Entity;

import phase2.UI.GamePanel;
import phase2.game.combat.DamageSource;
import phase2.game.stats.HealthComponent;

import java.awt.*;

public class BombEnemy extends Enemy {

    private Image bombImg;
    private Image explosionImg;

    private boolean exploding = false;
    private boolean fuseLit = false;
    private int fuseTimer = 30;        // delay before explosion
    private final int explodeDuration = 20; // how long explosion stays visible
    private int explodeTimer = explodeDuration;

    private final int targetX; // fixed target position
    private final int targetY;

    private final int speed = 4;

    public BombEnemy(GamePanel gp, Player player, int startX, int startY) {
        super(gp, gp.pathfinder, player, startX, startY);

        this.health = new HealthComponent(1, 0, this);

        // store player's position at the moment of spawn
        this.targetX = player.worldX;
        this.targetY = player.worldY;

        loadImages();
    }

    private void loadImages() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        bombImg = tk.getImage(gp.getResourceAsImage(
                "Top_Down_Adventure_Pack_v.1.0/Enemies_Sprites/Bomberplant_Sprites/bomb_going_down_anim.gif"));

        explosionImg = tk.getImage(gp.getResourceAsImage(
                "Top_Down_Adventure_Pack_v.1.0/Enemies_Sprites/Bomberplant_Sprites/bomb_explosion_anim.gif"));
    }

    @Override
    public void update() {
        if (exploding) {
            explodeTimer--;
            if (explodeTimer <= 0) gp.enemies.remove(this);
            return;
        }

        if (!fuseLit) {
            moveToTarget();
            if (isAtTarget()) {
                fuseLit = true; // start the fuse when bomb reaches target
            }
        } else {
            fuseTimer--;
            if (fuseTimer <= 0) explode();
        }

        super.update();
    }

    private void moveToTarget() {
        int dx = targetX - worldX;
        int dy = targetY - worldY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= speed) {
            worldX = targetX;
            worldY = targetY;
            return;
        }

        worldX += (int) (dx / distance * speed);
        worldY += (int) (dy / distance * speed);
    }

    private boolean isAtTarget() {
        Rectangle bombRect = new Rectangle(worldX, worldY, gp.tileSize, gp.tileSize);
        Rectangle targetRect = new Rectangle(targetX, targetY, gp.tileSize, gp.tileSize);
        return bombRect.intersects(targetRect);
    }

    private void explode() {
        exploding = true;
        // Damage player if still in explosion area
        Rectangle explosionArea = new Rectangle(worldX - gp.tileSize/2, worldY - gp.tileSize/2, gp.tileSize * 2, gp.tileSize * 2);
        Rectangle playerRect = new Rectangle(player.worldX, player.worldY, gp.tileSize, gp.tileSize);
        if (explosionArea.intersects(playerRect)) {
            player.takeDamage(20, new DamageSource("Bomb Explosion"));
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        int sx = worldX - player.worldX + player.screenX;
        int sy = worldY - player.worldY + player.screenY;

        if (exploding || fuseLit) {
            g2.drawImage(explosionImg, sx, sy, gp.tileSize * 2, gp.tileSize * 2, gp);
        } else {
            g2.drawImage(bombImg, sx, sy, gp.tileSize, gp.tileSize, gp);
        }
    }
}
