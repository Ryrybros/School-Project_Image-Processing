package imageProcessTools;

import javafx.beans.value.ObservableDoubleValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;

import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import team.examples.centerPane;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ImageProcesser {

    //Main javaFX components
    private centerPane ctrPane;
    private StackPane stack;
    private ScrollPane scroller;
    private ImageView imView;
    private Canvas imageCanva;

    //ratio of image and booleans helping to keep track of the state of the stack
    private double imRatio ;
    private ImageOriginPoint Ori ;
    private boolean imageOn;
    private  boolean imageCanOn;
    private int zoomLevel;

    //Classes that are used as process tools
    private  ImageSizerPositionner sizerPositionner;
    private ImageRotator imRotator;

    //Other values and booleans to keep track of the calls to rotator
    protected boolean rotateOn;
    private int lastLayerRotatIndex;
    boolean newRotatorNeeded;

    private int stackMaxSize;




    public ImageProcesser(centerPane mainPane){
        //Initializing all classes and javaFX components
        stack = new StackPane();
        stack.getStyleClass().add("stack-pane");
        imView = new ImageView();
        ctrPane = mainPane;
        scroller = new ScrollPane();
        scroller.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroller.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroller.setContent(stack);
        scroller.setFitToWidth(false);
        scroller.setFitToHeight(false);
        Ori = new ImageOriginPoint(100,100);
        ctrPane.getChildren().add(scroller);
        scroller.setTranslateX(Ori.getX());
        scroller.setTranslateY(Ori.getY());

        stackMaxSize = 20;

        rotateOn = false;
        newRotatorNeeded = true;
        sizerPositionner = new ImageSizerPositionner(ctrPane,stack,scroller);
    }

    //First function that is actually called, it creates the image and can refresh everything with a new image.
    public void createImage(Image image) {
        try {
            if (!imageOn) {//If this is the first image called

                //import image
                double imW = image.getWidth();
                double imH = image.getHeight();
                imRatio = imH / imW;//get ratio of the image for the canva



                imView.setImage(image);
                imView.setPreserveRatio(true);
                imView.fitWidthProperty().bind(stack.prefWidthProperty());
                stack.prefHeightProperty().bind(stack.prefWidthProperty().multiply(imRatio));
                stack.getChildren().add(imView); // put in a stack so that we can put a canva OVER it
                imageOn = true;
                double lim = 600;
                scroller.setPrefWidth(Math.min(lim, imW));
                scroller.setPrefHeight(Math.min(imH, lim * imRatio));//We control the size of the scroller and the stack separately


                //Les Event Listener

                EventHandler<MouseEvent> mouseClickOnCorner = sizerPositionner.createInteractionListener((ImageDrawer) this, Ori, imRotator);
                scroller.setOnMouseClicked(mouseClickOnCorner);

            } else {//If new image to refresh and start over
                imRatio = image.getHeight() / image.getWidth();
                stack.getChildren().clear(); // refresh the stack
                setImageCanOn(false);
                imView.setImage(image);
                imView.setVisible(true);
                stack.getChildren().add(imView);
                callResizeStack(image.getWidth());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void callResizeStack(double newWidth){
        if(newWidth > 0) {//avoids bugs related to negative widths
            sizerPositionner.resizeStack(newWidth + Ori.getX(), Ori, imRatio);
            zoomLevel = 0;
        }else{
            throw new IllegalArgumentException("Width must me strictly positive");
        }
    }

    public void callResizeStackNoRatio(double width, double height){
        if(width > 0 && height> 0) {
            imRatio = height/width;
            sizerPositionner.resizeStackNoRatio(width + Ori.getX(),height + Ori.getY(),Ori);
            zoomLevel = 0;
        }else{
            throw new IllegalArgumentException("Width must me strictly positive");
        }
    }


    public void removeLayer(){
        if(imageCanOn) {
            if (stack.getChildren().size() > 2) {//To always keep two first layers beeing imView and Canva
                stack.getChildren().remove(imageCanva);
                stack.getChildren().removeLast();
                stack.getChildren().getLast().setVisible(true);
                ImageView lastLayer  = (ImageView) stack.getChildren().getLast();
                imRatio = lastLayer.getImage().getHeight()/lastLayer.getImage().getWidth();
                stack.getChildren().add(imageCanva);
            }
        }else{
            if (stack.getChildren().size() > 1) {//To always keep two first layers beeing imView and Canva
                stack.getChildren().removeLast();
                ImageView lastLayer  = (ImageView) stack.getChildren().getLast();
                imRatio = lastLayer.getImage().getHeight()/lastLayer.getImage().getWidth();
                stack.getChildren().getLast().setVisible(true); //restore vivibility
            }
        }
    }

    protected void addLayer(ImageView layer, boolean binding){

        if(binding) {// binds all layers to the root layer
            layer.fitWidthProperty().bind(imView.fitWidthProperty());
        }
        layer.setPreserveRatio(true);
        if(imageCanOn) {
            stack.getChildren().remove(imageCanva); //make previous layers invisible
            //ImageMerger.mergeBack(stack,imView,stackMaxSize); // This line does not allow memory leak but could be subject to bugs
            stack.getChildren().add(layer);
            stack.getChildren().add(imageCanva);
        }else{
            stack.getChildren().getLast().setVisible(false);
            //ImageMerger.mergeBack(stack,imView,stackMaxSize); // This line does not allow memory leak but could be subject to bugs
            stack.getChildren().add(layer);
        }
    }



    private void setStackVisiblity(boolean visiblity) {
        for (int i = 0; i < stack.getChildren().size(); ++i) {
            stack.getChildren().get(i).setVisible(visiblity);
        }
    }

    // !!! This function still contains bugs
    public void rotate(double angle) {
        imRatio = 1;

        if(rotateOn){//if rotation is beeing used
            ImageView layer = imRotator.rotate(this,stack,imView,scroller,angle,lastLayerRotatIndex); //rotates the layer
            if(layer != null){
                if(imageCanOn){  //updates the index of the last rotated layer
                    lastLayerRotatIndex = stack.getChildren().size() - 2;
                }else{
                    lastLayerRotatIndex = stack.getChildren().size() - 1;
                }
                addLayer(layer,true); //adds it if not already there
                if(imageCanOn){imageCanva.setVisible(true);}
            }
        }else{
            if(newRotatorNeeded) {//if this is the first rotation ever

                imRotator = new ImageRotator(stack.getWidth(), stack.getHeight(),false);
                newRotatorNeeded = false;
                rotateOn = true;
                setStackVisiblity(false);
                rotate(angle);
            }else{
                imRotator.reRotate(stack, angle);
            }
        }

    }

        //No use of this function yet
    private void bindAllLayers(){
        for(int i = 1 ; i < stack.getChildren().size() ; ++i){
            ImageView iv = (ImageView) stack.getChildren().get(i);
            iv.fitWidthProperty().bind(imView.fitWidthProperty());
        }
    }
    //allows to know that we stopped rotating and started drawing over it.
    protected void endRotate(){
        if(rotateOn){
            imRatio = 1;
            if(imageCanOn) {
                stack.getChildren().remove(imageCanva);
                ImageView lastLayer = (ImageView) stack.getChildren().getLast();
                lastLayer.fitWidthProperty().bind(imView.fitWidthProperty());
                stack.getChildren().add(imageCanva);
            }else{
                ImageView lastLayer = (ImageView) stack.getChildren().getLast();
                lastLayer.fitWidthProperty().bind(imView.fitWidthProperty());
            }
        }
        rotateOn = false;
    }

    private void newRotation(){
        newRotatorNeeded = true;
    }


    //allows to get the last layer's drawings so that we can add it to the stack properly
    protected WritableImage getCanImage(){

        SnapshotParameters spa = new SnapshotParameters();
        spa.setFill(Color.TRANSPARENT);

        WritableImage lastLayer = stack.getChildren().getLast().snapshot(spa,null);

        return lastLayer;

    }

    //The following methods are used by processing tools

    protected void setImageOn(boolean b){
        imageOn = b ;
    }

    protected void setImageCanOn(boolean b){
        imageCanOn = b ;
    }
    protected void setImageCanva(Canvas can){
        imageCanva = can;
    }
    protected ImageView getImView(){
        return imView;
    }

    protected final StackPane getStack(){
        return stack;
    }

    public boolean getImageOn(){
        return imageOn;
    }

    public double getStackWidth(){
        return stack.getWidth();
    }
    public double getStackHeight(){
        return stack.getHeight();
    }

    //Zooming by increments of 100 pixels (arbitrary), it makes the stack bigger in the scroller making it zoom
    public void zoom(boolean zoomIn){
        double zoom = 100;//set increment, can maybe be changed and passed as a parameter
        if(zoomLevel < 0){
            zoomLevel = -1;
        }
        if(zoomIn && zoomLevel < 15){
            ImageSizerPositionner.setDimensions(stack, stack.getWidth() +  zoom, imRatio);
            zoomLevel += 1;
        }else{
            if(stack.getWidth() > scroller.getWidth()/2 ) {
                ImageSizerPositionner.setDimensions(stack, stack.getWidth() - zoom, imRatio);
                zoomLevel -= 1;
            }
        }
    }

    //Calls the cutter on the image of imageProcesser passed as a parameter
    public void callCutImageCanvas(){
        sizerPositionner.createCutListener((ImageDrawer) this, Ori, imRatio);
    }

    //Cuts directly, not UI used
    public void cutImage(int oX, int oY , int width , int height){
        if(width != 0 && width < stack.getWidth()) {
            ImageView layer = new ImageView(ImageSizerPositionner.cut(getStackImage(), oX, oY, width, height));
            addLayer(layer, true);
            imRatio = (double) height / width;
            callResizeStack(width);
        }
    }

    //cuts from the UI canva helping the cut (czlled by confirmButton)
    public void cutImageFromCanva(){
        if(sizerPositionner.cutReady){
            double[] coord = sizerPositionner.getCutCoordinates(Ori); //sizer Positionner handles the cutCanvas
            cutImage((int)coord[0], (int) coord[1], (int) coord[2], (int)coord[3]);
        }
    }

    public final ImageOriginPoint getOriginPoint(){
        return Ori;
    }

    //calls flipator on the stack (displayed on screen) image
    public void flipImage(boolean isHorizontal){
        SnapshotParameters s = new SnapshotParameters();
        s.setFill(Color.TRANSPARENT);
        WritableImage layer = Flipator.flip(stack.snapshot(s, null), isHorizontal);
        addLayer(new ImageView(layer),true);
    }

    // adds a layer with the image of the mozaic of the stack (displayed on screen) image
    public void mosaicFilter(int cloudSize) {
        WritableImage image = getStackImage();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        mosaicMaker mosaicMaker = new mosaicMaker(image, width, height, cloudSize);

        mosaicMaker.makeMosaic();
        ImageView imView = new ImageView(image);
        addLayer(imView, true);
    }


    //gives the stack image
    public WritableImage getStackImage(){
        return stack.snapshot(new SnapshotParameters(), null);
    }
    public boolean getImageCanOn(){
        return imageCanOn;
    }


    public double getImageViewFitWidth(){
        return imView.getFitWidth();
    }

    public double getImageViewFitHeight(){
        return imView.getFitHeight();
    }
    public double getImRatio(){
        return imRatio;
    }

    public double getImWidth() {
        return stack.getWidth();
    }

    public double getImHeight() {
        return stack.getHeight();
    }

    public ObservableDoubleValue getObservableWidth() {
        return stack.widthProperty();
    }

    public ObservableDoubleValue getObservableHeight() {
        return stack.heightProperty();
    }

        public void callSeamCarving(int width, int height) throws NullPointerException{
        seamCarving carve = new seamCarving(getStackImage().getPixelReader(), width, height, (int) getStackImage().getWidth(), (int) getStackImage().getHeight());
        WritableImage wi = carve.carving();

        addLayer(new ImageView(wi), true);
        imRatio = (double) height / width;
        callResizeStack(width);

    }

}

