package phase2.Bonus;

import java.awt.*;

public class CrystalItem {

    public int worldX, worldY;
    public boolean collected = false;

    public Image sprite;

    public CrystalItem(int worldX, int worldY, Image sprite) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.sprite = sprite;
    }
}