package controller;

import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.springframework.stereotype.Controller;
import javax.annotation.Resource;
import java.io.IOException;

@Controller
public class BottomController {

    @Resource
    private MainController mainController;

    @FXML
    private Label labAlbum;

    @FXML
    private ProgressBar progressBarSong;

    @FXML
    private Slider sliderSong;

    @FXML
    private Label labSoundIcon;

    @FXML
    private ProgressBar progressBarVolume;

    @FXML
    private Slider sliderVolume;

    @FXML
    private Label labPlayModeIcon;
    public void initialize(){
        progressBarSong.prefWidthProperty().bind(((StackPane)progressBarSong.getParent()).widthProperty());  //宽度绑定
        sliderSong.prefWidthProperty().bind(((StackPane)sliderSong.getParent()).widthProperty());            //宽度绑定
        //设置音量滚动条的监听事件，使进度条始终跟随滚动条更新
        sliderVolume.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                progressBarVolume.setProgress(newValue.doubleValue());
            }
        });
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
            VBox borderPane1 = new VBox();
            HBox hBox = new HBox();
            Rectangle statusBar = new Rectangle(0, 0);
            hBox.getChildren().add(borderPane1);
            hBox.setAlignment(Pos.BOTTOM_LEFT);
            borderPane1.setBackground(new Background(new BackgroundFill(Color.RED,null,null)));

            borderPane1.setMaxWidth(0);
            borderPane1.setMaxHeight(0);
            Region region = (Region) borderPane.getCenter();

            borderPane.setCenter(hBox);

            /**/

            Timeline timeline = new Timeline();
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.seconds(1),new KeyValue(borderPane1.minHeightProperty(),region.getHeight())),
                    new KeyFrame(Duration.seconds(1),new KeyValue(borderPane1.minWidthProperty(),region.getWidth()))
            );
            timeline.play();


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
