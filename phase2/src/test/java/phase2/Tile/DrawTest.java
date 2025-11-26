package phase2.Tile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import phase2.Tile.GameComponent;
import phase2.Tile.TileManager;
import phase2.UI.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

public class DrawTest {

    GamePanel gp;
    TileManager tm;
    BufferedImage renderSurface;
    Graphics2D g2;

    @BeforeEach
    public void setup() {

        gp = new GamePanel();
        tm = new TileManager(gp);

        renderSurface = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
        g2 = renderSurface.createGraphics();

        // move player center world position
        gp.player.worldX = 100;
        gp.player.worldY = 100;

        // give tile 0 an image so it can be detected
        tm.tileType[0].image = new BufferedImage(gp.tileSize, gp.tileSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gTile = tm.tileType[0].image.createGraphics();
        gTile.setColor(Color.RED);
        gTile.fillRect(0, 0, gp.tileSize, gp.tileSize);
        gTile.dispose();
    }

    @Test
    public void testDrawVisibleTileRendersPixel() {

        // tile at (2,2) world position — close to the player
        tm.mapTileNum[2][2] = 0;

        tm.draw(g2);

        boolean foundRedPixel = false;

        for (int x = 0; x < renderSurface.getWidth(); x++) {
            for (int y = 0; y < renderSurface.getHeight(); y++) {
                int rgb = renderSurface.getRGB(x, y);
                if ((rgb & 0x00FF0000) != 0) {  // contains red
                    foundRedPixel = true;
                    break;
                }
            }
        }

        assertTrue(foundRedPixel, "Expected visible tile to be drawn.");
    }

    @Test
    public void testDrawComponentIsInvoked() {
        // Create a component that paints BLUE so we can detect it
        GameComponent comp = new GameComponent(
                1, 1, 1, 1,
                new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB),
                false
        );
        Graphics2D gComp = comp.image.createGraphics();
        gComp.setColor(Color.BLUE);
        gComp.fillRect(0, 0, 32, 32);
        gComp.dispose();

        int initialSize = tm.mapComponents[tm.currentMapIndex].size();
        tm.addComponent(comp);

        tm.draw(g2);

        boolean foundBlue = false;

        for (int x = 0; x < renderSurface.getWidth(); x++) {
            for (int y = 0; y < renderSurface.getHeight(); y++) {
                int rgb = renderSurface.getRGB(x, y);
                if ((rgb & 0x000000FF) != 0) { // Blue channel
                    foundBlue = true;
                    break;
                }
            }
            if (foundBlue) break;
        }

        assertEquals(initialSize + 1,
                tm.mapComponents[tm.currentMapIndex].size(),
                "Component should be added.");

        assertTrue(foundBlue, "Component.draw() should draw a blue pixel.");
    }
}
