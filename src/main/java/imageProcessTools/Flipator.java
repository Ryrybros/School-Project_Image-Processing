package imageProcessTools;

import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class Flipator {

    public static WritableImage flip(WritableImage image, boolean isHorizontal){
        PixelReader pixR = image.getPixelReader();
        WritableImage result = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        for (int y = 0 ; y < image.getHeight() ; ++y){
            for (int x = 0 ; x < image.getWidth(); ++x){
                if (isHorizontal) {
                    result.getPixelWriter().setColor(x,y,pixR.getColor(( int ) image.getWidth() - x - 1,y));
                }
                else {
                    result.getPixelWriter().setColor(x,y,pixR.getColor(x,(int) image.getHeight() - y - 1));
                }
            }
        }
        return result;
    }

}
