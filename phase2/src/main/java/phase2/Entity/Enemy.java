package phase2.Entity;

import phase2.UI.GamePanel;
import phase2.Tile.Tile;
import java.util.List;
import java.awt.*;
import phase2.game.combat.CombatManager;
import phase2.game.stats.HealthComponent;
import phase2.game.stats.Stats;

public class Enemy extends Entity {
    GamePanel gp;
    Pathfinder pathfinder;
    Player player;

    private List<Tile> currentPath;
    private int pathIndex = 0;
    private int pathUpdateCounter = 0;
    private static final int PATH_UPDATE_INTERVAL = 10; // Update path every 30 frames (~0.5 seconds at 60 FPS)
    public boolean hasKey = false;

    public Enemy (GamePanel gp, Pathfinder pathfinder, Player player, int x, int y) {
        this.gp = gp;
        this.pathfinder = pathfinder;
        this.player = player;
        this.worldX = x;
        this.worldY = y;
        direction = "down";
        speed = 2;
        collisionArea = new Rectangle(8 ,16, gp.tileSize - 16, gp.tileSize - 16);
        stats = new Stats(10, 2);
        health = new HealthComponent(50, health.getDefense(), this);
        currentAttack = stats.createBasicAttack();

        getImage();
        updatePath();
    }

    public void setDefaultValues() {
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
        if (!isOnScreen()) {
            return;
        }
        collisionOn = false;
        gp.checkCollision.checkTile(this);
        if(!collisionOn) {
            //follow current path
            followPath();
        }

        // Update path periodically
        pathUpdateCounter++;
        if (pathUpdateCounter >= PATH_UPDATE_INTERVAL) {
            updatePath();
            pathUpdateCounter = 0;
        }

        Rectangle enemyHitBox = new Rectangle(worldX + collisionArea.x, worldY + collisionArea.y,
                collisionArea.width, collisionArea.height);
        Rectangle playerHitBox = new Rectangle(player.worldX + player.collisionArea.x,
                player.worldY + player.collisionArea.y, player.collisionArea.width, player.collisionArea.height);

        if (enemyHitBox.intersects(playerHitBox) && canAttack()) {
            System.out.println("Enemy attacks player for" + currentAttack.getPower());
            attack(player);
        }
    }

    private void updatePath() {
        // Convert pixel positions to tile coordinates (use center of entity)
        int enemyCol = (worldX + gp.tileSize / 2) / gp.tileSize;
        int enemyRow = (worldY + gp.tileSize / 2) / gp.tileSize;
        int playerCol = (player.getWorldX() + gp.tileSize / 2) / gp.tileSize;
        int playerRow = (player.getWorldY() + gp.tileSize / 2) / gp.tileSize;

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
            int currentCol = (worldX + gp.tileSize / 2) / gp.tileSize;
            int currentRow = (worldY + gp.tileSize / 2) / gp.tileSize;

            if (firstTile.col == currentCol && firstTile.row == currentRow) {
                pathIndex = 1;
            }
        }

        if (pathIndex >= currentPath.size()) {
            return;
        }

        // Get target tile
        Tile targetTile = currentPath.get(pathIndex);
        int targetX = targetTile.col * gp.tileSize + gp.tileSize/2;
        int targetY = targetTile.row * gp.tileSize + gp.tileSize/2;

        // Calculate direction to target
        int dx = targetX - (worldX + gp.tileSize/2);
        int dy = targetY - (worldY + gp.tileSize/2);

        // Calculate distance
        double distance = Math.sqrt(dx * dx + dy * dy);

        // If very close to target, snap to it and move to next waypoint
        if (distance < speed) {
            worldX = targetX;
            worldY = targetY;
            pathIndex++;
        } else {
            // Move towards target using normalized direction
            // Prioritize one axis at a time to avoid diagonal movement
            if (!collisionOn) {
                if (Math.abs(dx) > Math.abs(dy)) {
                    if (dx > 0) {
                        worldX += speed;
                        direction = "right";
                    } else {
                        worldX -= speed;
                        direction = "left";
                    }
                } else {
                    if (dy > 0) {
                        worldY += speed;
                        direction = "down";
                    } else {
                        worldY -= speed;
                        direction = "up";
                    }
                }
            }
        }
    }

    public void draw(Graphics2D g2d) {
        int screenX = worldX - player.worldX + player.screenX;
        int screenY = worldY - player.worldY + player.screenY;
        if (screenX + gp.tileSize < 0 || screenX > gp.screenWidth || screenY + gp.tileSize < 0 || screenY > gp.screenHeight) {
            return;
        }
        Image image = switch (direction) {
            case "up" -> up;
            case "down" -> down;
            case "left" -> left;
            case "right" -> right;
            default -> down;
        };
        if (damageFlashTimer > 0) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.setColor(Color.red);
            g2d.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            damageFlashTimer --;
        }
        g2d.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, gp);

        int barWidth = gp.tileSize;
        int barHeight = 4;
        int barX = screenX;
        int barY = screenY - barHeight -4;
        drawHealthBar(g2d, barX, barY, barWidth, barHeight);
    }

    private boolean isOnScreen() {
        int screenLeft = player.worldX - (gp.screenWidth/2);
        int screenRight = player.worldX + (gp.screenWidth/2);
        int screenTop = player.worldY - (gp.screenHeight/2);
        int screenBottom = player.worldY + (gp.screenHeight/2);

        return worldX + gp.tileSize > screenLeft && worldX < screenRight
                && worldY + gp.tileSize > screenTop && worldY < screenBottom;
    }

    @Override
    public void onDeath() {
        System.out.println("Enemy died!");
        gp.enemies.remove(this);
        if (gp.droppedKey == null && hasKey) {
            gp.droppedKey = new KeyItem(worldX, worldY);
            System.out.println("This enemy had the key");
        }

        gp.player.grantRandomBuff();

    }
}
