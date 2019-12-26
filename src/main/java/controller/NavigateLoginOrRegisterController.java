package controller;

import application.SpringFXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import util.WindowUtils;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author super lollipop
 * @date 19-12-4
 */
@Controller
public class NavigateLoginOrRegisterController {


    /**“关闭”图标的label组件*/
    @FXML
    private Label labCloseIcon;

    @FXML
    private Button btnLoginByPhoneNumber;

    /**注入窗体根容器（BorderPane）的控制类*/
    @Resource
    MainController mainController;

    /**注入Spring上下文工具类*/
    @Resource
    private ConfigurableApplicationContext applicationContext;

    /**"导航登录、注册"的容器*/
    private Parent navigateLoginOrRegister;

    public Parent getNavigateLoginOrRegister() {
        return navigateLoginOrRegister;
    }

    /**单击“关闭按钮时的事件处理”*/
    @FXML
    public void onClickedCloseIcon(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){  //鼠标左击
            WindowUtils.releaseBorderPane(mainController.getBorderPane());  //调用释放borderPane的函数，改变不透明度为正常的。
            labCloseIcon.getScene().getWindow().hide();  //把当前窗口隐藏
        }
    }

    /**单击”立即登录“按钮的事件处理*/
    @FXML
    public void onClickedLoginButton(ActionEvent actionEvent) throws IOException {
        //保存当前的导航容器
        navigateLoginOrRegister = btnLoginByPhoneNumber.getScene().getRoot();
        Scene loginScene = btnLoginByPhoneNumber.getScene();
        FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/login.fxml");
        loginScene.setRoot(fxmlLoader.load());
    }

    /**单击”注册“按钮的事件处理*/
    @FXML
    public void onClickedRegisterButton(ActionEvent actionEvent) throws IOException {
        //保存当前的导航容器
        navigateLoginOrRegister = btnLoginByPhoneNumber.getScene().getRoot();
        Scene loginScene = btnLoginByPhoneNumber.getScene();
        FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/register.fxml");
        loginScene.setRoot(fxmlLoader.load());
    }
}
