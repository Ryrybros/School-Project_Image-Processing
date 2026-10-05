package team.examples;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.HashMap;


public class Controller {

    @FXML
    private VBox root;
    @FXML
    private SplitPane splitPane ;
    @FXML
    private MenuBar menuBar;
    private VBox toolScroll;

    private centerPane mainPane;

    // Dictionary that associates the buttons on the ToolBar with the different ScrollBars
    private HashMap<Integer,ScrollBar> scrollBarDictionary = new HashMap<>();
    // Tells which button on the ToolBar is clicked. Its value is -1 if none is clicked
    private int buttonClicked = -1;


    public void initialize(){

        VBox.setVgrow(splitPane, Priority.ALWAYS);

        VBox toolBar = new VBox(20);
        toolBar.setMaxWidth(Launcher.MAXWIDTH/20);
        toolBar.setMinWidth(Launcher.MAXWIDTH/20);
        toolBar.getStyleClass().add("toolbar");

        toolScroll = new VBox();
        toolScroll.setMaxWidth(0);

        // SplitPane and mainPane
        mainPane = new centerPane(100);
        SplitPane.setResizableWithParent(mainPane, true);
        mainPane.initialize();
        splitPane.getItems().addAll(toolBar, toolScroll, mainPane);
        mainPane.relocate(100,0);
        splitPane.setDividerPositions(0.0f, 0.0f, 0.8f);


        // Création of the ScrollBars
        DrawBar drawBar = new DrawBar(mainPane);
        ResizeBar resizeBar = new ResizeBar(mainPane);
        RotateBar rotateBar = new RotateBar(mainPane);
        FilterBar filterBar = new FilterBar(mainPane);

        // Construction of the toolBar
        Button drawButton = new Button("Draw");
        Button resizeButton = new Button("Resize");
        Button rotateButton = new Button("Rotate/Flip");
        Button filterButton = new Button("Filter");

        // Ids of the buttons to know which one is clicked
        int idDrawButton = 1;
        int idResizeButton = 2;
        int idRotateButton = 3;
        int idFilterButton = 4;
        scrollBarDictionary.put(idDrawButton, drawBar);
        scrollBarDictionary.put(idResizeButton, resizeBar);
        scrollBarDictionary.put(idRotateButton, rotateBar);
        scrollBarDictionary.put(idFilterButton, filterBar);

        // EventHandlers to show the associated ScrollBar
        EventHandler<ActionEvent> drawButtonEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (buttonClicked==idDrawButton) {
                    hideScrollBar();
                }
                else {
                    showScrollBar(idDrawButton);
                }
            }
        };
        drawButton.setOnAction(drawButtonEvent);

        EventHandler<ActionEvent> resizeButtonEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (buttonClicked== idResizeButton) {
                    hideScrollBar();
                }
                else {
                    showScrollBar(idResizeButton);
                }
            }
        };
        resizeButton.setOnAction(resizeButtonEvent);

        EventHandler<ActionEvent> rotateButtonEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (buttonClicked==idRotateButton) {
                    hideScrollBar();
                }
                else {
                    showScrollBar(idRotateButton);
                }
            }
        };
        rotateButton.setOnAction(rotateButtonEvent);

        EventHandler<ActionEvent> filterButtonEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (buttonClicked==idFilterButton) {
                    hideScrollBar();
                }
                else {
                    showScrollBar(idFilterButton);
                }
            }
        };
        filterButton.setOnAction(filterButtonEvent);

        Button backButton = new Button("UNDO");

        EventHandler<ActionEvent> backButtonEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                mainPane.imageProcesser.removeLayer();
            }
        };
        backButton.setOnAction(backButtonEvent);



        toolBar.getChildren().addAll(drawButton, resizeButton, rotateButton, filterButton,backButton);


        // Creation of the MenuBar
        Menu filesMenu = new FilesMenu(mainPane);
        menuBar.getMenus().add(filesMenu);

    }

    // Shows the ScrollBar associated with the clicked button (and hide the others)
    // The ScrollBars (and thus the options) are shown only once an image is in the mainPane
    public void showScrollBar(int idButton) {
        if(mainPane.imageProcesser.getImageOn()) {
            toolScroll.getChildren().clear();   // Pour supprimer les boutons déjà présents

            ScrollBar scrollBar = scrollBarDictionary.get(idButton);

            splitPane.setDividerPositions(0.0f, 0.25f, 0.8f);
            toolScroll.setMaxWidth(Launcher.MAXWIDTH / 4);

            toolScroll.getChildren().add(scrollBar);
            buttonClicked = idButton;
        }
    }

    // Hide the ScrollBar when a button of the ToolBar is clicked a second time
    public void hideScrollBar() {
        toolScroll.getChildren().clear();
        splitPane.setDividerPositions(0.0f, 0.0f, 0.8f);
        toolScroll.setMaxWidth(0);
        buttonClicked = -1;
    }

    public centerPane getCenterPane(){
        return mainPane;
    }


}

