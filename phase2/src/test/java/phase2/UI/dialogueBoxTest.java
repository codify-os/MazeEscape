package phase2.UI;

import org.junit.jupiter.api.Test;

import javax.swing.JPanel;
import java.lang.reflect.Field;
import java.awt.event.MouseEvent;
import static org.junit.jupiter.api.Assertions.*;

public class dialogueBoxTest {

    // Helper function from dialoguebox to move to the next line 
    static class dialogueBox_Test extends dialogueBox {
        int jumpCount = 0;
        @Override
        public void jumptoNext() {
            jumpCount++;
            super.jumptoNext();
        }
    }

    // Helper function to read the static fucntions fo the second test 
    private static int getStaticIntField(String text) throws Exception {
        Field field = dialogueBox.class.getDeclaredField(text);
        field.setAccessible(true);
        return field.getInt(null);
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
        assertTrue(box.isDialogue_shown(), "dialogue should not close yet");

        // second click 
        box.jumptoNext();
        assertEquals(2, box.jumpCount, "jumptoNext is called again");
        assertFalse(box.isDialogue_shown(), "dialogue box closes after the last line");
    }
   
    // Test if see skip button does anything when box is not visible 
    @Test 
    void isSkipDoingNothing() throws Exception {
        dialogueBox_Test box = new dialogueBox_Test(); 

        int panelWidth = 800;
        int panelHeight = 600;

        int marg = getStaticIntField("Marg");
        int height = getStaticIntField("height");
        int skipButtonW = getStaticIntField("SkipButtonWidth");

        int skipX = panelWidth - skipButtonW / 2;
        int skipY = panelHeight - height - marg + height/ 2;

        MouseEvent click = new MouseEvent(new JPanel(), MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(),
                                         0, skipX , skipY, 1, false); 
        
        box.skipClick(click, panelWidth, panelHeight);
        assertEquals(0, box.jumpCount, "skip should not be called hwen not visible on the screen");

    }

}
