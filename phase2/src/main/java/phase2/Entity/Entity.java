package phase2.Entity;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Entity {
    /*
     * TO-DO:
     * Implement the enemy stuff, (I'll help with this stuff, but we should be able
     * to generate at least one enemy my monday),
     * I'm purposefully ignoring the inventory stuff for now, we will make the UI
     * for it, and make it trigger on button press
     * and stuff
     */

    // shared stats
    public int x, y;
    public int speed;
    public Image up, up2, down, down2, left, left2, right, right2;
    public String direction;

    // shared methods
    public abstract void update();

    public abstract void draw(Graphics2D g2d);

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
