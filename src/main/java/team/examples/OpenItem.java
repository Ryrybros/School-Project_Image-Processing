package team.examples;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

import java.io.File;

import static team.examples.Launcher.primStage;

// MenuItem in the Files menu that opens a saved image
public class OpenItem extends MenuItem {

    private centerPane mainPane;

    public OpenItem(centerPane mainPane) {
        super("Open");
        this.mainPane = mainPane;

        EventHandler<ActionEvent> openEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                openFile();
            }
        };
        this.setOnAction(openEvent);
    }

    // Opens an image from a file
    private void openFile () {
        // FileChooser to open a file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");
        fileChooser.setInitialDirectory(new File("src/main/resources"));
        fileChooser.getExtensionFilters().addAll(
                // Allows several formats of image
                new FileChooser.ExtensionFilter("JPG Files", "*.jpg")
                ,new FileChooser.ExtensionFilter("JPEG Files", "*.jpeg")
                ,new FileChooser.ExtensionFilter("PNG Files", "*.png")
                ,new FileChooser.ExtensionFilter("GIF Files", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(primStage);

        String imagePath = selectedFile.getPath();

        // Creates the image from the selected file and adds it to the mainPane
        Image image = new Image("file:" + imagePath);
        try {
            mainPane.imageProcesser.createImage(image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
