package phase2.Tile;

import java.awt.image.BufferedImage;

public class Tile {
    public BufferedImage image;
    public boolean collision = false; // For walls/obstacles

    // A* pathfinding properties
    public int gCost; // Distance from start node
    public int hCost; // Heuristic distance to end node
    public int fCost; // gCost + hCost
    public Tile parent; // For reconstructing the path
    public int col;
    public int row;

    public Tile() {
        reset();
    }

    public void calculateFCost() {
        fCost = gCost + hCost;
    }

    public void reset() {
        gCost = 0;
        hCost = 0;
        fCost = 0;
        parent = null;
    }
}
