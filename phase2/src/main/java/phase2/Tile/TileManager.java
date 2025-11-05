package phase2.Tile;

import phase2.UI.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

public class TileManager {
    GamePanel gp;
    public Tile[] tileType;
    public int[][] mapTileNum;
    Tile[][] mapTiles;

    // Multiple maps
    String[] maps = {"maps/map2.txt" };
    int currentMapIndex = 0;

    // Components per map
    @SuppressWarnings("unchecked") // suppress generic array warning
    ArrayList<GameComponent>[] mapComponents = (ArrayList<GameComponent>[]) new ArrayList[maps.length];

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tileType = new Tile[40];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        mapTiles = new Tile[gp.maxWorldCol][gp.maxWorldRow];

        // Initialize component lists
        for (int i = 0; i < maps.length; i++) {
            mapComponents[i] = new ArrayList<>();
        }

        getTileImage();
        loadMap(maps[currentMapIndex]);
    }

    public void getTileImage() {
        try {
            tileType[0] = new Tile();
            tileType[0].image = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/floor_plain.png")));

            tileType[1] = new Tile();
            tileType[1].image = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/wall_center.png")));
            tileType[1].collision = true;

            tileType[2] = new Tile();
            tileType[2].image = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/column_wall.png")));

            tileType[3] = new Tile();
            tileType[3].image = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(1).png")));

            tileType[4] = new Tile();
            tileType[4].image = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(45).png")));

            tileType[5] = new Tile();
            tileType[5].image = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("Top_Down_Adventure_Pack_v.1.0/Tiles_(animated)/Overworld/water_tile_anim.gif")));
            tileType[5].collision = true;

            tileType[6] = new Tile();
            tileType[6].image = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("Top_Down_Adventure_Pack_v.1.0/Tiles_(animated)/Overworld/edge_water_tile_anim_strip_8.png")));
            tileType[6].collision = true;

            tileType[7] = new Tile();
            tileType[7].image = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(6).png")));

            tileType[8] = new Tile();
            tileType[8].image = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("Top_Down_Adventure_Pack_v.1.0/Tiles_(animated)/Overworld/edge_water_tile_anim_strip_(1).png")));
            tileType[8].collision = true;

            tileType[9] = new Tile();
            tileType[9].image = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(39).png")));

            tileType[10] = new Tile();
            tileType[10].image = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(14).png")));

            tileType[11] = new Tile();
            tileType[11].image =  ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/Wall_inner_e.png")));
            tileType[11].collision = true;

            tileType[12] = new Tile();
            tileType[12].image =  ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/Wall_inner_ne.png")));
            tileType[12].collision = true;

            tileType[13] = new Tile();
            tileType[13].image =  ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/Wall_inner_nw.png")));
            tileType[13].collision = true;

            tileType[14] = new Tile();
            tileType[14].image =  ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/Wall_inner_se.png")));
            tileType[14].collision = true;

            tileType[15] = new Tile();
            tileType[15].image =  ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/Wall_inner_sw.png")));
            tileType[15].collision = true;

            tileType[16] = new Tile();
            tileType[16].image =  ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/Wall_inner_w.png")));
            tileType[16].collision = true;

            tileType[17] = new Tile();
            tileType[17].image =  ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/Wall_outer_e.png")));
            tileType[17].collision = true;

            tileType[18] = new Tile();
            tileType[18].image =  ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/Wall_outer_e2.png")));
            tileType[18].collision = true;

            tileType[19] = new Tile();
            tileType[19].image =  ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/Wall_outer_n.png")));
            tileType[19].collision = true;

            tileType[20] = new Tile();
            tileType[20].image =  ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/Wall_outer_ne.png")));
            tileType[20].collision = true;

            tileType[21] = new Tile();
            tileType[21].image =  ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/Wall_outer_nw.png")));
            tileType[21].collision = true;

            tileType[22] = new Tile();
            tileType[22].image =  ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/Wall_outer_se.png")));
            tileType[22].collision = true;

            tileType[23] = new Tile();
            tileType[23].image =  ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/Wall_outer_sw.png")));
            tileType[23].collision = true;

            tileType[24] = new Tile();
            tileType[24].image =  ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/Wall_outer_w.png")));
            tileType[24].collision = true;

            tileType[25] = new Tile();
            tileType[25].image =  ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/Wall_outer_w.png")));
            tileType[25].collision = true;

            tileType[26] = new Tile();
            tileType[26].image =  ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("tile/blackTile.png")));
            tileType[26].collision = true;

            tileType[27] = new Tile();
            tileType[27].image =  ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/Wall_outer_n_reversed.png")));
            tileType[27].collision = true;

            tileType[28] = new Tile();
            tileType[28].image =  ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/chest_golden_closed.png")));
            tileType[28].collision = true;

            tileType[29] = new Tile();
            tileType[29].image =  ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/Wall_inner_sw_reversed.png")));
            tileType[29].collision = true;

            tileType[30] = new Tile();
            tileType[30].image =  ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().
                            getResourceAsStream("0x72_16x16DungeonTileset.v5/0x72_16x16DungeonTileset.v5/items/Wall_inner_se_reversed.png")));
            tileType[30].collision = true;


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMap(String mapFilePath) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(mapFilePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)));

            int col, row = 0;

            while (row < gp.maxWorldRow) {
                String line = br.readLine();
                String[] numbers = line.split(" ");
                for (col = 0; col < gp.maxWorldCol; col++) {
                    int tileNum = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = tileNum;

                    // Create pathfinding tile
                    mapTiles[col][row] = new Tile();
                    mapTiles[col][row].collision = tileType[tileNum].collision;
                    mapTiles[col][row].col = col;
                    mapTiles[col][row].row = row;
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
    // ===================
    public void addComponentToMap(int mapIndex, GameComponent component) {
        if (mapIndex >= 0 && mapIndex < maps.length) {
            mapComponents[mapIndex].add(component);
        } else {
            System.out.println("Invalid map index: " + mapIndex);
        }
    }
    // ==============================================================================

    // Draw tiles and components
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


    // =================== UPDATED: Load components ===================

    public void loadComponents() {
        try {
            // Example component for first map
            // BufferedImage chestImg = ImageIO.read(Objects.requireNonNull(
            // getClass().getClassLoader().getResourceAsStream(
            // "Top_Down_Adventure_Pack_v.1.0/Tiles_(animated)/Overworld/edge_water_tile_anim_strip_8.png")));
            // GameComponent chest = new GameComponent(1, 3, 1, 1, chestImg, true);
            // addComponentToMap(0, chest); // Added specifically to map 0 (first map)

            // New component for second map (your requested tile)
            BufferedImage secondMapImg = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(11).png")));
            GameComponent secondMapTile = new GameComponent(1, 9, 1, 1, secondMapImg, true);
            addComponentToMap(1, secondMapTile); // Added specifically to map 1 (second map)

            BufferedImage stoneMapImg = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(11).png")));
            GameComponent stoneMapTile = new GameComponent(3, 10, 1, 1, stoneMapImg, true);
            addComponentToMap(1, stoneMapTile); // Added specifically to map 1 (second map)

            BufferedImage stoneMapImg1 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(11).png")));
            GameComponent stoneMapTile1 = new GameComponent(2, 9, 1, 1, stoneMapImg1, true);
            addComponentToMap(1, stoneMapTile1); // Added specifically to map 1 (second map)

            BufferedImage stoneMapImg2 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(11).png")));
            GameComponent stoneMapTile2 = new GameComponent(1, 8, 1, 1, stoneMapImg2, true);
            addComponentToMap(1, stoneMapTile2); // Added specifically to map 1 (second map)

            BufferedImage stoneMapImg3 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(11).png")));
            GameComponent stoneMapTile3 = new GameComponent(2, 3, 1, 1, stoneMapImg3, true);
            addComponentToMap(1, stoneMapTile3); // Added specifically to map 1 (second map)

            BufferedImage stoneMapImg4 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(11).png")));
            GameComponent stoneMapTile4 = new GameComponent(9, 11, 1, 1, stoneMapImg4, true);
            addComponentToMap(1, stoneMapTile4); // Added specifically to map 1 (second map)

            BufferedImage stoneMapImg5 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(11).png")));
            GameComponent stoneMapTile5 = new GameComponent(1, 7, 1, 1, stoneMapImg5, true);
            addComponentToMap(1, stoneMapTile5); // Added specifically to map 1 (second map)

            BufferedImage stoneMapImg6 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(11).png")));
            GameComponent stoneMapTile6 = new GameComponent(0, 0, 1, 1, stoneMapImg6, true);
            addComponentToMap(1, stoneMapTile6); // Added specifically to map 1 (second map)

            BufferedImage stoneMapImg7 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(11).png")));
            GameComponent stoneMapTile7 = new GameComponent(8, 3, 1, 1, stoneMapImg7, true);
            addComponentToMap(1, stoneMapTile7); // Added specifically to map 1 (second map)

            BufferedImage stoneMapImg8 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(11).png")));
            GameComponent stoneMapTile8 = new GameComponent(0, 2, 1, 1, stoneMapImg8, true);
            addComponentToMap(1, stoneMapTile8); // Added specifically to map 1 (second map)

            BufferedImage tree = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(3).png")));
            GameComponent streeMapTile = new GameComponent(4, 7, 1, 1, tree, true);
            addComponentToMap(1, streeMapTile); // Added specifically to map 1 (second map)

            BufferedImage tree1 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(3).png")));
            GameComponent streeMapTile1 = new GameComponent(4, 2, 1, 1, tree1, true);
            addComponentToMap(1, streeMapTile1); // Added specifically to map 1 (second map)

            BufferedImage tree2 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(2).png")));
            GameComponent streeMapTile2 = new GameComponent(3, 0, 1, 1, tree2, true);
            addComponentToMap(1, streeMapTile2); // Added specifically to map 1 (second map)

            BufferedImage tree3 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(2).png")));
            GameComponent streeMapTile3 = new GameComponent(4, 8, 1, 1, tree3, true);
            addComponentToMap(1, streeMapTile3); // Added specifically to map 1 (second map)

            BufferedImage tree4 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(3).png")));
            GameComponent streeMapTile4 = new GameComponent(12, 0, 1, 1, tree4, true);
            addComponentToMap(1, streeMapTile4); // Added specifically to map 1 (second map)

            BufferedImage tree5 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(3).png")));
            GameComponent streeMapTile5 = new GameComponent(11, 0, 1, 1, tree5, true);
            addComponentToMap(1, streeMapTile5); // Added specifically to map 1 (second map)

            BufferedImage tree8 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(3).png")));
            GameComponent streeMapTile8 = new GameComponent(4, 0, 1, 1, tree8, true);
            addComponentToMap(1, streeMapTile8); // Added specifically to map 1 (second map)

            BufferedImage tree7 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(32).png")));
            GameComponent streeMapTile7 = new GameComponent(5, 0, 1, 1, tree7, true);
            addComponentToMap(1, streeMapTile7); // Added specifically to map 1 (second map)

            BufferedImage tree6 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(2).png")));
            GameComponent streeMapTile6 = new GameComponent(11, 1, 1, 1, tree6, true);
            addComponentToMap(1, streeMapTile6); // Added specifically to map 1 (second map)

            BufferedImage tree9 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(2).png")));
            GameComponent streeMapTile9 = new GameComponent(10, 0, 1, 1, tree9, true);
            addComponentToMap(1, streeMapTile9); // Added specifically to map 1 (second map)

            BufferedImage tree10 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(37).png")));
            GameComponent streeMapTile10 = new GameComponent(8, 1, 1, 1, tree10, true);
            addComponentToMap(1, streeMapTile10); // Added specifically to map 1 (second map)

            BufferedImage tree11 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(37).png")));
            GameComponent streeMapTile11 = new GameComponent(8, 2, 1, 1, tree11, true);
            addComponentToMap(1, streeMapTile11); // Added specifically to map 1 (second map)

            BufferedImage house = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/Overworld_Tileset-ezgif.com-crop.png")));
            GameComponent houseTile = new GameComponent(6, 0, 2, 2, house, true);
            addComponentToMap(1, houseTile); // Added specifically to map 1 (second map)

            BufferedImage lamp = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(35).png")));
            GameComponent lampTile = new GameComponent(5, 1, 1, 1, lamp, true);
            addComponentToMap(1, lampTile); // Added specifically to map 1 (second map)

            BufferedImage lamp1 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(35).png")));
            GameComponent lampTile1 = new GameComponent(5, 2, 1, 1, lamp1, true);
            addComponentToMap(1, lampTile1); // Added specifically to map 1 (second map)

            BufferedImage camp = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(14).png")));
            GameComponent campTile = new GameComponent(6, 2, 1, 1, camp, true);
            addComponentToMap(1, campTile); // Added specifically to map 1 (second map)

            BufferedImage trap = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/Dungeon_Tileset-ezgif.com-crop(5).png")));
            GameComponent trapTile = new GameComponent(12, 1, 1, 1, trap, true);
            addComponentToMap(1, trapTile); // Added specifically to map 1 (second map)

            BufferedImage thing = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/Dungeon_Tileset-ezgif.com-crop(4).png")));
            GameComponent thingTile = new GameComponent(14, 1, 1, 1, thing, true);
            addComponentToMap(1, thingTile); // Added specifically to map 1 (second map)

            BufferedImage direction = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(5).png")));
            GameComponent directionTile = new GameComponent(5, 5, 1, 1, direction, true);
            addComponentToMap(1, directionTile); // Added specifically to map 1 (second map)

            BufferedImage direction1 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(5).png")));
            GameComponent directionTile1 = new GameComponent(11, 3, 1, 1, direction1, true);
            addComponentToMap(1, directionTile1); // Added specifically to map 1 (second map)

            BufferedImage flower = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(7).png")));
            GameComponent flowerTile = new GameComponent(3, 1, 2, 1, flower, true);
            addComponentToMap(1, flowerTile); // Added specifically to map 1 (second map)

            BufferedImage flower1 = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(8).png")));
            GameComponent flowerTile1 = new GameComponent(8, 0, 2, 1, flower1, true);
            addComponentToMap(1, flowerTile1); // Added specifically to map 1 (second map)

            BufferedImage guide = ImageIO.read(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(
                            "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(20).png")));
            GameComponent guideTile1 = new GameComponent(3, 3, 1, 1, guide, true);
            addComponentToMap(1, guideTile1); // Added specifically to map 1 (second map)

        } catch (Exception e) {
            e.printStackTrace();
        }

        Object[][] componentsData = {
                // Trees
                { 8, 4, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(3).png" },
                { 8, 5, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(3).png" },
                { 6, 4, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(2).png" },
                { 6, 5, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(2).png" },
                { 14, 6, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(32).png" },
                { 5, 7, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(15).png" },
                { 14, 0, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(37).png" },
                { 3, 5, 2, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(37).png" },
                { 2, 7, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(37).png" },
                { 2, 4, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(37).png" },
                { 2, 4, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(37).png" },
                { 5, 6, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(37).png" },
                { 8, 8, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(37).png" },
                { 9, 2, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(3).png" },
                { 9, 5, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(37).png" },
                { 11, 7, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(2).png" },
                { 10, 6, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(2).png" },
                { 12, 3, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(37).png" },
                { 11, 1, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(37).png" },
                { 9, 1, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(37).png" },
                { 14, 5, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(37).png" },
                { 9, 8, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(37).png" },
                { 15, 8, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(37).png" },
                { 15, 7, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(37).png" },
                { 15, 6, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(3).png" },
                { 9, 6, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(3).png" },
                { 9, 5, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(3).png" },
                { 9, 4, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(37).png" },
                { 9, 3, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(2).png" },

                // flowers
                { 10, 5, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(7).png" },
                { 13, 5, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(8).png" },
                { 12, 7, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(8).png" },
                { 5, 8, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(8).png" },
                { 14, 8, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(8).png" },

                { 15, 0, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(18).png" },

                // traps
                { 13, 3, 1, 1,
                        "Top_Down_Adventure_Pack_v.1.0/Tiles_(animated)/Overworld/regia_waterplant_tile_anim.gif" },
                { 14, 3, 1, 1,
                        "Top_Down_Adventure_Pack_v.1.0/Tiles_(animated)/Overworld/regia_waterplant_tile_anim.gif" },
                { 3, 7, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(40).png" },
                { 11, 6, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(41).png" },
                { 11, 5, 1, 1, "Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(41).png" },

        };

        try {
            for (Object[] data : componentsData) {
                int x = (int) data[0];
                int y = (int) data[1];
                int width = (int) data[2];
                int height = (int) data[3];
                String path = (String) data[4];

                BufferedImage img = ImageIO.read(Objects.requireNonNull(
                        getClass().getClassLoader().getResourceAsStream(path)));
                GameComponent comp = new GameComponent(x, y, width, height, img, true);
                addComponentToMap(1, comp); // Use the correct map index if needed
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
}
