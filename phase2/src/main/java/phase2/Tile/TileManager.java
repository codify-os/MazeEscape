package phase2.Tile;

import phase2.UI.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class TileManager {
    GamePanel gp;
    Tile[] tile;
    int[][] mapTileNum;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[10];
        mapTileNum = new int[gp.maxScreenCol][gp.maxScreenRow];

        getTileImage();
        loadMap("maps/map1.txt"); // the location of the map file its stored as 16x12 text file
    }

    public void getTileImage() {
        try {
            /*
             * TO-DO:
             * Find a way to take out the tile images from the tileset they gave from the
             * Top_Down_Adventure_Pack assets
             *
             * Create new styles of rooms, I hope this format is easy enough to follow to
             * branch out and create more rooms
             * Also we need to enable collision with walls, but I am not sure on how to do
             * it
             *
             */

            tile[0] = new Tile();
            tile[0].image = ImageIO
                    .read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("tile/grass.png")));

            tile[1] = new Tile();
            tile[1].image = ImageIO
                    .read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("tile/wall.png")));

            tile[2] = new Tile();
            tile[2].image = ImageIO
                    .read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("tile/water.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMap(String mapFilePath) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(mapFilePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)));

            int col = 0;
            int row = 0;
            while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
                String line = br.readLine();

                while (col < gp.maxScreenCol) {
                    String[] numbers = line.split(" ");
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                    col++;
                }

                if (col == gp.maxScreenCol) {
                    col = 0;
                    row++;
                }

            }
            br.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void draw(Graphics2D g2) {
        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;

        while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
            int tileNum = mapTileNum[col][row];
            g2.drawImage(tile[tileNum].image, x, y, gp.tileSize, gp.tileSize, null);
            col++;
            x += gp.tileSize;

            if (col == gp.maxScreenCol) {
                col = 0;
                x = 0;
                row++;
                y += gp.tileSize;
            }
        }
    }
}
