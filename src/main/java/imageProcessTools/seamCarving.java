package imageProcessTools;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import static java.lang.Math.abs;

public class seamCarving {
    private int writableImWidth; // width of new image
    private int writableImHeight; // height of new image
    private int imWidth; // width of original image
    private int imHeight; // height of original image
    protected pixelEnergy[] energizedPixels; // array of energize pixels of the original image
    private PixelReader pixelReader; // pixelreader of the original image
    private WritableImage writableImage;
    private int currentWidth;
    private int currentHeight;

    private int index_of_xy(int x, int y){
        return (y*currentWidth+x);
    }


    seamCarving (PixelReader pixelReader, int writableWidth, int writableHeight, int originWidth, int originHeight){
        this.pixelReader = pixelReader;
        writableImWidth = writableWidth;
        writableImHeight = writableHeight;
        imWidth = originWidth;
        imHeight = originHeight;
        writableImage = new WritableImage(writableImWidth, writableImHeight);
        currentWidth = imWidth;
        currentHeight = imHeight;
    }

    protected double grad(int x, int y){
        double gradX = 0;
        double gradY = 0;
        if (x < currentWidth-1) { // Not last column
            Color c1 = energizedPixels[index_of_xy(x, y)].getPixel().c;
            Color c2 = energizedPixels[index_of_xy(x+1, y)].getPixel().c;
            gradX = abs(c2.getRed()-c1.getRed()) + abs(c2.getGreen()-c1.getGreen()) + abs(c2.getBlue()-c1.getBlue());
        }
        else if (x == currentWidth-1){ // Last column
            Color c1 = energizedPixels[index_of_xy(x-1,y)].getPixel().c;
            Color c2 = energizedPixels[index_of_xy(x,y)].getPixel().c;
            gradX = abs(c2.getRed()-c1.getRed()) + abs(c2.getGreen()-c1.getGreen()) + abs(c2.getBlue()-c1.getBlue());
        }
        if (y == currentHeight-1){ // Last line
            Color c1 = energizedPixels[index_of_xy(x,y-1)].getPixel().c;
            Color c2 = energizedPixels[index_of_xy(x,y)].getPixel().c;
            gradY = abs(c2.getRed()-c1.getRed()) + abs(c2.getGreen()-c1.getGreen()) + abs(c2.getBlue()-c1.getBlue());
        }
        else if (y < currentHeight-1){ // Not last line
            Color c1 = energizedPixels[index_of_xy(x,y)].getPixel().c;
            Color c2 = energizedPixels[index_of_xy(x,y+1)].getPixel().c;
            gradY = abs(c2.getRed()-c1.getRed()) + abs(c2.getGreen()-c1.getGreen()) + abs(c2.getBlue()-c1.getBlue());
        }
        return (gradX + gradY);
    }

    protected void createArray(){
        energizedPixels = new pixelEnergy[imWidth*imHeight];
        for (int x = 0; x < imWidth; x++){
            for (int y = 0; y < imHeight; y++){
                pixel p = new pixel();
                p.c = pixelReader.getColor(x,y);
                p.x = x;
                p.y = y;
                energizedPixels[y*imWidth+x] = new pixelEnergy(p, -1 ,0);
            }
        }
    }
    protected void energizeArray(){
        for (int y = 0; y < currentHeight; y++){
            for (int x = 0; x < currentWidth; x++){
                energizedPixels[index_of_xy(x,y)].setEnergy(grad(x,y));
            }
        }
    }

    private void goingBackY(int pixIndex){
        if (energizedPixels[pixIndex].getPixel().y == 0){
            energizedPixels[pixIndex] = null;
        }
        else{
            int parent = energizedPixels[pixIndex].getParent();
            energizedPixels[pixIndex] = null;
            goingBackY(parent);
        }
    }
    private void goingBackX(int pixIndex){
        if (energizedPixels[pixIndex].getPixel().x == 0){
            energizedPixels[pixIndex] = null;
        }
        else{
            int parent = energizedPixels[pixIndex].getParent();
            energizedPixels[pixIndex] = null;
            goingBackX(parent);
        }

    }

