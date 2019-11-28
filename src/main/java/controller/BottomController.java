package controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Time;


@Controller
public class BottomController {

    @Resource
    private MainController mainController;

    @FXML
    private Label labAlbum;

    @FXML
    private ProgressBar progressBarSong;

    public void initialize(){

    }

    /**专辑图片单击事件处理*/
    @FXML
    public void onClickedAlbum(MouseEvent mouseEvent) throws IOException,Throwable {
        //获取main布局的根组件
        BorderPane borderPane = mainController.getBorderPane();
        Label label = new Label("Just a test.");
        label.setPrefWidth(128);
        label.setPrefHeight(128);
        label.setBackground(new Background(new BackgroundFill(Color.rgb(221,221,221),null,null)));
        if (mouseEvent.getButton()== MouseButton.PRIMARY){

            borderPane.setLeft(null);
            BorderPane borderPane1 = new BorderPane();
            HBox hBox = new HBox();
            Rectangle statusBar = new Rectangle(0, 0);
            hBox.getChildren().add(statusBar);
            hBox.setAlignment(Pos.BOTTOM_LEFT);
//            borderPane1.setPrefHeight(100);
//            borderPane1.setPrefWidth(100);
            borderPane1.setBackground(new Background(new BackgroundFill(Color.RED,null,null)));
            Region pane = (Region) borderPane.getCenter();

            System.out.println(pane.getWidth());
            System.out.println(pane.getHeight());
            borderPane.setCenter(hBox);

            /**/

            KeyValue widthValue = new KeyValue(statusBar.heightProperty(), statusBar.getHeight() + 100);
            KeyFrame frame = new KeyFrame(Duration.seconds(2), widthValue);
            Timeline timeline = new Timeline(frame);
            KeyValue heightValue = new KeyValue(statusBar.widthProperty(),statusBar.getWidth() + 300);
            KeyFrame frame1 = new KeyFrame(Duration.seconds(2),heightValue);
            Timeline timeline1 = new Timeline(frame1);
            timeline.play();
            timeline1.play();
//            timeline.play();


            /*Fade Animation*/
//            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1),label);
//            fadeTransition.setFromValue(0);
//            fadeTransition.setToValue(1);
//            //开始播放渐变动画提示
//            fadeTransition.play();
//            fadeTransition.setOnFinished(event ->{
//
//            });

            /*Slide Animation*/
            TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),borderPane1);
//            translateTransition.setFromX(-pane.getWidth());
//            translateTransition.setToX(0);
//            borderPane1.setTranslateY(50);
//            translateTransition.setFromY(-pane.getHeight());
//            translateTransition.setToY(0);
//            translateTransition.play();

//            Duration cycleDuration = Duration.millis(500);
//            Timeline timeline = new Timeline(
//                    new KeyFrame(cycleDuration,
//                            new KeyValue(borderPane1.maxWidthProperty(),pane.getWidth()))
//                    ,
//                    new KeyFrame(cycleDuration,
//                            new KeyValue(borderPane1.prefHeightProperty(),pane.getHeight()))
//            );

//            timeline.play();

            //No effect
//            Timeline timeline = new Timeline();
//            timeline.getKeyFrames().addAll(
//                    new KeyFrame(Duration.ZERO,new KeyValue(borderPane1.prefHeightProperty(),0)),
//                    new KeyFrame(Duration.seconds(2),new KeyValue(borderPane1.prefHeightProperty(),50))
//            );
//            timeline.play();


//            Timeline timeline = new Timeline();
//            System.out.println(borderPane.getWidth());
//            System.out.println(borderPane.getHeight());
//            borderPane1.minWidthProperty().set(0);
//            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5),new KeyValue(borderPane1.minWidthProperty(),borderPane.getWidth(), Interpolator.EASE_OUT)));
//            timeline.play();


//            TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),borderPane1);
//            translateTransition.setToX(borderPane.getWidth());
//            translateTransition.play();
//            Timeline timeline1 = new Timeline();
//            borderPane1.translateYProperty().set(borderPane1.getHeight());
//            timeline1.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5),new KeyValue(borderPane1.translateYProperty(),0, Interpolator.EASE_IN)));
//            timeline1.play();
        }
        else if (mouseEvent.getButton()==MouseButton.SECONDARY){
//            borderPane.setLeft(this.getLeftPane());
//            borderPane.setCenter(this.getCenterPane());
        }
    }

    /**播放上一首单击事件处理*/
    @FXML
    public void onClickedPlayLast(MouseEvent mouseEvent) {
    }

    /**播放/暂停单击事件处理*/
    @FXML
    public void onClickedPlay(MouseEvent mouseEvent) {
        System.out.println("test");
        progressBarSong.setProgress(0.5);
    }
}
