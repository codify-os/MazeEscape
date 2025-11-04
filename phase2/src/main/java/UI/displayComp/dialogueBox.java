// dialogueBox.java
// Displays what the character is saying or prompts the game is providing to the character
// will add an addition button for skip in case player does not wanna read the prompts 
// The volume option also controls if the dialogue is read out load 


package UI.displayComp;

import java.awt.*;
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

}