package controller.authentication;

import application.SpringFXMLLoader;
import controller.main.MainController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import service.RegisterService;
import dao.UserDao;
import util.WindowUtils;
import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author super lollipop
 * @date 19-12-5
 */
@Controller
public class RegisterController {

    /**
     * "返回"Label图标
     */
    @FXML
    private Label labBackIcon;

    /**
     * "关闭"Label图标
     */
    @FXML
    private Label labCloseIcon;

    /**
     * "账号"的TextField组件
     */
    @FXML
    private TextField tfAccountID;

    /**
     * "清除"账号的图标
     */
    @FXML
    private Label labClearIcon;

    /**
     * "密码"的TextField组件
     */
    @FXML
    private PasswordField pfPassword;

    @FXML
    /**注册信息反馈的Label组件*/
    private Label labRegisterInformation;

    /**
     * "注册"按钮组件
     */
    @FXML
    private Button btnRegister;

    /**加载指示器*/
    @FXML
    private ProgressIndicator registerProgressIndicator;

    /**
     * 注入窗体根容器（BorderPane）的控制类
     */
    @Resource
    private MainController mainController;

    /**注入“导航去登录、注册”面板的控制器Controller*/
    @Resource
    private NavigateLoginOrRegisterController navigateLoginOrRegisterController;

    /**注入userDao类*/
    @Resource
    private UserDao userDao;

    /**注入Spring上下文工具类*/
    @Resource
    private ConfigurableApplicationContext applicationContext;

    public TextField getTfAccountID() {
        return tfAccountID;
    }

    public PasswordField getPfPassword() {
        return pfPassword;
    }

    public Label getLabRegisterInformation() {
        return labRegisterInformation;
    }

    public Button getBtnRegister() {
        return btnRegister;
    }

    public void initialize() {
        labClearIcon.setVisible(false);  //初始化为不可见
        btnRegister.setMouseTransparent(true); //初始化不可以点击
        btnRegister.setOpacity(0.8);           //初始化不透明度为0.8
        registerProgressIndicator.setVisible(false);  //设置加载指示器不显示

        Platform.runLater(() -> {
            btnRegister.requestFocus();         //"登录"按钮请求聚焦
        });

        //"清除"账号的图标的显示时机
        tfAccountID.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!observable.getValue().equals("")) {
                labClearIcon.setVisible(true);
                if (!pfPassword.getText().equals("")) {
                    btnRegister.setMouseTransparent(false);
                    btnRegister.setOpacity(1);
                }

            } else {
                labClearIcon.setVisible(false);
            }
        });
        //"清除"账号的图标的显示时机
        tfAccountID.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == true && !tfAccountID.getText().equals("")) {
                labClearIcon.setVisible(true);
            } else {
                labClearIcon.setVisible(false);
            }
        }));

        //设置"注册"按钮的可点击性
        pfPassword.textProperty().addListener(((observable1, oldValue1, newValue1) -> {
            if (!pfPassword.getText().equals("") && !tfAccountID.getText().equals("")) {
                btnRegister.setMouseTransparent(false);
                btnRegister.setOpacity(1);
            } else {
                btnRegister.setMouseTransparent(true);
                btnRegister.setOpacity(0.8);
            }
        }));

    }

    /**
     * "返回"Label图标鼠标点击事件处理
     */
    @FXML
    public void onClickedBackIcon(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {  //鼠标左击
            labBackIcon.getScene().setRoot(navigateLoginOrRegisterController.getNavigateLoginOrRegister());  //设置根容器为"登录、注册的导航容器"
        }
    }

    /**
     * "关闭"Label图标鼠标点击事件处理
     */
    @FXML
    public void onClickedCloseIcon(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {  //鼠标左击
            ((Stage)labCloseIcon.getScene().getWindow()).close();      //关闭窗口
            WindowUtils.releaseBorderPane(mainController.getBorderPane());  //释放中间的面板，可以接受鼠标事件和改变透明度
        }

    }

    /**
     * "清除"账号的图标点击事件处理
     */
    @FXML
    public void onClickedClearIcon(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            tfAccountID.setText("");
            btnRegister.setMouseTransparent(true);
            btnRegister.setOpacity(0.8);
        }
    }

    /**
     * "注册"按钮点击事件处理
     */
    @FXML
    public void onClickedRegisterButton(ActionEvent actionEvent) throws IOException {
        if (!btnRegister.getText().equals("转到登录页面")){   //如果还没有注册成功

            RegisterService registerService = applicationContext.getBean(RegisterService.class);
            registerProgressIndicator.visibleProperty().bind(registerService.runningProperty());
            registerService.start();
            registerService.valueProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (observable.getValue()){
                        labRegisterInformation.setTextFill(Color.BLACK);
                        labRegisterInformation.setText("注册成功");
                        btnRegister.setText("转到登录页面");
                    }else {
                        labRegisterInformation.setTextFill(Color.rgb(181,44,46));
                        labRegisterInformation.setText("注册失败");
                    }
                }
            });
        }
        else {  //否则,则注册成功了,按钮可以转到登录页面。
            Scene registerScene = btnRegister.getScene();
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/authentication/login.fxml");
            registerScene.setRoot(fxmlLoader.load());
        }
    }
}
