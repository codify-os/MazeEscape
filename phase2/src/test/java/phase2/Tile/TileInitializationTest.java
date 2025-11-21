package test.java.phase2.Tile;

import phase2.Tile.Tile;
import phase2.Tile.TileManager;
import phase2.UI.GamePanel;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class TileInitializationTest {

    @Test
    public void testTilesLoaded() {
        GamePanel gp = new GamePanel();
        TileManager tm = new TileManager(gp);

        for (int i = 0; i <= 31; i++) {
            assertNotNull(tm.tileType[i].image, "Tile " + i + " image should load");

        }
    }

    @Test
    public void testCollisionFlags() {
        GamePanel gp = new GamePanel();
        TileManager tm = new TileManager(gp);

        assertTrue(tm.tileType[1].collision);  // wall
        assertTrue(tm.tileType[5].collision);  // water
        assertFalse(tm.tileType[0].collision); // floor
    }

   @Test
   public void testTrapTileProperties() {
      GamePanel gp = new GamePanel();
      TileManager tm = new TileManager(gp);

      Tile trap = tm.tileType[31];
      assertTrue(trap.isTrap);
      assertEquals(15, trap.trapDamage);
      assertEquals(60, trap.trapCooldown);
    }
}
