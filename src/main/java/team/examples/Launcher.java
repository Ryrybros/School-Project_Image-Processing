package team.examples;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
    public final static int MAXWIDTH = 1500;
    private final static int startWidth  = 1000;
    public static Stage primStage ;
    public static float screenRatio ;


    @Override
    public void start(Stage primaryStage) throws Exception{
        primStage = primaryStage;
        screenRatio = 0.75f ;
        Parent root = FXMLLoader.load(getClass().getResource("View.fxml"));
        primaryStage.setTitle("Image Processing");
        int height = (int) Math.floor(screenRatio*startWidth);
        Scene scene = new Scene(root, startWidth, height );
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

