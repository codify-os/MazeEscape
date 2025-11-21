package phase2.UI;

import org.junit.jupiter.api.Test;

import javax.swing.JPanel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.*;

public class dialogueBoxTest {

    // Helper function from dialoguebox to move to the next line 
    static class dialogueBox_Test extends dialogueBox {
        int jumpCount = 0;
        int closeCount = 0;
        @Override
        public void jumptoNext() {
            jumpCount++;
            super.jumptoNext();
        }
        @Override
        public void DialogueCLOSED() {
            closeCount++;
            super.DialogueCLOSED();
        }
    }

    // goes through all the lines and then closes 
    @Test
    void isMovingToLastLineAndClose(){
        dialogueBox_Test box = new dialogueBox_Test(); 
        box.loadLine("Line 1", "Line 2");
        box.show_Dialogue();
        
        // first click 
        box.jumptoNext();
        assertEquals(1, box.jumpCount, "jumptoNext is called once");
        assertEquals(0, box.closeCount, "dialogue should not close yet");
        // second click 
        box.jumptoNext();
        assertEquals(2, box.jumpCount, "jumptoNext is called again");
        assertEquals(1, box.closeCount, "dialogue box closes after the last line");
    }
   
    // Test if see skip button does anything when box is not visible 
    @Test 
    void isSkipDoingNothing(){
        
    }




    
}
