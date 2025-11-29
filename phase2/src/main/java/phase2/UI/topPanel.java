/** 
 * topPanel.java
 * 
 * Represents the top bar panel of the game UI.
 * Contains buttons and haptics such as sound, zoom, pause, reset, help, dialogue box, and exit button.
 * Handles drawing the top bar, button interactions, and related states.
 */

package phase2.UI;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import phase2.UI.ActionBar;

/**
 * Class responsible for drawing and managing the game's top panel.
 * Includes the placement of buttons and handling click events.
 * Provides functionality to toggle pause, sound, reset the game, and adjust zoom.
 */
public class topPanel {

    /** Height of the top bar in pixels. */
    private static final int Bar_Height = 68;

    /** Width of each button in pixels. */
    private static final int Bttn_Width = 70;

    /** Height of each button in pixels. */
    private static final int Bttn_Height = 28;

    /** Padding for the top bar from the left and right edges. */
    private static final int Bar_Gap_Space = 12;

    /** Gap between adjacent buttons in pixels. */
    private static final int Bttn_Gap = 8;

    /** Indicates whether the game is currently paused. */
    private boolean pause = false;

    /** Indicates whether the sound is muted. */
    private boolean mute = false;

    /** Current zoom factor of the game view. */
    private double zoom = 1.0;

    /** Reference to the game panel. */
    private GamePanel gp;

    /** Reference to the music manager. */
    private MusicManager musicManager;

    /** Stores button rectangles for click detection. */
    private final Map<ActionBar, Rectangle> box = new LinkedHashMap<>();

    /** Background color of the top bar. */
    private static final Color bar_backgroud_color = new Color(0x3B2F2F);

    /** Border color of the top bar. */
    private static final Color bar_border_color = new Color(0xFFFDD0);

    /** Background color of buttons. */
    private static final Color button_background_color = new Color(0xC65D57);

    /** Border color of buttons. */
    private static final Color button_border_color = new Color(255, 255, 255, 60);

    /** Text color for button labels and title. */
    private static final Color text_color = new Color(0xFFFDD0);

    /** Returns the height of the top bar. */
    public int getHeight() { return Bar_Height; }

    /** Returns true if the game is currently paused. */
    public boolean isPaused() { return pause; }

    /** Returns true if the game sound is muted. */
    public boolean isMuted() { return mute; }

    /** Returns the current zoom factor of the game. */
    public double getZoom() { return zoom; }

    /** Default constructor initializes an empty top panel. */
    public topPanel() { }

    /**
     * Sets the music manager for controlling sound.
     * @param manager the MusicManager instance
     */
    public void setMusicManager(MusicManager manager) {
        this.musicManager = manager;
        if (mute) {
            musicManager.setMuted(true);
        }
    }

    /**
     * Sets the associated game panel.
     * @param gp the GamePanel instance
     */
    public void setGamePanel(GamePanel gp) {
        this.gp = gp;
    }

    /** Sets the paused state of the game. */
    public void setPause(boolean value) {
        pause = value;
    }

    /**
     * Adjusts the zoom factor of the game view.
     * Clamps the value between 0.5 and 2.0.
     * @param delta the amount to adjust zoom by
     */
    public void adjustZoom(double delta) {
        zoom = Math.max(0.5, Math.min(2.0, zoom + delta));
    }

    /**
     * Toggles the pause state of the game.
     * @param gp the GamePanel instance
     */
    public void togglePause(GamePanel gp) {
        if (gp == null) {
            pause = !pause;
            return;
        }

        if (gp.gameState == GamePanel.GameState.START_SCREEN) {
            gp.gameState = GamePanel.GameState.PLAY;
            pause = false;
        } else {
            pause = !pause;
        }
    }

    /**
     * Toggles the sound on or off using the music manager.
     * @param musicManager the MusicManager instance
     */
    public void toggleSound(MusicManager musicManager) {
        mute = !mute;
        if (musicManager != null) {
            musicManager.setMuted(mute);
        }
    }

    /** Resets the top panel state to default values. */
    public void resetGame() {
        pause = false;
        zoom = 1.0;
        mute = false;
        if (musicManager != null) musicManager.setMuted(false);
    }

    /**
     * Draws the game title at the center of the top bar.
     * @param g Graphics2D instance for drawing
     * @param width width of the panel
     */
    private void drawTitle(Graphics2D g, int width) {
        g.setColor(text_color);
        Font oldFont = g.getFont();
        g.setFont(oldFont.deriveFont(Font.BOLD, 18f));

        String title = "Dungeon Escape";
        int textWidth = g.getFontMetrics().stringWidth(title);
        int baseY = 20;

        g.drawString(title, (width - textWidth) / 2, baseY);
        g.setFont(oldFont);
    }

