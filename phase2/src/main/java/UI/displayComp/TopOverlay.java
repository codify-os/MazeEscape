package UI.displayComp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Transparent overlay that draws the topPanel above everything else. */
public class TopOverlay extends JComponent {
    private final topPanel top = new topPanel();

    public TopOverlay(JRootPane root) {
        setOpaque(false);

        // forward clicks to topPanel; repaint on any state change
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                var action = top.click(e);
                if (action == topPanel.ActionBar.Button_EXIT) System.exit(0);
                // Back/Help/etc. can be wired later from Main if you want.
                repaint();
            }
        });

        // keep overlay sized with the root content
        root.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                setBounds(0, 0, root.getWidth(), root.getHeight());
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        top.draw(g2, getWidth()); // draws only the top bar (no need to scale Y)
    }
}
