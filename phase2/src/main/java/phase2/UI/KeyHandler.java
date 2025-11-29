package phase2.UI;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * KeyHandler implements KeyListener to track player input.
 * Keeps boolean flags for key presses such as movement, attack, reset, and interactions.
 */
public class KeyHandler implements KeyListener {

    /** Flag indicating if the W key is pressed (move up). */
    public boolean wPressed;

    /** Flag indicating if the S key is pressed (move down). */
    public boolean sPressed;

    /** Flag indicating if the A key is pressed (move left). */
    public boolean aPressed;

    /** Flag indicating if the D key is pressed (move right). */
    public boolean dPressed;

    /** Flag indicating if the SPACE key is pressed (attack/jump). */
    public boolean spacePressed;

    /** Flag indicating if the R key is pressed (reset or other action). */
    public boolean rPressed;

    /** Flag indicating if the ENTER key is pressed (confirm/select). */
    public boolean enterPressed;

    /** Flag indicating if the T key is pressed (for testing pop-ups). */
    public boolean tPressed;

    /** Flag indicating if the E key is pressed (interaction). */
    public boolean ePressed;

    /**
     * Invoked when a key has been typed (pressed and released).
     * Currently unused.
     *
     * @param e KeyEvent object
     */
    @Override
    public void keyTyped(KeyEvent e) {}

    /**
     * Invoked when a key is pressed.
     * Updates the corresponding boolean flag to true.
     *
     * @param e KeyEvent object
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W){
            wPressed = true;
        }
        if (code == KeyEvent.VK_A){
            aPressed = true;
        }
        if (code == KeyEvent.VK_S){
            sPressed = true;
        }
        if (code == KeyEvent.VK_D){
            dPressed = true;
        }
        if (code == KeyEvent.VK_SPACE){
            spacePressed = true;
        }
        if (code == KeyEvent.VK_R) {
            rPressed = true;
        }
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = true;
        }
        if (code == KeyEvent.VK_T) { // for force testing pop-ups
            tPressed = true;
        }
        if (code == KeyEvent.VK_E) {
            ePressed = true;
        }
    }

    /**
     * Invoked when a key is released.
     * Updates the corresponding boolean flag to false.
     *
     * @param e KeyEvent object
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W){
            wPressed = false;
        }
        if (code == KeyEvent.VK_A){
            aPressed = false;
        }
        if (code == KeyEvent.VK_S){
            sPressed = false;
        }
        if (code == KeyEvent.VK_D){
            dPressed = false;
        }
        if (code == KeyEvent.VK_SPACE){
            spacePressed = false;
        }
        if (code == KeyEvent.VK_R) {
            rPressed = false;
        }
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = true; // keep pressed? seems intentional
        }
        if (code == KeyEvent.VK_T) {
            tPressed = false;
        }
        if (code == KeyEvent.VK_E) {
            ePressed = false;
        }
    }
}