    private void carvingColumn() { // modifies the pixel array, when a pixel is unnecessary, sets its value to null
        for (int y = 1; y < currentHeight; y++) {
            if (energizedPixels[index_of_xy(0, y-1)].getEnergy() <= energizedPixels[index_of_xy(1,y-1)].getEnergy()) { // Only 2 choices for first of the line
                energizedPixels[index_of_xy(0,y)].setParent(index_of_xy(0,y-1));
            } else {
                energizedPixels[index_of_xy(0,y)].setParent(index_of_xy(1,y-1));
            }
            if (energizedPixels[index_of_xy(currentWidth-2, y-1)].getEnergy() <= energizedPixels[index_of_xy(currentWidth-1, y-1)].getEnergy()) { // Only 2 choices for last of the line
                energizedPixels[index_of_xy(currentWidth-1, y)].setParent(index_of_xy(currentWidth-2, y-1));
            }
            else {
                energizedPixels[index_of_xy(currentWidth-1, y)].setParent(index_of_xy(currentWidth-1, y-1));
            }
            for (int x = 1; x < currentWidth-1; x++) {
                if (energizedPixels[index_of_xy(x-1,y-1)].getEnergy() <= energizedPixels[index_of_xy(x,y-1)].getEnergy() && energizedPixels[index_of_xy(x-1,y-1)].getEnergy() <= energizedPixels[index_of_xy(x+1,y-1)].getEnergy()) { // x-1 is smallest
                    energizedPixels[index_of_xy(x,y)].setParent(index_of_xy(x-1, y-1));
                } else if ((energizedPixels[index_of_xy(x, y-1)].getEnergy() <= energizedPixels[index_of_xy(x-1, y-1)].getEnergy() && energizedPixels[index_of_xy(x,y-1)].getEnergy() <= energizedPixels[index_of_xy(x+1, y-1)].getEnergy())) { // x is smallest
                    energizedPixels[index_of_xy(x,y)].setParent(index_of_xy(x,y-1));
                } else {
                    energizedPixels[index_of_xy(x,y)].setParent(index_of_xy(x+1,y-1)); // x+1 is smallest
                }
            }
        }

        double min = energizedPixels[index_of_xy(0, currentHeight-1)].getEnergy();
        int index = 0;
        for (int x = 1; x < currentWidth; x++) { // get the pixel with the least energy on the last line
            if (energizedPixels[index_of_xy(x, currentHeight-1)].getEnergy() < min) {
                min = energizedPixels[index_of_xy(x, currentHeight-1)].getEnergy();
                index = x;
            }
        }
        goingBackY(index_of_xy(index, currentHeight-1));
        updateArrayColumn();
    }


