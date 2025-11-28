package phase2.UI;

import org.junit.jupiter.api.Test;

import javax.swing.JPanel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.*;

public class topPanelTest {
    
    // helper fucntion to draw the tests out 
    private Graphics2D drawGraphics(){
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB); 
        return image.createGraphics(); 
    }

    // pause button switching "toggling" to play 
    @Test
    void isPauseSwitchingToPlay(){
        topPanel panel = new topPanel(); 
        Graphics2D graphics = drawGraphics (); 
        int screenWidth = 768; 
        panel.draw(graphics, screenWidth, false);

        assertFalse(panel.isPaused(), "Top Panel is displaying PLAY initially"); 

        int PauseX = 90 + 10; // coordinates for withing the button 
        int PauseY = (68 - 28 - 10) + 10;

        // clicking implementation 
        MouseEvent click = new MouseEvent(new JPanel(), MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(),
                                         0, PauseX, PauseY, 1, false); 
        
        ActionBar actionBar = panel.clickButton(click);

        assertEquals(ActionBar.Button_Pause, actionBar, "Clicking the button detected as PAUSE button");
        assertTrue(panel.isPaused(), "Clicking the button should set PAUSE (pause = true)");

        // to play again or unpause should click again 
        ActionBar anotherActionBar = panel.clickButton(click);
        assertEquals(ActionBar.Button_Pause, anotherActionBar);
        assertFalse(panel.isPaused(), "Another click will switch button to PLAY (pause = false)");

    }

    // Test to see if the sounds button switches from SOUND to MUTE
    @Test 
    void isSoundSwitchingToMute(){
        topPanel panel = new topPanel(); 
        Graphics2D graphics = drawGraphics (); 
        int screenWidth = 768; 
        panel.draw(graphics, screenWidth, false);

        assertFalse(panel.isMuted(), "Top Panel is displaying SOUND initially");
        int SoundX = 12 + 10;
        int SoundY = (68 - 28 - 10) + 10;

        // clicking implementation 
        MouseEvent click = new MouseEvent(new JPanel(), MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(),
                                            0, SoundX, SoundY, 1, false); 
                                           
        ActionBar actionBar = panel.clickButton(click);
        assertEquals(ActionBar.Button_Sound, actionBar, "Clicking the button detected as SOUND button");
        assertTrue(panel.isMuted(), "Clicking the button should set MUTE (mute = true)");

        // toggling action 
        ActionBar anotherActionBar = panel.clickButton(click);
        assertEquals(ActionBar.Button_Sound, anotherActionBar);
        assertFalse(panel.isMuted(), "Another click will switch button to SOUND (pause = false)");

    }

    // Test to for nothing to happen if player presses beyond the where the buttons are location 
    @Test
    void isIgnoredWhenClickedOutside(){
        topPanel panel = new topPanel(); 
        Graphics2D graphics = drawGraphics (); 
        int screenWidth = 768; 
        panel.draw(graphics, screenWidth, false);

        MouseEvent clickUP = new MouseEvent(new JPanel(), MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(),
                                            0, 20, -7, 1, false); 

        ActionBar actionBar = panel.clickButton(clickUP);
        assertEquals(ActionBar.none, actionBar, "Clicking above vertical width of panel -- return none ");

        MouseEvent clickDOWN = new MouseEvent(new JPanel(), MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(),
                                            0, 20, -7, 1, false); 

        ActionBar anotherActionBar = panel.clickButton(clickDOWN);
        assertEquals(ActionBar.none, anotherActionBar, "Clicking below vertical width of panel -- return none ");
    }

}
