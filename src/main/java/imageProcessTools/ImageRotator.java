package imageProcessTools;

import javafx.scene.SnapshotParameters;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class ImageRotator {

    private double imAngle;
    public final double diag;
    public ImageView argImView;
    boolean previousCall;



    public ImageRotator(double width, double height,boolean prevCall){
        imAngle = 0;
        diag = Math.sqrt(width*width + height*height);
        previousCall = prevCall;
    }


    public ImageView rotate(ImageProcesser imgPro ,StackPane stack,ImageView imView,ScrollPane scroller,double angle,int ind){

        if(!previousCall){
            SnapshotParameters s = new SnapshotParameters();
            s.setFill(Color.TRANSPARENT);
            setStackVisiblity(true, stack);
            ImageView imV = new ImageView( stack.snapshot(s, null) );
            setStackVisiblity(false, stack);
            argImView = new ImageView(imV.getImage());
            previousCall = true;


            reDim(stack,diag,diag); // sets the stack as a square around the rotating layer
            reDim(scroller,diag,diag);


        }


        argImView.setRotate(angle);


        if(!stack.getChildren().contains(argImView)){
            return argImView;
        }else{
            return null;
        }
    }


    public ImageView reRotate(StackPane stack,double angle){
        //does not try to recreate a rotation

        argImView.setRotate(angle);

        argImView.setVisible(true);

        return argImView;
    }

    private void reDim(Region n, double wid, double hei){
        n.setPrefWidth(wid);
        n.setMaxWidth(wid);
        n.setMinWidth(wid);
        n.setMinHeight(hei);
        n.setMaxHeight(hei);
    }

    private void setStackVisiblity(boolean visiblity, StackPane stack){
        for (int i = 0 ; i < stack.getChildren().size() ; ++i){
            stack.getChildren().get(i).setVisible(visiblity);
        }
    }
}
