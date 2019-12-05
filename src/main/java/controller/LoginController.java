package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Controller;
import util.WindowUtils;

import javax.annotation.Resource;

@Controller
public class LoginController {

    /**"返回"Label图标*/
    @FXML
    private Label labBackIcon;

    /**"关闭"Label图标*/
    @FXML
    private Label labCloseIcon;

    /**"账号"的TextField组件*/
    @FXML
    private TextField tfAccountID;

    /**"清除"账号的图标*/
    @FXML
    private Label labClearIcon;

    /**"密码"的TextField组件*/
    @FXML
    private PasswordField pfPassword;

    @FXML
    /**登录信息反馈的Label组建*/
    private Label labLoginInformation;

    /**"登录"按钮组建*/
    @FXML
    private Button btnLogin;

    /**注入窗体根容器（BorderPane）的控制类*/
    @Resource
    MainController mainController;

    /**注入window工具类*/
    @Resource
    private WindowUtils windowUtils;

    /**注入“导航去登录、注册”面板的控制器Controller*/
    @Resource
    private NavigateLoginOrRegisterController navigateLoginOrRegisterController;

    public void initialize(){
        labClearIcon.setVisible(false);  //初始化为不可见
        btnLogin.setMouseTransparent(true); //初始化不可以点击
        btnLogin.setOpacity(0.8);           //初始化不透明度为0.8

        Platform.runLater(()->{
            btnLogin.requestFocus();         //"登录"按钮请求聚焦
        });

        //设置"登录"按钮的可点击性和"清除"账号的图标的显示时机
        tfAccountID.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!tfAccountID.getText().equals("")){
                labClearIcon.setVisible(true);
                pfPassword.textProperty().addListener(((observable1, oldValue1, newValue1) -> {
                    if (!pfPassword.getText().equals("")){
                        btnLogin.setMouseTransparent(false);
                        btnLogin.setOpacity(1);
                    }
                    else {
                        btnLogin.setMouseTransparent(true);
                        btnLogin.setOpacity(0.8);
                    }
                }));
            }
            else {
                labClearIcon.setVisible(false);
            }
        });

        //"清除"账号的图标的显示时机
        tfAccountID.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == true && !tfAccountID.getText().equals("")){
                labClearIcon.setVisible(true);
            }
            else {
                labClearIcon.setVisible(false);
            }
        }));

    }

    /**"返回"Label图标鼠标点击事件处理*/
    @FXML
    public void onClickedBackIcon(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){  //鼠标左击
            labBackIcon.getScene().setRoot(navigateLoginOrRegisterController.getNavigateLoginOrRegister());  //设置根容器为"登录、注册的导航容器"
        }
    }

    /**"关闭"Label图标鼠标点击事件处理*/
    @FXML
    public void onClickedCloseIcon(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){  //鼠标左击
            labCloseIcon.getScene().getWindow().hide();      //关闭窗口
            windowUtils.releaseBorderPane(mainController.getBorderPane());  //释放中间的面板，可以接受鼠标事件和改变透明度
        }

    }

    /**"清除"账号的图标点击事件处理*/
    @FXML
    public void onClickedClearIcon(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            tfAccountID.setText("");
        }
    }

    /**"登录"按钮点击事件处理*/
    @FXML
    public void onClickedLoginButton(ActionEvent actionEvent) {
        String accountID = tfAccountID.getText();
        String password = pfPassword.getText();
        labLoginInformation.setText(accountID + " " + pfPassword);
        System.out.println(accountID);
        System.out.println(password);
    }
}
