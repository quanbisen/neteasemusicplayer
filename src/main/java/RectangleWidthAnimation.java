import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Time;

public class RectangleWidthAnimation extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Rectangle statusBar = new Rectangle(0, 0);
        Button animationButton = new Button("Animate width decrease by 25");
        animationButton.setOnAction(event -> {
            System.out.println("Animation start: width = " + statusBar.getWidth());
            KeyValue widthValue = new KeyValue(statusBar.heightProperty(), statusBar.getHeight() + 100);
            KeyFrame frame = new KeyFrame(Duration.seconds(2), widthValue);
            Timeline timeline = new Timeline(frame);
            KeyValue heightValue = new KeyValue(statusBar.widthProperty(),statusBar.getWidth() + 300);
            KeyFrame frame1 = new KeyFrame(Duration.seconds(2),heightValue);
            Timeline timeline1 = new Timeline(frame1);
            timeline.play();
            timeline1.play();
            timeline.setOnFinished(finishedEvent -> System.out.println("Animation end: width = " + statusBar.getWidth()));
        });

        VBox container = new VBox(10, statusBar, animationButton);

        //Experiment with these alignments
//        container.setAlignment(Pos.CENTER);
        container.setAlignment(Pos.BOTTOM_LEFT);
//        container.setAlignment(Pos.CENTER_RIGHT);

        primaryStage.setScene(new Scene(container, 350, 150));
        primaryStage.show();
    }
}