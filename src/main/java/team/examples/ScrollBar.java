package team.examples;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import java.util.function.UnaryOperator;
import javafx.util.converter.IntegerStringConverter;

// Abstract class to represent the ScrollBars containing the interface elements that allow the user to modify the image
abstract public class ScrollBar extends VBox {

    centerPane mainPane;

    public ScrollBar(centerPane mainPane) {
        super.setSpacing(10);
        this.mainPane = mainPane;
        this.getStyleClass().add("scrollbar");
    }

    // When a button is clicked, show or hide the corresponding options
    protected void showHide(Node node){
        node.setVisible(!node.isVisible());
        node.setManaged(!node.isManaged());
    }

    // Show the element and adapt the layout to manage it
    protected void show(Node node) {
        node.setVisible(true);
        node.setManaged(true);
    }

    // Hide the element and adapt the layout to ignore it
    protected void hide(Node node) {
        node.setVisible(false);
        node.setManaged(false);
    }

    // TextField that accepts only positive integers
    protected TextField getNumberField() {
        TextField numberField = new TextField();
        // Filter to accept only numbers
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("([0-9]*)")) {
                return change;
            }
            return null;
        };

        TextFormatter<Integer> textFormatter = new TextFormatter<>(new IntegerStringConverter(), 100, integerFilter);
        numberField.setTextFormatter(textFormatter);
        numberField.getStyleClass().add("text-field");
        return numberField;
    }
}
