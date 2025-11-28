package phase2.Entity;

import phase2.UI.GamePanel;
import phase2.game.combat.DamageSource;
import phase2.game.stats.HealthComponent;
import phase2.game.stats.Stats;

import java.awt.*;
import java.util.Random;

/**
 * BigBoss enemy that occupies a 3x3 tile space with two behavior modes.
 * In NORMAL mode, the boss chases the player and spawns bombs.
 * In SPECIAL mode, the boss charges and dashes at the player's position.
 */
public class BigBoss extends Enemy {

    /** Boss maximum health points */
    private static final int BOSS_HP = 550;
    /** Detection range for normal mode (in pixels) */
    private static final int NORMAL_DETECTION_RANGE = 16 * 48; // ~16 tiles
    /** Detection range for special mode (in pixels) */
    private static final int SPECIAL_DETECTION_RANGE = 12 * 48; // ~12 tiles
    /** Last time a charge attack was initiated */
    private long lastChargeTime = 0;

    /** Cooldown between charge attacks in frames */
    private static final int CHARGE_COOLDOWN = 100; // <1 second between red marks

    //private static final int MINION_DELAY = 4000;
    /** Delay between bomb spawns in milliseconds */
    private static final int BOMB_DELAY = 3500;

    /** Idle animation sprite */
    private Image idleImg;
    /** Death animation sprite */
    private Image deathImg;

    //private long lastMinionTime = 0;
    /** Last time a bomb was spawned */
    private long lastBombTime = 0;

    /** Whether boss is in death animation */
    private boolean dying = false;
    /** Frames remaining in death animation */
    private int deathTimer = 60;

    /** Flag indicating damage should be dealt on dash hit */
    private boolean damageReady = false;  // flag to hit player only after reaching target

    /** Random number generator for boss behaviors */
    private final Random random = new Random();

    /** Whether boss has detected and locked onto player */
    private boolean alertTriggered = false;
    /** Whether warning has been shown to player */
    private boolean warningTriggered = false;


    // ------------------------- BOSS MODE SYSTEM -------------------------
    /** Boss behavior modes */
    private enum BossMode { NORMAL, SPECIAL }

    /** Current behavior mode */
    private BossMode mode = BossMode.NORMAL;   // Start in NORMAL mode
    /** Time when current mode started */
    private long modeStartTime = System.currentTimeMillis();

    /** Visual indicator of danger zone during charge attack */
    private Rectangle dangerZone = null;
    /** Target X position for dash attack */
    private int dashTargetX;
    /** Target Y position for dash attack */
    private int dashTargetY;

    /** Whether boss is charging up a dash attack */
    private boolean isCharging = false;
    /** Whether boss is currently dashing */
    private boolean isDashing = false;
    /** Time when charge attack started */
    private long chargeStartTime = 0;

    /** Whether player has been detected (unused) */
    private boolean playerDetected = false; // tracks if boss locked onto player
    /** Whether key has been dropped on death */
    private boolean keyDropped = false;
    
    /** Duration of invincibility frames (unused) */
    private static final int IFRAME_DURATION = 20; // ~0.33 seconds at 60 FPS
    /** Current invincibility frame counter */
    private int iframeCounter = 0;


    /**
     * Constructs a BigBoss enemy.
     * @param gp The game panel
     * @param pathfinder Pathfinding system for movement
     * @param player Reference to the player
     * @param x Starting X position in world coordinates
     * @param y Starting Y position in world coordinates
     */
    public BigBoss(GamePanel gp, Pathfinder pathfinder, Player player, int x, int y) {
        super(gp, pathfinder, player, x, y);

        this.health = new HealthComponent(BOSS_HP, 5, this);
        this.stats = new Stats(20, 5);
        this.speed = 1;
        
        // BigBoss is 3x3 tiles, so collision area should cover most of the sprite
        // Slightly smaller than full 3x3 to avoid edge collision issues
        this.collisionArea = new Rectangle(
            gp.tileSize / 2,
            gp.tileSize / 2,
            gp.tileSize * 2,
            gp.tileSize * 2
        );

        loadImages();
        modeStartTime = System.currentTimeMillis();
        alertTriggered = false;
        warningTriggered = false;

    }

