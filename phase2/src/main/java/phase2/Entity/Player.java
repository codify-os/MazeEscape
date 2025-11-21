package phase2.Entity;

import phase2.Tile.Tile;
import phase2.UI.GamePanel;
import phase2.UI.KeyHandler;
import phase2.game.combat.DamageSource;
import phase2.game.stats.Stats;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Player extends Entity{
    // Constants
    private static final int PLAYER_MAX_HEALTH = 100;
    private static final int PLAYER_ATTACK = 20;
    private static final int PLAYER_DEFENSE = 5;
    private static final int PLAYER_SPEED = 4;
    private static final int ATK_DURATION = 20;
    private static final int TRAP_DAMAGE_FLASH = 15;
    
    private final KeyHandler keyH;
    public final int screenX;
    public final int screenY;

    private Image atkUp, atkDown, atkLeft, atkRight;
    private boolean isAttacking = false;
    private int atkCounter = 0;
    private boolean critBuffActive = false;
    private int critBuffTimer = 0;
    private double prevCritChance;

    private final Map<String, Integer> inventory = new HashMap<>();
    private final Random random = new Random();

    /**
     * Create a player instance
     * @param gp GamePanel instance
     * @param keyH KeyHandler for input
     * @throws IllegalArgumentException if gp or keyH is null
     */
    public Player(GamePanel gp, KeyHandler keyH) {
        super(PLAYER_MAX_HEALTH, new Stats(PLAYER_ATTACK, PLAYER_DEFENSE));
        
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
    }

    public void setDefaultValues() {
        worldX = gp.tileSize; //spawn x
        worldY = gp.tileSize * 4; //spawn y
        speed = PLAYER_SPEED;
        direction = "down";
    }

    public void getPlayerImage() {
        try{
            //If you find a better way to do this, then you can mess around with it, but this is the best I could research into this
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            up = toolkit.getImage(gp.getResourceAsImage("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_run_up_anim.gif"));
            down = toolkit.getImage(gp.getResourceAsImage("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_run_down_anim.gif"));
            right = toolkit.getImage(gp.getResourceAsImage("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_run_right_anim.gif"));
            left = toolkit.getImage(gp.getResourceAsImage("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_run_left_anim.gif"));

            atkUp = toolkit.getImage(gp.getResourceAsImage("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_attack_up_anim.gif"));
            atkDown = toolkit.getImage(gp.getResourceAsImage("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_attack_down_anim.gif"));
            atkLeft = toolkit.getImage(gp.getResourceAsImage("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_attack_left_anim.gif"));
            atkRight = toolkit.getImage(gp.getResourceAsImage("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_attack_right_anim.gif"));

            MediaTracker tracker = new MediaTracker(new java.awt.Canvas());
            tracker.addImage(up, 0);
            tracker.addImage(down, 1);
            tracker.addImage(right, 2);
            tracker.addImage(left, 3);

            tracker.addImage(atkUp, 4);
            tracker.addImage(atkDown, 5);
            tracker.addImage(atkLeft, 6);
            tracker.addImage(atkRight, 7);
            tracker.waitForAll();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void update() {

        if(keyH.wPressed) {
            direction = "up";

        } else if (keyH.sPressed) {
            direction = "down";

        } else if (keyH.aPressed) {
            direction = "left";

        } else if (keyH.dPressed){
            direction = "right";
        }
        int playerCol = (worldX + collisionArea.x) /gp.tileSize;
        int playerRow = (worldY + collisionArea.y)/gp.tileSize;
        Tile curTile = gp.tileManager.getTile(playerCol, playerRow);
        if (curTile != null && curTile.isTrap) {
            trapDamageHandler(curTile);
        }
        collisionOn = false;
        gp.checkCollision.checkTile(this);
        if (keyH.wPressed || keyH.sPressed || keyH.aPressed || keyH.dPressed) {
            if (!collisionOn) {
                switch (direction) {
                    case "up" -> worldY -= speed;
                    case "down" -> worldY += speed;
                    case "left" -> worldX -= speed;
                    case "right" -> worldX += speed;
                }
            }
            if (gp.droppedKey != null && !gp.droppedKey.collected) {
                Rectangle playerHitBox = new Rectangle(worldX + collisionArea.x,
                        worldY + collisionArea.y, collisionArea.width, collisionArea.height);
                Rectangle keyHitBox = new Rectangle(gp.droppedKey.worldX,
                        gp.droppedKey.worldY, gp.tileSize, gp.tileSize);

                if (playerHitBox.intersects(keyHitBox)) {
                    gp.droppedKey.collected = true;
                    addItem("key");
                }
            }
        }

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
        if (isAttacking) {
            atkCounter++;
            if (atkCounter > ATK_DURATION) {
                isAttacking = false;
                atkCounter = 0;
            }
        }
        if (critBuffActive) {
            critBuffTimer--;
            if (critBuffTimer <= 0) {
                critBuffActive = false;
                stats.setCritChance(prevCritChance);
            }
        }
        if (playerCol == 48 && playerRow == 47) {
            if (hasItem("key")) {
                gp.finalScore = (int) (health.getHealthPercentage() * 1000);
                gp.gameState = GamePanel.GameState.GAME_WON;
            }
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
        Image image;
        int drawWidth = gp.tileSize;
        int drawHeight = gp.tileSize;
        int drawX = screenX;
        int drawY = screenY;

        // Use attack animation if attacking, otherwise use movement animation
        if(isAttacking) {
            image = switch (direction) {
                case "up" -> atkUp;
                case "down" -> atkDown;
                case "left" -> atkLeft;
                case "right" -> atkRight;
                default -> down;
            };

            // Position sprite so sword swings in front of character based on direction
            switch (direction) {
                case "up":
                    // Up/down: 3 tiles wide, 2 tiles tall
                    drawWidth = gp.tileSize * 3;
                    drawHeight = gp.tileSize * 2;
                    drawX = screenX - gp.tileSize;
                    drawY = screenY - gp.tileSize;
                    break;
                case "down":
                    // Up/down: 3 tiles wide, 2 tiles tall
                    drawWidth = gp.tileSize * 3;
                    drawHeight = gp.tileSize * 2;
                    drawX = screenX - gp.tileSize;
                    drawY = screenY;
                    break;
                case "left":
                    // Left/right: 2 tiles wide, 3 tiles tall (rotated aspect ratio)
                    drawWidth = gp.tileSize * 2;
                    drawHeight = gp.tileSize * 3;
                    drawX = screenX - gp.tileSize;
                    drawY = screenY - gp.tileSize;
                    break;
                case "right":
                    // Left/right: 2 tiles wide, 3 tiles tall (rotated aspect ratio)
                    drawWidth = gp.tileSize * 2;
                    drawHeight = gp.tileSize * 3;
                    drawX = screenX;
                    drawY = screenY - gp.tileSize;
                    break;
                default:
                    drawWidth = gp.tileSize * 3;
                    drawHeight = gp.tileSize * 2;
                    drawX = screenX - gp.tileSize;
                    drawY = screenY;
            }
        } else {
            image = switch (direction) {
                case "up" -> up;
                case "down" -> down;
                case "left" -> left;
                case "right" -> right;
                default -> down;
            };
        }

        if (damageFlashTimer > 0) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.setColor(Color.red);
            g2d.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            damageFlashTimer--;
        }

       if (image != null) {
            g2d.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
       }

       int barWidth = gp.tileSize;
       int barHeight = 4;
       int barX = screenX;
       int barY = screenY - barHeight -4;
       drawHealthBar(g2d, barX, barY, barWidth, barHeight);

       if(damageTextTimer > 0) {
           String message = "-" + previousDamageAmount;
           int hpStatX = screenX + gp.tileSize/2;
           int hpStatY = screenY - 10 - (30 - damageTextTimer);

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
           damageTextTimer--;
       }
    }
    @Override
    public void onDeath() {
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
        double roll = random.nextDouble();
        if (roll < 0.1) {
            prevCritChance = stats.getCritChance();
            stats.setCritChance(1.0);
            critBuffActive = true;
            critBuffTimer = 180;

            System.out.println("Crit Buff gained");
        }
    }

    public void forceCritBuff() {
        critBuffActive = true;
        critBuffTimer = 180;
        stats.setCritChance(1.0);
    }

    public boolean isCritBuffActive() {
        return critBuffActive;
    }
    public int getCritBuffTimer() {
        return critBuffTimer;
    }
}
