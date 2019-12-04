package controller;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.springframework.stereotype.Controller;
import util.WindowUtils;

import javax.annotation.Resource;

@Controller
public class RightSlideController {

    /**根容器BorderPane，见right-slide.fxml文件*/
    @FXML
    private BorderPane borderPaneRoot;

    /**注入窗体根容器（BorderPane）的中间容器的控制器*/
    @Resource
    CenterController centerController;

//    /**注入window工具类*/
//    @Resource
//    private WindowUtils windowUtils;

    public void initialize(){
        //宽高绑定
        borderPaneRoot.prefWidthProperty().bind(centerController.getBorderPane().widthProperty());
        borderPaneRoot.prefHeightProperty().bind(centerController.getBorderPane().heightProperty());

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),borderPaneRoot);
        borderPaneRoot.setTranslateX(310);
        translateTransition.setToX(0);
        translateTransition.play();
        translateTransition.setOnFinished(event -> {
            ((Pane)borderPaneRoot.getCenter()).setOnMouseClicked(event1 -> {
                TranslateTransition translateTransitionOut = new TranslateTransition(Duration.seconds(0.5),borderPaneRoot);
                borderPaneRoot.setTranslateX(0);
                translateTransitionOut.setToX(310);
                translateTransitionOut.play();
                translateTransitionOut.setOnFinished(event2 -> {
                    centerController.getStackPane().getChildren().remove(1,centerController.getStackPane().getChildren().size());
                });
            });
        });
    }

    @FXML
    public void onClickedLoginButton(ActionEvent actionEvent) {
        System.out.println("test");
    }
}
