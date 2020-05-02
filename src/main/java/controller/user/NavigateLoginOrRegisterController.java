package controller.user;

import application.SpringFXMLLoader;
import controller.main.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
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


    /**根容器*/
    @FXML
    private BorderPane shadowPane;

    /**“关闭”图标的label组件*/
    @FXML
    private Label labCloseIcon;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnRegister;

    /**注入窗体根容器（BorderPane）的控制类*/
    @Resource
    private MainController mainController;

    /**注入Spring上下文工具类*/
    @Resource
    private ApplicationContext applicationContext;

    public BorderPane getShadowPane() {
        return shadowPane;
    }

    /**单击“关闭按钮时的事件处理”*/
    @FXML
    public void onClickedCloseIcon(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){  //鼠标左击
            WindowUtils.releaseBorderPane(mainController.getBorderPane());  //调用释放borderPane的函数，改变不透明度为正常的。
            ((Stage)labCloseIcon.getScene().getWindow()).close();  //把当前窗口关闭
        }
    }

    /**单击”登录“按钮的事件处理*/
    @FXML
    public void onClickedLoginButton(ActionEvent actionEvent) throws IOException {

        Scene loginScene = btnLogin.getScene();
        FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/user/login.fxml");
        loginScene.setRoot(fxmlLoader.load());
    }

    /**单击”注册“按钮的事件处理*/
    @FXML
    public void onClickedRegisterButton(ActionEvent actionEvent) throws IOException {

        Scene registerScene = btnRegister.getScene();
        FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/user/register-input.fxml");
        registerScene.setRoot(fxmlLoader.load());
    }
}
