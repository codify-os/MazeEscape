package test.java.phase2.Tile;

import phase2.Tile.TileManager;
import phase2.Tile.GameComponent;
import phase2.Tile.Tile;
import phase2.UI.GamePanel;


import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.BeforeAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TileManagerTest {
   public GamePanel gp;
   public TileManager tm;

    @BeforeAll
    public void setup() {
        gp = new GamePanel();
        tm = new TileManager(gp);
    }


    @Test
    public void testMapArrayFilled() {
        GamePanel gp = new GamePanel();
        TileManager tm = new TileManager(gp);

        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                assertTrue(tm.mapTileNum[col][row] >= 0, "Tile index must be >= 0");
                assertNotNull(tm.mapTiles[col][row], "Tile instance must be created");

            }
        }
    }

    @Test
    public void testAllInitializedTileSlotsNotNull() {
    for (int i = 0; i < 40; i++) {
        if (tm.tileType[i] != null) {
            assertNotNull(
                tm.tileType[i].image,
                "Tile image at index " + i + " should not be null"
            );
        }
    }
}


    @Test
    public void testTileCopyCollision() {
        GamePanel gp = new GamePanel();
        TileManager tm = new TileManager(gp);

        int col = 2, row = 2;
        int tileNum = tm.mapTileNum[col][row];

        assertEquals(tm.tileType[tileNum].collision,
                     tm.mapTiles[col][row].collision);
    }

    @Test
    public void testCollisionCopiedCorrectlyFromTileType() {
        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {

                int id = tm.mapTileNum[col][row];
                boolean expected = tm.tileType[id].collision;
                boolean actual   = tm.mapTiles[col][row].collision;

                assertEquals(expected, actual,
                    "Collision mismatch at [" + col + "," + row + "]");
            }
        }
    }


    @Test
    public void testMapLoadsCorrectSize() {
        assertEquals(gp.maxWorldCol, tm.mapTileNum.length);
        assertEquals(gp.maxWorldRow, tm.mapTileNum[0].length);
    }

    @Test
    public void testMapCopiesTileCollisionCorrectly() {
        int x = 5, y = 5;
        int tileNum = tm.mapTileNum[x][y];
        assertEquals(
                tm.tileType[tileNum].collision,
                tm.mapTiles[x][y].collision
        );
    }

   @Test
   public void testTileCoordinatesStoredCorrectly() {
      int x = 4, y = 7;
      assertEquals(x, tm.mapTiles[x][y].col);
      assertEquals(y, tm.mapTiles[x][y].row);
   }

    @Test
    public void testMapCopiesTrapAttributes() {
        int x = 3, y = 3;
        int tileNum = tm.mapTileNum[x][y];

        Tile base = tm.tileType[tileNum];
        Tile copied = tm.mapTiles[x][y];

        assertEquals(base.isTrap, copied.isTrap);
        assertEquals(base.trapDamage, copied.trapDamage);
        assertEquals(base.trapCooldown, copied.trapCooldown);

      
    }

    @Test
    public void testAddComponentStoresInsideCorrectMap() {
    int index = tm.currentMapIndex;
    int before = tm.mapComponents[index].size();

    GameComponent dummy = new GameComponent(0, 0, 1, 1, null, false);
    tm.addComponent(dummy);

    assertEquals(before + 1, tm.mapComponents[index].size());
}


    @Test
    public void testAddComponentToMapValidIndex() {
    int index = tm.currentMapIndex;
    int before = tm.mapComponents[index].size();

    GameComponent dummy = new GameComponent(0, 0, 1, 1, null, false);
    tm.addComponentToMap(index, dummy);

    assertEquals(before + 1, tm.mapComponents[index].size());
}


    @Test
    public void testAddComponentToMapInvalidIndexDoesNotThrow() {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        GameComponent dummy = new GameComponent(0, 0, 1, 1, img, false);

        // Should not crash
        tm.addComponentToMap(999, dummy);
    }

    @Test
    public void testNextMapDoesNotCrash() {
        int oldIndex = tm.currentMapIndex;

        tm.nextMap();

        assertTrue(tm.currentMapIndex >= oldIndex);
    }

    private boolean invokeBool(TileManager tm, String methodName, int arg) {
    try {
        var m = TileManager.class.getDeclaredMethod(methodName, int.class);
        m.setAccessible(true);
        return (boolean) m.invoke(tm, arg);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}


    @Test
    public void testTileVisibilityWithinRange() {
    // Use whatever player position the constructor initializes
    int px = gp.player.worldX;
    int py = gp.player.worldY;

    int tileX = px; // exactly aligned
    int tileY = py;

    assertTrue(invokeBool(tm, "isWorldXLarger", tileX));
    assertTrue(invokeBool(tm, "isWorldXLess", tileX));
    assertTrue(invokeBool(tm, "isWorldYLarger", tileY));
    assertTrue(invokeBool(tm, "isWorldYLess", tileY));
}
    @Test
    public void testTileVisibilityOutsideRange() {
    gp.player.worldX = 100;
    gp.player.worldY = 100;

    int tileX = 1000;
    int tileY = 1000;

    boolean visible =  
       invokeBool(tm, "isWorldXLarger", tileX) &&
       invokeBool(tm, "isWorldXLess",   tileX) &&
       invokeBool(tm, "isWorldYLarger", tileY) &&
       invokeBool(tm, "isWorldYLess",   tileY);

    assertFalse(visible);
    }

    @Test
    public void testWalkableTileReturnsTrue() {
        Tile tile = tm.mapTiles[5][5];
        tile.collision = false;

        assertTrue(tm.isWalkable(5, 5),
                "Tile with collision=false should be walkable");
    }

    @Test
    public void testCollisionTileReturnsFalse() {
        Tile tile = tm.mapTiles[6][6];
        tile.collision = true;

        assertFalse(tm.isWalkable(6, 6),
                "Tile with collision=true should NOT be walkable");
    }

    @Test
    public void testOutOfBoundsReturnsFalse() {
        assertFalse(tm.isWalkable(999, 999),
                "Out-of-bounds should return false");
    }

    

}
