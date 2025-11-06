package phase2.Entity;

import phase2.UI.GamePanel;
import phase2.UI.KeyHandler;
import phase2.game.combat.CombatManager;
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

            MediaTracker tracker = new MediaTracker(new java.awt.Canvas());
            tracker.addImage(up, 0);
            tracker.addImage(down, 1);
            tracker.addImage(right, 2);
            tracker.addImage(left, 3);
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
        if (keyH.spacePressed) {
            for(Enemy e: new ArrayList<>(gp.enemies)) {
                if (isInRange(e)) {
                    System.out.println("The player is attacking the enemy");
                    attack(e);
                }
            }
        }

    }
    public void draw(Graphics2D g2d){

       Image image = switch (direction) {
            case "up" -> up;
            case "down" -> down;
            case "left" -> left;
            case "right" -> right;
            default -> null;
       };
       if (damageFlashTimer > 0) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.setColor(Color.red);
            g2d.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            damageFlashTimer --;
       }
       g2d.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, gp);
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