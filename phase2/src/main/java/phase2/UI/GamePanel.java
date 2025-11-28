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
import phase2.Bonus.ChestItem;
import phase2.Bonus.CrystalItem;
import phase2.Entity.BigBoss;
import phase2.Entity.Enemy;
import phase2.Entity.Spider;

import phase2.Entity.Pathfinder;
import phase2.Entity.PhantomMinion;
import phase2.Tile.TileManager;
import phase2.game.combat.*;
import javax.swing.JPanel;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;



/**
 * Main game panel that handles the game loop and rendering
 */
public class GamePanel extends JPanel implements Runnable {
    // SCREEN SETTINGS
    final int originalTileSize = 16;
    final int scale = 3;
    public int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    public MusicManager musicManager;

    public List<CrystalItem> crystals = new ArrayList<>();
    public Image crystalIcon;

    public int totalCrystals = 20 + new Random().nextInt(10); // at 20 crystals on the map
    private final int MAX_ACTIVE_CRYSTALS = 15; // maintain this many crystals on the map
    public int crystalsCollected = 0;
    public boolean crystalBonusApplied = false;

    public List<ChestItem> chests = new ArrayList<>();

    Image chestClosed;
    Image chestOpenEmpty;
    private Image chestOpenFull;




    // FPS
    final double FPS = 60.0;

    public TileManager tileManager;
    public KeyHandler keyHandler = new KeyHandler();
    Thread gameThread;

    /** Collision detection system */
    public CheckCollision checkCollision;
    public Player player;
    public Pathfinder pathfinder;

    /** List of all active enemies */
    public List<Enemy> enemies = new ArrayList<>();
    /** Dropped key item if any */
    public KeyItem droppedKey = null;

    public List<Enemy> enemiesToAdd = new ArrayList<>();
    public List<Enemy> enemiesToRemove = new ArrayList<>();


    private final dialogueBox dialogueBox = new dialogueBox();
    private final topPanel topPanel = new topPanel();
    private final Image keyIcon;


// --- BIG BOSS ALERT FLASH ---
    public boolean bossFlashActive = false;
    public long bossFlashStart = 0;
    public final int BOSS_FLASH_DURATION = 2000; // 2s

    // --- BIG BOSS WARNING TEXT ---
    public boolean bossWarningActive = false;
    public long bossWarningStart = 0;
    public final int BOSS_WARNING_DURATION = 2000; // show for 2 seconds
    
    
    //Status flags
    /** Enum representing different game states */
    public enum GameState {
        PLAY,
        GAME_WON,
        GAME_OVER,
        START_SCREEN
    }
    
    /** Current game state */
    public GameState gameState = GameState.START_SCREEN;
    /** Final score at end of game */
    public int finalScore = 0;

    // Timer variables
    private long gameStartTime = 0;
    private long totalPlayTime = 0; // in milliseconds

