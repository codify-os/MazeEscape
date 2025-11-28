package phase2.Entity;

import phase2.Tile.Tile;
import phase2.UI.GamePanel;
import phase2.UI.KeyHandler;
import phase2.game.combat.AttackData;
import phase2.game.combat.DamageSource;
import phase2.game.stats.Stats;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Player extends Entity{
    // Constants
    private static final int PLAYER_MAX_HEALTH = 150;
    private static final int PLAYER_ATTACK = 20;
    private static final int PLAYER_DEFENSE = 5;
    private static final int PLAYER_SPEED = 4;
    private static final int ATK_DURATION = 20;
    private static final int TRAP_DAMAGE_FLASH = 15;
    private static final int CRIT_BUFF_DURATION = 180;
    private static final double CRIT_BUFF_CHANCE = 0.1;
    private static final int WIN_POSITION_COL = 48;
    private static final int WIN_POSITION_ROW = 47;
    private static final int ATK_COUNTER = 0;
    private static final int CRIT_BUFF_TIMER = 0;

    private final KeyHandler keyH;
    public final int screenX;
    public final int screenY;

    private Image atkUp, atkDown, atkLeft, atkRight;
    private boolean isAttacking = false;
    private int atkCounter = ATK_COUNTER;
    private boolean critBuffActive = false;
    private int critBuffTimer = CRIT_BUFF_TIMER;
    private double prevCritChance;

    private final Map<String, Integer> inventory = new HashMap<>();
    private final Random random = new Random();

    private int buffTextTimer = 0;
    private String buffText;

    public enum BuffTier{
        AMAZING,
        NORMAL
    }

    public enum BuffType{
        CRIT_CHANCE,
        CRIT_MULTIPLIER,
        SPEED,
        HEAL,
        ATTACK
    }
    /**
     * Create a player instance
     * @param gp GamePanel instance
     * @param keyH KeyHandler for input
     * @throws IllegalArgumentException if gp or keyH is null
     */
    public Player(GamePanel gp, KeyHandler keyH) {
        super(PLAYER_MAX_HEALTH,
                new Stats(
                        PLAYER_ATTACK,
                        PLAYER_DEFENSE,
                        PLAYER_SPEED,
                        0.05,
                        1.5
                ));

        if (gp == null || keyH == null) {
            throw new IllegalArgumentException("GamePanel and KeyHandler cannot be null");
        }

        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.screenWidth/2 - (gp.tileSize/2);
        screenY = gp.screenHeight/2 - (gp.tileSize/2);

        this.prevCritChance = stats.getCritChance();

        collisionArea = new Rectangle(8, 16, gp.tileSize - 16, gp.tileSize - 16);
        setDefaultValues();
        getPlayerImage();
        System.out.println("Player Created: " + this);
    }

    public void setDefaultValues() {
        worldX = gp.tileSize; //spawn x
        worldY = gp.tileSize * 4; //spawn y
        speed = PLAYER_SPEED;
        direction = "down";
    }

    public void getPlayerImage() {
        try{
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            MediaTracker tracker = new MediaTracker(new java.awt.Canvas());

            int id = 0;
            up = addImage(toolkit, tracker, "Char_Sprites/char_run_up_anim.gif", id++);
            down = addImage(toolkit, tracker, "Char_Sprites/char_run_down_anim.gif", id++);
            left = addImage(toolkit, tracker, "Char_Sprites/char_run_left_anim.gif", id++);
            right = addImage(toolkit, tracker, "Char_Sprites/char_run_right_anim.gif", id++);
            atkUp = addImage(toolkit, tracker, "Char_Sprites/char_attack_up_anim.gif", id++);
            atkDown = addImage(toolkit, tracker, "Char_Sprites/char_attack_down_anim.gif", id++);
            atkLeft = addImage(toolkit, tracker, "Char_Sprites/char_attack_left_anim.gif", id++);
            atkRight = addImage(toolkit, tracker, "Char_Sprites/char_attack_right_anim.gif", id++);

            tracker.waitForAll();
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Image addImage(Toolkit toolkit, MediaTracker tracker, String path, int id){
        Image image = toolkit.getImage(gp.getResourceAsImage(path));
        tracker.addImage(image, id);
        return image;
    }
    @Override
    public void update() {
        updateDirection();
        handleTraps();
        collisionOn = false;
        gp.checkCollision.checkTile(this);
        handleMovement();

        updateCooldown();
        handleAttack();
        updateAttackAnimation();
        updateCritBuff();
        handleWinCondition();
        if (buffTextTimer > 0) {
            buffTextTimer--;
        }
    }

    private void updateDirection() {
        if(keyH.wPressed) {
            direction = "up";
        } else if (keyH.sPressed) {
            direction = "down";
        } else if (keyH.aPressed) {
            direction = "left";
        } else if (keyH.dPressed){
            direction = "right";
        }
    }

    private void handleMovement() {
        if (keyH.wPressed || keyH.sPressed || keyH.aPressed || keyH.dPressed) {
            if (!collisionOn) {
                switch (direction) {
                    case "up" -> worldY -= speed;
                    case "down" -> worldY += speed;
                    case "left" -> worldX -= speed;
                    case "right" -> worldX += speed;
                }
            }
            collectKey();
        }
    }
    private void handleTraps() {
        int playerCol = (worldX + collisionArea.x) / gp.tileSize;
        int playerRow = (worldY + collisionArea.y) / gp.tileSize;
        Tile curTile = gp.tileManager.getTile(playerCol, playerRow);
        if (curTile != null && curTile.isTrap) {
            trapDamageHandler(curTile);
        }
    }

    private void handleAttack() {
        if (keyH.spacePressed && !isAttacking) {
            isAttacking = true;
            atkCounter = 0;

            for(Enemy e: new ArrayList<>(gp.enemies)) {
                if (isInRange(e)) {
                    System.out.println("The player is attacking the enemy");
                    attack(e);
                }
            }
        }
    }

    private void updateAttackAnimation() {
        if (isAttacking) {
            atkCounter++;
            if (atkCounter > ATK_DURATION) {
                isAttacking = false;
                atkCounter = 0;
            }
        }
    }

    private void updateCritBuff() {
        if (critBuffActive) {
            if (critBuffTimer <= 0) {
                critBuffActive = false;
                stats.setCritChance(prevCritChance);
            }
        }
    }

    private void handleWinCondition() {
        int playerCol = (worldX + collisionArea.x) / gp.tileSize;
        int playerRow = (worldY + collisionArea.y) / gp.tileSize;
        checkWinCondition(playerCol, playerRow);
}

    private void checkWinCondition(int playerCol, int playerRow) {
        if (playerCol == WIN_POSITION_COL && playerRow == WIN_POSITION_ROW) {
            if (hasItem("key")) {
                gp.finalScore = (int) (health.getHealthPercentage() * 1000);
                gp.gameState = GamePanel.GameState.GAME_WON;
            }
        }
    }

    private void collectKey() {
        Rectangle playerHitBox = new Rectangle(
            worldX + collisionArea.x,
            worldY + collisionArea.y,
            collisionArea.width,
            collisionArea.height
        );
        if (gp.checkKeyCollection(playerHitBox)) {
            addItem("key"); // Update player inventory
        }
    }


    private void trapDamageHandler(Tile curTile) {
        if (curTile.trapTimer > 0) {
            curTile.trapTimer--;
            return;
        }
        health.takeDamage(curTile.trapDamage, new DamageSource("Trap"));
        System.out.println("Trap triggered! Current HP: " + health.getHealthPercentage());
        curTile.trapTimer = curTile.trapCooldown;
        damageFlashTimer = TRAP_DAMAGE_FLASH;
    }

    @Override
    public void draw(Graphics2D g2d){
        Image image = isAttacking ? getAttackSprite() : getMovementSprite();
        Rectangle spriteBounds = isAttacking ? getAttackSpriteDimensions() : getDefaultSpriteDimensions();

        takeDamageFlash(g2d);

        if (image != null) {
            g2d.drawImage(image, spriteBounds.x, spriteBounds.y, spriteBounds.width, spriteBounds.height, null);
        }

        int barWidth = gp.tileSize;
        int barHeight = 4;
        int barX = screenX;
        int barY = screenY - barHeight - 4;
        drawHealthBar(g2d, barX, barY, barWidth, barHeight);

        if(damageTextTimer > 0) {
            showDamage(g2d);
            damageTextTimer--;
        }
        if (buffTextTimer > 0) {
            showBuffText(g2d);
        }
    }

    private Image getMovementSprite() {
        return switch (direction) {
            case "up" -> up;
            case "down" -> down;
            case "left" -> left;
            case "right" -> right;
            default -> down;
        };
    }

    private Image getAttackSprite() {
        return switch (direction) {
            case "up" -> atkUp;
            case "down" -> atkDown;
            case "left" -> atkLeft;
            case "right" -> atkRight;
            default -> down;
        };
    }

    private Rectangle getDefaultSpriteDimensions() {
        return new Rectangle(screenX, screenY, gp.tileSize, gp.tileSize);
    }

    private Rectangle getAttackSpriteDimensions() {
        return switch (direction) {
            case "up" -> new Rectangle(screenX - gp.tileSize, screenY - gp.tileSize, gp.tileSize * 3, gp.tileSize * 2);
            case "down" -> new Rectangle(screenX - gp.tileSize, screenY, gp.tileSize * 3, gp.tileSize * 2);
            case "left" -> new Rectangle(screenX - gp.tileSize, screenY - gp.tileSize, gp.tileSize * 2, gp.tileSize * 3);
            case "right" -> new Rectangle(screenX, screenY - gp.tileSize, gp.tileSize * 2, gp.tileSize * 3);
            default -> new Rectangle(screenX - gp.tileSize, screenY, gp.tileSize * 3, gp.tileSize * 2);
        };
    }

    private void showDamage(Graphics2D g2d) {
        String message = "-" + previousDamageAmount;
        int hpStatX = screenX + gp.tileSize/2;
        int hpStatY = screenY - 10 - (30 - damageTextTimer);

        // Save original font
        Font originalFont = g2d.getFont();

        g2d.setColor(Color.black);
        g2d.drawString(message, hpStatX + 2, hpStatY + 2);

        if (lastCrit) {
            g2d.setFont(new Font("Comic Sans", Font.BOLD, 22));
            g2d.setColor(Color.ORANGE);
        } else  {
            g2d.setFont(new Font("Comic Sans", Font.BOLD, 16));
            g2d.setColor(Color.red);
        }
        g2d.drawString(message, hpStatX, hpStatY);
        
        // Restore original font
        g2d.setFont(originalFont);
    }

    private void takeDamageFlash(Graphics2D g2d) {
        if (damageFlashTimer > 0) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.setColor(Color.red);
            g2d.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            damageFlashTimer--;
        }
    }

    //    @Override
//    public void onDeath() {
//        System.out.println("Game Over!");
//    }
    public void handleDeath() {
        System.out.println("Game Over!");
    }

    public void addItem(String itemName) {
        inventory.put(itemName, inventory.getOrDefault(itemName, 0) + 1);
    }

    public boolean hasItem(String itemName) {
        return inventory.getOrDefault(itemName, 0) > 0;
    }

    public void removeItem(String itemName) {
        if (hasItem(itemName)) {
            inventory.put(itemName, inventory.get(itemName) - 1);
        }
    }

    public Map<String, Integer> getInventory() {
        return inventory;
    }
    public void clearInventory() {
        inventory.clear();
    }

    public void grantRandomBuff() {
        System.out.println("[BUFF] granRandomBuff() Called");
        BuffTier tier = rollBuffTier();
        BuffType type = rollBuffType();

        System.out.println("[Buff] Rolled: " + type + " (" + tier + ")");

        applyBuff(type, tier);
//        grantCritChanceBuff();
//        grantHPBuff();
//        // 10% chance to gain small speed buff (+1 or +2)
//        grantSpeedBuff();
    }

//    private void grantCritChanceBuff() {
//        double roll = random.nextDouble();
//        if (roll < CRIT_BUFF_CHANCE) {
//            activateCritBuff();
//            System.out.println("Crit Buff gained");
//        }
//    }
//
//    private void grantHPBuff() {
//        double healRoll = random.nextDouble();
//        if (healRoll < 0.15) { // 15% chance
//            int healAmount = 1 + random.nextInt(10); // Heal 1-10 HP
//            this.heal(healAmount);
//            System.out.println("Player absorbs " + healAmount + " health from buff!");
//        }
//    }
//
//    private void grantSpeedBuff() {
//        double speedRoll = random.nextDouble();
//        if (speedRoll < 0.10) {
//            int speedBuff = 1 + random.nextInt(2); // 1 or 2
//            speed += speedBuff;
//            System.out.println("Player gains +" + speedBuff + " speed from buff!");
//        // Optionally, you can make it temporary by storing it and reverting after some ticks
//        }
//    }

    public void forceCritBuff() {
        activateCritBuff();
    }

    private void activateCritBuff() {
        prevCritChance = stats.getCritChance();
        stats.setCritChance(1.0);
        critBuffActive = true;
        critBuffTimer = CRIT_BUFF_DURATION;
    }

    public boolean isCritBuffActive() {
        return critBuffActive;
    }
    public int getCritBuffTimer() {
        return critBuffTimer;
    }

    private BuffTier rollBuffTier() {
        double roll = random.nextDouble();
        return roll < 0.20 ? BuffTier.AMAZING : BuffTier.NORMAL;
    }
    private BuffType rollBuffType() {
        BuffType[] values = BuffType.values();
        return values[random.nextInt(values.length)];
    }

    private void applyBuff(BuffType type, BuffTier tier) {
        boolean amazing = (tier == BuffTier.AMAZING);
        System.out.println("[Buff] Applying " + type + " (" + type +")");
        System.out.println("[TEST] Entered applyBuff() with type=" + type + " tier=" + tier);

        switch (type) {
            case HEAL -> {
                int amount = amazing ? 30 : 10;
                health.heal(amount);
                buffText = "+" + amount + " HP (" + tier + ")";
                buffTextTimer = 200;
                System.out.println("Heal from kill: +" + amount);
                System.out.println("[BUFF POPUP DEBUG] buffText set to: " + buffText + " | timer = " + buffTextTimer);
            }
            case SPEED -> {
                int amount = amazing ? 3 : 1;
                speed += amount;
                buffText = "+" + amount + " speed (" + tier + ")";
                buffTextTimer = 200;
                System.out.println("speed +: +" + amount);
                System.out.println("[BUFF POPUP DEBUG] buffText set to: " + buffText + " | timer = " + buffTextTimer);
            }
            case CRIT_CHANCE -> {
                double amount = amazing ? 1.00 : 0.30;
                stats.setCritChance(Math.min(1.0, stats.getCritChance() + amount));
                refreshAttackFromStats();
                buffText =  "+"  + amount + " Crit Chance (" + tier + ")";
                buffTextTimer = 200;
                System.out.println("crit chance +: +" + amount);
                System.out.println("[BUFF POPUP DEBUG] buffText set to: " + buffText + " | timer = " + buffTextTimer);
            }
            case ATTACK -> {
                int amount = amazing ? 10 : 2;
                stats.setAttackPower(stats.getAttackPower() + amount);
                refreshAttackFromStats();
                buffText = "+" + amount + " Damage (" + tier + ")";
                buffTextTimer = 200;
                System.out.println("atk +: +" + amount);
                System.out.println("[BUFF POPUP DEBUG] buffText set to: " + buffText + " | timer = " + buffTextTimer);
            }
            case CRIT_MULTIPLIER -> {
                double amount = amazing ? 2 : 0.5;
                stats.setCritMultiplier(stats.getCritMultiplier() + amount);
                refreshAttackFromStats();
                buffText = "+" + amount + " Crit Mul (" + tier + ")";
                buffTextTimer = 200;
                System.out.println("cd +: +" + amount);
                System.out.println("[BUFF POPUP DEBUG] buffText set to: " + buffText + " | timer = " + buffTextTimer);
            }
        }
        System.out.printf(
                "[BUFF RESULT] Stats now: atk=%d, def=%d, critChance=%.2f, critMult=%.2f%n",
                stats.getAttackPower(),
                stats.getDefense(),
                stats.getCritChance(),
                stats.getCritMultiplier()
        );
    }

    public void showBuffText(Graphics2D g2d) {
        // Save original font
        Font originalFont = g2d.getFont();
        
        Color buffColor = buffText.contains("AMAZING") ? new Color(255, 215, 0) : Color.green;
        g2d.setFont(new Font("Comic Sans", Font.BOLD, 18));

        int x = screenX;
        int y = screenY - 40 - (30 - buffTextTimer);

        g2d.setColor(Color.black);
        g2d.drawString(buffText, x + 2, y + 2);

        g2d.setColor(buffColor);
        g2d.drawString(buffText, x, y);
        
        // Restore original font
        g2d.setFont(originalFont);
    }

//    private void triggerBuffPopup(String msg) {
//        System.out.println("POPUP TRIGGERED: >>> " + msg);
//        buffText = msg;
//        buffTextTimer = 30;
//        System.out.println("DRAWING BUFF POPUP: " + buffText + " timer=" + buffTextTimer);
//    }

    public void healFromKill() {
        int healAmount = 2;

        if (Math.random() < 0.15) {
            healAmount = Math.max(1, (int) ((0.15 + Math.random()*0.05) * health.getCurrentHealth()));
        }

        health.heal(healAmount);

        //triggerBuffPopup("+" + healAmount + "HP");
        System.out.println("Heal from kill: +" + healAmount);
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