package controller;

import application.SpringFXMLLoader;
import com.alibaba.fastjson.JSON;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.User;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
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


    private Stage loginOrRegisterStage;

    /**播放器登录文件的存放路径*/
    private String Login_CONFIG_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "config" + File.separator + "login-config.properties";

    /**播放器登录配置文件*/
    private File LOGIN_CONFIG_FILE;


    public BorderPane getBorderPaneRoot() {
        return borderPaneRoot;
    }

    public BorderPane getVisualBorderPane() {
        return visualBorderPane;
    }

    public Stage getLoginOrRegisterStage() {
        return loginOrRegisterStage;
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
        LOGIN_CONFIG_FILE = new File(Login_CONFIG_PATH);
        FileInputStream fileInputStream = new FileInputStream(LOGIN_CONFIG_FILE);
        int n = 0;
        StringBuffer stringBuffer = new StringBuffer();
        while (n!=-1){
            n=fileInputStream.read();//读取文件的一个字节(8个二进制位),并将其由二进制转成十进制的整数返回
            char by=(char) n; //转成字符
            stringBuffer.append(by);
        }
        String str = stringBuffer.substring(0,stringBuffer.length()-1);
        User user = JSON.parseObject(str,User.class);
        //设置登录用户的UI组件显示
        Circle circle = new Circle(20,20,20);
        ImageView userImage = new ImageView(new Image(user.getImage()));
        userImage.setFitWidth(40);
        userImage.setFitHeight(40);
        userImage.setClip(circle);   //设置圆形图片
        labUserImage.setGraphic(userImage);
        labUserName.setText(user.getName());
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
    public void onClickedLogout(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            //播放移除动画
            TranslateTransition translateTransitionOut = new TranslateTransition(Duration.seconds(0.5),borderPaneRoot);
            borderPaneRoot.setTranslateX(0);
            translateTransitionOut.setToX(310);
            translateTransitionOut.play();
            translateTransitionOut.setOnFinished(event2 -> {
                centerController.getStackPane().getChildren().remove(1,centerController.getStackPane().getChildren().size());
            });
            System.out.println("logout");

            //清除登录存储文件
            if (LOGIN_CONFIG_FILE.exists()){  //如果文件存在，删除它
                LOGIN_CONFIG_FILE.delete();
            }
        }
    }
}
