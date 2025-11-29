package phase2.Tile;

/**
 * Represents the definition of a tile in the game, including its graphical path,
 * collision properties, spawnability, and trap characteristics.
 */
public class TileDef {
    /** Path to the tile's image or resource. */
    public final String path;

    /** Indicates if the tile has collision (cannot be passed through). */
    public boolean collision = false;

    /** Indicates if entities can spawn on this tile. */
    public boolean spawnable = true;

    /** Indicates if the tile is a trap. */
    public boolean isTrap = false;

    /** Damage dealt by the trap, if the tile is a trap. */
    public int trapDamage = 0;

    /** Cooldown time for the trap, if the tile is a trap. */
    public int trapCooldown = 0;

    /**
     * Creates a new TileDef with the given path.
     *
     * @param path The path to the tile's image or resource.
     */
    public TileDef(String path) {
        this.path = path;
    }

    /**
     * Sets the collision property of this tile.
     *
     * @param value True if the tile should have collision, false otherwise.
     * @return The current TileDef instance for chaining.
     */
    public TileDef collision(boolean value) {
        this.collision = value;
        return this;
    }

    /**
     * Sets whether entities can spawn on this tile.
     *
     * @param value True if the tile is spawnable, false otherwise.
     * @return The current TileDef instance for chaining.
     */
    public TileDef spawnable(boolean value) {
        this.spawnable = value;
        return this;
    }

    /**
     * Configures the tile as a trap with specified damage and cooldown.
     *
     * @param value    True if the tile should be a trap, false otherwise.
     * @param damage   Damage dealt by the trap.
     * @param cooldown Cooldown time of the trap in ticks or seconds (depending on your game logic).
     * @return The current TileDef instance for chaining.
     */
    public TileDef trap(boolean value, int damage, int cooldown) {
        this.isTrap = value;
        this.trapDamage = damage;
        this.trapCooldown = cooldown;
        return this;
    }
}
