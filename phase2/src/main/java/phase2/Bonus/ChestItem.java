package phase2.Bonus;

import java.awt.*;

public class ChestItem {

    public int worldX, worldY;
    public boolean opened = false;
    public boolean hasLoot; // true = have crystals, false = empty

    public Image closedSprite;
    public Image openEmptySprite;
    public Image openFullSprite;

    public ChestItem(int worldX, int worldY,
                     Image closedSprite,
                     Image openEmptySprite,
                     Image openFullSprite,
                     boolean hasLoot) {

        this.worldX = worldX;
        this.worldY = worldY;

        this.closedSprite = closedSprite;
        this.openEmptySprite = openEmptySprite;
        this.openFullSprite = openFullSprite;

        this.hasLoot = hasLoot;
    }

    public Image getCurrentSprite() {
        if (!opened) return closedSprite;
        return hasLoot ? openFullSprite : openEmptySprite;
    }
}
