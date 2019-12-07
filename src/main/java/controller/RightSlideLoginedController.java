package controller;

import application.SpringFXMLLoader;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.User;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import util.StageUtils;
import util.UserUtils;
import util.WindowUtils;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author super lollipop
 * @date 19-12-6
 */
@Controller
public class RightSlideLoginedController {
    /**根容器BorderPane，见right-slide.fxml文件*/
    @FXML
    private BorderPane borderPaneRoot;

    /**显示用户头像的Label组件*/
    @FXML
    private Label labUserImage;

    /**显示用户名称的Label组件*/
    @FXML
    private Label labUserName;

    /**”退出登录“选项的HBox容器*/
    @FXML
    private HBox hBoxLogout;

    /**右边显示”音乐”的可视化滑动容器*/
    private BorderPane visualBorderPane;

    /**注入窗体根容器（BorderPane）的中间容器的控制器*/
    @Resource
    CenterController centerController;

    /**注入Spring上下文工具类*/
    @Resource
    private ConfigurableApplicationContext applicationContext;


    /**注入窗体根容器（BorderPane）的控制类*/
    @Resource
    MainController mainController;

    /**注入标签的控制类*/
    @Resource
    private TabsController tabsController;

    public BorderPane getBorderPaneRoot() {
        return borderPaneRoot;
    }

    public BorderPane getVisualBorderPane() {
        return visualBorderPane;
    }


    public void initialize() throws IOException {
        //宽高绑定
        borderPaneRoot.prefHeightProperty().bind(centerController.getBorderPane().heightProperty());
        borderPaneRoot.prefWidthProperty().bind(centerController.getBorderPane().widthProperty());

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

        //读取出配置文件存储的用户信息。
        if (tabsController.getLOGIN_CONFIG_FILE().exists()){  //如果登录文件存在，解析成用户对象
            User user = UserUtils.parseUser(tabsController.getLOGIN_CONFIG_FILE());
            //设置登录用户的UI组件显示
            Circle circle = new Circle(20,20,20);
            ImageView userImage = new ImageView(new Image(user.getImage()));
            userImage.setFitWidth(40);
            userImage.setFitHeight(40);
            userImage.setClip(circle);   //设置圆形图片
            labUserImage.setGraphic(userImage);
            labUserName.setText(user.getName());
        }
    }

    /**"关于"HBox的鼠标点击事件处理*/
    @FXML
    public void onClickedAbout(MouseEvent mouseEvent) throws IOException {
        if(mouseEvent.getButton() == MouseButton.PRIMARY){
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/right-about.fxml");
            visualBorderPane = (BorderPane) borderPaneRoot.getRight();
            borderPaneRoot.setRight(fxmlLoader.load());
        }
    }

    /**“退出登录”HBox的鼠标点击事件处理*/
    @FXML
    public void onClickedLogout(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){  //鼠标左击
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/logout-confirm-dialog.fxml");
            Stage primaryStage = ((Stage)hBoxLogout.getScene().getWindow());              //获取主窗体的stage对象primaryStage
            Stage dialogStage = StageUtils.getStage((Stage) hBoxLogout.getScene().getWindow(),fxmlLoader.load());
            StageUtils.syncCenter(primaryStage,dialogStage);
            WindowUtils.blockBorderPane(mainController.getBorderPane());
            dialogStage.showAndWait();  //显示对话框
        }
    }
}
