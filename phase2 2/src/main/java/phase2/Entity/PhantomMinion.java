package main.java.phase2.Entity;

import phase2.UI.GamePanel;
import phase2.game.combat.DamageSource;
import phase2.game.stats.HealthComponent;

import java.awt.*;

public class PhantomMinion extends Enemy {

    private Image runImg;
    private Image deathImg;

    private boolean dying = false;
    private int deathTimer = 30;

    public PhantomMinion(GamePanel gp, Pathfinder pathfinder, Player player, int x, int y) {
        super(gp, pathfinder, player, x, y);

        this.health = new HealthComponent(30, 0, this);
        this.speed = 2;

        loadImages();
    }

    private void loadImages() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        runImg = tk.getImage(gp.getResourceAsImage("Top_Down_Adventure_Pack_v.1.0/Enemies_Sprites/Phantom_Sprites/phantom_run_anim_left.gif"));

        deathImg = tk.getImage(gp.getResourceAsImage("Top_Down_Adventure_Pack_v.1.0/Enemies_Sprites/Phantom_Sprites/phantom_death_anim_left.gif"));

    }

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

    private void followPlayer() {
        if (worldX < player.worldX) worldX += speed;
        else worldX -= speed;

        if (worldY < player.worldY) worldY += speed;
        else worldY -= speed;
    }

    @Override
    public void takeDamage(int amount, DamageSource src) {
        health.takeDamage(amount, src);
        if (!health.isAlive()) dying = true;
    }
    @Override
    protected void drawEnemySprite(Graphics2D g2d, int screenX, int screenY) {
        Image image = dying ? deathImg : runImg;
        g2d.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, gp);
    }

//    @Override
//    public void draw(Graphics2D g2) {
//        int sx = worldX - player.worldX + player.screenX;
//        int sy = worldY - player.worldY + player.screenY;
//
//        g2.drawImage(dying ? deathImg : runImg, sx, sy, gp.tileSize, gp.tileSize, gp);
//    }
}