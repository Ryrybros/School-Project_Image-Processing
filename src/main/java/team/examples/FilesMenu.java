package team.examples;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

// Menu in the MenuBar for files gestion
public class FilesMenu extends Menu {

    private MenuItem open;
    private MenuItem save;

    public FilesMenu(centerPane mainPane) {
        super("Files");
        open = new OpenItem(mainPane);
        save = new SaveItem(mainPane);

        this.getItems().addAll(open, save);
    }


}
