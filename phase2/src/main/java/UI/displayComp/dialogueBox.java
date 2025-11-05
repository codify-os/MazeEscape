// dialogueBox.java
// Displays what the character is saying or prompts the game is providing to the character
// will add an addition button for skip in case player does not wanna read the prompts 
// The volume option also controls if the dialogue is read out load 


package UI.displayComp;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List; 

public class dialogueBox {
    // some variable declarations 
    private static final int Marg = 10; 
    private static final int cornerR = 12; 
    private static final int height = 56; 

    private boolean view = false; 
    private final List<String> line = new ArrayList<>(); // to hold a list of all the dialogue lines 
    private int index = 0; // to keep track of the current line 

    // Loading a dialogue box with strings + clearing when needed 
    public void loadLine(String... text) { 
        line.clear();
        for (String s : text) if (s != null ) line.add(s); // if line is not empty, adds it to the list 
        index = 0; 
        view = !line.isEmpty();
    }

    // some basic visibility function 
    public void hide_Dialogue(){
                            view = false;}
    public void show_Dialogue(){
                            view = true;}
    public boolean isDialogue_shown(){
                            return view;}                       
    public void toggle(){ // between show and hide 
                            view = !view;}
    public boolean tolastLine(){
                            return index >= Math.max(1, line.size())-1;} 

    public int getHeight() { 
                            return height; }

    // jumps to next line when the prompt is done 
    public void jumptoNext(){
        // checking some cases of visibility from above 
        if (!view) return; 
        if (index < line.size() -1) index++; // increase index if havent reached the end of the line array list 
        else view = false; 
        }

    /**
     * Drawing the dialogue box itself using the width and height components 
     */
    public void draw(Graphics2D bar_Graphics, int panelWidth, int y){
        // double checking at first to see if it is visible or the arraylist created for lines is empty 
        
        // crating the Dialogue box border 
        bar_Graphics.setColor(new Color (0, 0, 128, 120)); // navy blue 
        bar_Graphics.drawRoundRect(Marg, y, panelWidth - 2*Marg, height, cornerR, cornerR);

        // Dialogue background 
        bar_Graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        bar_Graphics.setColor(new Color(135, 206, 235, 180)); // light blue 
        bar_Graphics.fillRoundRect(Marg,y,panelWidth - 2*Marg,  height, cornerR, cornerR);

        // Text design inside the text box 
        bar_Graphics.setColor(new Color(78, 42, 35)); // dark brown 
        Font old = bar_Graphics.getFont(); 
        bar_Graphics.setFont(old.deriveFont(Font.PLAIN,14f)); // indicates the font and the size 
        String text = line.get(index); 
        int textX = Marg*2; 
        // making sure the text stays perfectly centred in the text box with the following 
        int textY = y + (height + bar_Graphics.getFontMetrics().getAscent() - bar_Graphics.getFontMetrics().getDescent())/2;
        bar_Graphics.drawString(text, textX, textY);

        // creating a button to click when the user doesnt wanna actually read the prompts 
        String skip = "SKIP ▶"; 
        Font skipFont = bar_Graphics.getFont().deriveFont(Font.PLAIN,18f); // making the symol a bit larger 
        bar_Graphics.setFont(skipFont);
        int textWidth = bar_Graphics.getFontMetrics().stringWidth(skip); 
        bar_Graphics.drawString(skip, panelWidth - Marg - textWidth - 6, textY); 
        bar_Graphics.setFont(old);
    }

    /**
     * implementing the click to skip the prompts + using the MouseEvent function for button purposes 
     */
    public void skipClick(MouseEvent e, int panelWidth, int y){ 
        if (!view) return; 
        // creating the clickable region of the dialogue box 
        Rectangle clickable = new Rectangle(Marg, y, panelWidth - 2*Marg, height); 
        if (clickable.contains(e.getPoint())) jumptoNext();
    }

}