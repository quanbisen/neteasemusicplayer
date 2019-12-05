package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
    private TextField tfPassword;

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

        tfAccountID.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!tfAccountID.getText().equals("")){
                labClearIcon.setVisible(true);
            }
            else {
                labClearIcon.setVisible(false);
            }
        });

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
}
