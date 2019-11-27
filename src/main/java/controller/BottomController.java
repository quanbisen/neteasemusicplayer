package controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import javax.annotation.Resource;
import java.io.IOException;


@Controller
public class BottomController {

    @Resource
    private MainController mainController;

    @FXML
    private Label labAlbum;

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
            borderPane.setCenter(label);
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1),label);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            //开始播放渐变动画提示
            fadeTransition.play();
            fadeTransition.setOnFinished(event ->{

            });
        }
        else if (mouseEvent.getButton()==MouseButton.SECONDARY){
//            borderPane.setLeft(this.getLeftPane());
//            borderPane.setCenter(this.getCenterPane());
        }
    }
}
