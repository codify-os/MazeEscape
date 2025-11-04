/** topPanel.java */
// Will contain the general buttons or haptics such as sound, zoom, pause, back, dialogue box, & exit button 

package UI.displayComp;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * Work in progress topPanel for the initial start up of the game 
 *  

*/


/**
 * Want to draw out the bar and button within it 
 * Also  get lay out of the button correct 
 */

 // Estimate layout 
 public class topPanel{
    private static final int Bar_Height     = 68;  // height of the top bar
    private static final int Bttn_Width     = 70;  // button width
    private static final int Bttn_Height    = 28;  // button height
    private static final int Bar_Gap_Space  = 12;  // left/right padding
    private static final int Bttn_Gap       = 8;   // gap between buttons

// indicating state of the button for the ones that are yes or no AKA Boolean
  
    private boolean pause = false;
    private boolean mute  = false;
    private double  zoom   = 1.0;

// hashmap set up for the top panel 
public enum ActionBar {
    Zoom_In , Zoom_Out, Button_Pause, Button_Sound, Button_Back, Button_Help, Button_EXIT, none}
    private final Map<ActionBar, Rectangle> box = new LinkedHashMap<>();

    // creating the top bar functions using the states and button functions from above 
    public int getHeight() { 
                                return Bar_Height; }
    public boolean isPaused() { 
                                return pause; }
    public boolean isMuted()  { 
                                return mute;  }
    public double  getZoom()  { 
                                return zoom;   }

    public topPanel() {
            // empty boxes are created 
    }

    /**
     * Drawing the bar, and using the buttons and actions created 
     * * graphcis2D makes edges of java pixels look a but nicer 
     */
    public void draw(Graphics2D bar_graphics, int panelWidth){
        // The back ground colour 
        bar_graphics.setColor(new Color(0x3B2F2F));
        bar_graphics.fillRect(0, 0, panelWidth, Bar_Height);

        // for nicer icons and icon edges 
        bar_graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Title and buttons seperation -- title first , Buttons second 
        // adjusing the texts within the boxes/ buttons 
        bar_graphics.setColor(new Color(0xFFFDD0));  // creamy white color
        Font oldFont = bar_graphics.getFont();
        bar_graphics.setFont(oldFont.deriveFont(Font.BOLD, 18f));

        final String gameTitle = "Dungeon Escape";
        int textWidth = bar_graphics.getFontMetrics().stringWidth(gameTitle);
        int topP_baseLine = 20; 
        bar_graphics.drawString(gameTitle, (panelWidth - textWidth) / 2, topP_baseLine);
        bar_graphics.setFont(oldFont);

       // Buttons on botton of the top panel 
       int x = Bar_Gap_Space;
       int y = Bar_Height - Bttn_Height - 10; // centering the box 

       // when boxes are empty, clear no action is being taken 
       box.clear();
       x = drawsButton(bar_graphics, x, y, ActionBar.Zoom_In,  "+");
       x = drawsButton(bar_graphics, x, y, ActionBar.Zoom_Out, "-");
       x = drawsButton(bar_graphics, x, y, ActionBar.Button_Sound,    pause? "▶" : "⏸");
       x = drawsButton(bar_graphics, x, y, ActionBar.Button_Pause, pause ? "PLAY" : "PAUSE");
       x = drawsButton(bar_graphics, x, y, ActionBar.Button_Sound, mute ? "MUTE" : "SOUND");
       x = drawsButton(bar_graphics, x, y, ActionBar.Button_Back,  "BACK");

        // Bottom border line bounds 
        bar_graphics.setColor(new Color(0xFFFDD0));
        bar_graphics.drawLine(0, Bar_Height - 1, panelWidth, Bar_Height - 1);

        // Right aligned exit button
        int rightX = panelWidth - Bar_Gap_Space - Bttn_Width;
        draw_ButtonAt(bar_graphics, rightX, y, ActionBar.Button_EXIT, "EXIT");
        
    }
    
    /**
     * Creating button clicking function 
     * determines which button is pressed 
     */
    public ActionBar click(MouseEvent e) {
        int mouse_x = e.getX(); // mouse coordinates variables 
        int mouse_y = e.getY();
        if (mouse_y < 0 || mouse_y > Bar_Height) return ActionBar.none;

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
            case Button_Pause -> pause = !pause; 
            case Button_Sound -> mute = !mute; 
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
        bar_graphics.setColor(new Color(0xC65D57));  // muted tomato red
        bar_graphics.fillRect(rectBase.x, rectBase.y, rectBase.width, rectBase.height);

        // button border 
        bar_graphics.setColor(new Color(255, 255, 255, 60)); // hoping to get shadowy mist 
        bar_graphics.drawRect(rectBase.x, rectBase.y, rectBase.width, rectBase.height);

        // location and of titles and texts on the box 
        bar_graphics.setColor(new Color(0xFFFDD0));
        FontMetrics fontSize = bar_graphics.getFontMetrics(); 
        int textX = rectBase.x + (rectBase.width - fontSize.stringWidth(label))/2; 
        int textY = rectBase.y + (rectBase.height + fontSize.getAscent() - fontSize.getDescent())/2; 
        bar_graphics.drawString(label, textX, textY);
  }
} 