package phase2.Tile;

import java.awt.*;
import java.awt.image.BufferedImage;  // <--- ADD THIS
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * Represents a drawable game component with position and image
 */
public class GameComponent {
   int x, y;       // tile coordinates
   int width, height;
   /** Image for this component */
   public BufferedImage image;
    boolean solid;

    /**
     * Create a game component
     * @param x X tile coordinate
     * @param y Y tile coordinate
     * @param width Width in tiles
     * @param height Height in tiles
     * @param image Component image
     * @param solid Whether component is solid
     */
    public GameComponent(int x, int y, int width, int height, BufferedImage image, boolean solid) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        this.solid = solid;
    }

    /**
     * Draw this component
     * @param g2 Graphics context
     * @param tileSize Size of each tile in pixels
     */
    public void draw(Graphics2D g2, int tileSize) {
        if (image != null) {
            g2.drawImage(image, x * tileSize, y * tileSize, width * tileSize, height * tileSize, null);
        }
    }
}
