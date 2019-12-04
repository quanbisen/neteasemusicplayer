package controller;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class RightAboutController {

    /**注入右边"滑动弹出"的页面的控制器*/
    @Resource
    private RightSlideController rightSlideController;

    /**"返回"图标的单击事件处理*/
    @FXML
    public void onClickedBack(MouseEvent mouseEvent) {
        rightSlideController.getBorderPaneRoot().setRight(rightSlideController.getVisualBorderPane());
    }
}
