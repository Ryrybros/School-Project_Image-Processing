package imageProcessTools;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import team.examples.Launcher;
import team.examples.ResizeBar;
import team.examples.centerPane;


public class ImageSizerPositionner {
    protected centerPane ctrPane ;
    protected StackPane stack;
    protected ScrollPane scroller;
    protected ImageView cutCanvas ;
    protected boolean cutReady;
    protected boolean cutWorking;


    public ImageSizerPositionner(centerPane cPane , StackPane stck, ScrollPane scroll){
        ctrPane = cPane;
        stack = stck;
        scroller = scroll;
        cutReady = false;
        cutWorking = false;

    }


    public void resizeStack(double newWidth, ImageOriginPoint O, double imageRatio){

        double val = Math.max(50, Math.min(newWidth, Math.min(ctrPane.getWidth() - O.getX(), (Launcher.primStage.getHeight() - 25) / imageRatio - O.getX() ) ) );
        double x = O.getX();

        setDimensions(stack, Math.max(val - x , 50), imageRatio);
        setDimensions(scroller, Math.min(Math.max(val - x , 50) , Launcher.primStage.getWidth()*0.95), imageRatio);
        stack.setOnMouseDragged(null);
        stack.setOnMouseReleased(null);
        //ends the resize action correcting the size if needed

    }



    public void moveScrollPane(double x , double y, ImageOriginPoint Ori){
        Ori.setOrigin(x, y);
        scroller.setTranslateX(Ori.getX());
        scroller.setTranslateY(Ori.getY());
    }

    public void resizeStackNoRatio(double newWidth, double newHeight ,ImageOriginPoint O){

        resizeStack(newWidth,O,newHeight/newWidth);
        //trick to resize without keeping ratio
    }

    public static void drawCan(Canvas can, Color c, double ox, double oy , double wid, double height){
        can.getGraphicsContext2D().setFill(c);
        can.getGraphicsContext2D().fillRect(ox,oy,wid,height);
    }

    public static void setDimensions(Region n, double width, double imageRatio){
        n.setPrefWidth(width);
        n.setMaxWidth(width);
        n.setMinWidth(width);
        n.setMinHeight(width*imageRatio);
        n.setMaxHeight(width*imageRatio);
    }

    public static void setDimensions(ImageView n, double width, double imageRatio){
        n.setFitWidth(width);
        n.setFitHeight(width*imageRatio);
    }

    public static WritableImage cut(Image im , int oX, int oY , int width , int height){
        PixelReader pixR = im.getPixelReader();
        WritableImage imRes = new WritableImage(width, height);
        if(oX+ width < im.getWidth() && oY + height < im.getHeight() ) {  // gets and returns a subset of original image pixels
            for (int y = 0; y < height - 1; ++y) {
                for (int x = 0; x < width; ++x) {
                    imRes.getPixelWriter().setColor(x, y, pixR.getColor(oX + x, oY + y));
                }
            }
            return imRes;
        }
        return null;
    }

    private EventHandler<KeyEvent> getKeyHandler(Node can, boolean cutting){

            return
                    new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(javafx.scene.input.KeyEvent ev) {
                            if (ev.getCode() == KeyCode.ESCAPE) {
                                ctrPane.getChildren().remove(can);
                                if(cutting){//removes UI temporary canvas/image views
                                    ResizeBar.hideConfirm();
                                    cutReady = false;
                                    cutWorking = false;
                                }
                            }
                        }
                    };


    }


