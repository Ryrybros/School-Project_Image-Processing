package team.examples;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

// ScrollBar displaying the rotate and flip tools
public class RotateBar extends ScrollBar{

    private Slider angleSlider ;

    public RotateBar(centerPane mainPane) {
        super(mainPane);

        // Rotate tools
        Button rotateBut = new Button("Rotate");

        Label angleValue = new Label("Angle of rotation: 0.0");
        angleSlider = new Slider();

        // Slider to rotate the image with the selected angle
        angleSlider = new Slider(0, 360, 0);
        angleSlider.setMajorTickUnit(30);
        angleSlider.setMinorTickCount(15);
        angleSlider.setShowTickMarks(true);
        angleSlider.setShowTickLabels(true);
        angleSlider.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            double newAngle = newValue.doubleValue();
            mainPane.imageProcesser.rotate(newAngle);
            angleValue.setText("Angle of rotation: " + String.format("%.1f",newAngle) ) ;

        });
        hide(angleSlider);
        hide(angleValue);

        // Flip options
        Button flip = new Button("Flip");

        Button horizontalFlip = new Button("Horizontal flip");
        horizontalFlip.getStyleClass().add("button-option");
        hide(horizontalFlip);
        Button verticalFlip = new Button("Vertical flip");
        verticalFlip.getStyleClass().add("button-option");
        hide(verticalFlip);

        // EventHandlers to apply the flips on the image
        EventHandler<ActionEvent> horizontalFlipEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                mainPane.imageProcesser.flipImage(true);
            }
        };
        horizontalFlip.setOnAction(horizontalFlipEvent);

        EventHandler<ActionEvent> verticalFlipEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                mainPane.imageProcesser.flipImage(false);
            }
        };
        verticalFlip.setOnAction(verticalFlipEvent);

        // Shows or hides the rotate options
        EventHandler<ActionEvent> rotateAction = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                hide(horizontalFlip);
                hide(verticalFlip);

                showHide(angleSlider);
                showHide(angleValue);
            }
        };
        rotateBut.setOnAction(rotateAction);

        // Shows or hides the flip options
        EventHandler<ActionEvent> flipButtonEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                hide(angleValue);
                hide(angleSlider);

                showHide(horizontalFlip);
                showHide(verticalFlip);
            }
        };
        flip.setOnAction(flipButtonEvent);


        this.getChildren().addAll(rotateBut,angleSlider,angleValue ,  flip, horizontalFlip, verticalFlip);
    }

}
