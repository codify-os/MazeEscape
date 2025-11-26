package phase2.Tile;

import java.awt.image.BufferedImage;
// import java.awt.Image;

public class Tile {
    public BufferedImage image;
    public boolean collision = false; // For walls/obstacles
    public boolean isTrap = false;
    public int trapDamage = 10;
    public int trapCooldown = 60;
    public int trapTimer = 0;
    public boolean spawnable = true;
    //public Image image;

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
    
    public Tile(Tile other) {
        this.image = other.image;
        this.collision = other.collision;
        this.spawnable = other.spawnable;
        this.isTrap = other.isTrap;
        this.trapDamage = other.trapDamage;
        this.trapCooldown = other.trapCooldown;
        this.trapTimer = other.trapTimer;
}


    /**
     * helper function to calculate F cost
     */
    public void calculateFCost() {
        fCost = gCost + hCost;
    }

    /**
     * resets all a* related variables to 0
     */
    public void reset() {
        gCost = 0;
        hCost = 0;
        fCost = 0;
        parent = null;
    }

    /**
     * helper for calculating H cost
     * 
     * @param target tile of the destination/target
     */
    public void setHCost(Tile target) {
        hCost = Math.abs(col - target.col) + Math.abs(row - target.row);
    }

    /**
     * helper function that returns if the tile is walkable through a boolean
     * 
     * @return boolean representing the ability to move through the tile
     */
    public boolean isWalkable() {
        return !collision;
    }

    /**
     * used for checking if two tiles are equal
     * 
     * @return boolean true if equal or false if not
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Tile))
            return false;
        Tile other = (Tile) obj;
        return this.col == other.col && this.row == other.row;
    }

    /**
     * hash code for tile through the forumla col*1000+row
     * 
     * @return hash code for tile
     */
    @Override
    public int hashCode() {
        return col * 1000 + row;
    }
}
