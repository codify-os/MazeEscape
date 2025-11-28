/**
 * dialogueBox.java
 * Displays what the character is saying or prompts the game is providing to the character
 * Lines will keep rolling one after another when necessary 
 * Player can skip these prompts with the skip button 
*/ 


package phase2.UI;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List; 

/**
 * UI component for displaying dialogue text with skip functionality
 */
public class dialogueBox {
    // some variable declarations 
    private static final int Marg = 10; 
    private static final int cornerR = 12; 
    private static final int height = 56; 

    // clickable SKIP button on the dialogue box 
    private static final int SkipButtonWidth = 100; 

    private boolean view = false; 
    private final List<String> line = new ArrayList<>(); // to hold a list of all the dialogue lines 
    private int index = 0; // to keep track of the current line 

    /**
     * Load dialogue lines to display
     * @param text Variable number of text strings to display
     */
    public void loadLine(String... text) { 
        line.clear();
        if (text != null) {
            for (String s : text) {
                if (s != null && !s.isEmpty()) {
                    line.add(s);
                }
            }
        }
        index = 0;
    }

    /** Hide the dialogue box */
    public void hide_Dialogue(){ view = false;}
    
    /** Show the dialogue box */
    public void show_Dialogue(){ view = true;}
    
    /**
     * Check if dialogue is currently shown
     * @return true if dialogue is visible
     */
    public boolean isDialogue_shown(){ return view;}
    
    /** Toggle dialogue visibility */
    public void toggle(){ // between show and hide 
                            view = !view;}
    
    /**
     * Check if on the last line of dialogue
     * @return true if on last line
     */
    public boolean tolastLine(){
                            return index >= Math.max(1, line.size())-1;}

    /**
     * Get the height of the dialogue box
     * @return Height in pixels
     */
    public int getHeight() { return height; }

    // jumps to next line when the prompt is done 
    public void jumptoNext(){
        // checking some cases of visibility from above 
        if (!view) {return;} 
        if (index < line.size() -1) { 
            index++; // increase index if havent reached the end of the line array list 
        } else {
            view = false; 
            }
        }

    /**
     * Advance to next prompt line
     */
    public void nextPrompt(){ 
        if (!line.isEmpty() && index < line.size() -1){
            index++; 
        }else {
            view = false; 
        }
    }

    /**
     * Drawing the dialogue box itself using the width and height components
     * @param g Graphics2D context
     * @param panelWidth Width of the panel
     * @param panelHeight Height of the panel
     */
    public void draw(Graphics2D g, int panelWidth, int panelHeight){
        // checking if box is needed to be drawn, if no prompts dialogue box is not needed 
        if (!view || line.isEmpty()){
            return; 
        }    

        int y = panelHeight - height - Marg; 
        // crating the Dialogue box border 
        g.setColor(new Color (0, 0, 128, 120)); // navy blue 
        g.drawRoundRect(Marg, y, panelWidth - 2*Marg, height, cornerR, cornerR);

        // Dialogue background 
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(135, 206, 235, 180)); // light blue 
        g.fillRoundRect(Marg,y,panelWidth - 2*Marg,  height, cornerR, cornerR);

        // Text design inside the text box 
        g.setColor(new Color(78, 42, 35)); // dark brown 
        Font old = g.getFont(); 
        g.setFont(old.deriveFont(Font.PLAIN,14f)); // indicates the font and the size 
        String text = line.get(index); 

        // making sure the text stays perfectly centred in the text box using the following 
        FontMetrics fontMetrics = g.getFontMetrics(); 
        int textX = Marg*2; 
        int textY = y + (height + fontMetrics.getAscent() - fontMetrics.getDescent())/2;
        g.drawString(text, textX, textY);

        // creating a button to click when the user doesnt want to actually read the prompts 
        String skip = "SKIP ▶"; 
        Font skipFont = g.getFont().deriveFont(Font.PLAIN,18f); // making the symol a bit larger 
        g.setFont(skipFont);
        FontMetrics skipFontMetrics = g.getFontMetrics(); 
        int WidthSKIP = skipFontMetrics.stringWidth(skip); 

        // where the SKIP button really goes 
        int textSKIP = panelWidth - Marg - WidthSKIP - 6; 
        g.drawString(skip, textSKIP, textY);
        g.setFont(old);
    }

    /**
     * implementing the click to skip the prompts + using the MouseEvent function for button purposes
     * @param e Mouse event
     * @param panelWidth Width of the panel
     * @param panelHeight Height of the panel
     */
    public void skipClick(MouseEvent e, int panelWidth, int panelHeight){ 
        if (!view) { return; } 
        // creating the clickable region of the dialogue box -- to the right 
        int y = panelHeight - height - Marg; 
        int skipButton = panelWidth - SkipButtonWidth; 

        Rectangle clickable = new Rectangle(skipButton, y, SkipButtonWidth, height); 
        if (clickable.contains(e.getPoint())) { jumptoNext(); 
        }
    }

}