package team.examples;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static team.examples.Launcher.primStage;

// MenuItem in the Files menu that saves the image and its modifications
public class SaveItem extends MenuItem {

    private centerPane mainPane;

    public SaveItem(centerPane mainPane) {
        super("Save as...");

        this.mainPane = mainPane;

        EventHandler<ActionEvent> saveEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                saveFile();
            }
        };
        this.setOnAction(saveEvent);

    }

    // Saves the image and its modification
    private void saveFile() {
        // FileChooser to choose the path and the name of the file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as...");
        fileChooser.setInitialDirectory(new File("src/main/resources"));
        fileChooser.getExtensionFilters().addAll(
                // Allows several formats of image
                new FileChooser.ExtensionFilter("PNG Files", "*.png")
                ,new FileChooser.ExtensionFilter("JPEG Files", "*.jpeg")
                ,new FileChooser.ExtensionFilter("JPG Files", "*.jpg")
                ,new FileChooser.ExtensionFilter("GIF Files", "*.gif")  // Saves only an image (not an animation)
        );
        File savedFile = fileChooser.showSaveDialog(primStage);

        // Save the image in the selected file
        Image image = mainPane.imageProcesser.getStackImage();
        String format = getFileExtension(savedFile);
        try {
            if(format.equals("jpg") | format.equals("jpeg")) {
                // Issue with SwingFXUtils for formats that doesn't support transparency
                // In this situation, we need to use a bufferedImage that doesn't have the alpha channel
                BufferedImage bufferedImage = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_RGB);
                ImageIO.write(SwingFXUtils.fromFXImage(image, bufferedImage), format, savedFile);
            }
            else {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), format, savedFile);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    // Gives the extension name of a file from its complete name
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf + 1);
    }

}
