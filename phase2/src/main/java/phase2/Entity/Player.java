package phase2.Entity;

import UI.GamePanel;
import UI.KeyHandler;
import java.awt.*;


public class Player extends Entity{
    GamePanel gp;
    KeyHandler keyH;
    private Image up, down, left, right;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        x = 100;
        y = 100;
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
            y -= speed;
        } else if (keyH.sPressed) {
            direction = "down";
            y += speed;
        } else if (keyH.aPressed) {
            direction = "left";
            x -= speed;
        } else if (keyH.dPressed){
            direction = "right";
            x += speed;
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
        g2d.drawImage(image, x, y, gp.tileSize, gp.tileSize, gp);
    }
}