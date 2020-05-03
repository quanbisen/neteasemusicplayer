package controller.user;

import application.SpringFXMLLoader;
import controller.main.CenterController;
import controller.main.LeftController;
import controller.main.MainController;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
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
import mediaplayer.UserStatus;
import org.springframework.context.ApplicationContext;
import pojo.User;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import util.ImageUtils;
import util.StageUtils;
import util.WindowUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * @author super lollipop
 * @date 19-12-6
 */
@Controller
public class RightSlideLoginController {

    /**根容器BorderPane，见right-slide.fxml文件*/
    @FXML
    private BorderPane borderPaneRoot;

    /**显示用户头像的ImageView组件*/
    @FXML
    private ImageView ivUserImage;

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
    private CenterController centerController;

    /**注入Spring上下文工具类*/
    @Resource
    private ApplicationContext applicationContext;

    /**注入窗体根容器（BorderPane）的控制类*/
    @Resource
    private MainController mainController;

    /**注入左侧的控制器*/
    @Resource
    private LeftController leftController;

    @Resource
    private UserStatus userStatus;

    public BorderPane getBorderPaneRoot() {
        return borderPaneRoot;
    }

    public BorderPane getVisualBorderPane() {
        return visualBorderPane;
    }

    public void initialize() {
        //宽高绑定
        borderPaneRoot.prefHeightProperty().bind(centerController.getBorderPane().heightProperty());
        borderPaneRoot.prefWidthProperty().bind(centerController.getBorderPane().widthProperty());

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.2),borderPaneRoot);
        borderPaneRoot.setTranslateX(310);
        translateTransition.setToX(0);
        translateTransition.play();
        translateTransition.setOnFinished(event -> {
            borderPaneRoot.getCenter().setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    TranslateTransition translateTransitionOut = new TranslateTransition(Duration.seconds(0.2),borderPaneRoot);
                    borderPaneRoot.setTranslateX(0);
                    translateTransitionOut.setToX(310);
                    translateTransitionOut.play();
                    translateTransitionOut.setOnFinished(event2 -> {
                        centerController.getStackPane().getChildren().remove(1,centerController.getStackPane().getChildren().size());
                    });
                    borderPaneRoot.getCenter().removeEventHandler(MouseEvent.MOUSE_CLICKED,this);
                }
            });
        });

        //设置登录用户的UI组件显示
        Circle circle = new Circle(20,20,20);
        User user = userStatus.getUser();
        if (user.getLocalImagePath() != null && new File(user.getLocalImagePath().substring(5)).exists()){  //如果本地存储的头像文件存在
            ivUserImage.setImage(new Image(user.getLocalImagePath(),40,40,true,true));
        }else {
            ivUserImage.setImage(new Image("image/UserDefaultImage.png",40,40,true,true));
        }
        new Service<Void>() {   //创建加载在线图片的后台服务
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        if (user.getImageURL() != null){
                            Image image = new Image(user.getImageURL(),40,40,true,true);
                            if (!image.isError()){
                                ivUserImage.setImage(image);
                            }else {
                                ivUserImage.setImage(new Image("image/UserDefaultImage.png",40,40,true,true));
                            }
                        }
                        return null;
                    }
                };
            }
        }.start();
        ivUserImage.setClip(circle);   //设置圆形图片
        labUserName.setText(user.getName());

    }

    /**"关于"HBox的鼠标点击事件处理*/
    @FXML
    public void onClickedAbout(MouseEvent mouseEvent) throws IOException {
        if(mouseEvent.getButton() == MouseButton.PRIMARY){
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/user/right-about.fxml");
            visualBorderPane = (BorderPane) borderPaneRoot.getRight();
            borderPaneRoot.setRight(fxmlLoader.load());
        }
    }

    /**“退出登录”HBox的鼠标点击事件处理*/
    @FXML
    public void onClickedLogout(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){  //鼠标左击
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/dialog/logout-confirm-dialog.fxml");
            Stage primaryStage = ((Stage)hBoxLogout.getScene().getWindow());              //获取主窗体的stage对象primaryStage
            Stage dialogStage = StageUtils.getStage((Stage) hBoxLogout.getScene().getWindow(),fxmlLoader.load());
            StageUtils.synchronizeCenter(primaryStage,dialogStage);
            WindowUtils.blockBorderPane(mainController.getBorderPane());
            dialogStage.showAndWait();  //显示对话框
        }
    }

    /**“编辑”按钮的事件处理*/
    @FXML
    public void onClickedBtnEdit(ActionEvent actionEvent) throws IOException {
        Event.fireEvent(borderPaneRoot.getCenter(),new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                true, true, true, true, true, true, null)); //fire单击中间容器的事件，关闭右滑出来的页面
        FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/content/edit-user-content.fxml");
        leftController.resetSelectedTab();
        centerController.getBorderPane().setCenter(fxmlLoader.load());
    }
}
