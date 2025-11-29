package phase2.Entity;

import phase2.UI.GamePanel;
import phase2.game.combat.DamageSource;
import phase2.game.stats.HealthComponent;

import java.awt.*;

/**
 * Represents a phantom minion enemy that chases the player.
 * This enemy moves directly toward the player, plays a running animation
 * while alive, and a death animation when killed before being removed from the game.
 *
 * <p>PhantomMinion extends {@link Enemy} and uses its update cycle and rendering logic.</p>
 */
public class PhantomMinion extends Enemy {

    /** Running animation sprite. */
    private Image runImg;

    /** Death animation sprite. */
    private Image deathImg;

    /** Whether the minion is currently dying (triggered when health reaches zero). */
    private boolean dying = false;

    /** Timer controlling how long the death animation is shown before removal. */
    private int deathTimer = 30;

    /**
     * Creates a new Phantom Minion.
     *
     * @param gp          reference to the main game panel
     * @param pathfinder  pathfinding utility for this enemy
     * @param player      reference to the player entity
     * @param x           initial world X position
     * @param y           initial world Y position
     */
    public PhantomMinion(GamePanel gp, Pathfinder pathfinder, Player player, int x, int y) {
        super(gp, pathfinder, player, x, y);

        this.health = new HealthComponent(30, 0, this); // 30 HP, no armor
        this.speed = 2;

        loadImages();
    }

    /**
     * Loads the running and dying animation sprites for the phantom minion.
     * Uses the GamePanel's resource loader to resolve image paths.
     */
    private void loadImages() {
        Toolkit tk = Toolkit.getDefaultToolkit();

        runImg = tk.getImage(
                gp.getResourceAsImage("Top_Down_Adventure_Pack_v.1.0/Enemies_Sprites/Phantom_Sprites/phantom_run_anim_left.gif")
        );

        deathImg = tk.getImage(
                gp.getResourceAsImage("Top_Down_Adventure_Pack_v.1.0/Enemies_Sprites/Phantom_Sprites/phantom_death_anim_left.gif")
        );
    }

    /**
     * Updates the phantom's behavior each game tick.
     * <ul>
     *     <li>If dying: count down death timer and remove the minion when finished.</li>
     *     <li>Otherwise: move toward the player and perform the standard enemy update.</li>
     * </ul>
     */
    @Override
    public void update() {
        if (dying) {
            deathTimer--;
            if (deathTimer <= 0) gp.enemies.remove(this);
            return;
        }

        followPlayer();
        super.update();
    }

    /**
     * Moves the phantom directly toward the player's current world coordinates.
     * This is a simple tracking movement without pathfinding.
     */
    private void followPlayer() {
        if (worldX < player.worldX) worldX += speed;
        else worldX -= speed;

        if (worldY < player.worldY) worldY += speed;
        else worldY -= speed;
    }

    /**
     * Applies damage to the phantom and triggers the death state if health reaches zero.
     *
     * @param amount the amount of damage dealt
     * @param src    the source of the damage
     */
    @Override
    public void takeDamage(int amount, DamageSource src) {
        health.takeDamage(amount, src);
        if (!health.isAlive()) dying = true;
    }

    /**
     * Draws the phantom’s sprite depending on its current state.
     *
     * @param g2d      the drawing context
     * @param screenX  screen X coordinate where the sprite should be drawn
     * @param screenY  screen Y coordinate where the sprite should be drawn
     */
    @Override
    protected void drawEnemySprite(Graphics2D g2d, int screenX, int screenY) {
        Image image = dying ? deathImg : runImg;
        g2d.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, gp);
    }
}
