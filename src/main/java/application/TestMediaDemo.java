package application;

import javafx.application.Application;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class TestMediaDemo extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        String url = "http://www.gov.cn/guoqing/guoge/hc.mp3";
        Media media = new Media(url);
        MediaPlayer mplayer = new MediaPlayer(media);
        MediaView mView = new MediaView(mplayer);
        System.out.println(media.getSource());

        Pane pane = new Pane();
        pane.getChildren().add(mView);
        mView.fitHeightProperty().bind(pane.heightProperty());
        mView.fitWidthProperty().bind(pane.widthProperty());

        Scene scene = new Scene(pane,640,360);
        primaryStage.setTitle("MediaDemo");
        primaryStage.setScene(scene);
        primaryStage.show();

        mplayer.play();
    }
}
