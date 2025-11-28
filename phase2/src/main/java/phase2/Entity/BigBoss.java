package phase2.Entity;

import phase2.UI.GamePanel;
import phase2.game.combat.DamageSource;
import phase2.game.stats.HealthComponent;
import phase2.game.stats.Stats;

import java.awt.*;
import java.util.Random;

public class BigBoss extends Enemy {

    private static final int BOSS_HP = 550;
    private static final int NORMAL_DETECTION_RANGE = 16 * 48; // ~16 tiles
    private static final int SPECIAL_DETECTION_RANGE = 12 * 48; // ~12 tiles
    private long lastChargeTime = 0;

    private static final int CHARGE_COOLDOWN = 100; // <1 second between red marks

    //private static final int MINION_DELAY = 4000;
    private static final int BOMB_DELAY = 3500;

    private Image idleImg;
    private Image deathImg;

    //private long lastMinionTime = 0;
    private long lastBombTime = 0;

    private boolean dying = false;
    private int deathTimer = 60;

    private boolean damageReady = false;  // flag to hit player only after reaching target

    private final Random random = new Random();

    private boolean alertTriggered = false;
    private boolean warningTriggered = false;


    // ------------------------- BOSS MODE SYSTEM -------------------------
    private enum BossMode { NORMAL, SPECIAL }

    private BossMode mode = BossMode.NORMAL;   // Start in NORMAL mode
    private long modeStartTime = System.currentTimeMillis();

    // Special attack data
    private Rectangle dangerZone = null;
    private int dashTargetX, dashTargetY;

    private boolean isCharging = false;
    private boolean isDashing = false;
    private long chargeStartTime = 0;

    private boolean playerDetected = false; // tracks if boss locked onto player
    private boolean keyDropped = false; // add this as a field in BigBoss


    public BigBoss(GamePanel gp, Pathfinder pathfinder, Player player, int x, int y) {
        super(gp, pathfinder, player, x, y);

        this.health = new HealthComponent(BOSS_HP, 5, this);
        this.stats = new Stats(20, 5);
        this.speed = 1;
        
        // BigBoss is 3x3 tiles, so collision area should be larger
        this.collisionArea = new Rectangle(
            gp.tileSize / 4, 
            gp.tileSize / 2, 
            (gp.tileSize * 3) - (gp.tileSize / 2), 
            (gp.tileSize * 3) - (gp.tileSize)
        );

        loadImages();
        modeStartTime = System.currentTimeMillis();
        alertTriggered = false;
        warningTriggered = false;

    }

