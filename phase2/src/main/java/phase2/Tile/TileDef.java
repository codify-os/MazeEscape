package phase2.Tile;

public class TileDef {
    public final String path;
    public boolean collision = false;
    public boolean spawnable = true;
    public boolean isTrap = false;
    public int trapDamage = 0;
    public int trapCooldown = 0;
    

    public TileDef(String path) {
        this.path = path;
    }

    public TileDef collision(boolean value) {
        this.collision = value;
        return this;
    }

    public TileDef spawnable(boolean value) {
        this.spawnable = value;
        return this;
    }

    public TileDef trap(boolean value, int damage, int cooldown) {
        this.isTrap = value;
        this.trapDamage = damage;
        this.trapCooldown = cooldown;
        return this;
    }
}
