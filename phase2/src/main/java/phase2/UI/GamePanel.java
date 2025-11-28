/*
* This is the main class that handles loading the ui that we will be using, try not to change it too much, any changes
* made to this file need to be discussed across the group, because it will literally cause many changes to occur across
* the entire project.
*
* The remaining thing left in relation to UI, is to implement our mockup, we will format this into a formal JavaDoc then.
* */

package phase2.UI;
import phase2.Entity.KeyItem;
import phase2.Entity.Player;
import phase2.Entity.Enemy;
import phase2.Entity.Pathfinder;
import phase2.Tile.TileManager;
import phase2.game.combat.*;
import javax.swing.JPanel;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Main game panel that handles the game loop and rendering
 */
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
    /** Collision detection system */
    public CheckCollision checkCollision = new CheckCollision(this);
    public Player player = new Player(this, keyHandler);
    Pathfinder pathfinder = new Pathfinder(tileManager);

    /** List of all active enemies */
    public List<Enemy> enemies = new ArrayList<>();
    /** Dropped key item if any */
    public KeyItem droppedKey = null;

    private final dialogueBox dialogueBox = new dialogueBox();
    private final topPanel topPanel = new topPanel();

    private final Image keyIcon;

    //Status flags
    /** Enum representing different game states */
    public enum GameState {
        PLAY,
        GAME_WON,
        GAME_OVER
    }
    /** Current game state */
    public GameState gameState = GameState.PLAY;
    /** Final score at end of game */
    public int finalScore = 0;
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent click) {
                if (gameState == GameState.PLAY) {
                    topPanel.ActionBar pressButton = topPanel.clickButton(click);
                    switch(pressButton){
                        case Button_EXIT -> {
                            System.exit(0);
                        }
                        case Button_Help -> {
                            dialogueBox.loadLine(
                    "Need Help?",
                            "Use WASD to move and SPACE to attack"
                        );
                            dialogueBox.show_Dialogue();
                        }
                        case Button_Back -> {
                            // for if button menu is added
                        }
                        default -> { }
                    }

                    dialogueBox.skipClick(click, screenWidth, screenHeight);
                }
            }
        });

        tileManager = new TileManager(this);
        checkCollision = new CheckCollision(this);
        player = new Player(this, keyHandler);
        pathfinder = new Pathfinder(tileManager);
        //tileManager.loadComponents(); // <- ADD HERE
        spawnEnemies();
        CombatManager.addListener(new CombatLogger(true));

        dialogueBox.loadLine(
            "Welcome to the dungeon!",
            "Find keys to escape the dungeon but beware of the enemies",
            "Fight the enemies to get a keys", 
            "GOOD LUCK PLAYER !!"
        );
        dialogueBox.show_Dialogue();    

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        keyIcon = toolkit.getImage(getResourceAsImage("Top_Down_Adventure_Pack_v.1.0/Props_Items_(animated)/key_item_anim.gif"));
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
        if(gameState == GameState.GAME_WON){
            if  (keyHandler.enterPressed){
                System.exit(0);
            }
            return;
        }

        if (keyHandler.spacePressed) {
            dialogueBox.jumptoNext();
            keyHandler.spacePressed = true;
        }

        if (topPanel.isPaused()) {
            return;
        }

        player.update();
        player.updateCooldown();
        for (Enemy e: enemies) {
            e.update();
            e.updateCooldown();
        }
        checkMapSwitch();

        if (!player.isAlive()) {
            gameState = GameState.GAME_OVER;
        }

        if (gameState == GameState.GAME_OVER && keyHandler.rPressed) {
            restartGame();
        }
        tileManager.updateTraps();


    }

    private void restartGame() {
        player.setDefaultValues();
        player.health.fullHeal();
        player.clearInventory();

        enemies.clear();
        spawnEnemies();

        gameState = GameState.PLAY;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        tileManager.draw(g2d);
        player.draw(g2d);
        for (Enemy e: enemies) {
            e.draw(g2d);
        }

        if (gameState == GameState.GAME_OVER) {
            gameOverScreen(g2d);
        }
        if(gameState == GameState.GAME_WON) {
            drawVictoryScreen(g2d);
        }
        topPanel.draw(g2d, screenWidth);
        drawInventory(g2d);
        dialogueBox.draw(g2d, screenWidth, screenHeight);
        g2d.dispose();
    }

    private void gameOverScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0,0,0,100));
        g2d.fillRect(0,0,screenWidth, screenHeight);

        g2d.setFont(new Font("Comic Sans", Font.BOLD, 72));
        g2d.setColor(Color.red);
        String message = "YOU DIED!";
        int messageWidth = g2d.getFontMetrics().stringWidth(message);
        g2d.drawString(message, (screenWidth - messageWidth)/2, screenHeight/2);

        g2d.setFont(new Font("Comic Sans", Font.PLAIN, 32));
        g2d.setColor(Color.white);
        String subMessage = "Press R to restart";
        int subWidth = g2d.getFontMetrics().stringWidth(subMessage);
        g2d.drawString(subMessage, (screenWidth - subWidth)/2, (screenHeight/2) + 60);
    }

    public void spawnEnemies() {

        int enemyCount = 20;

        enemies.clear();
        int keyHolderIndex = (int) (Math.random()*enemyCount);

        for (int i = 0; i < enemyCount; i++ ) {
            int[] spawnPoints = tileManager.getValidTile();

            int col = spawnPoints[0];
            int row = spawnPoints[1];

            int worldX = col * tileSize;
            int worldY = row * tileSize;

            Enemy enemy = new Enemy(this, pathfinder, player, worldX, worldY);

            if(i == keyHolderIndex) {
                enemy.hasKey = true;
            }
            enemies.add(enemy);
        }
    }

    public void dropKey(int worldX, int worldY) {
        droppedKey = new KeyItem(worldX, worldY);
    }

    public void drawInventory(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRoundRect(10, 80, 200, 60, 15, 15);

        g2d.setFont(new Font("Comic Sans", Font.BOLD, 10));
        g2d.setColor(Color.white);
        g2d.drawString("Inventory", 20, 80);

        if (keyIcon != null) {
            g2d.drawImage(keyIcon, 25, 100, tileSize/2, tileSize/2, this);
        }

        Integer keyCount = player.getInventory().get("key");
        if(keyCount != null && keyCount > 0) {
            g2d.setFont(new Font("Comic Sans", Font.PLAIN, 10));
            g2d.drawString("x" + keyCount, 25 + tileSize + 5, 120);
        }
    }
    public void drawVictoryScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(0, 0, screenWidth, screenHeight);

        g2d.setFont(new Font("Comic Sans", Font.BOLD, 48));
        g2d.setColor(Color.green);
        String message = "You Win!";
        int messageWidth = g2d.getFontMetrics().stringWidth(message);
        g2d.drawString(message, (screenWidth - messageWidth)/2, screenHeight/2 - 100);

        g2d.setFont(new Font("Comic Sans", Font.PLAIN, 32));
        String score = "Final Score" + finalScore;
        int scoreWidth = g2d.getFontMetrics().stringWidth(score);
        g2d.drawString(score, (screenWidth - scoreWidth)/2, screenHeight/2 - 30);

        g2d.setFont(new Font("Comic Sans", Font.PLAIN,24 ));
        g2d.setColor(Color.white);
        String endingMessage = "Press ENTER to exit";
        int endingWidth = g2d.getFontMetrics().stringWidth(endingMessage);
        g2d.drawString(endingMessage, (screenWidth - endingWidth)/2, screenHeight/2 + 40);
    }

    public URL getResourceAsImage(String path) {
        return getClass().getClassLoader().getResource(path);
    }
}
