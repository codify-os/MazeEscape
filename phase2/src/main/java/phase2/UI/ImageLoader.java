package phase2.UI;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

// class for laading the images in the same space
// created this so that GamePanel was not loading the imaages in the main loop
// this task is better done seperatly 
public class ImageLoader {
    private static final Toolkit ToolKit = Toolkit.getDefaultToolkit();

    private ImageLoader() {
        // there are no instances yet 
    }
    public static Image image(String path) {
        URL url = ImageLoader.class.getClassLoader().getResource(path);
        if (url == null) {
            throw new IllegalArgumentException("Image was not found :" + path);
        }
        return ToolKit.getImage(url);
    }

}
