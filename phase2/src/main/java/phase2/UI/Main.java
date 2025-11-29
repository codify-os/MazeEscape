package phase2.UI;

import javax.swing.JFrame;

/**
 * Main class to launch the 2D Adventure game.
 * Initializes the main window and starts the game panel thread.
 */
public class Main {

    /**
     * The entry point of the application.
     * Creates a JFrame window, adds the GamePanel, and starts the game loop.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Create the main game window
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("2D Adventure");

        // Create and add the game panel
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Start the game loop thread
        gamePanel.startGameThread();
    }
}
