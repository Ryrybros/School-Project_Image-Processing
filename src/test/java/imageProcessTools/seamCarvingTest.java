package imageProcessTools;

import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class seamCarvingTest {
    WritableImage wi = new WritableImage(100, 100);
    seamCarving seam = new seamCarving(wi.getPixelReader(), 95, 95, 100, 100);


    @Test
    void createArray() {
        seam.createArray();
        assert(seam.energizedPixels[50*50].getEnergy() == 0);
        assert(seam.energizedPixels[50*50].getParent() == -1);
    }

    @Test
    void energizeArray() {
        seam.createArray();
        seam.energizedPixels[475].getPixel().c = Color.RED;
        seam.energizedPixels[575].getPixel().c = Color.RED;
        seam.energizedPixels[476].getPixel().c = Color.RED;
        seam.energizeArray();
        double gradient = seam.energizedPixels[475].getEnergy();
        assert(gradient == 0);

    }
    @Test
    void grad() {
        seam.createArray();
        seam.energizedPixels[475].getPixel().c = Color.RED;
        seam.energizedPixels[575].getPixel().c = Color.RED;
        seam.energizedPixels[476].getPixel().c = Color.RED;
        double gradient = seam.grad(75,4);
        assert(gradient == 0);
    }


}