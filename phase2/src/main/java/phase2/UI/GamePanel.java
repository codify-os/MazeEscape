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
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements Runnable {
    //SCREEN SETTINGS
    final int originalTileSize = 16; //16x16 tile
    final int scale = 3;

    public int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; //768 pixels
    public final int screenHeight = tileSize * maxScreenRow; //576 pixels

    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    //FPS
    final double FPS = 60.0;
    public TileManager tileManager;

    KeyHandler keyHandler = new KeyHandler();
    Thread gameThread;
    public CheckCollision checkCollision = new CheckCollision(this);
    public Player player = new Player(this, keyHandler);
    Pathfinder pathfinder = new Pathfinder(tileManager);

    public List<Enemy> enemies = new ArrayList<>();

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

        tileManager = new TileManager(this);
        checkCollision = new CheckCollision(this);
        player = new Player(this, keyHandler);
        pathfinder = new Pathfinder(tileManager);
        //tileManager.loadComponents(); // <- ADD HERE
        spawnEnemies();
    }
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    @Override
    public void run() {

        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;
        //delta-time algorithm to improve game performance (IDK if it did, but it felt like it, also I spent way too long on this to not use it)
        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >=1) {
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
        int playerCol = player.worldX / tileSize;
        int playerRow = player.worldY / tileSize;

        // Right edge
        if (playerCol >= maxWorldCol) {
            player.worldX = 0; // move player to left side of new map
        }

        // Left edge
        if (playerCol < 0) {
            // Optional: previous map
            player.worldX = (maxWorldCol - 1) * tileSize;
        }

        // Bottom edge
        if (playerRow >= maxWorldRow) {
            player.worldY = 0; // move player to top of new map
        }

        // Top edge
        if (playerRow < 0) {
            tileManager.nextMap();
            player.worldY = (maxWorldRow - 1) * tileSize;
        }
    }

    public void update() {
        player.update();
        for (Enemy e: enemies) {
            e.update();
        }
        checkMapSwitch();

    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        tileManager.draw(g2d);
        player.draw(g2d);
        for (Enemy e: enemies) {
            e.draw(g2d);
        }
        g2d.dispose();
    }

    public void spawnEnemies() {
        int [][] spawnPoints = {
                {3, 12},
                {14, 2}
        };

        for (int[] p: spawnPoints) {
            int worldX = p[0] * tileSize;
            int worldY = p[1] * tileSize;

            enemies.add(new Enemy(this, pathfinder, player, worldX, worldY));
        }
    }
}