    private void carvingLine() { // modifies the pixel array, when a pixel is unnecessary, sets its value to null
        for (int x = 1; x < currentWidth; x++) {
            if (energizedPixels[index_of_xy(x-1, 0)].getEnergy() < energizedPixels[index_of_xy(x-1, 1)].getEnergy()) { // Only 2 choices for first of the column
                energizedPixels[index_of_xy(x,0)].setParent(index_of_xy(x-1,0));
            } else {
                energizedPixels[index_of_xy(x,0)].setParent(index_of_xy(x-1,1));
            }
            if (energizedPixels[index_of_xy(x-1, currentHeight-1)].getEnergy() <= energizedPixels[index_of_xy(x-1, currentHeight-2)].getEnergy()) { // Only 2 choices for last of the line
                energizedPixels[index_of_xy(x, currentHeight-1)].setParent(index_of_xy(x-1, currentHeight-1));
            } else {
                energizedPixels[index_of_xy(x, currentHeight-1)].setParent(index_of_xy(x-1, currentHeight-2));
            }
            for (int y = 1; y < currentHeight-1; y++) {
                if (energizedPixels[index_of_xy(x-1, y)].getEnergy() <= energizedPixels[index_of_xy(x-1, y-1)].getEnergy() && energizedPixels[index_of_xy(x-1,y)].getEnergy() <= energizedPixels[index_of_xy(x-1, y+1)].getEnergy()) { // y is smallest
                    energizedPixels[index_of_xy(x,y)].setParent(index_of_xy(x-1,y));
                } else if (energizedPixels[index_of_xy(x-1, y-1)].getEnergy() <= energizedPixels[index_of_xy(x-1, y)].getEnergy() && energizedPixels[index_of_xy(x-1, y-1)].getEnergy() <= energizedPixels[index_of_xy(x-1, y+1)].getEnergy()) { // y-1 is smallest
                    energizedPixels[index_of_xy(x,y)].setParent(index_of_xy(x-1,y-1));
                } else {
                    energizedPixels[index_of_xy(x,y)].setParent(index_of_xy(x-1, y+1));
                }
            }
        }

        double min = energizedPixels[index_of_xy(currentWidth-1, 0)].getEnergy();
        int index = 0;
        for (int y = 1; y < currentHeight; y++) {
            if (min > energizedPixels[index_of_xy(currentWidth-1, y)].getEnergy()) {
                min = energizedPixels[index_of_xy(currentWidth-1, y)].getEnergy();
                index = y;
            }
        }
        goingBackX(index_of_xy(imWidth-1, index));
        updateArrayLine();
    }


    public void updateArrayColumn(){ // Removes null pixels and updates energies
        int kx = 0;
        int ky = 0;
        int k = 0;
        for (int y = 0; y < currentHeight; y++){ // currentHeight and currentWidth have not yet been updated at this point
            for (int x = 0; x<currentWidth; x++){
                if (energizedPixels[index_of_xy(x,y)] != null){
                    energizedPixels[k] = energizedPixels[index_of_xy(x,y)]; // Only sets pixels already seen in the loop
                    energizedPixels[k].setPixel(kx, ky, energizedPixels[k].getPixel().c);
                    k++;
                    kx++;
                }
            }
            kx = 0;
            ky++;
        }
    }



    public void updateArrayLine(){ // Removes null pixels and updates energies
        int kx = 0;
        int ky = 0;
        for (int x = 0; x < currentWidth; x++){ // currentHeight and currentWidth have not yet been updated at this point
            for (int y = 0; y<currentHeight; y++){
                if (energizedPixels[index_of_xy(x,y)] != null){
                    energizedPixels[index_of_xy(kx,ky)] = energizedPixels[index_of_xy(x,y)]; // Only sets pixels already seen in the loop
                    energizedPixels[index_of_xy(kx,ky)].setPixel(kx, ky, energizedPixels[index_of_xy(kx,ky)].getPixel().c);
                    ky++;
                }

            }
            ky = 0;
            kx++;
        }
    }





    public WritableImage carving(){
        currentWidth = imWidth;
        currentHeight = imHeight;
        if (imHeight < writableImHeight || imWidth < writableImWidth || writableImWidth < 0 || writableImHeight< 0){ // if incorrect values, just return the original image
            writableImage = new WritableImage(imWidth, imHeight);
            PixelWriter pixelWriter = writableImage.getPixelWriter();
            for (int y = 0; y < imHeight; y++){
                for (int x = 0; x < imWidth; x++){
                    pixelWriter.setColor(x,y, pixelReader.getColor(x,y));
                }
            }
            return writableImage;
        }
        createArray();
        energizeArray();
        for (int i = 0; i < imWidth - writableImWidth; i++){
            carvingColumn();
            currentWidth-=1;
            energizeArray();
        }
        for (int i = 0; i < imHeight - writableImHeight; i++){
            carvingLine();
            currentHeight-=1;
            energizeArray();
        }
        writableImage = new WritableImage(writableImWidth, writableImHeight);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int y = 0; y < writableImHeight; y++){
            for (int x = 0; x < writableImWidth; x++){
                pixelWriter.setColor(x,y, energizedPixels[index_of_xy(x,y)].getPixel().c);
            }
        }
        return writableImage;
    }

}
