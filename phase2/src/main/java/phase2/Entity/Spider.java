package phase2.Entity;

import phase2.UI.GamePanel;
import phase2.game.combat.DamageSource;
import java.awt.*;
import java.util.Random;

public class Spider extends Enemy {

    private boolean playerSpotted = false;
    private long explodeStartTime = 0;
    private static final long EXPLOSION_DELAY_MS = 2000;
    private boolean hasExploded = false;
    private static final int MIN_SPEED = 2;
    private static final int MAX_SPEED = 5;

    private Image idleImage;
    private Image hitImage;

    private final Random random = new Random();

    public Spider(GamePanel gp, Pathfinder pathfinder, Player player, int x, int y) {
        super(gp, pathfinder, player, x, y);
        speed = MIN_SPEED + random.nextInt(MAX_SPEED - MIN_SPEED + 1);
        getSpiderImages();
    }

    private void getSpiderImages() {
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            idleImage = toolkit.getImage(gp.getResourceAsImage(
                "Top_Down_Adventure_Pack_v.1.0/Enemies_Sprites/Spider_Sprites/spider_idle_anim_all_dir.gif"));
            hitImage = toolkit.getImage(gp.getResourceAsImage(
                "Top_Down_Adventure_Pack_v.1.0/Enemies_Sprites/Spider_Sprites/spider_hit_anim_all_dir.gif"));
            up = down = left = right = idleImage;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
public void update() {
    if (!isOnScreen() || hasExploded) return;

    // Detect player
    if (!playerSpotted && canSeePlayer()) {
        playerSpotted = true;
        explodeStartTime = System.currentTimeMillis();
        speed = MAX_SPEED; // chase faster
    }

    collisionOn = false;
    gp.checkCollision.checkTile(this);

    if (!collisionOn) followPathSmooth();

    // Explosion after delay only if player still in range
    if (playerSpotted) {
        long elapsed = System.currentTimeMillis() - explodeStartTime;
        if (elapsed >= EXPLOSION_DELAY_MS) {
            if (canSeePlayer()) { // <--- only explode if player is close
                explode();
            } else {
                // reset spotted if player ran away
                playerSpotted = false;
                speed = MIN_SPEED;
            }
        }
    }
}

private boolean canSeePlayer() {
    int dx = player.worldX - worldX;
    int dy = player.worldY - worldY;
    double distance = Math.sqrt(dx * dx + dy * dy);
    int detectionRange = 5 * gp.tileSize; // 5 tiles
    return distance <= detectionRange;
}


    private void followPathSmooth() {
        int dx = player.worldX - worldX;
        int dy = player.worldY - worldY;
        double distance = Math.sqrt(dx*dx + dy*dy);
        if (distance == 0) return;

        int moveX = (int) Math.round(speed * dx / distance);
        int moveY = (int) Math.round(speed * dy / distance);

        worldX += moveX;
        worldY += moveY;

        if (Math.abs(dx) > Math.abs(dy)) {
            direction = dx > 0 ? "right" : "left";
        } else {
            direction = dy > 0 ? "down" : "up";
        }
    }

    private void explode() {
        hasExploded = true;

        Rectangle explosionArea = new Rectangle(worldX, worldY, gp.tileSize, gp.tileSize);
        Rectangle playerBox = new Rectangle(player.worldX + player.collisionArea.x,
                                            player.worldY + player.collisionArea.y,
                                            player.collisionArea.width,
                                            player.collisionArea.height);

        if (explosionArea.intersects(playerBox)) {
            player.takeDamage(30, new DamageSource("Spider Explosion"));
        }

        up = down = left = right = hitImage;

        // Remove safely after next update cycle
        gp.enemies.remove(this);
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (hasExploded) return;

        int screenX = worldX - player.worldX + player.screenX;
        int screenY = worldY - player.worldY + player.screenY;

        if (screenX + gp.tileSize < 0 || screenX > gp.screenWidth ||
            screenY + gp.tileSize < 0 || screenY > gp.screenHeight) return;

        g2d.drawImage(up, screenX, screenY, gp.tileSize, gp.tileSize, gp);
    }
}