//creates the listeners for move and resize

    //HUGE code repetition here, this is the main modification to add to this class cutHandler and InteractionHandler are too similar
    public EventHandler<MouseEvent> createInteractionListener(ImageDrawer imgProcesser, ImageOriginPoint Ori , ImageRotator rot){

        return        new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent e) {
                if (imgProcesser.getStackWidth()>50 && Math.max(Math.abs(e.getX() - scroller.getWidth()) % scroller.getWidth(), Math.abs(e.getY() - scroller.getHeight())) % scroller.getHeight() < 50 & !imgProcesser.getHoldingPen() & !imgProcesser.getHoldingEyedropper()) {
                    //This is accessed if we touch the bottom right corner of the image
                    Canvas resizeCanvas = new Canvas(Ori.getX() + scroller.getWidth(), Ori.getY() + scroller.getHeight());
                    ImageSizerPositionner.drawCan(resizeCanvas, new Color(0, 0, 0, 0.5), Ori.getX(), Ori.getY(), e.getX(), e.getX() * imgProcesser.getImRatio());
                    ctrPane.getChildren().add(resizeCanvas);

                    EventHandler<KeyEvent> resizeEchap = getKeyHandler(resizeCanvas,false);//allows to remove it if echap is pressed

                    Launcher.primStage.getScene().addEventHandler(KeyEvent.ANY, resizeEchap);




                    EventHandler<MouseEvent> canDrag = resizeDragHandler(resizeCanvas,Ori, imgProcesser.getImRatio(), false);
                    resizeCanvas.setOnMouseDragged(canDrag);

                    resizeCanvas.setOnMouseReleased(ev -> { // follows the mouse drag
                        resizeCanvas.getGraphicsContext2D().clearRect(0, 0, ev.getX(), ev.getX() * imgProcesser.getImRatio());
                        resizeStack(ev.getX(), Ori, imgProcesser.getImRatio() );
                        ctrPane.getChildren().remove(resizeCanvas);
                        Launcher.primStage.getScene().removeEventHandler(KeyEvent.ANY, resizeEchap);
                    });
                } else {

                    if (e.getY() < 25 && !imgProcesser.getHoldingPen() && !imgProcesser.getHoldingEyedropper()) {
                        //if we touch the top of the image

                        Canvas moveCanvas = new Canvas(scroller.getWidth() + Ori.getX(), scroller.getHeight() + Ori.getY());
                        ImageSizerPositionner.drawCan(moveCanvas, new Color(0, 0, 0, 0.5), Ori.getX(), Ori.getY(), scroller.getWidth(), scroller.getHeight());
                        ctrPane.getChildren().add(moveCanvas);
                        EventHandler<KeyEvent> moveEchap = getKeyHandler(moveCanvas,false);
                        Launcher.primStage.getScene().addEventHandler(KeyEvent.ANY, moveEchap );

                        EventHandler<MouseEvent> canDrag = moveDragHandler(moveCanvas,Ori,false);//handles the drag action
                        moveCanvas.setOnMouseDragged(canDrag);

                        moveCanvas.setOnMouseReleased(ev -> {//translating the whole image. Stays in bounds thanks to previous canDrag handler
                            moveScrollPane(moveCanvas.getTranslateX() + Ori.getX(), moveCanvas.getTranslateY() + Ori.getY(), Ori);
                            ctrPane.getChildren().remove(moveCanvas);
                            Launcher.primStage.getScene().removeEventHandler(KeyEvent.ANY, moveEchap );

                        });
                    }
                }

            }
        };
    }

