package controller.dialog;

import controller.main.CenterController;
import controller.main.LeftController;
import controller.main.MainController;
import controller.authentication.RightSlideLoginController;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import mediaplayer.Config;
import mediaplayer.UserStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import service.ValidateUserService;
import util.ImageUtils;
import util.WindowUtils;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;

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
    private RightSlideLoginController rightSlideLoginController;

    /**
     * 注入窗体根容器（BorderPane）的中间容器的控制器
     */
    @Resource
    private CenterController centerController;

    /**
     * 注入标签的控制类
     */
    @Resource
    private LeftController leftController;

    /**
     * 注入窗体根容器（BorderPane）的控制类
     */
    @Resource
    private MainController mainController;

    @Resource
    private ApplicationContext applicationContext;

    /**
     * "确定"退出登录按钮的事件处理
     */
    @FXML
    public void onClickedConfirm(ActionEvent actionEvent) throws IOException {
        if (applicationContext.getBean(UserStatus.class).getUser() != null) {   //判断用户为登录状态

            applicationContext.getBean(UserStatus.class).setUser(null); //清空登录用户对象
            File userStatusFile = applicationContext.getBean(Config.class).getUserStatusFile(); //获取登录文件
            if (userStatusFile.exists()){
                userStatusFile.delete();   //删除播放器的登录文件
            }

            applicationContext.getBean(ValidateUserService.class).cancel();   //停止定时任务

            onClickedCancel(actionEvent);   //关闭dialog
            //播放移除动画
            TranslateTransition translateTransitionOut = new TranslateTransition(Duration.seconds(0.5), rightSlideLoginController.getBorderPaneRoot());
            rightSlideLoginController.getBorderPaneRoot().setTranslateX(0);
            translateTransitionOut.setToX(310);
            translateTransitionOut.play();
            translateTransitionOut.setOnFinished(event2 -> {
                centerController.getStackPane().getChildren().remove(1, centerController.getStackPane().getChildren().size());  //移除添加的容器
                //播放“退出登录”动画提示
                WindowUtils.toastInfo(centerController.getStackPane(),new Label("退出登录"));
            });
            System.out.println("logout");


            //更新显示用户头像和名称的GUI组件显示为未登录的状态
            leftController.getLabUserImage().setGraphic(ImageUtils.createImageView("/image/UnLoginImage.png",38,38));
            leftController.getLabUserName().setText("未登录");

            //删除GUI创建的歌单列表......
            leftController.getVBoxTabContainer().getChildren().remove(5,leftController.getVBoxTabContainer().getChildren().size());
            List<HBox> tabList = leftController.getTabList();
            for (int i = 4; i < tabList.size();) {
                tabList.remove(i);
            }
        }

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
