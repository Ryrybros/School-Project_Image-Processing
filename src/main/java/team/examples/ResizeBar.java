package team.examples;

import javafx.beans.value.ObservableDoubleValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;

// ScrollBar displaying the resize options
public class ResizeBar extends ScrollBar{

    // Maximal and minimal width of the image
    private final double MINWIDTH = 50;
    private final double MAXWIDTH = 1000;

    private Label widthLabel;
    private Label heightLabel;
    private Slider widthSlider;
    private TextField widthChoice;
    private static Button confirmCrop;

    private ObservableDoubleValue obsWidth;
    private ObservableDoubleValue obsHeight;

    public ResizeBar(centerPane mainPane) {
        super(mainPane);

        // Width and height values of the image
        widthLabel = new Label("Width = 0");
        heightLabel = new Label("Height = 0");
      
        // These values change automatically when the size of the image is modified, even from the mainPane
        obsWidth = mainPane.imageProcesser.getObservableWidth();
        
        obsWidth.addListener((target, oldVal, newVal) ->
                updateWidth(newVal.doubleValue())
        );

        obsHeight = mainPane.imageProcesser.getObservableHeight();
        obsHeight.addListener((target, oldVal, newVal) ->
                updateHeight(newVal.doubleValue())
        );


        // Resize keeping image ratio
        Button resize = new Button("Resize");

        // Slider to choose the wanted width of the image
        widthSlider = new Slider(MINWIDTH, MAXWIDTH, 0);
        widthSlider.setMajorTickUnit(100);
        widthSlider.setMinorTickCount(50);
        widthSlider.setShowTickMarks(true);
        widthSlider.setShowTickLabels(true);
        widthSlider.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            double newWidth = newValue.doubleValue();
            mainPane.imageProcesser.callResizeStack(newWidth);
        });
        hide(widthSlider);


        Label chooseWidthLabel = new Label("Width:");
        chooseWidthLabel.getStyleClass().add("label-option");
        hide(chooseWidthLabel);

        // NumberField to choose the width precisely
        widthChoice = getNumberField();
        hide(widthChoice);
        EventHandler<ActionEvent> widthChoiceEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                double newWidth = Double.parseDouble(widthChoice.getText());
                mainPane.imageProcesser.callResizeStack(newWidth);
            }
        };
        widthChoice.setOnAction(widthChoiceEvent);


        // Crop
        Button crop = new Button("Crop");
        confirmCrop = new Button("Confirm");
        confirmCrop.getStyleClass().add("button-option");
        hide(confirmCrop);

        // Applies the resizing displayed in the mainPane
        EventHandler<ActionEvent> onConfirm = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                mainPane.imageProcesser.cutImageFromCanva();
                showHide(confirmCrop);
            }
        };
        confirmCrop.setOnAction(onConfirm);


        // Zoom
        Button zoom = new Button("Zoom");

        Button plus = new Button("+");
        plus.getStyleClass().add("button-option");
        hide(plus);

        Button minus = new Button("-");
        minus.getStyleClass().add("button-option");
        hide(minus);


        EventHandler<ActionEvent> zoomPlus = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                mainPane.imageProcesser.zoom(true);
            }
        };
        plus.setOnAction(zoomPlus);

        EventHandler<ActionEvent> zoomMinus = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                mainPane.imageProcesser.zoom(false);
            }
        };
        minus.setOnAction(zoomMinus);


        // Seam carving
        Button seamCarving = new Button("Seam carving");

        Label carvingWidth = new Label("New width:");
        carvingWidth.getStyleClass().add("label-option");
        hide(carvingWidth);

        TextField widthField = getNumberField();
        widthField.setText("150");
        hide(widthField);

        Label carvingHeight = new Label("New heigth:");
        carvingHeight.getStyleClass().add("label-option");
        hide(carvingHeight);

        TextField heightField = getNumberField();
        heightField.setText("150");
        hide(heightField);

        Button carvingButton = new Button("Start");
        carvingButton.getStyleClass().add("button-option");
        hide(carvingButton);

        Label tooSmall = new Label("The dimensions are too small");
        hide(tooSmall);

        Label tooLarge = new Label("The new dimensions should not be larger than the actual ones.");
        hide(tooLarge);

        // Let the user know when the dimensions are too small or too large
        EventHandler<ActionEvent> carvingEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                hide(tooLarge);
                hide(tooSmall);
                int wid = Integer.parseInt(widthField.getText());
                int hei = Integer.parseInt(heightField.getText());

                if(wid > obsWidth.get() || hei > obsHeight.get()) {
                    show(tooLarge);
                }
                else {
                    try {
                        mainPane.imageProcesser.callSeamCarving(wid, hei);
                    } catch (NullPointerException e) {
                        // This exception is thrown when the chosen dimensions are too small compared to the base image
                        show(tooSmall);
                    }
                }
            }
        };
        carvingButton.setOnAction(carvingEvent);



        // Shows or hides the resize with ratio options
        EventHandler<ActionEvent> resizeButtonEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                hide(confirmCrop);
                hide(plus);
                hide(minus);
                hide(carvingWidth);
                hide(widthField);
                hide(carvingHeight);
                hide(heightField);
                hide(carvingButton);

                showHide(widthSlider);
                showHide(chooseWidthLabel);
                showHide(widthChoice);
            }
        };
        resize.setOnAction(resizeButtonEvent);

        // Shows or hides the crop options
        EventHandler<ActionEvent> onCropClick = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                hide(chooseWidthLabel);
                hide(widthChoice);
                hide(widthSlider);
                hide(plus);
                hide(minus);
                hide(carvingWidth);
                hide(widthField);
                hide(carvingHeight);
                hide(heightField);
                hide(carvingButton);

                mainPane.imageProcesser.callCutImageCanvas();
                showHide(confirmCrop);
            }
        };
        crop.setOnAction(onCropClick);

        // Shows or hides the zoom options
        EventHandler<ActionEvent> zoomButtonEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                hide(chooseWidthLabel);
                hide(widthChoice);
                hide(widthSlider);
                hide(confirmCrop);
                hide(carvingWidth);
                hide(widthField);
                hide(carvingHeight);
                hide(heightField);
                hide(carvingButton);

                showHide(plus);
                showHide(minus);
            }
        };
        zoom.setOnAction(zoomButtonEvent);


        EventHandler<ActionEvent> seamCarvingEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                hide(chooseWidthLabel);
                hide(widthChoice);
                hide(widthSlider);
                hide(confirmCrop);
                hide(plus);
                hide(minus);

                showHide(carvingWidth);
                showHide(widthField);
                showHide(carvingHeight);
                showHide(heightField);
                showHide(carvingButton);
            }
        };
        seamCarving.setOnAction(seamCarvingEvent);


        this.getChildren().addAll(widthLabel, heightLabel, resize, chooseWidthLabel, widthChoice, widthSlider, crop,confirmCrop,zoom, plus, minus, seamCarving, carvingWidth, widthField, carvingHeight, heightField,carvingButton, tooSmall, tooLarge);
    }

    // Hide the confirm button for crop from another class
    public static void hideConfirm(){
        confirmCrop.setVisible(false);
        confirmCrop.setManaged(false);
    }

    // Change showed values of width
    private void updateWidth(double newWidth) {
        widthLabel.setText("Width = " + String.format("%.1f", newWidth));
    }

    // Change showed values of height
    private void updateHeight(double newHeight) {
        heightLabel.setText("Height = " + String.format("%.1f", newHeight));
    }
    


}
