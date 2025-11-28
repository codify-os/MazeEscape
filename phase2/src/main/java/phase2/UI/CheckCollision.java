package phase2.UI;

import phase2.Entity.Entity;

public class CheckCollision {
    public GamePanel gp;
    public CheckCollision (GamePanel gp) {
        this.gp = gp;
    }
    public void checkTile(Entity entity) {
        int leftX = entity.worldX + entity.collisionArea.x;
        int rightX = entity.worldX + entity.collisionArea.x + entity.collisionArea.width;
        int topY = entity.worldY + entity.collisionArea.y;
        int bottomY = entity.worldY + entity.collisionArea.y + entity.collisionArea.height;

        int entColLeft = leftX/gp.tileSize;
        int entColRight = rightX/gp.tileSize;
        int entRowTop = topY/gp.tileSize;
        int entRowBottom = bottomY/gp.tileSize;

        int numTile1, numTile2;

        switch (entity.direction) {
            case "up":
                entRowTop = (topY - entity.speed)/gp.tileSize;
                // Bounds check
                if (entColLeft < 0 || entColLeft >= gp.maxWorldCol || entColRight < 0 || entColRight >= gp.maxWorldCol ||
                    entRowTop < 0 || entRowTop >= gp.maxWorldRow) {
                    entity.collisionOn = true;
                    break;
                }
                numTile1 = gp.tileManager.mapTileNum[entColLeft][entRowTop];
                numTile2 = gp.tileManager.mapTileNum[entColRight][entRowTop];
                if ((gp.tileManager.tileType[numTile1].collision || gp.tileManager.tileType[numTile2].collision)) {
                    entity.collisionOn = true;
                }
                break;
            case "down":
                entRowBottom = (bottomY + entity.speed)/gp.tileSize;
                // Bounds check
                if (entColLeft < 0 || entColLeft >= gp.maxWorldCol || entColRight < 0 || entColRight >= gp.maxWorldCol ||
                    entRowBottom < 0 || entRowBottom >= gp.maxWorldRow) {
                    entity.collisionOn = true;
                    break;
                }
                numTile1 = gp.tileManager.mapTileNum[entColLeft][entRowBottom];
                numTile2 = gp.tileManager.mapTileNum[entColRight][entRowBottom];
                if ((gp.tileManager.tileType[numTile1].collision || gp.tileManager.tileType[numTile2].collision)) {
                    entity.collisionOn = true;
                }
                break;
            case "left":
                entColLeft = (leftX - entity.speed)/gp.tileSize;
                // Bounds check
                if (entColLeft < 0 || entColLeft >= gp.maxWorldCol || entRowTop < 0 || entRowTop >= gp.maxWorldRow ||
                    entRowBottom < 0 || entRowBottom >= gp.maxWorldRow) {
                    entity.collisionOn = true;
                    break;
                }
                numTile1 = gp.tileManager.mapTileNum[entColLeft][entRowTop];
                numTile2 = gp.tileManager.mapTileNum[entColLeft][entRowBottom];
                if ((gp.tileManager.tileType[numTile1].collision || gp.tileManager.tileType[numTile2].collision)) {
                    entity.collisionOn = true;
                }
                break;
            case "right":
                entColRight = (rightX + entity.speed)/gp.tileSize;
                // Bounds check
                if (entColRight < 0 || entColRight >= gp.maxWorldCol || entRowTop < 0 || entRowTop >= gp.maxWorldRow ||
                    entRowBottom < 0 || entRowBottom >= gp.maxWorldRow) {
                    entity.collisionOn = true;
                    break;
                }
                numTile1 = gp.tileManager.mapTileNum[entColRight][entRowTop];
                numTile2 = gp.tileManager.mapTileNum[entColRight][entRowBottom];
                if ((gp.tileManager.tileType[numTile1].collision || gp.tileManager.tileType[numTile2].collision)) {
                    entity.collisionOn = true;
                }
                break;
        }
    }
}
