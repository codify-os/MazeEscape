package phase2.Tile;

import phase2.UI.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import static phase2.Tile.TileResources.TILE_DEFS;



public class TileManager {
    GamePanel gp;
    public Tile[] tileType;
    public int[][] mapTileNum;
    public Tile[][] mapTiles;

    // Multiple maps
    String[] maps = {"maps/map2.txt" };
    public int currentMapIndex = 0;

    // Components per map
    @SuppressWarnings("unchecked")
    public// suppress generic array warning
    ArrayList<GameComponent>[] mapComponents = (ArrayList<GameComponent>[]) new ArrayList[maps.length];

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tileType = new Tile[TILE_DEFS.length];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        mapTiles = new Tile[gp.maxWorldCol][gp.maxWorldRow];

        // Initialize component lists
        for (int i = 0; i < maps.length; i++) {
            mapComponents[i] = new ArrayList<>();
        }

        getTileImage();
        loadMap("maps/map2.txt");
    }

    public void getTileImage() {
    try {
        tileType = new Tile[TileResources.TILE_DEFS.length];

        for (int i = 0; i < TileResources.TILE_DEFS.length; i++) {
            TileDef def = TileResources.TILE_DEFS[i];
            Tile tile = new Tile();

            // Use the path as-is from TileDef, but make sure it matches the classpath
            InputStream is = getClass().getClassLoader().getResourceAsStream(def.path);
            tile.image = ImageIO.read(is);

            // Copy properties
            tile.collision    = def.collision;
            tile.spawnable    = def.spawnable;
            tile.isTrap       = def.isTrap;
            tile.trapDamage   = def.trapDamage;
            tile.trapCooldown = def.trapCooldown;

            tileType[i] = tile;
        }

    } catch (Exception e) {
        throw new RuntimeException("Failed loading tile images", e);
    }
}

    public void loadMap(String mapFilePath) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(mapFilePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)));

            int col, row = 0;

                        while (row < gp.maxWorldRow) {
                                String line = br.readLine();
                                if (line == null) {
                                        throw new RuntimeException("Map file too short: '" + mapFilePath + "' ended at row " + row + " but expected " + gp.maxWorldRow + " lines");
                                }
                                String[] numbers = line.split(" ");
                for (col = 0; col < gp.maxWorldCol; col++) {
                    int tileNum = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = tileNum;

                    // Create pathfinding tile
                    Tile baseTile = tileType[tileNum];
                    Tile newTile = new Tile(baseTile);
                    
                    newTile.col = col;
                    newTile.row = row;

                    mapTiles[col][row] = newTile;

//                    mapTiles[col][row] = new Tile();
//                    mapTiles[col][row].collision = tileType[tileNum].collision;
//                    mapTiles[col][row].col = col;
//                    mapTiles[col][row].row = row;
                }
                row++;
            }

            br.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load map: " + mapFilePath, e);
        }
    }

    // Switch to next map
    public void nextMap() {
        currentMapIndex++;
        if (currentMapIndex < maps.length) {
            loadMap(maps[currentMapIndex]);
        } else {
            System.out.println("No more maps!");
            currentMapIndex = maps.length - 1;
        }
    }

    // Add a component to current map
    public void addComponent(GameComponent component) {
        mapComponents[currentMapIndex].add(component);
    }

    // =================== NEW: Add a component to a specific map
 
    public void addComponentToMap(int mapIndex, GameComponent component) {
        if (mapIndex >= 0 && mapIndex < maps.length) {
            mapComponents[mapIndex].add(component);
        } else {
            System.out.println("Invalid map index: " + mapIndex);
        }
    }
    // ================================== Draw tiles and components ================================= //

    public void draw(Graphics2D g2) {

        for (int row = 0; row < gp.maxWorldRow; row++) {
            for (int col = 0; col < gp.maxWorldCol; col++) {
                int tileNum = mapTileNum[col][row];
                int worldX = col * gp.tileSize;
                int worldY = row * gp.tileSize;
                int screenX = worldX - gp.player.worldX + gp.player.screenX;
                int screenY = worldY - gp.player.worldY + gp.player.screenY;

                if (isWorldXLarger(worldX) && isWorldXLess(worldX)
                        && isWorldYLarger(worldY) && isWorldYLess(worldY)) {
                    g2.drawImage(tileType[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
                }
            }
        }

        // Draw components
        for (GameComponent comp : mapComponents[currentMapIndex]) {
            comp.draw(g2, gp.tileSize);
        }
    }

    private boolean isWorldXLarger(int worldX) {
        return worldX + gp.tileSize > gp.player.worldX - gp.player.screenX;
    }
    private boolean isWorldXLess(int worldX) {
        return worldX - gp.tileSize < gp.player.worldX + gp.player.screenX;
    }
    private boolean isWorldYLarger(int worldY) {
        return worldY + gp.tileSize > gp.player.worldY - gp.player.screenY;
    }
    private boolean isWorldYLess(int worldY) {
        return worldY - gp.tileSize < gp.player.worldY + gp.player.screenY;
    }


    // Pathfinding helper methods
    public Tile getTile(int col, int row) {
        if (col >= 0 && col < gp.maxWorldCol && row >= 0 && row < gp.maxWorldRow) {
            return mapTiles[col][row];
        }
        return null;
    }

    public boolean isWalkable(int col, int row) {
        Tile tile = getTile(col, row);
        return tile != null && !tile.collision;
    }

    public void resetPathfinding() {
        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                mapTiles[col][row].reset();
            }
        }
    }

    public void updateTraps() {
        for(int col = 0; col<gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                Tile tile = mapTiles[col][row];
                if (tile.isTrap && tile.trapTimer > 0) {
                    tile.trapTimer--;
                }
            }
        }
    }

    private boolean isValidSwanTile(Tile tile) {
        if (tile == null){
            return false;
        }
        return tile.spawnable;
    }

    public int[] getValidTile() {
        Random  r = new Random();

        while (true) {
            int col = r.nextInt(gp.maxWorldCol);
            int row = r.nextInt(gp.maxWorldRow);

            Tile tile = mapTiles[col][row];
            if(isValidSwanTile(tile)) {
                return new int[]{col, row};
            }
        }
    }
}
