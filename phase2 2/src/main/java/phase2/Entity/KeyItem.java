package main.java.phase2.Entity;

import phase2.UI.GamePanel;

import java.awt.*;

public class KeyItem {
    public int worldX, worldY;
    public boolean collected = false;

    private Image keyImage;

    public KeyItem(int worldX, int worldY) {
        this.worldX = worldX;
        this.worldY = worldY;
    }

    public void loadKeyImage() {
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();

            keyImage = toolkit.getImage(getClass().getClassLoader().getResource("Top_Down_Adventure_Pack_v.1.0/Props_Items_(animated)/key_item_anim.gif"));

            MediaTracker tracker = new MediaTracker(new java.awt.Canvas());
            tracker.addImage(keyImage, 0);
            tracker.waitForAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2d, GamePanel gp) {
        if (!collected) {
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            g2d.drawImage(keyImage, screenX, screenY, gp.tileSize, gp.tileSize, gp);
        }
    }
}
