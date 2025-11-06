package phase2.Entity;

import phase2.UI.GamePanel;
import phase2.UI.KeyHandler;
import phase2.game.stats.HealthComponent;
import phase2.game.stats.Stats;

import java.awt.*;
import java.util.ArrayList;


public class Player extends Entity{
    GamePanel gp;
    KeyHandler keyH;
    private Image up, down, left, right;
    public final int screenX;
    public final int screenY;

    private Image atkUp, atkDown, atkLeft, atkRight;
    private boolean isAttacking = false;
    private int atkCounter = 0;
    private final int ATK_DURATION = 20;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.screenWidth/2 - (gp.tileSize/2);
        screenY = gp.screenHeight/2- (gp.tileSize/2);

        collisionArea = new Rectangle(8, 16 ,gp.tileSize - 16, gp.tileSize - 16);
        setDefaultValues();
        getPlayerImage();

        stats = new Stats(20, 5);
        health = new HealthComponent(100, stats.getDefense(), this);
        currentAttack = stats.createBasicAttack();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize;
        worldY = gp.tileSize*2;
        speed = 4;
        direction = "down";
    }

    public void getPlayerImage() {
        try{
            //If you find a better way to do this, then you can mess around with it, but this is the best I could research into this
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            up = toolkit.getImage(getClass().getClassLoader().getResource("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_run_up_anim.gif"));
            down = toolkit.getImage(getClass().getClassLoader().getResource("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_run_down_anim.gif"));
            right = toolkit.getImage(getClass().getClassLoader().getResource("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_run_right_anim.gif"));
            left = toolkit.getImage(getClass().getClassLoader().getResource("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_run_left_anim.gif"));

            atkUp = toolkit.getImage(getClass().getClassLoader().getResource("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_attack_up_anim.gif"));
            atkDown = toolkit.getImage(getClass().getClassLoader().getResource("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_attack_down_anim.gif"));
            atkLeft = toolkit.getImage(getClass().getClassLoader().getResource("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_attack_left_anim.gif"));
            atkRight = toolkit.getImage(getClass().getClassLoader().getResource("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_attack_right_anim.gif"));

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

    }
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
    }
    @Override
    public void onDeath() {
        System.out.println("Game Over!");
    }
}