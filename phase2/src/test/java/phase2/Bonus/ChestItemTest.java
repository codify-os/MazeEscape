package phase2.Bonus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class ChestItemTest {

    private BufferedImage closedImage;
    private BufferedImage openEmptyImage;
    private BufferedImage openFullImage;

    @BeforeEach
    void setUp() {
        // Dummy images to pass into ChestItem constructor
        closedImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        openEmptyImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        openFullImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    }

    @Test
    void testChestInitialization() {
        ChestItem chest = new ChestItem(10, 20, closedImage, openEmptyImage, openFullImage, true);

        assertEquals(10, chest.worldX);
        assertEquals(20, chest.worldY);
        assertFalse(chest.opened);
        assertTrue(chest.hasLoot);
        assertEquals(closedImage, chest.closedSprite);
        assertEquals(openEmptyImage, chest.openEmptySprite);
        assertEquals(openFullImage, chest.openFullSprite);
    }

    @Test
    void testGetCurrentSprite_NotOpened() {
        ChestItem chest = new ChestItem(0, 0, closedImage, openEmptyImage, openFullImage, true);
        assertEquals(closedImage, chest.getCurrentSprite(), "Closed chest should return closedSprite");
    }

    @Test
    void testGetCurrentSprite_OpenedWithLoot() {
        ChestItem chest = new ChestItem(0, 0, closedImage, openEmptyImage, openFullImage, true);
        chest.opened = true;
        assertEquals(openFullImage, chest.getCurrentSprite(), "Opened chest with loot should return openFullSprite");
    }

    @Test
    void testGetCurrentSprite_OpenedWithoutLoot() {
        ChestItem chest = new ChestItem(0, 0, closedImage, openEmptyImage, openFullImage, false);
        chest.opened = true;
        assertEquals(openEmptyImage, chest.getCurrentSprite(), "Opened chest without loot should return openEmptySprite");
    }
}