    /**
     * Draws the top bar with buttons and title.
     * @param bar_graphics Graphics2D instance for drawing
     * @param panelWidth width of the panel
     * @param gameOver true if the game is over, buttons may be disabled
     */
    public void draw(Graphics2D bar_graphics, int panelWidth, boolean gameOver) {
        bar_graphics.setColor(bar_backgroud_color);
        bar_graphics.fillRect(0, 0, panelWidth, Bar_Height);
        bar_graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawTitle(bar_graphics, panelWidth);

        bar_graphics.setColor(bar_border_color);
        bar_graphics.drawLine(0, Bar_Height - 1, panelWidth, Bar_Height - 1);

        int y = Bar_Height - Bttn_Height - 10;
        int x = Bar_Gap_Space;

        box.clear();

        if (!gameOver) {
            String soundButton = mute ? "MUTE" : "SOUND"; 
            x = drawsButton(bar_graphics, x, y, ActionBar.Button_Sound, soundButton); 

            String pauseButton;
            if (gp != null && gp.gameState == GamePanel.GameState.START_SCREEN) {
                pauseButton = "PLAY";
            } else {
                pauseButton = pause ? "PLAY" : "PAUSE";
            }
            x = drawsButton(bar_graphics, x, y, ActionBar.Button_Pause, pauseButton); 

            x = drawsButton(bar_graphics, x, y, ActionBar.Button_Reset, "RESET"); 
            x = drawsButton(bar_graphics, x, y, ActionBar.Button_Help, "?");
        }

        int rightX = panelWidth - Bar_Gap_Space - Bttn_Width;
        draw_ButtonAt(bar_graphics, rightX, y, ActionBar.Button_EXIT, "EXIT");
    }

    /**
     * Handles a click event on the top panel buttons.
     * @param e MouseEvent instance representing the click
     * @return ActionBar enum corresponding to the clicked button, or ActionBar.none if no button was clicked
     */
    public ActionBar clickButton(MouseEvent e) {
        int mouse_x = e.getX();
        int mouse_y = e.getY();
        if (mouse_y < 0 || mouse_y > Bar_Height) return ActionBar.none;

        for (Map.Entry<ActionBar, Rectangle> button : box.entrySet()) {
            if (button.getValue().contains(mouse_x, mouse_y)) {
                ActionBar click = button.getKey();
                click.execute(this, gp, musicManager);
                return click;
            }
        }
        return ActionBar.none;
    }

    /**
     * Draws a button and returns the next X position for spacing.
     * @param bar_graphics Graphics2D instance
     * @param x X coordinate of button
     * @param y Y coordinate of button
     * @param action ActionBar enum for this button
     * @param label Text label for the button
     * @return next X coordinate after spacing
     */
    private int drawsButton(Graphics2D bar_graphics, int x, int y, ActionBar action, String label) {
        draw_ButtonAt(bar_graphics, x, y, action, label);
        return x + Bttn_Width + Bttn_Gap;
    }

    /**
     * Draws an individual button rectangle with label.
     * Stores the rectangle for click detection.
     * @param bar_graphics Graphics2D instance
     * @param x X coordinate of the button
     * @param y Y coordinate of the button
     * @param action ActionBar enum for this button
     * @param label Text label for the button
     */
    private void draw_ButtonAt(Graphics2D bar_graphics, int x, int y, ActionBar action, String label) { 
        Rectangle rectBase = new Rectangle(x, y, Bttn_Width, Bttn_Height); 
        box.put(action, rectBase); 

        bar_graphics.setColor(button_background_color);
        bar_graphics.fillRect(rectBase.x, rectBase.y, rectBase.width, rectBase.height);

        bar_graphics.setColor(button_border_color);
        bar_graphics.drawRect(rectBase.x, rectBase.y, rectBase.width, rectBase.height);

        bar_graphics.setColor(text_color);
        FontMetrics fontSize = bar_graphics.getFontMetrics(); 
        int textX = rectBase.x + (rectBase.width - fontSize.stringWidth(label)) / 2; 
        int textY = rectBase.y + (rectBase.height + fontSize.getAscent() - fontSize.getDescent()) / 2; 
        bar_graphics.drawString(label, textX, textY);
    }
}
