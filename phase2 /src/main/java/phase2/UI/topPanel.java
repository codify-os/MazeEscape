/** topPanel.java */
// Will contain the general buttons or haptics such as sound, zoom, pause, back, dialogue box, & exit button 

package phase2.UI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Want to draw out top bar panel of the game with the correct placement of the needed buttons  
 * These buttons will be clickable 
 */

 public class topPanel{
    private static final int Bar_Height = 68;  // height of the top bar
    private static final int Bttn_Width = 70;  // button width
    private static final int Bttn_Height = 28;  // button height
    private static final int Bar_Gap_Space = 12;  // left/right padding
    private static final int Bttn_Gap = 8;   // gap between buttons

// indicating state of the button for the ones that are yes or no AKA Boolean
    private boolean pause = false;
    private boolean mute  = false;
    private double  zoom   = 1.0;


    private GamePanel gp;



    // Music manager reference
    private MusicManager musicManager;

// hashmap set up for the top panel 
    public enum ActionBar {
        Zoom_In , Zoom_Out, Button_Pause, Button_Sound, Button_Back, Button_Help, Button_EXIT, none}
    
    private final Map<ActionBar, Rectangle> box = new LinkedHashMap<>();

// declaring colours of the panel here to avoid repetition of code 
    private static final Color bar_backgroud_color = new Color(0x3B2F2F); // deep brown 
    private static final Color bar_border_color = new Color(0xFFFDD0); // creamy white 
    private static final Color button_background_color = new Color(0xC65D57); // muted red 
    private static final Color button_border_color = new Color(255, 255, 255, 60); // white mistyness 
    private static final Color text_color = new Color(0xFFFDD0); // creamy white 

    // creating the top bar functions using the states and button functions from above -- AKA getters and setters 
    public int getHeight() { return Bar_Height; }

    public boolean isPaused() { return pause; }

    public boolean isMuted()  { return mute;  }

    public double  getZoom()  { return zoom;   }

    public topPanel() {
            // empty boxes are created 
            }

    // setter for MusicManager
    public void setMusicManager(MusicManager manager) {
        this.musicManager = manager;
        if (mute) {
            musicManager.setMuted(true);
        }
    }

    public void setGamePanel(GamePanel gp) {
    this.gp = gp;
}

    /**
     * Drawing the bar, and using the buttons and actions created 
     * * graphcis2D makes edges of java pixels look a but nicer 
     */
    public void draw(Graphics2D bar_graphics, int panelWidth, boolean gameOver) {
    // Background
    bar_graphics.setColor(bar_backgroud_color);
    bar_graphics.fillRect(0, 0, panelWidth, Bar_Height);

    // Anti-aliasing for nicer edges
    bar_graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Title
    bar_graphics.setColor(text_color);
    Font oldFont = bar_graphics.getFont();
    bar_graphics.setFont(oldFont.deriveFont(Font.BOLD, 18f));
    final String gameTitle = "Dungeon Escape";
    int textWidth = bar_graphics.getFontMetrics().stringWidth(gameTitle);
    int top_baseLine = 20; 
    bar_graphics.drawString(gameTitle, (panelWidth - textWidth) / 2, top_baseLine);
    bar_graphics.setFont(oldFont);

    // Bottom border line
    bar_graphics.setColor(bar_border_color);
    bar_graphics.drawLine(0, Bar_Height - 1, panelWidth, Bar_Height - 1);

    // Buttons
    int y = Bar_Height - Bttn_Height - 10;
    int x = Bar_Gap_Space;

    box.clear(); // clear old buttons

    if (!gameOver) {
        // Normal buttons when game is not over
        x = drawsButton(bar_graphics, x, y, ActionBar.Zoom_In, "+");
        x = drawsButton(bar_graphics, x, y, ActionBar.Zoom_Out, "-");

        String soundButton = mute ? "MUTE" : "SOUND"; 
        x = drawsButton(bar_graphics, x, y, ActionBar.Button_Sound, soundButton); 

        String pauseButton;
        if (gp != null && gp.gameState == GamePanel.GameState.START_SCREEN) {
            pauseButton = "PLAY";   // initial label
            }
        else {
                pauseButton = pause ? "PLAY" : "PAUSE";
        }
        x = drawsButton(bar_graphics, x, y, ActionBar.Button_Pause, pauseButton); 

        x = drawsButton(bar_graphics, x, y, ActionBar.Button_Back, "BACK"); 
        x = drawsButton(bar_graphics, x, y, ActionBar.Button_Help, "?");
    }

    // EXIT button is always visible
    int rightX = panelWidth - Bar_Gap_Space - Bttn_Width;
    draw_ButtonAt(bar_graphics, rightX, y, ActionBar.Button_EXIT, "EXIT");
}


    
    /**
     * Creating button clicking function 
     * determines which button is pressed 
     */
    public ActionBar clickButton(MouseEvent e) {
        int mouse_x = e.getX(); // mouse coordinates variables 
        int mouse_y = e.getY();
        if (mouse_y < 0 || mouse_y > Bar_Height) { return ActionBar.none;}

        // case where mouse is being used + clicks on a button 
        for (Map.Entry<ActionBar, Rectangle> button : box.entrySet()) {
            if (button.getValue().contains(mouse_x, mouse_y)) {
                ActionBar click = button.getKey();
                pressButton(click);
                return click;
            }
        }
        return ActionBar.none;
    }

    // using apply action function created to have button pressing affect - might not be needed 
    private void pressButton (ActionBar click){
        // creating switch cases to create an action for each button when pressed 
        // ex) sound volume from mute -> unmute, same goes for other buttosn too 
        switch(click){ 
            case Zoom_In -> zoom = Math.min(2.0, zoom + 0.1);
            case Zoom_Out -> zoom = Math.max(0.5, zoom - 0.1); 
            case Button_Pause -> {
                if (gp != null) {
                    // If game is on START SCREEN, pressing Play begins the game
                    if (gp.gameState == GamePanel.GameState.START_SCREEN) {
                        gp.gameState = GamePanel.GameState.PLAY;
                        pause = false;       // ensure game runs
                        } 
                        else {
                            // toggle pause normally
                        pause = !pause;
                    }}}
             case Button_Sound -> {
                mute = !mute;
                if (musicManager != null) {
                    musicManager.setMuted(mute); // toggle music
                }
            }
            default -> { /* cannot think of anything else for now, might be other future cases */}
        }
    }


    // Helper function to help draw a button and space it correctly 
    private int drawsButton(Graphics2D bar_graphics, int x, int y, ActionBar action, String label) {
        draw_ButtonAt(bar_graphics, x, y, action, label);
        return x + Bttn_Width + Bttn_Gap;
    }

    /**
     * section draws the genuine rectangle, creates one rectangle with the desired affects "Hopefully"
     * 
     */
    private void draw_ButtonAt(Graphics2D bar_graphics, int x, int y, ActionBar action, String label){ 
        // rectangle shape 
        Rectangle rectBase = new Rectangle(x, y, Bttn_Width, Bttn_Height); 
        box.put(action,rectBase); 

        // Button background base 
        bar_graphics.setColor(button_background_color);  // muted tomato red
        bar_graphics.fillRect(rectBase.x, rectBase.y, rectBase.width, rectBase.height);

        // button border 
        bar_graphics.setColor(button_border_color); // hoping to get shadowy mist 
        bar_graphics.drawRect(rectBase.x, rectBase.y, rectBase.width, rectBase.height);

        // location and of titles and texts on the box 
        bar_graphics.setColor(text_color);
        FontMetrics fontSize = bar_graphics.getFontMetrics(); 
        int textX = rectBase.x + (rectBase.width - fontSize.stringWidth(label))/2; 
        int textY = rectBase.y + (rectBase.height + fontSize.getAscent() - fontSize.getDescent())/2; 
        bar_graphics.drawString(label, textX, textY);
  }
} 