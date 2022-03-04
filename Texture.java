import java.awt.*;
import java.awt.image.BufferedImage;

public class Texture {
    
    BufferedImage image;

    /**
     * Creates a new texture with the given image.
     * 
     * @param image The texture image.
     */
    public Texture(BufferedImage image) {
        this.image = image;
    }

    /**
     * Gets a pixel from the texture at the specified normalized coordinates.
     * @param x
     * @param y
     * @return
     */
    public Color getPixel(double x, double y) {
        return new Color(image.getRGB(
            (int)(x * (image.getWidth() - 1)), (int)(y * (image.getHeight() - 1))
        ));
    }
}
