package imageProcessTools;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import team.examples.DrawBar;
import team.examples.centerPane;

public class ImageDrawer extends ImageProcesser{

    //This class inherites from imageProcesser because it uses attributes of the image processor in its listener

    private boolean drawingOn;
    private boolean holdingPen;
    private boolean holdingEyedropper;
    private Canvas drawCan;
    
    public ImageDrawer(centerPane mainPane) {
        super(mainPane);
        drawingOn = false;
    }




    //creates the drawing canvas
    public void startCanva(){
        super.setImageCanOn(true);
        drawCan = ImageDrawer.createCanva(super.getStack(),super.getImView());
        super.setImageCanva(drawCan);
        setDrawingOn(true);//allows drawing or not
        setHoldingEyedropper(false);
        createDrawListener();
        createEyedropperListener();

    }


//actually creates a canvas
    private static Canvas createCanva(StackPane stack , ImageView imView){
        double wid = imView.getFitWidth();
        double heig = imView.getFitHeight();
        Canvas drawCan = new Canvas(wid, heig);
        stack.getChildren().add(drawCan);
        drawCan.widthProperty().bind(stack.widthProperty());
        drawCan.heightProperty().bind(stack.heightProperty());
        return drawCan;
    }



    protected void setDrawingOn(boolean b){
        drawingOn = b;
    }

    public void setHoldingPen(boolean b){
        if(drawingOn) {
            holdingPen = b;
        }
    }

    public boolean getHoldingPen(){
        return holdingPen;
    }

    public void setHoldingEyedropper(boolean b) {
        holdingEyedropper = b;
    }

    public boolean getHoldingEyedropper() {
        return holdingEyedropper;
    }

    //DRAWING PART

    private void createDrawListener(){
        if(drawingOn) {
            EventHandler<MouseEvent> drawMousePressEvent =
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            if(holdingPen) {
                                GraphicsContext gc = drawCan.getGraphicsContext2D();
                                gc.setLineCap(StrokeLineCap.ROUND); // Les bords de la ligne tracée
                                gc.setLineJoin(StrokeLineJoin.ROUND);   // Les angles de la ligne tracée
                                gc.setLineWidth(DrawBar.getDrawSize());
                                gc.setStroke(DrawBar.getDrawColor());
                                double x = e.getX();
                                double y = e.getY();
                                gc.beginPath();
                                gc.lineTo(x, y);
                                gc.stroke();
                            }
                        }
                    };


            EventHandler<MouseEvent> drawMouseDragEvent =
                    new EventHandler<javafx.scene.input.MouseEvent>() {
                        @Override
                        public void handle(javafx.scene.input.MouseEvent e) {
                            if (holdingPen) {
                                GraphicsContext gc = drawCan.getGraphicsContext2D();
                                gc.setLineCap(StrokeLineCap.ROUND);
                                gc.setLineJoin(StrokeLineJoin.ROUND);
                                gc.setLineWidth(DrawBar.getDrawSize());
                                gc.setStroke(DrawBar.getDrawColor());
                                double x = e.getX();
                                double y = e.getY();
                                gc.lineTo(x,y);
                                gc.stroke();
                            }

                        }
                    };

            EventHandler<MouseEvent> drawMouseReleaseEvent =
                    new EventHandler<javafx.scene.input.MouseEvent>() {
                        @Override
                        public void handle(javafx.scene.input.MouseEvent e) {
                            if (holdingPen) {
                                callAddLayer();
                            }
                        }
                    };

            drawCan.setOnMousePressed(drawMousePressEvent);
            drawCan.setOnMouseReleased(drawMouseReleaseEvent);
            drawCan.setOnMouseDragged(drawMouseDragEvent);
        }
    }

    private void createEyedropperListener() {
        EventHandler<MouseEvent> eyedropperEvent = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if(holdingEyedropper) {
                    double x = e.getX();
                    double y = e.getY();
                    // On peut sûrement faire mieux que ça ? C'est pratique mais ça paraît un peu overkill
                    Robot robot = new Robot();
                    Color newColor = robot.getPixelColor(robot.getMouseX(), robot.getMouseY());
                    DrawBar.setDrawColor(newColor);
                }
            }
        };
        drawCan.setOnMouseClicked(eyedropperEvent);
    }

    private void callAddLayer(){
        WritableImage writableImage = getCanImage();
        ImageView layer = new ImageView();
        addLayer(layer, true);
        layer.setImage(writableImage);
        drawCan.getGraphicsContext2D().clearRect(0, 0, drawCan.getWidth(), drawCan.getHeight());
        if (rotateOn) {//tracks the fact that rotate was interrupted
            endRotate();
        }
    }

    public void createRectangle(Color c,int oX, int oY , int width , int height){
        createShape(c, oX,  oY ,  width ,  height,  "rectangle");
    }

    public void createOval(Color c,int oX, int oY , int width , int height){
        createShape(c, oX,  oY ,  width ,  height,  "oval");
    }

    public void createSquare(Color c, int oX, int oY , int side ){
        createRectangle(c,oX,oY,side,side);
    }

    public void createImage(Image im, int oX, int oY, int width , int height ){
        createShape(Color.TRANSPARENT, oX,oY,width,height, "image",im );
    }

    private void createShape(Color c, int oX, int oY , int width , int height, String shape, Image... images){
        if(drawingOn){
                drawCan.getGraphicsContext2D().setFill(c);
                double x = Math.min(Math.max(0, oX), getStackWidth());
                double y = Math.min(Math.max(0, oY), getStackHeight());
                double wid = Math.max(Math.min(width, getStackWidth() - x), 0);
                double hig = Math.max(Math.min(height, getStackHeight() - y), 0);

            switch (shape) {
                case "rectangle":
                    drawCan.getGraphicsContext2D().fillRect(x, y, wid, hig);
                    break;
                case "oval":
                    drawCan.getGraphicsContext2D().fillOval(x,y,wid,hig);
                    break;
                case "image":
                    drawCan.getGraphicsContext2D().drawImage(images[0],x,y,wid,hig);
                    break;

            }
            callAddLayer();
        }
    }

}
