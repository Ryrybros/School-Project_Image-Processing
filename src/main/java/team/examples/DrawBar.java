package team.examples;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;

import static team.examples.Launcher.primStage;

// ScrollBar displaying the drawing tools
public class DrawBar extends ScrollBar {

    // Properties of the drawing
    private static Color drawColor;
    private static double drawSize;

    // Canvas displaying the drawing color
    private static Canvas colorCanvas;
    private static GraphicsContext gc;

    public DrawBar(centerPane mainPane) {
        super(mainPane);

        drawSize = 10.0;

        // Drawing color
        colorCanvas = new Canvas(50, 50);
        gc = colorCanvas.getGraphicsContext2D();
        setDrawColor(Color.BLACK);
        colorCanvas.getStyleClass().add("canvas");

        ColorPicker colorPicker = new ColorPicker(drawColor);
        colorPicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                setDrawColor(colorPicker.getValue());
            }
        });

        // Eyedropper tool
        Button eyedropper = new Button("Eyedropper");

        // Pencil tool
        Button drawButton = new Button("Draw");

        Label size = new Label("Pencil size: " + String.format("%.1f", drawSize));
        size.getStyleClass().add("label-option");
        hide(size);

        Slider changeSize = new Slider(1.0, 50.0, drawSize);
        hide(changeSize);
        changeSize.setMajorTickUnit(5);
        changeSize.setShowTickMarks(true);
        changeSize.setShowTickLabels(true);
        changeSize.valueProperty().addListener((observable, oldValue, newValue) ->
                {
                    drawSize = newValue.doubleValue();
                    size.setText("Pencil size: " + String.format("%.1f", drawSize));
                }
        );

        // Insert shapes and images
        Button shapes = new Button("Shapes");

        // Number fields to choose the position and the size of the shapes
        Label posX = new Label("X position");
        size.getStyleClass().add("label-option");
        hide(posX);
        TextField choosePosX = getNumberField();
        choosePosX.setText("100");
        hide(choosePosX);

        Label posY = new Label("Y position");
        size.getStyleClass().add("label-option");
        hide(posY);
        TextField choosePosY = getNumberField();
        choosePosY.setText("100");
        hide(choosePosY);

        Label widthLab = new Label("Width");
        size.getStyleClass().add("label-option");
        hide(widthLab);
        TextField chooseWidth = getNumberField();
        chooseWidth.setText("50");
        hide(chooseWidth);

        Label heightLab = new Label("Height");
        size.getStyleClass().add("label-option");
        hide(heightLab);
        TextField chooseHeight = getNumberField();
        chooseHeight.setText("20");
        hide(chooseHeight);

        // Let the user know when the position of the shape is not allowed in the image
        Label notAllowedValues = new Label();
        size.getStyleClass().add("label-option");
        hide(notAllowedValues);

        // Buttons to draw the shapes
        Button rectangle = new Button("Rectangle");
        rectangle.getStyleClass().add("button-option");
        hide(rectangle);

        Button square = new Button("Square");
        square.getStyleClass().add("button-option");
        hide(square);

        Button oval = new Button("Oval");
        oval.getStyleClass().add("button-option");
        hide(oval);

        Button circle = new Button("Circle");
        circle.getStyleClass().add("button-option");
        hide(circle);

        Button importImage = new Button("Add image");
        importImage.getStyleClass().add("button-option");
        hide(importImage);

        // Array of nodes linked to the shape button
        Node[] shapeNodes = new Node[]{posX, choosePosX, posY, choosePosY, widthLab, chooseWidth, heightLab, chooseHeight, notAllowedValues, rectangle, square, oval, circle, importImage};

        // Let the user know that the image is no longer resizable or movable from the image view
        Label noResizable = new Label("The image can't be resized \n" +
                "and moved from the image view \n" +
                "while eyedropper or pencil is activated.");
        hide(noResizable);

        // EventHandlers for the tools
        EventHandler<ActionEvent> eyedropperEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // Hiding the other tools while this one is activated
                mainPane.imageProcesser.setHoldingPen(false);
                hide(size);
                hide(changeSize);
                for(Node node:shapeNodes) {
                    hide(node);
                }

                // Activates the drawing canvas the first time it is used
                if(!mainPane.imageProcesser.getImageCanOn()) {
                    mainPane.imageProcesser.startCanva();
                }

                // Activates or desactivates the eyedropper tool
                mainPane.imageProcesser.setHoldingEyedropper(!mainPane.imageProcesser.getHoldingEyedropper());

                // Shows or hides the informative label
                if(mainPane.imageProcesser.getHoldingEyedropper()) {
                    show(noResizable);
                }
                else {
                    hide(noResizable);
                }
            }
        };
        eyedropper.setOnAction(eyedropperEvent);

        EventHandler<ActionEvent> drawEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                mainPane.imageProcesser.setHoldingEyedropper(false);
                for(Node node:shapeNodes) {
                    hide(node);
                }
                // Shows or hides the pencil options
                showHide(size);
                showHide(changeSize);


                if(!mainPane.imageProcesser.getImageCanOn()) {
                    mainPane.imageProcesser.startCanva();
                }
                mainPane.imageProcesser.setHoldingPen(!mainPane.imageProcesser.getHoldingPen());
                if(mainPane.imageProcesser.getHoldingPen()) {
                    show(noResizable);
                }
                else {
                    hide(noResizable);
                }
            }
        };
        drawButton.setOnAction(drawEvent);


        EventHandler<ActionEvent> shapesButtonEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                mainPane.imageProcesser.setHoldingPen(false);
                mainPane.imageProcesser.setHoldingEyedropper(false);
                hide(size);
                hide(changeSize);
                hide(noResizable);

                if(!mainPane.imageProcesser.getImageCanOn()) {
                    mainPane.imageProcesser.startCanva();
                }

                for(Node node:shapeNodes) {
                    showHide(node);
                    hide(notAllowedValues);
                }
            }
        };
        shapes.setOnAction(shapesButtonEvent);

        // EventHandlers for the buttons that insert shapes and image
        EventHandler<ActionEvent> rectEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int oX = Integer.parseInt(choosePosX.getText());
                int oY = Integer.parseInt(choosePosY.getText());
                int width = Integer.parseInt(chooseWidth.getText());
                int height = Integer.parseInt(chooseHeight.getText());

                // Checks if the position is allowed, and let the user know if it is not
                if(oX > mainPane.imageProcesser.getImWidth()) {
                    notAllowedValues.setText("X is too large");
                    show(notAllowedValues);
                } else if (oY > mainPane.imageProcesser.getImHeight()) {
                    notAllowedValues.setText("Y is too large");
                    show(notAllowedValues);
                }
                // Draws the shape
                else {
                    hide(notAllowedValues);
                    mainPane.imageProcesser.createRectangle(drawColor, oX, oY, width, height);
                }
            }
        };
        rectangle.setOnAction(rectEvent);


        EventHandler<ActionEvent> squareEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int oX = Integer.parseInt(choosePosX.getText());
                int oY = Integer.parseInt(choosePosY.getText());
                int side = Integer.parseInt(chooseWidth.getText());

                if(oX > mainPane.imageProcesser.getImWidth()) {
                    notAllowedValues.setText("X is too large");
                    show(notAllowedValues);
                } else if (oY > mainPane.imageProcesser.getImHeight()) {
                    notAllowedValues.setText("Y is too large");
                    show(notAllowedValues);
                }
                else {
                    hide(notAllowedValues);
                    mainPane.imageProcesser.createSquare(drawColor, oX, oY, side);
                }
            }
        };
        square.setOnAction(squareEvent);

        EventHandler<ActionEvent> ovalEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int oX = Integer.parseInt(choosePosX.getText());
                int oY = Integer.parseInt(choosePosY.getText());
                int width = Integer.parseInt(chooseWidth.getText());
                int height = Integer.parseInt(chooseHeight.getText());

                if(oX > mainPane.imageProcesser.getImWidth()) {
                    notAllowedValues.setText("X is too large");
                    show(notAllowedValues);
                } else if (oY > mainPane.imageProcesser.getImHeight()) {
                    notAllowedValues.setText("Y is too large");
                    show(notAllowedValues);
                }
                else {
                    hide(notAllowedValues);
                    mainPane.imageProcesser.createOval(drawColor, oX, oY, width, height);
                }
            }
        };
        oval.setOnAction(ovalEvent);


        EventHandler<ActionEvent> circleEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int oX = Integer.parseInt(choosePosX.getText());
                int oY = Integer.parseInt(choosePosY.getText());
                int radius = Integer.parseInt(chooseWidth.getText());

                if(oX > mainPane.imageProcesser.getImWidth()) {
                    notAllowedValues.setText("X is too large");
                    show(notAllowedValues);
                } else if (oY > mainPane.imageProcesser.getImHeight()) {
                    notAllowedValues.setText("Y is too large");
                    show(notAllowedValues);
                }
                else {
                    hide(notAllowedValues);
                    mainPane.imageProcesser.createOval(drawColor, oX, oY, radius, radius);
                }
            }
        };
        circle.setOnAction(circleEvent);

        // Import an image and insert it in the existing image
        EventHandler<ActionEvent> imageEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int oX = Integer.parseInt(choosePosX.getText());
                int oY = Integer.parseInt(choosePosY.getText());
                int width = Integer.parseInt(chooseWidth.getText());
                int height = Integer.parseInt(chooseHeight.getText());

                if(oX > mainPane.imageProcesser.getImWidth()) {
                    notAllowedValues.setText("X is too large");
                    show(notAllowedValues);
                } else if (oY > mainPane.imageProcesser.getImHeight()) {
                    notAllowedValues.setText("Y is too large");
                    show(notAllowedValues);
                }
                else {
                    hide(notAllowedValues);

                    // Open a file chooser to import the image (same as the "open" option)
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Open");
                    fileChooser.setInitialDirectory(new File("src/main/resources"));
                    fileChooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("JPG Files", "*.jpg")
                            ,new FileChooser.ExtensionFilter("JPEG Files", "*.jpeg")
                            ,new FileChooser.ExtensionFilter("PNG Files", "*.png")
                            ,new FileChooser.ExtensionFilter("GIF Files", "*.gif")
                    );
                    File selectedFile = fileChooser.showOpenDialog(primStage);
                    String imagePath = selectedFile.getPath();
                    Image image = new Image("file:" + imagePath);

                    // Insert the image in the canvas
                    mainPane.imageProcesser.createImage(image, oX, oY, width, height);
                }
            }
        };
        importImage.setOnAction(imageEvent);


        this.getChildren().addAll(colorCanvas, colorPicker,eyedropper, drawButton, size, changeSize, shapes, noResizable);
        this.getChildren().addAll(shapeNodes);
    }

    // Returns the drawing color
    public static Color getDrawColor() {
        return drawColor;
    }

    // Returns the size of the drawing line
    public static double getDrawSize() {
        return drawSize;
    }

    // Changes the drawing color and the canvas that displays it
    public static void setDrawColor(Color newColor) {
        drawColor = newColor;
        gc.setFill(drawColor);
        gc.fillRect(0, 0, colorCanvas.getWidth(), colorCanvas.getHeight());
    }

}
