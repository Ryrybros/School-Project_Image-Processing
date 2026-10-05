package imageProcessTools;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.util.*;


public class mosaicMaker {
    private final Set<Integer> cloudSet = new LinkedHashSet<Integer>();
    private pixel[] pointCloud;
    private WritableImage im;
    private final double imageWidth; // Width in number of pixels
    private final double imageHeight;// Height in number of pixels
    private final int sizeOfCloud; // Between 5 and 50

    public mosaicMaker(WritableImage im, int imageW, int imageH, int cloudSize){
        this.im = im;
        imageWidth = imageW;
        imageHeight = imageH;
        sizeOfCloud = cloudSize;
    }
    private int partition(pixel[] arr, int begin, int end) {
        pixel pivot = arr[end];
        int i = (begin-1);

        for (int j = begin; j < end; j++) {
            if (arr[j].compareToY(pivot)<=0) {
                i++;

                pixel swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;
            }
        }

        pixel swapTemp = arr[i+1];
        arr[i+1] = arr[end];
        arr[end] = swapTemp;
        return i+1;
    }

    private void quickSort(pixel[] arr, int begin, int end) { // Sort y
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end);

            quickSort(arr, begin, partitionIndex-1);
            quickSort(arr, partitionIndex+1, end);
        }
    }

    private void randCloud(int numPixels, int cloudSize){
        Random r = new Random();
        while (cloudSet.size() < cloudSize) {
            Integer next = r.nextInt(numPixels);
            cloudSet.add(next);
        }
        pixel[] cloud = new pixel[cloudSize];
        Iterator<Integer> iter = cloudSet.iterator();
        PixelReader reader = im.getPixelReader();
        for (int i = 0; i<cloudSize;i++){
            pixel p = new pixel();
            int n = iter.next();
            p.x = (int) (n%imageWidth);
            p.y = (int) (n/imageWidth);
            p.c = reader.getColor(p.x, p.y);
            cloud[i] = p;
        }
        pointCloud = cloud;
    }


    binaryTree<pixel> createTreeX(int start, int end){
        if (start>end){return null;}
        Arrays.sort(pointCloud, start, end); // Sort x
        return new binaryTree<>(pointCloud[(end+start)/2], createTreeY(start, (end+start)/2-1), createTreeY((end+start)/2+1, end), true);
    }

    binaryTree<pixel> createTreeY(int start, int end){
        if (start>end){return null;}
        quickSort(pointCloud, start, end);
        return new binaryTree<>(pointCloud[(end+start)/2], createTreeX(start, (end+start)/2-1), createTreeX((end+start)/2+1, end), false);

    }

    binaryTree<pixel> createTree(){
        return createTreeX(0, pointCloud.length-1);
    }



    pixel closerPoint(pixel p, binaryTree<pixel> node, pixel closer, int distanceToCloser, int lvl){ // p = current pixel studied
        if (node == null){return closer;} // If a leaf, just return closer
        if (node.getValue().x == p.x && node.getValue().y == p.y){
            return node.getValue(); // If the pixel is in the cloud, return it
        }
        boolean goLeft;
        int currentMinDist = (int) distanceToCloser;
        pixel currentCloser = closer;
        int d = ((node.getValue().x - p.x)*(node.getValue().x - p.x)) + ((node.getValue().y - p.y)*(node.getValue().y - p.y));
        // Distance to current pixel node
        if (node.isXNode){ // If split considering x
            if (d < currentMinDist){ // If the node is closer to the point than the current closer
                if (node.getValue().compareTo(p) > 0){ // If the pixel is on the left of the node
                    currentCloser = closerPoint(p, node.left, node.getValue(), d, lvl+1); // go left with d as closerDist and node as closer
                    currentMinDist = ((currentCloser.x - p.x)*(currentCloser.x - p.x)) + ((currentCloser.y - p.y)*(currentCloser.y - p.y));
                    // calculate new minDist
                    goLeft = true;}
                else{ // to the right
                    currentCloser = closerPoint(p, node.right, node.getValue(), d, lvl+1); // new closer
                    currentMinDist = ((currentCloser.x - p.x)*(currentCloser.x - p.x)) + ((currentCloser.y - p.y)*(currentCloser.y - p.y));
                    // recalculated distance
                    goLeft = false;}
            }
            else{ // if the node is farther to the point
                if (node.getValue().compareTo(p) > 0){ // go left
                    currentCloser = closerPoint(p, node.left, currentCloser, currentMinDist, lvl+1); // keep current minDist and closer and go left
                    currentMinDist = ((currentCloser.x - p.x)*(currentCloser.x - p.x)) + ((currentCloser.y - p.y)*(currentCloser.y - p.y));
                    goLeft = true;
                }
                else{ // go right
                    currentCloser = closerPoint(p, node.right, currentCloser, currentMinDist, lvl+1); // keep current minDist and closer and go right
                    currentMinDist = ((currentCloser.x - p.x)*(currentCloser.x - p.x)) + ((currentCloser.y - p.y)*(currentCloser.y - p.y));
                    goLeft = false;}
            }
        }
        else{ // if split considering y
            if (d < currentMinDist){ // node is closer than current closer
                if (node.getValue().compareToY(p) > 0){ // go left
                    currentCloser = closerPoint(p, node.left, node.getValue(), d, lvl+1); // with new closer and dist
                    currentMinDist = ((currentCloser.x - p.x)*(currentCloser.x - p.x)) + ((currentCloser.y - p.y)*(currentCloser.y - p.y));
                    //recalculate dist
                    goLeft = true;
                }
                else{ // go right
                    currentCloser = closerPoint(p, node.right, node.getValue(), d, lvl+1); // idem
                    currentMinDist = ((currentCloser.x - p.x)*(currentCloser.x - p.x)) + ((currentCloser.y - p.y)*(currentCloser.y - p.y));
                    //recalculate dist
                    goLeft = false;
                }
            }
            else{ // is node is farther
                if (node.getValue().compareToY(p) > 0){ // go left
                    currentCloser = closerPoint(p, node.left, currentCloser, currentMinDist, lvl+1); // with current closer and dist
                    currentMinDist = ((currentCloser.x - p.x)*(currentCloser.x - p.x)) + ((currentCloser.y - p.y)*(currentCloser.y - p.y));
                    // recalculate
                    goLeft = true;}
                else{
                    currentCloser = closerPoint(p, node.right, currentCloser, currentMinDist, lvl+1); // idem
                    currentMinDist = ((currentCloser.x - p.x)*(currentCloser.x - p.x)) + ((currentCloser.y - p.y)*(currentCloser.y - p.y));
                    //recalculate
                    goLeft = false;}
            }
        }
        // Intersection
        pixel otherCloser = null;
        if (node.isXNode){ // split with x
            if (currentMinDist >= (p.x - node.getValue().x)*(p.x - node.getValue().x)) { // if there is an intersection
                if (goLeft){ // if left child already seen
                    otherCloser = closerPoint(p, node.right, currentCloser, (int) (currentMinDist), lvl+1); // go right
                }
                else{
                    otherCloser = closerPoint(p, node.left, currentCloser, (int) (currentMinDist), lvl+1); // go left
                }
            }
        }
        else{ // same with y comparisons
            if (currentMinDist >= (p.y - node.getValue().y)*(p.y - node.getValue().y)) {
                if (goLeft){
                    otherCloser = closerPoint(p, node.right, currentCloser, (int) (currentMinDist), lvl+1);
                }
                else{
                    otherCloser = closerPoint(p, node.left, currentCloser, (int) (currentMinDist), lvl+1);
                }
            }
        }
        if (otherCloser != null){ // if other child visited
            int otherDist = ((otherCloser.x - p.x)*(otherCloser.x - p.x)) + ((otherCloser.y - p.y)*(otherCloser.y - p.y));
            if (otherDist < currentMinDist){ // compare the distances
                return otherCloser; // if the new point is closer, return it
            }
        }
        return currentCloser; // return currentCloser if nothing was returned before
    }
    

    public void makeMosaic(){
        PixelWriter pixelWriter = im.getPixelWriter(); // Get writer
        PixelReader reader = im.getPixelReader(); // Get reader
        randCloud((int) (imageWidth*imageHeight), sizeOfCloud);
        binaryTree<pixel> bTree = createTree();
        for (int i = 0; i< imageWidth; i++){
            for (int j = 0; j< imageHeight; j++){
                pixel pix = new pixel(); // Get the studied pixel
                pix.x = i;
                pix.y = j;
                pix.c = reader.getColor(pix.x, pix.y);
                int d = ((bTree.getValue().x - pix.x)*(bTree.getValue().x - pix.x)) + ((bTree.getValue().y - pix.y)*(bTree.getValue().y - pix.y));
                // Distance between root and pixel
                pixel p = closerPoint(pix, bTree, bTree.getValue(), d, 0); // Get closer point
                pixelWriter.setColor(i,j,p.c); // Change the pixel in the writable image
            }
        }
    }

}