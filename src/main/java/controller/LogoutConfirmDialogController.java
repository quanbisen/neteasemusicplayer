package controller;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import model.User;
import org.springframework.stereotype.Controller;
import util.UserUtils;
import util.WindowUtils;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * @author super lollipop
 * @date 19-12-7
 */
@Controller
public class LogoutConfirmDialogController {

    /**
     * “取消”按钮组件
     */
    @FXML
    private Button btnCancel;

    /**
     * 注入上一个页面的控制器
     */
    @Resource
    private RightSlideLoginedController rightSlideLoginedController;

    /**
     * 注入窗体根容器（BorderPane）的中间容器的控制器
     */
    @Resource
    CenterController centerController;

    /**
     * 注入标签的控制类
     */
    @Resource
    private TabsController tabsController;

    /**
     * 注入窗体根容器（BorderPane）的控制类
     */
    @Resource
    MainController mainController;

    /**
     * "确定"退出登录按钮的事件处理
     */
    @FXML
    public void onClickedConfirm(ActionEvent actionEvent) throws IOException {
        onClickedCancel(actionEvent);
        //播放移除动画
        TranslateTransition translateTransitionOut = new TranslateTransition(Duration.seconds(0.5), rightSlideLoginedController.getBorderPaneRoot());
        rightSlideLoginedController.getBorderPaneRoot().setTranslateX(0);
        translateTransitionOut.setToX(310);
        translateTransitionOut.play();
        translateTransitionOut.setOnFinished(event2 -> {
            centerController.getStackPane().getChildren().remove(1, centerController.getStackPane().getChildren().size());  //移除添加的容器
            //播放“退出登录”动画提示
            WindowUtils.toastInfo(centerController.getStackPane(),new Label("退出登录"));
        });
        System.out.println("logout");

        //清除登录存储文件
        if (tabsController.getLOGIN_CONFIG_FILE().exists()) {  //如果文件存在，删除它
            User user = UserUtils.parseUser(tabsController.getLOGIN_CONFIG_FILE());  //解析用户对象
            String USER_CONFIG_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "config" + File.separator + user.getId();     //获取存储用户图片的目录
            File USER_CONFIG_FILE = new File(USER_CONFIG_PATH);   //创建文件句柄
            if (USER_CONFIG_FILE.exists()&&USER_CONFIG_FILE.isDirectory()){
                File[] files = USER_CONFIG_FILE.listFiles();
                for (File file : files){
                    file.delete();            //遍历删除文件
                }
                USER_CONFIG_FILE.delete();    //删除目录
            }
            tabsController.getLOGIN_CONFIG_FILE().delete();  //删除播放器的登录配置文件
        }

        //更新显示用户头像和名称的GUI组件显示为未登录的状态
        ImageView imageView = new ImageView(new Image("/image/UnLoginImage.png"));
        imageView.setFitHeight(38);
        imageView.setFitWidth(38);
        tabsController.getLabUserImage().setGraphic(imageView);
        tabsController.getLabUserName().setText("未登录");


        //删除GUI创建的歌单列表......
    }

    /**
     * “取消”按钮的事件处理
     */
    @FXML
    public void onClickedCancel(ActionEvent actionEvent) {
        btnCancel.getScene().getWindow().hide();  //隐藏窗口
        WindowUtils.releaseBorderPane(mainController.getBorderPane());
    }

}