//Really similar to the previous one !!!
    public void createCutListener(ImageDrawer imgProcesser, ImageOriginPoint Ori , double imRatio){

        if(!cutWorking) {
            cutWorking = true;
            cutCanvas = new ImageView(new Image("imageContour.jpg"));
            cutCanvas.setOpacity(0.3);
            cutReady = true;
            cutCanvas.setTranslateX(Ori.getX());
            cutCanvas.setTranslateY(Ori.getY());
            cutCanvas.setFitWidth(scroller.getWidth());
            cutCanvas.setFitHeight(scroller.getHeight());

            ctrPane.getChildren().add(cutCanvas);

            Launcher.primStage.getScene().setOnKeyPressed(getKeyHandler(cutCanvas,true));

            EventHandler<MouseEvent> clickHandler = new EventHandler<javafx.scene.input.MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent e) {

                    if (e.getY() < cutCanvas.getFitHeight() / 4) {

                        EventHandler<MouseEvent> canMoveDrag = moveDragHandler(cutCanvas, Ori, true);
                        cutCanvas.setOnMouseDragged(canMoveDrag);
                    } else {

                        if ((e.getX() > 3 * cutCanvas.getFitWidth() / 4) && (e.getY() > 3 * cutCanvas.getFitHeight() / 4)) {
                            EventHandler<MouseEvent> canResizeDrag = resizeDragHandler(cutCanvas, Ori, imRatio, true);
                            cutCanvas.setOnMouseDragged(canResizeDrag);
                        }
                    }
                }
            };
            cutCanvas.setOnMouseClicked(clickHandler);

            cutCanvas.setOnMouseReleased(new EventHandler<javafx.scene.input.MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent e) {
                    cutCanvas.setOnMouseDragged(null);
                }
            });

        }

    }
    //The actual handler for resizing
    protected EventHandler<MouseEvent>  resizeDragHandler(Node node, ImageOriginPoint Ori , double imRatio,boolean cutting){

        return
                new EventHandler<javafx.scene.input.MouseEvent>() {
                    @Override
                    public void handle(javafx.scene.input.MouseEvent e2) {
                        double colorVal = 0;
                        //clears the canvas and redraws it to the current size, it is called at every move of the mouse
                        if(!cutting) {
                            Canvas resizeCanvas = (Canvas) node;
                            resizeCanvas.getGraphicsContext2D().clearRect(0, 0, resizeCanvas.getWidth(), resizeCanvas.getHeight());
                            resizeCanvas.setWidth(e2.getX());
                            resizeCanvas.setHeight(e2.getX() * imRatio);

                            ImageSizerPositionner.drawCan(resizeCanvas, new Color(colorVal, 0, 0, 0.5), Ori.getX(), Ori.getY(), e2.getX() - Ori.getX(), (e2.getX() - Ori.getX()) * imRatio);
                        }else{
                            ImageView imView = (ImageView) node;
                            imView.setFitWidth(e2.getX() - Ori.getX());
                            imView.setFitHeight(e2.getY() - Ori.getY());
                        }

                    }
                };
    }



    protected EventHandler<MouseEvent>  moveDragHandler(Node moveCanvas, ImageOriginPoint Ori , boolean cutting){
        return
                        new EventHandler<javafx.scene.input.MouseEvent>() {
                            @Override
                            public void handle(javafx.scene.input.MouseEvent e2) {
                                //moveCanvas.setLayoutX(e2.getSceneX() - Ori.getX());

                                double x = ctrPane.sceneToLocal(e2.getSceneX(), e2.getSceneY()).getX();
                                double y = ctrPane.sceneToLocal(e2.getSceneX(), e2.getSceneY()).getY();

                                if(!cutting) {
                                    moveCanvas.setTranslateX(Math.max(20 - Ori.getX(), Math.min(x - Ori.getX(), ctrPane.getWidth() - Ori.getX() - scroller.getWidth())));
                                    moveCanvas.setTranslateY(Math.max(20 - Ori.getY(), Math.min(y - Ori.getY(), ctrPane.getHeight() - Ori.getY() - scroller.getHeight())));
                                }else{
                                    ImageView imView = (ImageView) moveCanvas;
                                    imView.setTranslateX(Math.max(Ori.getX(), Math.min(x,  Ori.getX() + scroller.getWidth() - imView.getFitWidth())));
                                    imView.setTranslateY(Math.max(Ori.getY(), Math.min(y, Ori.getY() + scroller.getHeight() - imView.getFitHeight())));

                                }

                            }
                        };

    }

    public double[] getCutCoordinates(ImageOriginPoint Ori){//Returns the coordinates and dimensions of the subimage to keep
          Canvas can = new Canvas(scroller.getWidth(), scroller.getHeight());
          ctrPane.getChildren().remove(cutCanvas);

          double[] list = new double[4];
          list[0] = cutCanvas.getTranslateX() - Ori.getX();
          list[1] = cutCanvas.getTranslateY() -Ori.getY();
          list[2] = cutCanvas.getFitWidth();
          list[3] = cutCanvas.getFitHeight();
          cutReady = false;
          cutWorking = false;
          return list;

      }

}