    /**
     * Loads sprite images for idle and death animations.
     */
    private void loadImages() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        idleImg = tk.getImage(gp.getResourceAsImage(
                "Top_Down_Adventure_Pack_v.1.0/Enemies_Sprites/Pinkbat_Sprites/pinkbat_idle_left_anim.gif"));
        deathImg = tk.getImage(gp.getResourceAsImage(
                "Top_Down_Adventure_Pack_v.1.0/Enemies_Sprites/Pinkbat_Sprites/pinkbat_death_anim_left.gif"));
    }

    /**
     * Updates the boss each frame.
     * Handles death animation, mode switching between NORMAL and SPECIAL,
     * attack cooldown, and collision-based attacks.
     */
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

        // Don't call super.update() to avoid pathfinding movement
        // Just handle attack cooldown manually
        if (attackCoolDown > 0) {
            attackCoolDown--;
        }
        
        // Handle invincibility frames
        if (iframeCounter > 0) {
            iframeCounter--;
        }
        
        // Check for player collision and attack
        Rectangle enemyHitBox = new Rectangle(worldX + collisionArea.x, worldY + collisionArea.y,
                collisionArea.width, collisionArea.height);
        Rectangle playerHitBox = new Rectangle(player.worldX + player.collisionArea.x,
                player.worldY + player.collisionArea.y, player.collisionArea.width, player.collisionArea.height);

        if (enemyHitBox.intersects(playerHitBox) && attackCoolDown <= 0) {
            System.out.println("BigBoss attacks player for " + currentAttack.getPower());
            attack(player);
            attackCoolDown = ATTACK_COOLDOWN_FRAMES;
        }
    }

    /**
     * Updates boss behavior in NORMAL mode.
     * Boss chases the player and periodically spawns bombs.
     * Triggers alert state when player enters detection range.
     */
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


    /**
     * Calculates Euclidean distance from boss to player.
     * @return Distance in pixels
     */
    private double distanceToPlayer() {
        int dx = player.worldX - worldX;
        int dy = player.worldY - worldY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Moves the boss toward the player position.
     * Checks for collision and reverts movement if collision occurs.
     */
    private void chasePlayer() {
        int oldX = worldX;
        int oldY = worldY;
        
        if (player.worldX > worldX) worldX += speed;
        if (player.worldX < worldX) worldX -= speed;
        
        // Check horizontal collision
        collisionOn = false;
        gp.checkCollision.checkTile(this);
        if (collisionOn) {
            worldX = oldX; // Undo horizontal movement
        }
        
        oldY = worldY; // Update oldY before vertical movement
        
        if (player.worldY > worldY) worldY += speed;
        if (player.worldY < worldY) worldY -= speed;
        
        // Check vertical collision
        collisionOn = false;
        gp.checkCollision.checkTile(this);
        if (collisionOn) {
            worldY = oldY; // Undo vertical movement
        }
    }

//     private void spawnMinion() {
//     long now = System.currentTimeMillis();
//     if (now - lastMinionTime < MINION_DELAY) return;
//     lastMinionTime = now;

//     gp.enemiesToAdd.add(new PhantomMinion(gp, gp.pathfinder, player, worldX, worldY));
// }

/**
 * Spawns a BombEnemy at the boss's current position.
 * Respects a cooldown timer between spawns.
 */
private void spawnBomb() {
    long now = System.currentTimeMillis();
    if (now - lastBombTime < BOMB_DELAY) return;
    lastBombTime = now;

    gp.enemiesToAdd.add(new BombEnemy(gp, player, worldX, worldY));
}

    /**
     * Enters SPECIAL mode where boss performs charge-dash attacks.
     * Resets mode timer and initiates first charge.
     */
    private void enterSpecialMode() {
        mode = BossMode.SPECIAL;
        modeStartTime = System.currentTimeMillis();

        playerDetected = false; // reset detection for new special mode
        startCharge();
    }

    /**
     * Exits SPECIAL mode and returns to NORMAL mode.
     * Resets attack state and returns speed to normal.
     */
    private void exitSpecialMode() {
        mode = BossMode.NORMAL;
        modeStartTime = System.currentTimeMillis();

        dangerZone = null;
        isCharging = false;
        isDashing = false;
        speed = 1;
        playerDetected = false;
    }

    /**
     * Updates boss behavior in SPECIAL mode.
     * Initiates charge-dash attacks when player is in range,
     * and updates ongoing charge/dash states.
     */
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

    /**
     * Initiates a charge attack by locking onto the player's current position.
     * Creates a red danger zone indicator at the target location.
     */
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

    /**
     * Updates the charge state.
     * Danger zone follows player during charge, then initiates dash after 1 second.
     */
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

    /**
     * Initiates the dash attack phase.
     * Boss moves rapidly toward the locked target position.
     */
    private void startDash() {
    isCharging = false;
    isDashing = true;
    damageReady = true; // ready to hit player when reached
    speed = 15;  // fast movement
}

    /**
     * Updates the dash state.
     * Moves boss toward target, checks collision, deals damage on contact,
     * and initiates next charge if player is in range.
     */
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

    // Move toward target with collision detection
    int oldX = worldX;
    int oldY = worldY;
    
    worldX += (int)(dx / dist * speed);
    worldY += (int)(dy / dist * speed);
    
    // Check collision and revert if blocked
    collisionOn = false;
    gp.checkCollision.checkTile(this);
    if (collisionOn) {
        worldX = oldX;
        worldY = oldY;
        // Stop dash if blocked
        isDashing = false;
        speed = 1;
        dangerZone = null;
    }

    // Keep red mark following target (optional)
    if (dangerZone != null) {
        dangerZone.x = dashTargetX - gp.tileSize / 2;
        dangerZone.y = dashTargetY - gp.tileSize / 2;
    }
}

    /**
     * Handles damage taken by the boss.
     * Initiates death sequence if health reaches zero.
     * Resets charge attack state in SPECIAL mode.
     * @param amount Damage amount
     * @param src Source of the damage
     */
    @Override
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

    /**
     * Completes the death sequence.
     * Deals explosion damage to player if in range, awards score,
     * drops key item, and removes boss from game.
     */
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

    /**
     * Renders the boss sprite, health bar, and red danger zone indicator.
     * @param g2 Graphics context for drawing
     */
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

    /**
     * Draws the boss health bar above the sprite.
     * @param g2 Graphics context
     * @param x X position on screen
     * @param y Y position on screen
     * @param w Width of health bar
     * @param h Height of health bar
     */
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

