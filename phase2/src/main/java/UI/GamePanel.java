/*
* This is the main class that handles loading the ui that we will be using, try not to change it too much, any changes
* made to this file need to be discussed across the group, because it will literally cause many changes to occur across
* the entire project.
*
* The remaining thing left in relation to UI, is to implement our mockup, we will format this into a formal JavaDoc then.
* */


package UI;

import Entity.Player;
import Tile.TileManager;
import UI.displayComp.topPanel;
import UI.displayComp.dialogueBox;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePanel extends JPanel implements Runnable {
    //SCREEN SETTINGS
    final int originalTileSize = 16; //16x16 tile
    final int scale = 3;

    public int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; //768 pixels
    public final int screenHeight = tileSize * maxScreenRow; //576 pixels

    //FPS
    final double FPS = 60.0;
    TileManager tileManager = new TileManager(this);

    KeyHandler keyHandler = new KeyHandler();
    Thread gameThread;
    Player player = new Player(this, keyHandler);

    // ----- Adding the UI components 
    private final topPanel topPanel = new topPanel(); 
    private final dialogueBox dialogueBox = new dialogueBox(); 

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

        // Creating some input for the dialogue box 
        dialogueBox.loadLine(
            "Welcome to the Dungeon", 
            "Beware of traps and enemies player!!", 
            "ANNND dont forget to find the keys to escape rooms"
        );
        dialogueBox.toggle();

        // Adding functioning clicks 
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                handleButtonClick(e); 
            }
        });
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

    public void update() {
        player.update();

    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        // Draws the actual game layout 
        tileManager.draw(g2d);
        player.draw(g2d);
        // Draws the top panel with haptics buttons 
        topPanel.draw(g2d, screenWidth); 
        dialogueBox.draw(g2d, screenWidth, screenHeight); 
        g2d.dispose();
    }

    // mouse event helper function 
    private void handleButtonClick(MouseEvent e){
        topPanel.ActionBar action = topPanel.clickButton(e); 
        // creating switch cases for the buttons 
        switch (action){
            case Button_Pause -> {
                System.out.println("Paused =" + topPanel.isPaused());
            }
            case Button_Sound -> {
                System.out.println("Mute =" + topPanel.isMuted());
            }
            case Zoom_In, Zoom_Out -> {
                System.out.println("Zoomed to =" + topPanel.getZoom());
            }
            case Button_Help -> {
                dialogueBox.toggle();
                repaint();
            }
            case Button_Back -> {
                System.out.println("Back button pressed");
            }
            case Button_EXIT -> {
                System.out.println("Exit button pressed");
                System.exit(0); 
            }
            case none ->{

            }
        }

    }

    }
