package team.examples;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


// ScrollBar displaying the filter tools (mosaic maker)
public class FilterBar extends ScrollBar {

    // Number of points for the algorithm
    private int cloudSize = 10000;
    // Number of pixels in the image
    private int pixels;

    public FilterBar(centerPane mainPane) {
        super(mainPane);

        Button mosaic = new Button("Mosaic");

        Label nbPixels = new Label("Number of pixels: ");
        hide(nbPixels);
        Label nbPoints = new Label("Number of points:");
        hide(nbPoints);
        TextField chooseNbPoints = getNumberField();
        chooseNbPoints.setText(String.valueOf(cloudSize));
        hide(chooseNbPoints);

        Button startMosaic = new Button("Start");
        startMosaic.getStyleClass().add("button-option");
        hide(startMosaic);

        // Let the user know if the number of points is greater than the number of pixels
        Label tooManyPoints = new Label("Too many points");
        hide(tooManyPoints);

        // Show and hide the algorithm options
        EventHandler<ActionEvent> mosaicButtonEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                pixels = (int) mainPane.imageProcesser.getImWidth() * (int) mainPane.imageProcesser.getImHeight();
                nbPixels.setText("Number of pixels: " + pixels);
                showHide(nbPixels);
                showHide(nbPoints);
                showHide(chooseNbPoints);
                showHide(startMosaic);
            }
        };
        mosaic.setOnAction(mosaicButtonEvent);

        // Applies the algorithm to the image
        EventHandler<ActionEvent> startMosaicEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                cloudSize = Integer.parseInt(chooseNbPoints.getText());

                if(cloudSize > pixels) {
                    show(tooManyPoints);
                }
                else {
                    hide(tooManyPoints);
                    mainPane.imageProcesser.mosaicFilter(cloudSize);
                }
            }
        };
        startMosaic.setOnAction(startMosaicEvent);

        this.getChildren().addAll(mosaic, nbPixels, nbPoints, chooseNbPoints, tooManyPoints, startMosaic);
    }
}
