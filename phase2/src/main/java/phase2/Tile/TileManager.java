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
    Tile[] tileTypes; // Renamed for clarity - these are tile type definitions
    public Tile[][] mapTiles; // 2D array of actual tile instances for the map

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tileTypes = new Tile[10];
        mapTiles = new Tile[gp.maxScreenCol][gp.maxScreenRow];

        getTileImage();
        loadMap("maps/map1.txt");
    }

    public void getTileImage() {
        try {
            tileTypes[0] = new Tile();
            tileTypes[0].image = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream("tile/grass.png")));
            tileTypes[0].collision = false; // Grass is walkable

            tileTypes[1] = new Tile();
            tileTypes[1].image = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream("tile/wall.png")));
            tileTypes[1].collision = true; // Wall blocks movement

            tileTypes[2] = new Tile();
            tileTypes[2].image = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream("tile/water.png")));
            tileTypes[2].collision = true; // Water blocks movement
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

                    // Create a new Tile instance for each map position
                    mapTiles[col][row] = new Tile();
                    mapTiles[col][row].image = tileTypes[num].image;
                    mapTiles[col][row].collision = tileTypes[num].collision;
                    mapTiles[col][row].col = col;
                    mapTiles[col][row].row = row;

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
            Tile tile = mapTiles[col][row];
            g2.drawImage(tile.image, x, y, gp.tileSize, gp.tileSize, null);
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

    // Pathfinding helper methods
    public Tile getTile(int col, int row) {
        if (col >= 0 && col < gp.maxScreenCol && row >= 0 && row < gp.maxScreenRow) {
            return mapTiles[col][row];
        }
        return null;
    }

    public boolean isWalkable(int col, int row) {
        Tile tile = getTile(col, row);
        return tile != null && !tile.collision;
    }

    public void resetPathfinding() {
        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {
                mapTiles[col][row].reset();
            }
        }
    }
}
