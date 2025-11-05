//menuButton.java
// When player presses this button it directs them to a menue where there's four options
// Start, Resume, Setting, Exit 
// other button like a question mark to indicate help button can be added

package UI.displayComp;

import java.awt.*;
import java.awt.event.MouseEvent;

public class menuButton{
    // the names of the seperate buttons on the menu 
    public enum menutButton_actions { START, RESUME, SETTINGS, EXIT, none } 
    // clickable regions on the menu 
    private Rectangle menuPanel, STARTB, RESUMEB, SETTINGSB, EXITB; 
    private boolean view = false; 

    private static final int menuWidth = 260; 
    private static final int menuHeight = 220; 
    private static final int buttnWidth = 200; 
    private static final int buttnHeight = 35; 
    private static final int buttnGap = 11; 

    // some basic visibility function 
    public void hideMenu(){
                            view = false;}
    public void showMenu(){
                            view = true;}
    public boolean isMenu_shown(){
                            return view;}                       
    public void toggle(){ // between show and hide 
                                view = !view;}

    // function to draw the menue on the screen and make sure that it is laso centered 
    public void draw(Graphics2D bar_Graphics, int screenWidth, int screenHeight){ 
        // checking if screen is visible 
        if (!view) return; 
        bar_Graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // to make corners and graphics nicer 
        
        // dimming for when the menu is pressed 
        bar_Graphics.setColor(new Color(0,0,0,120));
        bar_Graphics.fillRect(0,0, screenWidth, screenHeight);

        // centers the menue panel 
        int x = (screenWidth - menuWidth)/2; 
        int y = (screenHeight - menuHeight)/2; 
        menuPanel = new Rectangle(x, y, menuWidth, menuHeight); 

        // drawing the panel + colors 
        bar_Graphics.setColor(new Color(0x2F2728));
        bar_Graphics.fillRoundRect(x, y, menuWidth, menuHeight, 16, 16);
        bar_Graphics.setColor(new Color(255,255,255,60));
        bar_Graphics.drawRoundRect(x, y, menuWidth, menuHeight, 16, 16);

        // creating the titles and centering it on the menu 
        bar_Graphics.setColor(new Color(0xFFFDD0)); 
        Font old = bar_Graphics.getFont(); 
        bar_Graphics.setFont(old.deriveFont(Font.BOLD,18f)); 
        String menu = "MENU";
        int textWidth = bar_Graphics.getFontMetrics().stringWidth(menu);
        bar_Graphics.drawString(menu, x + (menuWidth - textWidth)/2, y + 28);
        bar_Graphics.setFont(old); 

        // creating the buttons + each buttons characteristics 
        int buttnX = x + (menuWidth - buttnWidth)/2; 
        int buttnY = y + 55; 

        STARTB = new Rectangle(buttnX, buttnY, buttnWidth, buttnHeight); 
                buttnY += buttnHeight + buttnGap; 
        RESUMEB = new Rectangle(buttnX, buttnY, buttnWidth, buttnHeight); 
                buttnY += buttnHeight + buttnGap; 
        SETTINGSB = new Rectangle(buttnX, buttnY, buttnWidth, buttnHeight); 
                buttnY += buttnHeight + buttnGap; 
        EXITB = new Rectangle(buttnX, buttnY, buttnWidth, buttnHeight); 

        // drawing the actual buttons created 




    }

    public void drawsButton


















}
