package imageProcessTools;

import javafx.scene.paint.Color;

import java.awt.*;

public class pixel implements Comparable<pixel> {
    protected int x;
    protected int y;
    protected Color c;


    public int compareTo(pixel p) { // lexicographic order
        if(this.x < p.x){
            return -1;
        }
        else if (this.x > p.x){
            return 1;
        }
        else{
            if (this.y == p.y){return 0;}
            else if (this.y < p.y){return -1;}
            else{return 1;}
        }
    }

    public int compareToY(pixel p) { // y takes priority
        if(this.y < p.y){
            return -1;
        }
        else if (this.y > p.y){
            return 1;
        }
        else{
            if (this.x == p.x){return 0;}
            else if (this.x < p.x){return -1;}
            else{return 1;}
        }
    }
}
