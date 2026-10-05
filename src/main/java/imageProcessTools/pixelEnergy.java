package imageProcessTools;

import javafx.scene.paint.Color;

public class pixelEnergy {
    private pixel p;
    private int parentIndex;
    private double energy;

    pixelEnergy (pixel p, int parent, double energy) {
        this.p = p;
        this.parentIndex = parent;
        this.energy = energy;
    }

    public double getEnergy(){
        return energy;
    }

    public pixel getPixel() {
        return p;
    }
    protected void setPixel(int x, int y, Color c){
        p.x = x;
        p.y = y;
        p.c = c;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public void setParent(int parent){
        this.parentIndex = parent;
    }
    public int getParent(){
        return parentIndex;
    }
}