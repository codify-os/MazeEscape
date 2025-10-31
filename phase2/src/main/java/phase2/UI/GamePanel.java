/*
* This is the main class that handles loading the ui that we will be using, try not to change it too much, any changes
* made to this file need to be discussed across the group, because it will literally cause many changes to occur across
* the entire project.
*
* The remaining thing left in relation to UI, is to implement our mockup, we will format this into a formal JavaDoc then.
* */

package phase2.UI;

import phase2.Entity.Player;
import phase2.Entity.Enemy;
import phase2.Entity.Pathfinder;
import phase2.Tile.TileManager;
import javax.swing.JPanel;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {
    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3;

    public int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    // FPS
    final double FPS = 60.0;
    public TileManager tileManager = new TileManager(this);

    KeyHandler keyHandler = new KeyHandler();
    Thread gameThread;
    Player player = new Player(this, keyHandler);
    Pathfinder pathfinder = new Pathfinder(tileManager);
    Enemy enemy = new Enemy(this, pathfinder, player);

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

        tileManager = new TileManager(this);
        tileManager.loadComponents(); // <- ADD HERE
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;
        // delta-time algorithm to improve game performance (IDK if it did, but it felt
        // like it, also I spent way too long on this to not use it)
        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1000000000) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }

    }

    public void checkMapSwitch() {
        int playerCol = player.x / tileSize;
        int playerRow = player.y / tileSize;

        // Right edge
        if (playerCol >= maxScreenCol) {
            player.x = 0; // move player to left side of new map
        }

        // Left edge
        if (playerCol < 0) {
            // Optional: previous map
            player.x = (maxScreenCol - 1) * tileSize;
        }

        // Bottom edge
        if (playerRow >= maxScreenRow) {
            player.y = 0; // move player to top of new map
        }

        // Top edge
        if (playerRow < 0) {
            tileManager.nextMap();
            player.y = (maxScreenRow - 1) * tileSize;
        }
    }

    public void update() {
        player.update();
        enemy.update();
        checkMapSwitch();

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        tileManager.draw(g2d);
        player.draw(g2d);
        enemy.draw(g2d);
        g2d.dispose();
    }
}
