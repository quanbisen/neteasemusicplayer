package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Controller;
import util.WindowUtils;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 19-12-4
 */
@Controller
public class NavigateLoginOrRegisterController {


    /**“关闭”图标的label组件*/
    @FXML
    private Label labCloseIcon;

    /**注入window工具类*/
    @Resource
    private WindowUtils windowUtils;

    /**注入窗体根容器（BorderPane）的控制类*/
    @Resource
    MainController mainController;

    /**单击“关闭按钮时的事件处理”*/
    @FXML
    public void onClickedCloseIcon(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){  //鼠标左击
            windowUtils.releaseBorderPane(mainController.getBorderPane());  //调用释放borderPane的函数，改变不透明度为正常的。
            labCloseIcon.getScene().getWindow().hide();  //把当前窗口隐藏
        }
    }

    /**单击”立即登录“按钮的事件处理*/
    @FXML
    public void onClickedLoginButton(ActionEvent actionEvent) {

    }
}
