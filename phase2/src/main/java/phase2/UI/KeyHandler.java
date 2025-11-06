package phase2.UI;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public boolean wPressed, sPressed, aPressed, dPressed, spacePressed, rPressed, enterPressed;

    @Override
    public void keyTyped(KeyEvent e) {}

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
    }

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
            enterPressed = true;
        }
    }
}
