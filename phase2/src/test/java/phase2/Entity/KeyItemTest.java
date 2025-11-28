package phase2.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import phase2.UI.GamePanel;

import static org.junit.jupiter.api.Assertions.*;

public class KeyItemTest {
    private KeyItem keyItem;
    private GamePanel mockGamePanel;

    @BeforeEach
    public void setUp() {
        keyItem = new KeyItem(100, 200);
        mockGamePanel = new GamePanel();
    }

    @Test
    public void testKeyItemCreation() {
        assertEquals(100, keyItem.worldX);
        assertEquals(200, keyItem.worldY);
        assertFalse(keyItem.collected);
    }

    @Test
    public void testKeyItemInitiallyNotCollected() {
        KeyItem key = new KeyItem(0, 0);
        assertFalse(key.collected);
    }

    @Test
    public void testKeyItemCanBeMarkedCollected() {
        keyItem.collected = true;
        assertTrue(keyItem.collected);
    }

    @Test
    public void testKeyItemPosition() {
        KeyItem key = new KeyItem(500, 600);
        assertEquals(500, key.worldX);
        assertEquals(600, key.worldY);
    }

    @Test
    public void testKeyItemLoadImageDoesNotThrowException() {
        assertDoesNotThrow(() -> keyItem.loadKeyImage());
    }

    @Test
    public void testMultipleKeyItemsHaveIndependentState() {
        KeyItem key1 = new KeyItem(100, 100);
        KeyItem key2 = new KeyItem(200, 200);
        
        key1.collected = true;
        
        assertTrue(key1.collected);
        assertFalse(key2.collected);
    }

    @Test
    public void testKeyItemNegativeCoordinates() {
        KeyItem key = new KeyItem(-50, -100);
        assertEquals(-50, key.worldX);
        assertEquals(-100, key.worldY);
    }

    @Test
    public void testKeyItemZeroCoordinates() {
        KeyItem key = new KeyItem(0, 0);
        assertEquals(0, key.worldX);
        assertEquals(0, key.worldY);
    }

    @Test
    public void testKeyItemLargeCoordinates() {
        KeyItem key = new KeyItem(10000, 10000);
        assertEquals(10000, key.worldX);
        assertEquals(10000, key.worldY);
    }
}
