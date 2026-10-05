package team.examples;

import imageProcessTools.ImageDrawer;
import javafx.scene.layout.AnchorPane;

public class centerPane extends AnchorPane {


    public final double minX;
    private float mainWidth;
    ImageDrawer imageProcesser;
    public centerPane(double mX){
        mainWidth = 300;//  arbitrary value to initialize
        minX = mX;
        this.getStyleClass().add("center-pane");

    }


    public void initialize(){
        imageProcesser = new ImageDrawer(this);


        Launcher.primStage.widthProperty().addListener((o, oldValue, newValue) -> {

            if(imageProcesser.getImageOn()) {//Does not allow to shrink the screen more that the dimensions of the image
                double limWidth = Math.max(300, imageProcesser.getStackHeight() / imageProcesser.getImRatio() + minX  + 50);
                if (newValue.floatValue() < limWidth) {
                    Launcher.primStage.setResizable(false);
                    Launcher.primStage.setWidth(limWidth);
                    Launcher.screenRatio = (float) (Launcher.primStage.getHeight() / Launcher.primStage.getWidth());
                    Launcher.primStage.setResizable(true);
                }
            }
        });

        Launcher.primStage.heightProperty().addListener((o, oldValue, newValue) -> {

            if(imageProcesser.getImageOn()) {
                float max = (float) Math.max(imageProcesser.getImageViewFitWidth() * imageProcesser.getImRatio(), 100);
                if (newValue.floatValue() < max * 1.05) {
                    Launcher.primStage.setResizable(false);
                    Launcher.primStage.setHeight(max * 1.1);
                    Launcher.primStage.setResizable(true);

                }
            }
        });

    }


}
