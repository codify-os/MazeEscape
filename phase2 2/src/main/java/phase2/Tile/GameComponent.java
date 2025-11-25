package phase2.Tile;

import java.awt.*;
import java.awt.image.BufferedImage;  // <--- ADD THIS
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;


public class GameComponent {
   int x, y;       // tile coordinates
   int width, height;
   public BufferedImage image; // <-- use BufferedImage
    boolean solid;

    public GameComponent(int x, int y, int width, int height, BufferedImage image, boolean solid) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        this.solid = solid;
    }

    public void draw(Graphics2D g2, int tileSize) {
        if (image != null) {
            g2.drawImage(image, x * tileSize, y * tileSize, width * tileSize, height * tileSize, null);
        }
    }
}
