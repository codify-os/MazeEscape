package phase2.Bonus;

import java.awt.*;

/**
 * Represents a chest placed in the game world.
 * A chest can be opened and may contain loot (such as crystals).
 * Its visual appearance changes based on its state.
 */
public class ChestItem {

    /** The X coordinate of the chest in the world. */
    public int worldX;

    /** The Y coordinate of the chest in the world. */
    public int worldY;

    /** Whether the chest has been opened. */
    public boolean opened = false;

    /** Whether the chest contains loot (true = has loot, false = empty). */
    public boolean hasLoot;

    /** Sprite displayed when the chest is closed. */
    public Image closedSprite;

    /** Sprite displayed when the chest is open but empty. */
    public Image openEmptySprite;

    /** Sprite displayed when the chest is open and contains loot. */
    public Image openFullSprite;

    /**
     * Creates a new ChestItem.
     *
     * @param worldX          The X coordinate in the world.
     * @param worldY          The Y coordinate in the world.
     * @param closedSprite    Sprite for the closed chest.
     * @param openEmptySprite Sprite for the opened chest with no loot.
     * @param openFullSprite  Sprite for the opened chest with loot.
     * @param hasLoot         Whether the chest contains loot.
     */
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

    /**
     * Returns the sprite corresponding to the current chest state.
     *
     * @return The correct sprite (closed, open-empty, or open-with-loot).
     */
    public Image getCurrentSprite() {
        if (!opened) return closedSprite;
        return hasLoot ? openFullSprite : openEmptySprite;
    }
}
