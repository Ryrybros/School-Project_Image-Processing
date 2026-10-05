module teamclauderyadjolan {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires java.rmi;
    requires javafx.swing;
    requires javafx.base;
    requires org.junit.jupiter.api;

    opens team.examples to javafx.fxml;
    exports team.examples;
    exports imageProcessTools;
    opens imageProcessTools to javafx.fxml;
}
