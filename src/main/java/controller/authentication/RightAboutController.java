package controller.authentication;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class RightAboutController {

    /**注入右边未登录"滑动弹出"的页面的控制器*/
    @Resource
    private RightSlideUnLoginController rightSlideUnLoginController;

    /**注入右边已登录"滑动弹出"的页面的控制器*/
    @Resource RightSlideLoginedController rightSlideLoginedController;

    /**"返回"图标的单击事件处理*/
    @FXML
    public void onClickedBack(MouseEvent mouseEvent) {
        try {
            rightSlideUnLoginController.getBorderPaneRoot().setRight(rightSlideUnLoginController.getVisualBorderPane());
        }catch (Exception e){
            rightSlideLoginedController.getBorderPaneRoot().setRight(rightSlideLoginedController.getVisualBorderPane());
        }
    }
}
