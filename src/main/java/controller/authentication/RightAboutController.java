package controller.authentication;

import controller.main.LeftController;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import mediaplayer.Config;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import javax.annotation.Resource;

@Controller
public class RightAboutController {

    /**注入右边未登录"滑动弹出"的页面的控制器*/
    @Resource
    private RightSlideUnLoginController rightSlideUnLoginController;

    /**注入右边已登录"滑动弹出"的页面的控制器*/
    @Resource
    private RightSlideLoginedController rightSlideLoginedController;

    @Resource
    private ApplicationContext applicationContext;

    /**"返回"图标的单击事件处理*/
    @FXML
    public void onClickedBack(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            if (applicationContext.getBean(Config.class).getUser() != null){
                rightSlideLoginedController.getBorderPaneRoot().setRight(rightSlideLoginedController.getVisualBorderPane());
            }else {
                rightSlideUnLoginController.getBorderPaneRoot().setRight(rightSlideUnLoginController.getVisualBorderPane());
            }
        }
    }
}