    // Big Play Button
    private Rectangle playButtonRect;
    private boolean playButtonHovered = false;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent click) {
                // Check if big play button is clicked
                if (gameState == GameState.START_SCREEN && playButtonRect != null) {
                    if (playButtonRect.contains(click.getPoint())) {
                        gameState = GameState.PLAY;
                        dialogueBox.hide_Dialogue();
                        return;
                    }
                }
                topPanel.clickButton(click);  // simplified, no switch needed
                dialogueBox.skipClick(click, screenWidth, screenHeight);
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                if (gameState == GameState.START_SCREEN && playButtonRect != null) {
                    playButtonHovered = playButtonRect.contains(e.getPoint());
                    repaint();
                }
            }
        });

        // Initialize game components
        tileManager = new TileManager(this);
        checkCollision = new CheckCollision(this);
        player = new Player(this, keyHandler);
        pathfinder = new Pathfinder(tileManager);

        spawnEnemies();
        spawnCrystals();
        spawnChests(); 

        CombatManager.addListener(new CombatLogger(true));

        dialogueBox.loadLine(
                "Welcome to the dungeon!",
                "Find a key to escape the dungeon but beware of the enemies and traps",
                "Fight the enemies to get the key",
                "Press PLAY to Start",
                "HAVE FUN PLAYER !!"
        );
        dialogueBox.show_Dialogue();

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        keyIcon = toolkit.getImage(getResourceAsImage(
                "Top_Down_Adventure_Pack_v.1.0/Props_Items_(animated)/key_item_anim.gif"));
        crystalIcon = Toolkit.getDefaultToolkit().getImage(
    getResourceAsImage("Top_Down_Adventure_Pack_v.1.0/Props_Items_(animated)/crystal_item_anim.gif"));

         // --- MUSIC INITIALIZATION ---
    String[] tracks = {"/musics/epic-battle-sound-9414.mp3", "/musics/horde-war-drums-loop-130bpm-342956.mp3"};

    musicManager = new MusicManager(tracks);  // initialize with tracks
    musicManager.play();                      // start looping music

    // --- LINK MUSIC MANAGER TO TOP PANEL ---
    topPanel.setMusicManager(musicManager);   // allows sound button to mute/unmute
    topPanel.setGamePanel(this);

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

    public void checkCrystalCollection() {

    Rectangle playerHitBox = new Rectangle(
            player.worldX + player.collisionArea.x,
            player.worldY + player.collisionArea.y,
            player.collisionArea.width,
            player.collisionArea.height
    );

    for (CrystalItem c : crystals) {
        if (!c.collected) {
            Rectangle crystalHitBox = new Rectangle(
                    c.worldX,
                    c.worldY,
                    tileSize,
                    tileSize
            );

            if (playerHitBox.intersects(crystalHitBox)) {
                c.collected = true;
                crystalsCollected++;
                player.addItem("crystal");
            }
        }
    }
}


    public void checkMapSwitch() {
        int playerCol = player.worldX / tileSize;
        int playerRow = player.worldY / tileSize;

        if (playerCol >= maxWorldCol) player.worldX = 0;
        if (playerCol < 0) player.worldX = (maxWorldCol - 1) * tileSize;
        if (playerRow >= maxWorldRow) player.worldY = 0;
        if (playerRow < 0) {
            tileManager.nextMap();
            player.worldY = (maxWorldRow - 1) * tileSize;
        }
    }

   public void update() {
    // NEW: do nothing until user presses PLAY
    if (gameState == GameState.START_SCREEN) return;

    // Start timer when game begins
    if (gameStartTime == 0 && gameState == GameState.PLAY) {
        gameStartTime = System.currentTimeMillis();
        // Start crystal despawn timers when game starts
        for (CrystalItem c : crystals) {
            c.startTimer();
        }
    }

    if(gameState == GameState.GAME_WON){
        if  (keyHandler.enterPressed){
            System.exit(0);
        }
        return;
    }
    
    // Check for game over and allow restart
    if (!player.isAlive()) {
        if (gameState != GameState.GAME_OVER) {
            // Calculate total play time when game ends
            totalPlayTime = System.currentTimeMillis() - gameStartTime;
        }
        gameState = GameState.GAME_OVER;
    }

    if (gameState == GameState.GAME_OVER) {
        if (keyHandler.rPressed) {
            restartGame();
        }
        return; // Stop updating game when dead
    }

    if (keyHandler.spacePressed) {
        dialogueBox.jumptoNext();
        keyHandler.spacePressed = true;
    }

    if (topPanel.isPaused()) return;

    // Update player
    player.update();
    player.updateCooldown();

    // Update crystals (check for despawn)
    for (CrystalItem c : crystals) {
        c.update();
    }
    
    // Respawn crystals to maintain count
    maintainCrystalCount();
    
    checkCrystalCollection();
    checkChestInteraction();


    // Update enemies safely
    List<Enemy> enemiesCopy = new ArrayList<>(enemies); // iterate over a copy
    for (Enemy e : enemiesCopy) {
        e.update();
        e.updateCooldown();
    }

    // Apply pending enemy additions/removals
    if (!enemiesToAdd.isEmpty()) {
        enemies.addAll(enemiesToAdd);
        enemiesToAdd.clear();
    }
    if (!enemiesToRemove.isEmpty()) {
        enemies.removeAll(enemiesToRemove);
        enemiesToRemove.clear();
    }

    checkMapSwitch();

    tileManager.updateTraps();
}

    public dialogueBox getDialogueBox() {
    return dialogueBox;
}

    public void restartGame() {
        player.setDefaultValues();
        player.health.fullHeal();
        player.clearInventory();

        enemies.clear();
        spawnEnemies();

        // Reset timer
        gameStartTime = System.currentTimeMillis();
        totalPlayTime = 0;

        gameState = GameState.PLAY;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        topPanel.draw(g2d, screenWidth, gameState == GameState.GAME_OVER); 
        tileManager.draw(g2d);
        
        // --- DRAW CRYSTALS ---
        for (CrystalItem c : crystals) {
            if (c.isActive()) {
                int screenX = c.worldX - player.worldX + player.screenX;
                int screenY = c.worldY - player.worldY + player.screenY;

            // Only draw if visible on screen
            if (screenX + tileSize > 0 && screenX < screenWidth && screenY + tileSize > 0 && screenY < screenHeight) {
                g2d.drawImage(
                    c.sprite,
                    screenX,
                    screenY,
                    tileSize,
                    tileSize,
                    this
                );
            }}}

        // DRAW CHESTS
        for (ChestItem c : chests) {
            int sx = c.worldX - player.worldX + player.screenX;
            int sy = c.worldY - player.worldY + player.screenY;
            if (sx + tileSize > 0 && sx < screenWidth &&
                sy + tileSize > 0 && sy < screenHeight) {
                    g2d.drawImage(
                        c.getCurrentSprite(),
                        sx, sy,
                        tileSize, tileSize,
                        this
                    );}}

        player.draw(g2d);     

        // After
        List<Enemy> enemiesCopy = new ArrayList<>(enemies);
        for (Enemy e : enemiesCopy) {
            e.draw(g2d);
        }

        // Draw dropped key if exists
        if (droppedKey != null && !droppedKey.collected) {
            droppedKey.draw(g2d, this);
        }

        // ---- BIG BOSS SCREEN FLASH ----
        if (bossFlashActive) {
            long elapsed = System.currentTimeMillis() - bossFlashStart;
        if (elapsed < BOSS_FLASH_DURATION) {
            // Fade out effect
            float alpha = 1f - (float)elapsed / BOSS_FLASH_DURATION;
            if (alpha < 0) alpha = 0;

            g2d.setColor(new Color(255, 0, 0, (int)(150 * alpha)));
            g2d.fillRect(0, 0, screenWidth, screenHeight);
        } else {
            bossFlashActive = false;
        }
    }
        if (gameState == GameState.START_SCREEN) drawStartScreen(g2d);
        if (gameState == GameState.GAME_OVER) gameOverScreen(g2d);
        if (gameState == GameState.GAME_WON) drawVictoryScreen(g2d);

        topPanel.draw(g2d, screenWidth, gameState == GameState.GAME_OVER);
        drawInventory(g2d);
        dialogueBox.draw(g2d, screenWidth, screenHeight);

         // ---- BIG BOSS WARNING TEXT ----
        if (bossWarningActive) {
            long elapsed = System.currentTimeMillis() - bossWarningStart;

            if (elapsed < BOSS_WARNING_DURATION) {
                g2d.setFont(new Font("Comic Sans", Font.BOLD, 30));
                g2d.setColor(Color.red);

                String msg = "WARNING! BIG BOSS IS COMING!";
                int msgWidth = g2d.getFontMetrics().stringWidth(msg);
                int x = (screenWidth - msgWidth) / 2;
                int y = screenHeight / 2;

                g2d.drawString(msg, x, y);
            } else {
                bossWarningActive = false;
            }
        }
    
        g2d.dispose();
    }

    private void drawStartScreen(Graphics2D g2d) {
        // Semi-transparent overlay
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, screenWidth, screenHeight);

        // Big Play Button dimensions
        int buttonWidth = 300;
        int buttonHeight = 100;
        int buttonX = (screenWidth - buttonWidth) / 2;
        int buttonY = (screenHeight - buttonHeight) / 2;

        // Store button rectangle for click detection
        playButtonRect = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);

        // Button background (changes color on hover)
        if (playButtonHovered) {
            g2d.setColor(new Color(50, 150, 50, 200));
        } else {
            g2d.setColor(new Color(34, 139, 34, 180));
        }
        g2d.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 30, 30);

        // Button border
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 30, 30);

        // Play text
        g2d.setFont(new Font("Comic Sans", Font.BOLD, 60));
        g2d.setColor(Color.WHITE);
        String playText = "PLAY";
        int textWidth = g2d.getFontMetrics().stringWidth(playText);
        g2d.drawString(playText, buttonX + (buttonWidth - textWidth) / 2, buttonY + 70);

        // Subtitle text
        g2d.setFont(new Font("Comic Sans", Font.PLAIN, 20));
        g2d.setColor(new Color(200, 200, 200));
        String subtitle = "Click to start your adventure";
        int subtitleWidth = g2d.getFontMetrics().stringWidth(subtitle);
        g2d.drawString(subtitle, (screenWidth - subtitleWidth) / 2, buttonY + buttonHeight + 40);
    }

    private void gameOverScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(0, 0, screenWidth, screenHeight);

        g2d.setFont(new Font("Comic Sans", Font.BOLD, 72));
        g2d.setColor(Color.red);
        String message = "YOU DIED!";
        int messageWidth = g2d.getFontMetrics().stringWidth(message);
        g2d.drawString(message, (screenWidth - messageWidth) / 2, screenHeight / 2 - 40);

        g2d.setFont(new Font("Comic Sans", Font.PLAIN, 28));
        g2d.setColor(Color.white);
        String timeMessage = "Time Survived: " + formatTime(totalPlayTime);
        int timeWidth = g2d.getFontMetrics().stringWidth(timeMessage);
        g2d.drawString(timeMessage, (screenWidth - timeWidth) / 2, (screenHeight / 2) + 20);

        g2d.setFont(new Font("Comic Sans", Font.PLAIN, 32));
        String subMessage = "Press R to restart";
        int subWidth = g2d.getFontMetrics().stringWidth(subMessage);
        g2d.drawString(subMessage, (screenWidth - subWidth) / 2, (screenHeight / 2) + 60);
    }

    public void spawnChests() {

    chests.clear();

    //FIXED LOCATIONS for CHEST
    int[][] positions = {
            {29, 2},
            {21, 5},
            {40, 33},
            {48, 43}
    };

    Random r = new Random();

    for (int[] p : positions) {

        boolean loot = r.nextBoolean(); // 50% chance full chest

        chests.add(new ChestItem(
                p[0] * tileSize,
                p[1] * tileSize,
                chestClosed = Toolkit.getDefaultToolkit().getImage(getResourceAsImage(
                        "0x72_16x16DungeonTileset.v5/items/chest_golden_closed.png")),
                chestOpenEmpty = Toolkit.getDefaultToolkit().getImage(getResourceAsImage(
                        "0x72_16x16DungeonTileset.v5/items/chest_golden_open_empty.png")),
                chestOpenFull = Toolkit.getDefaultToolkit().getImage(getResourceAsImage(
                        "0x72_16x16DungeonTileset.v5/items/chest_golden_open_full.png")),
                loot
        ));
    }
}


    public void spawnCrystals() {

    crystals.clear();

    for (int i = 0; i < totalCrystals; i++) {
        int[] pos = tileManager.getValidTile(); // you have this method

        int x = pos[0] * tileSize;
        int y = pos[1] * tileSize;

        crystals.add(new CrystalItem(x, y,
                Toolkit.getDefaultToolkit().getImage(
                        getResourceAsImage("Top_Down_Adventure_Pack_v.1.0/Props_Items_(animated)/crystal_item_anim.gif")
                )
        ));
    }
}

    /**
     * Maintains a constant number of active crystals by spawning new ones
     */
    private void maintainCrystalCount() {
        // Count active crystals
        long activeCrystals = crystals.stream().filter(CrystalItem::isActive).count();
        
        // If below threshold, spawn new crystals
        int toSpawn = (int)(MAX_ACTIVE_CRYSTALS - activeCrystals);
        
        for (int i = 0; i < toSpawn; i++) {
            int[] pos = tileManager.getValidTile();
            int x = pos[0] * tileSize;
            int y = pos[1] * tileSize;
            
            CrystalItem newCrystal = new CrystalItem(x, y,
                    Toolkit.getDefaultToolkit().getImage(
                            getResourceAsImage("Top_Down_Adventure_Pack_v.1.0/Props_Items_(animated)/crystal_item_anim.gif")
                    )
            );
            newCrystal.startTimer(); // Start timer immediately for new crystals
            crystals.add(newCrystal);
        }
    }


    // ------------------- UPDATED SPAWN ENEMIES -------------------
    public void spawnEnemies() {
    enemies.clear();

    // ----------------------------
    // 1. Spawn BIG BOSS 
    // ----------------------------

    // near portal to prevent player coming through the door
    int bossCol = maxWorldCol - 10;   
    int bossRow = maxWorldRow - 6;    // bottom row (0-based index)

    int bossX = bossCol * tileSize;
    int bossY = bossRow * tileSize;

    BigBoss bigBoss = new BigBoss(this, pathfinder, player, bossX, bossY);
    enemies.add(bigBoss);

    // ----------------------------
// 2. Spawn normal enemies + spiders + phantoms near portal
// ----------------------------
int enemyCount = 30;
int minSpiders = 5;
int keyHolderIndex = (int) (Math.random() * enemyCount);

// Use BigBoss spawn coordinates as portal center
int portalCol = bossCol;
int portalRow = bossRow;

Random random = new Random();


for (int i = 0; i < enemyCount; i++) {
    int col, row;
    int worldX, worldY;

            // Decide if we spawn a phantom here (e.g., first 3 enemies)
            boolean spawnPhantom = (i < 3); // adjust number as needed
            if (spawnPhantom) {
                // small random offset around portal
                int offsetX = random.nextInt(3) - 1; // -1,0,1
                int offsetY = random.nextInt(3) - 1;
                col = portalCol + offsetX;
                row = portalRow + offsetY;
                worldX = col * tileSize;
                worldY = row * tileSize;

                PhantomMinion phantom = new PhantomMinion(this, pathfinder, player, worldX, worldY);
                enemies.add(phantom);
                continue; // skip normal enemy/spider logic
            }

            // Normal enemy/spider spawn
            int[] spawnPoints = tileManager.getValidTile();
            col = spawnPoints[0];
            row = spawnPoints[1];
            worldX = col * tileSize;
            worldY = row * tileSize;

            Enemy enemy;
            if (i < minSpiders || Math.random() < 0.3) {
                enemy = new Spider(this, pathfinder, player, worldX, worldY);
            } else {
                enemy = new Enemy(this, pathfinder, player, worldX, worldY);
            }

            if (i == keyHolderIndex) {
                enemy.hasKey = true;
            }

            enemies.add(enemy);
        }
}

    public void dropKey(int worldX, int worldY) {
        droppedKey = new KeyItem(worldX, worldY);
    }

    public void checkChestInteraction() {
    if (!keyHandler.ePressed) return;

    Rectangle playerHitBox = new Rectangle(
            player.worldX + player.collisionArea.x,
            player.worldY + player.collisionArea.y,
            player.collisionArea.width,
            player.collisionArea.height
    );

    for (ChestItem c : chests) {

        Rectangle chestBox = new Rectangle(
                c.worldX,
                c.worldY,
                tileSize,
                tileSize
        );

        if (playerHitBox.intersects(chestBox) && !c.opened) {

            c.opened = true;

            if (c.hasLoot) {
                // Give 2 crystals
                player.addItem("crystal");
                player.addItem("crystal");
            }

            break;
        }
    }
}


    /**
     * Checks if the player has collected the key.
     * Handles hitbox calculation and marks the key as collected.
     */
    public boolean checkKeyCollection(Rectangle playerHitBox) {
        if (droppedKey == null || droppedKey.collected) return false;

        Rectangle keyHitBox = new Rectangle(
                droppedKey.worldX,
                droppedKey.worldY,
                tileSize,
                tileSize
        );
        if (playerHitBox.intersects(keyHitBox)) {
            droppedKey.collected = true;
            return true; // Player collected the key
        }

        return false;
    }

    
    public void drawInventory(Graphics2D g2d) {

    // Background box
    g2d.setColor(new Color(0, 0, 0, 120));
    g2d.fillRoundRect(10, 70, 200, 90, 15, 15);

    // Title
    g2d.setFont(new Font("Comic Sans", Font.BOLD, 14));
    g2d.setColor(Color.white);
    g2d.drawString("Inventory", 20, 90);

    // -------------------- KEY DISPLAY --------------------
    if (keyIcon != null) {
        g2d.drawImage(keyIcon,
                20, 100,
                tileSize / 2, tileSize / 2,
                this);
    }

    int keyCount = player.getInventory().getOrDefault("key", 0);
    if (keyCount > 0) {
        g2d.setFont(new Font("Comic Sans", Font.PLAIN, 14));
        g2d.drawString("x" + keyCount, 20 + (tileSize / 2) + 10, 118);
    }

    // -------------------- CRYSTAL DISPLAY --------------------
    if (crystalIcon != null) {
        g2d.drawImage(crystalIcon,
                20, 140,
                tileSize / 2, tileSize / 2,
                this);
    }

    int crystalCount = player.getInventory().getOrDefault("crystal", 0);
    if (crystalCount > 0) {
        g2d.setFont(new Font("Comic Sans", Font.PLAIN, 14));
        g2d.drawString("x" + crystalCount, 20 + (tileSize / 2) + 10, 158);
    }
}


    public void drawVictoryScreen(Graphics2D g2d) {
        // Calculate total play time when won
        if (totalPlayTime == 0 && gameStartTime > 0) {
            totalPlayTime = System.currentTimeMillis() - gameStartTime;
        }

        // -------------------------
        // 2. Calculate crystal bonus
        // -------------------------
        int crystalCount = player.getInventory().getOrDefault("crystal", 0);

        // +2 points per crystal
        int crystalBonus = crystalCount * 2;

        // Add to final score once
        if (!crystalBonusApplied) {
            finalScore += crystalBonus;
            crystalBonusApplied = true;
        }

        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(0, 0, screenWidth, screenHeight);

        g2d.setFont(new Font("Comic Sans", Font.BOLD, 48));
        g2d.setColor(Color.green);
        String message = "You Win!";
        int messageWidth = g2d.getFontMetrics().stringWidth(message);
        g2d.drawString(message, (screenWidth - messageWidth) / 2, screenHeight / 2 - 100);

        g2d.setFont(new Font("Comic Sans", Font.PLAIN, 32));
        String score = "Final Score: " + finalScore;
        int scoreWidth = g2d.getFontMetrics().stringWidth(score);
        g2d.drawString(score, (screenWidth - scoreWidth) / 2, screenHeight / 2 - 30);

        g2d.setFont(new Font("Comic Sans", Font.PLAIN, 28));
        String timeMessage = "Total Time: " + formatTime(totalPlayTime);
        int timeWidth = g2d.getFontMetrics().stringWidth(timeMessage);
        g2d.drawString(timeMessage, (screenWidth - timeWidth) / 2, screenHeight / 2 + 10);

        g2d.setFont(new Font("Comic Sans", Font.PLAIN, 24));
        g2d.setColor(Color.white);
        String endingMessage = "Press ENTER to exit";
        int endingWidth = g2d.getFontMetrics().stringWidth(endingMessage);
        g2d.drawString(endingMessage, (screenWidth - endingWidth) / 2, screenHeight / 2 + 60);
    }

    public URL getResourceAsImage(String path) {
        return getClass().getClassLoader().getResource(path);
    }

    /**
     * Formats time in milliseconds to MM:SS format
     * @param timeMillis Time in milliseconds
     * @return Formatted time string
     */
    private String formatTime(long timeMillis) {
        long seconds = timeMillis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}