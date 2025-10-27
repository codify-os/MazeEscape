package phase2.Entity;

import phase2.UI.GamePanel;
import phase2.Floor.Grid;
import java.util.ArrayList;
import java.awt.*;

public class Enemy extends Entity {
    GamePanel gp;

    public Enemy(GamePanel gp) {
        this.gp = gp;
    }

    public void setDefaultValues() {
        x = 120;
        y = 120;
        speed = 4;
        direction = "down";
    }

    public void getImage() {
        try {
            // If you find a better way to do this, then you can mess around with it, but
            // this is the best I could research into this
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            up = toolkit.getImage(getClass().getClassLoader()
                    .getResource("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_run_up_anim.gif"));
            down = toolkit.getImage(getClass().getClassLoader()
                    .getResource("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_run_down_anim.gif"));
            right = toolkit.getImage(getClass().getClassLoader()
                    .getResource("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_run_right_anim.gif"));
            left = toolkit.getImage(getClass().getClassLoader()
                    .getResource("Top_Down_Adventure_Pack_v.1.0/Char_Sprites/char_run_left_anim.gif"));

            MediaTracker tracker = new MediaTracker(new java.awt.Canvas());
            tracker.addImage(up, 0);
            tracker.addImage(down, 1);
            tracker.addImage(right, 2);
            tracker.addImage(left, 3);
            tracker.waitForAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Astar(Player player) {
        int px,py;

        float g=Math.abs(px-x)+Math.abs(py+y);
        float h=Math.abs(player.getX()-px)+Math.abs(player.getY()-py);
        float f=g+h;
        ArrayList <Grid> toSearch=new ArrayList<Grid>(Arrays.asList(Grid));    
        ArrayList<Grid> processed=new ArrayList<Grid>;
        while(toSearch.Any()){
            current=toSearch[0];
            for each(Grid t:toSearch){
                t.F<
            }
        }
    }

    public void update() {

    }

    public void draw(Graphics2D g2d) {

    }
}
