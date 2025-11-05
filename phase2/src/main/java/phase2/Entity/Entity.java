package phase2.Entity;

import java.awt.*;

public abstract class Entity {
    // shared stats
    public int worldX, worldY;
    public int speed;
    public Image up, down, left, right;
    public String direction;

    // shared methods
    public abstract void update();

    public abstract void draw(Graphics2D g2d);

    public int getWorldX() {
        return worldX;
    }

    public int getWorldY() {
        return worldY;
    }
    public Rectangle collisionArea;
    public boolean collisionOn = false;
}
