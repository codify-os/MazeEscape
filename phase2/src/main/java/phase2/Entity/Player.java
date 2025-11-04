package phase2.Entity;

import phase2.UI.GamePanel;
import phase2.UI.KeyHandler;
import phase2.game.combat.*;
import phase2.game.stats.*;
import java.awt.*;


public class Player extends Entity{
    GamePanel gp;
    KeyHandler keyH;
    private Image up, down, left, right;
    private Image attackUp, attackDown, attackLeft, attackRight;
    private Enemy targetEnemy;
    private boolean isAttacking = false;
    private long attackAnimationStart = 0;
    private static final long ATTACK_ANIMATION_DURATION = 300; // ms

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        setDefaultValues();
        getPlayerImage();
        initializeCombat();
    }

    public void setDefaultValues() {
        x = 100;
        y = 100;
        speed = 4;
        direction = "down";
    }

    private void initializeCombat() {
        // Initialize health (100 HP)
        this.health = new HealthComponent(100);
        
        // Initialize stats (20 attack, 5 defense)
        this.stats = new Stats(20, 5);
        
        // Initialize attack (Sword Slash with 1 tile range, 10% crit, 2x crit damage, 500ms cooldown)
        this.attackData = new AttackData(
            "Sword Slash",
            stats.getAttackPower(),
            gp.tileSize,  // 1 tile range
            AttackData.DamageType.PHYSICAL,
            0.1,  // 10% crit chance
            2.0,  // 2x crit multiplier
            500   // 500ms cooldown
        );
    }

    public void setTargetEnemy(Enemy enemy) {
        this.targetEnemy = enemy;
    }

    public void getPlayerImage() {
        try{
            //If you find a better way to do this, then you can mess around with it, but this is the best I could research into this
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            up = toolkit.getImage(getClass().getClassLoader().getResource("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_run_up_anim.gif"));
            down = toolkit.getImage(getClass().getClassLoader().getResource("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_run_down_anim.gif"));
            right = toolkit.getImage(getClass().getClassLoader().getResource("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_run_right_anim.gif"));
            left = toolkit.getImage(getClass().getClassLoader().getResource("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_run_left_anim.gif"));

            // Load attack animations
            attackUp = toolkit.getImage(getClass().getClassLoader().getResource("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_attack_up_anim.gif"));
            attackDown = toolkit.getImage(getClass().getClassLoader().getResource("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_attack_down_anim.gif"));
            attackRight = toolkit.getImage(getClass().getClassLoader().getResource("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_attack_right_anim.gif"));
            attackLeft = toolkit.getImage(getClass().getClassLoader().getResource("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_attack_left_anim.gif"));

            MediaTracker tracker = new MediaTracker(new java.awt.Canvas());
            tracker.addImage(up, 0);
            tracker.addImage(down, 1);
            tracker.addImage(right, 2);
            tracker.addImage(left, 3);
            tracker.addImage(attackUp, 4);
            tracker.addImage(attackDown, 5);
            tracker.addImage(attackRight, 6);
            tracker.addImage(attackLeft, 7);
            tracker.waitForAll();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void update() {
        if(keyH.wPressed) {
            direction = "up";
            y -= speed;
        } else if (keyH.sPressed) {
            direction = "down";
            y += speed;
        } else if (keyH.aPressed) {
            direction = "left";
            x -= speed;
        } else if (keyH.dPressed){
            direction = "right";
            x += speed;
        }

        // Handle attack input - always play animation when SPACE is pressed
        if (keyH.spacePressed && !isAttacking) {
            // Play swing animation regardless of target
            isAttacking = true;
            attackAnimationStart = System.currentTimeMillis();
            
            // Only deal damage if target is valid and in range
            if (targetEnemy != null && targetEnemy.isAlive() && isInRange(targetEnemy) && canAttack()) {
                attack(targetEnemy);
            }
        }

        // Update attack animation state
        if (isAttacking) {
            long elapsed = System.currentTimeMillis() - attackAnimationStart;
            if (elapsed >= ATTACK_ANIMATION_DURATION) {
                isAttacking = false;
            }
        }

        // Update combat cooldowns
        updateCooldown();
    }
    public void draw(Graphics2D g2d){
        Image image;
        
        // Use attack animation if attacking, otherwise use movement animation
        if (isAttacking) {
            image = switch (direction) {
                case "up" -> attackUp;
                case "down" -> attackDown;
                case "left" -> attackLeft;
                case "right" -> attackRight;
                default -> attackDown;
            };
        } else {
            image = switch (direction) {
                case "up" -> up;
                case "down" -> down;
                case "left" -> left;
                case "right" -> right;
                default -> down;
            };
        }
        
        if (image != null) {
            g2d.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }

        // Draw health bar
        drawHealthBar(g2d);
    }

    private void drawHealthBar(Graphics2D g2d) {
        if (health == null) return;

        int barWidth = gp.tileSize;
        int barHeight = 5;
        int barX = x;
        int barY = y - 10;

        // Background (black)
        g2d.setColor(Color.BLACK);
        g2d.fillRect(barX, barY, barWidth, barHeight);

        // Health (green)
        double healthPercent = health.getHealthPercentage();
        int healthWidth = (int) (barWidth * healthPercent);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(barX, barY, healthWidth, barHeight);

        // Border (white)
        g2d.setColor(Color.WHITE);
        g2d.drawRect(barX, barY, barWidth, barHeight);
    }
}