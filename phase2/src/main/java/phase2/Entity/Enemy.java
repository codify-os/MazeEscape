package phase2.Entity;

import phase2.UI.GamePanel;
import phase2.Tile.Tile;
import java.util.List;
import java.awt.*;

public class Enemy extends Entity {
    GamePanel gp;
    Pathfinder pathfinder;
    Player player;

    private List<Tile> currentPath;
    private int pathIndex = 0;
    private int pathUpdateCounter = 0;
    private static final int PATH_UPDATE_INTERVAL = 30; // Update path every 30 frames (~0.5 seconds at 60 FPS)

    public Enemy(GamePanel gp, Pathfinder pathfinder, Player player) {
        this.gp = gp;
        this.pathfinder = pathfinder;
        this.player = player;
        setDefaultValues();
        getImage();
        // Calculate initial path
        updatePath();
    }

    public void setDefaultValues() {
        x = 400;
        y = 400;
        speed = 2; // Slower than player
        direction = "down";
    }

    public void getImage() {
        try {
            // Using enemy sprites - the pink slime has one animated gif for all directions
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Image slimeImage = toolkit.getImage(getClass().getClassLoader()
                    .getResource(
                            "Top_Down_Adventure_Pack_v.1.0/Enemies_Sprites/Pinkslime_Sprites/pinkslime_run_anim_anim_all_dir.gif"));

            // Use the same image for all directions since it's an omnidirectional sprite
            up = slimeImage;
            down = slimeImage;
            right = slimeImage;
            left = slimeImage;

            MediaTracker tracker = new MediaTracker(new java.awt.Canvas());
            tracker.addImage(slimeImage, 0);
            tracker.waitForAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        // Follow the current path first
        followPath();

        // Update path periodically
        pathUpdateCounter++;
        if (pathUpdateCounter >= PATH_UPDATE_INTERVAL) {
            updatePath();
            pathUpdateCounter = 0;
        }
    }

    private void updatePath() {
        // Convert pixel positions to tile coordinates (use center of entity)
        int enemyCol = (x + gp.tileSize / 2) / gp.tileSize;
        int enemyRow = (y + gp.tileSize / 2) / gp.tileSize;
        int playerCol = (player.getX() + gp.tileSize / 2) / gp.tileSize;
        int playerRow = (player.getY() + gp.tileSize / 2) / gp.tileSize;

        Tile startTile = gp.tileManager.getTile(enemyCol, enemyRow);
        Tile goalTile = gp.tileManager.getTile(playerCol, playerRow);

        if (startTile != null && goalTile != null && !startTile.equals(goalTile)) {
            currentPath = pathfinder.findPath(startTile, goalTile);
            pathIndex = 0;
        }
    }

    private void followPath() {
        if (currentPath == null || currentPath.isEmpty() || pathIndex >= currentPath.size()) {
            return;
        }

        // Skip the first tile if we're already on it
        if (pathIndex == 0 && currentPath.size() > 1) {
            Tile firstTile = currentPath.get(0);
            int currentCol = (x + gp.tileSize / 2) / gp.tileSize;
            int currentRow = (y + gp.tileSize / 2) / gp.tileSize;

            if (firstTile.col == currentCol && firstTile.row == currentRow) {
                pathIndex = 1;
            }
        }

        if (pathIndex >= currentPath.size()) {
            return;
        }

        // Get target tile
        Tile targetTile = currentPath.get(pathIndex);
        int targetX = targetTile.col * gp.tileSize;
        int targetY = targetTile.row * gp.tileSize;

        // Calculate direction to target
        int dx = targetX - x;
        int dy = targetY - y;

        // Calculate distance
        double distance = Math.sqrt(dx * dx + dy * dy);

        // If very close to target, snap to it and move to next waypoint
        if (distance < speed) {
            x = targetX;
            y = targetY;
            pathIndex++;
        } else {
            // Move towards target using normalized direction
            // Prioritize one axis at a time to avoid diagonal movement
            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx > 0) {
                    x += speed;
                    direction = "right";
                } else {
                    x -= speed;
                    direction = "left";
                }
            } else {
                if (dy > 0) {
                    y += speed;
                    direction = "down";
                } else {
                    y -= speed;
                    direction = "up";
                }
            }
        }
    }

    public void draw(Graphics2D g2d) {
        Image image = switch (direction) {
            case "up" -> up;
            case "down" -> down;
            case "left" -> left;
            case "right" -> right;
            default -> down;
        };
        g2d.drawImage(image, x, y, gp.tileSize, gp.tileSize, gp);
    }
}
