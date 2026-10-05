package imageProcessTools;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.junit.jupiter.api.Test;

import static javafx.scene.paint.Color.RED;

class mosaicMakerTest {
    WritableImage wi = new WritableImage(100, 100);
    PixelWriter write = wi.getPixelWriter();
    mosaicMaker testMos = new mosaicMaker(wi, 100, 100, 10);
    @Test
    void createTreeX() {
        binaryTree<pixel> testTree = testMos.createTreeX(1,0); // start > end means null result
        assert(testTree == null);
    }

    @Test
    void createTreeY() {
        binaryTree<pixel> testTree = testMos.createTreeY(1,0); // start > end means null result
        assert(testTree == null);
    }

    @Test
    void closerPoint() {
        for (int y = 0; y<100; y++){ // creates red image 100x100
            for (int x = 0; x<100; x++){
                write.setColor(x,y, RED);
            }
        }
        binaryTree<pixel> testTree = testMos.createTreeY(1,0);
        pixel p = new pixel();
        p.x = 0;
        p.y = 0;
        p.c = RED;
        pixel p2 =  testMos.closerPoint(p, testTree, null, 0, 0);
        assert(p2 == null);
    }

}