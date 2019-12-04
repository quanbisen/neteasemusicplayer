package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Shadowed extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage stage) throws Exception {
        Label clear = new Label("Clear, with no shadow");
        StackPane shadowedPane = new StackPane(clear);
        shadowedPane.setStyle(
                "-fx-background-color: palegreen; " +
                        "-fx-background-insets: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, purple, 10, 0, 0, 0);"
        );
        shadowedPane.setPrefSize(200, 50);
        stage.setScene(new Scene(shadowedPane));
        stage.show();
    }
}
