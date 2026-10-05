package imageProcessTools;

import org.junit.jupiter.api.Test;

class pixelTest {

    @Test
    void compareTo() {
        pixel p1 = new pixel();
        p1.x = 3;
        p1.y = 57943096;
        pixel p2 = new pixel();
        p2.x = 5;
        p2.y = 1;
        int res1= p1.compareTo(p2);
        pixel p3 = new pixel();
        p3.x = 3;
        p3.y = 1;
        int res2 = p1.compareTo(p3);
        assert(res1 < 0);
        assert(res2 > 0);
    }

    @Test
    void compareToY() {
        pixel p1 = new pixel();
        p1.x = 3;
        p1.y = 999999999;
        pixel p2 = new pixel();
        p2.x = 5;
        p2.y = 1;
        int res1 = p1.compareToY(p2);
        pixel p3 = new pixel();
        p3.x = 3;
        p3.y = 1;
        int res2 = p2.compareToY(p3);
        assert(res1 > 0);
        assert(res2 > 0);
    }
}