    private void loadImages() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        idleImg = tk.getImage(gp.getResourceAsImage(
                "Top_Down_Adventure_Pack_v.1.0/Enemies_Sprites/Pinkbat_Sprites/pinkbat_idle_left_anim.gif"));
        deathImg = tk.getImage(gp.getResourceAsImage(
                "Top_Down_Adventure_Pack_v.1.0/Enemies_Sprites/Pinkbat_Sprites/pinkbat_death_anim_left.gif"));
    }

    // ======================================================================
    //                                UPDATE
    // ======================================================================
    @Override
    public void update() {

        if (dying) {
            deathTimer--;
            if (deathTimer <= 0) finishDeath();
            return;
        }

        long now = System.currentTimeMillis();

        // ---- MODE SWITCHING ----
        if (alertTriggered && mode == BossMode.NORMAL && now - modeStartTime >= 20000) {
            enterSpecialMode();
        }
        else if (mode == BossMode.SPECIAL && now - modeStartTime >= 30000) {
            exitSpecialMode();
        }

        // ----- BEHAVIOR -----
        if (mode == BossMode.NORMAL) {
            updateNormalMode();
        } else {
            updateSpecialMode();
        }

        super.update();
    }

    // ======================================================================
    //                           NORMAL MODE
    // ======================================================================
    private void updateNormalMode() {
    double dist = distanceToPlayer();

    if (dist < NORMAL_DETECTION_RANGE) {

        // FIRST TIME BOSS SEES PLAYER
        if (!alertTriggered) {
            alertTriggered = true;
            warningTriggered = true;

            mode = BossMode.NORMAL;
            isCharging = false;
            isDashing = false;
            dangerZone = null;

            // --- FLASH SCREEN ---
            gp.bossFlashActive = true;
            gp.bossFlashStart = System.currentTimeMillis();

            // --- SHOW WARNING TEXT ---
            gp.bossWarningActive = true;
            gp.bossWarningStart = System.currentTimeMillis();

            // --- IMPORTANT: RESET MODE TIMER so SPECIAL doesn't start instantly ---
            modeStartTime = System.currentTimeMillis();
        }

        chasePlayer();
        spawnBomb();
    }
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

//     private void spawnMinion() {
//     long now = System.currentTimeMillis();
//     if (now - lastMinionTime < MINION_DELAY) return;
//     lastMinionTime = now;

//     gp.enemiesToAdd.add(new PhantomMinion(gp, gp.pathfinder, player, worldX, worldY));
// }

private void spawnBomb() {
    long now = System.currentTimeMillis();
    if (now - lastBombTime < BOMB_DELAY) return;
    lastBombTime = now;

    gp.enemiesToAdd.add(new BombEnemy(gp, player, worldX, worldY));
}


    // ======================================================================
    //                         SPECIAL MODE START / END
    // ======================================================================
    private void enterSpecialMode() {
        mode = BossMode.SPECIAL;
        modeStartTime = System.currentTimeMillis();

        playerDetected = false; // reset detection for new special mode
        startCharge();
    }

    private void exitSpecialMode() {
        mode = BossMode.NORMAL;
        modeStartTime = System.currentTimeMillis();

        dangerZone = null;
        isCharging = false;
        isDashing = false;
        speed = 1;
        playerDetected = false;
    }

    // ======================================================================
    //                          SPECIAL MODE LOGIC
    // ======================================================================
    private void updateSpecialMode() {

    double distToPlayer = distanceToPlayer();

    // Start special attack if player is within range
    long now = System.currentTimeMillis();
    if (!isCharging && !isDashing && now - lastChargeTime >= CHARGE_COOLDOWN) {
      if (distToPlayer <= SPECIAL_DETECTION_RANGE) {
        startCharge();
        lastChargeTime = now;
    }
}

    // Update charge
    if (isCharging) {
        updateCharge();
    }

    // Update dash
    if (isDashing) {
        updateDash();
    }

    // Stop attacks if player is very far away (optional)
    if (distToPlayer > SPECIAL_DETECTION_RANGE * 2) { // very far = stop
        dangerZone = null;
        isCharging = false;
        isDashing = false;
        speed = 1;
    }
}

    // --------------------------- CHARGE ATTACK ---------------------------
    private void startCharge() {
    isCharging = true;
    isDashing = false;
    damageReady = false;

    // lock onto current player position
    dashTargetX = player.worldX;
    dashTargetY = player.worldY;

    // RED MARK AREA slightly larger than tile
    dangerZone = new Rectangle(
            dashTargetX - gp.tileSize / 2,
            dashTargetY - gp.tileSize / 2,
            gp.tileSize * 2,
            gp.tileSize * 2
    );

    chargeStartTime = System.currentTimeMillis();
}

    private void updateCharge() {
    // Show mark for 1 second before dash
    if (System.currentTimeMillis() - chargeStartTime >= 1000) {
        startDash();
    } else {
        // Danger zone follows player while charging
        dashTargetX = player.worldX;
        dashTargetY = player.worldY;
        dangerZone.x = dashTargetX - gp.tileSize / 2;
        dangerZone.y = dashTargetY - gp.tileSize / 2;
    }
}

    // --------------------------- DASH ATTACK ----------------------------
    private void startDash() {
    isCharging = false;
    isDashing = true;
    damageReady = true; // ready to hit player when reached
    speed = 15;  // fast movement
}

    private void updateDash() {
    int dx = dashTargetX - worldX;
    int dy = dashTargetY - worldY;
    double dist = Math.sqrt(dx * dx + dy * dy);

    if (dist < 10) {
        speed = 1;
        isDashing = false;
        dangerZone = null;

        // Deal damage only once
        if (damageReady) {
            Rectangle playerHitbox = new Rectangle(
                    player.worldX,
                    player.worldY,
                    gp.tileSize,
                    gp.tileSize
            );
            if (playerHitbox.intersects(new Rectangle(
                    dashTargetX - gp.tileSize / 2,
                    dashTargetY - gp.tileSize / 2,
                    gp.tileSize * 2,
                    gp.tileSize * 2
            ))) {
                player.takeDamage(20, new DamageSource("Boss Dash"));
            }
            damageReady = false;
        }

        // Immediately start next charge if player still in range
        if (distanceToPlayer() <= SPECIAL_DETECTION_RANGE) {
            startCharge();
        }
        return;
    }

    // Move toward target
    worldX += (int)(dx / dist * speed);
    worldY += (int)(dy / dist * speed);

    // Keep red mark following target (optional)
    if (dangerZone != null) {
        dangerZone.x = dashTargetX - gp.tileSize / 2;
        dangerZone.y = dashTargetY - gp.tileSize / 2;
    }
}


    // ======================================================================
    //                            DAMAGE & DEATH
    // ======================================================================
   //  @Override
public void takeDamage(int amount, DamageSource src) {
    health.takeDamage(amount, src);

    if (!health.isAlive() && !dying) {
        dying = true;
    }

    // Reset charge if in special mode, so red mark can appear again
    if (mode == BossMode.SPECIAL) {
        isCharging = false;
        isDashing = false;
        dangerZone = null;
        lastChargeTime = 0; // allow immediate re-detection
    }
}

    private void finishDeath() {
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

    // Add score for killing the boss
    gp.finalScore += 500;

    if (!keyDropped) {
        gp.dropKey(this.worldX, this.worldY); // drop 1 key at boss location
        keyDropped = true;
    }

    gp.enemiesToRemove.add(this);
}

    // ======================================================================
    //                                DRAW
    // ======================================================================
    @Override
    public void draw(Graphics2D g2) {
        int sx = worldX - player.worldX + player.screenX;
        int sy = worldY - player.worldY + player.screenY;

        Image img = dying ? deathImg : idleImg;
        g2.drawImage(img, sx, sy, gp.tileSize * 3, gp.tileSize * 3, gp);

        drawHealthBar(g2, sx, sy - 15, gp.tileSize * 3, 10);

        // draw red telegraph
        if (dangerZone != null) {
            g2.setColor(new Color(255, 0, 0, 120));
            g2.fillRect(
                    dangerZone.x - player.worldX + player.screenX,
                    dangerZone.y - player.worldY + player.screenY,
                    dangerZone.width,
                    dangerZone.height
            );
        }
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

