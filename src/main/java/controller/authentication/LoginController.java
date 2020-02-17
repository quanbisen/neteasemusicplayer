package controller.authentication;

import controller.main.CenterController;
import controller.main.LeftController;
import controller.main.MainController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mediaplayer.Config;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import pojo.User;
import service.LoginService;
import util.*;

import javax.annotation.Resource;
import java.io.*;

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
    /**登录信息反馈的Label组件*/
    private Label labLoginInformation;

    @FXML
    private ProgressIndicator loginProgressIndicator;

    /**"登录"按钮组建*/
    @FXML
    private Button btnLogin;

    /**注入窗体根容器（BorderPane）的控制类*/
    @Resource
    private MainController mainController;

    /**注入“导航去登录、注册”面板的控制器Controller*/
    @Resource
    private NavigateLoginOrRegisterController navigateLoginOrRegisterController;

    /**注入左侧的标签控制器*/
    @Resource
    private LeftController leftController;

    /**注入窗体根容器（BorderPane）的中间容器的控制器*/
    @Resource
    private CenterController centerController;

    /**注入Spring上下文类*/
    @Resource
    private ApplicationContext applicationContext;

    public TextField getTfAccountID() {
        return tfAccountID;
    }

    public PasswordField getPfPassword() {
        return pfPassword;
    }

    public Label getLabLoginInformation() {
        return labLoginInformation;
    }

    public void initialize(){

        btnLogin.setMouseTransparent(true); //初始化不可以点击
        labClearIcon.setVisible(false);  //初始化为不可见
        Platform.runLater(()->{
            btnLogin.requestFocus();         //"登录"按钮请求聚焦
        });
        btnLogin.setOpacity(0.8);           //初始化不透明度为0.8


        //"清除"账号的图标的显示时机
        tfAccountID.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!tfAccountID.getText().equals("")){
                labClearIcon.setVisible(true);
                if (!pfPassword.getText().equals("")){
                    btnLogin.setMouseTransparent(false);
                    btnLogin.setOpacity(1);
                }

            }
            else {
                labClearIcon.setVisible(false);
            }
        });
        //"清除"账号的图标的显示时机
        tfAccountID.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!tfAccountID.getText().equals("")){
                if (observable.getValue() == true) {
                    labClearIcon.setVisible(true);
                } else {
                    labClearIcon.setVisible(false);
                    this.verifyAccountID();
                }
            }
        }));

        //设置"登录"按钮的可点击性
        pfPassword.textProperty().addListener(((observable1, oldValue1, newValue1) -> {
            if (!pfPassword.getText().equals("") && !tfAccountID.getText().equals("")){
                btnLogin.setMouseTransparent(false);
                btnLogin.setOpacity(1);
            }
            else {
                btnLogin.setMouseTransparent(true);
                btnLogin.setOpacity(0.8);
            }
        }));

        loginProgressIndicator.setVisible(false);  //初始化“加载图标”不可见

    }

    /**验证账号合法性的函数*/
    private void verifyAccountID() {
        String reg = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        if (tfAccountID.getText().matches(reg)){
            labLoginInformation.setText("");
            btnLogin.setMouseTransparent(false);
        }else {
            labLoginInformation.setTextFill(Color.rgb(181,44,46));
            labLoginInformation.setText("邮箱不合法");
            btnLogin.setMouseTransparent(true);
        }
    }

    /**"返回"Label图标鼠标点击事件处理*/
    @FXML
    public void onClickedBackIcon(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){  //鼠标左击
            labBackIcon.getScene().setRoot(navigateLoginOrRegisterController.getShadowPane());  //设置根容器为"登录、注册的导航容器"
        }
    }

    /**"关闭"Label图标鼠标点击事件处理*/
    @FXML
    public void onClickedCloseIcon(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){  //鼠标左击
            ((Stage)labCloseIcon.getScene().getWindow()).close();      //关闭窗口
            WindowUtils.releaseBorderPane(mainController.getBorderPane());  //释放中间的面板，可以接受鼠标事件和改变透明度
        }

    }

    /**"清除"账号的图标点击事件处理*/
    @FXML
    public void onClickedClearIcon(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            tfAccountID.setText("");
            btnLogin.setMouseTransparent(true);
            btnLogin.setOpacity(0.8);
        }
    }

    /**"登录"按钮点击事件处理*/
    @FXML
    public void onClickedLoginButton(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){  //鼠标左击
            labLoginInformation.setText("");  //清空文本信息
            LoginService loginService = applicationContext.getBean(LoginService.class);
            loginProgressIndicator.visibleProperty().bind(loginService.runningProperty());
            loginService.start();
            loginService.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (observable.getValue()){
                    File loginConfigFile = applicationContext.getBean(Config.class).getLoginConfigFile();
                    User user = applicationContext.getBean(Config.class).getUser();
                    //存储登录成功的用户对象到本地文件
                    loginConfigFile.delete();
                    try {
                        loginConfigFile.createNewFile();  //创建新的文件
                        user.setCache(String.valueOf(System.currentTimeMillis()));
                        JSONObjectUtils.saveObject(user,loginConfigFile);  //调用存储的函数，写入到文件

                        String USER_IMAGE_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "cache";
                        File path = new File(USER_IMAGE_PATH);
                        if (!path.exists()){
                            path.mkdirs();              //创建目录
                        }

                        File imageFile = new File(USER_IMAGE_PATH + File.separator + user.getCache()); //用用户的用户名作为图片命名
                        user.setCache(null);
                        HttpClientUtils.download(user.getImageURL(),imageFile);  //下载用户的头像文件，保存供下次打开播放器使用
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    System.out.println("false");
                }
            });

        }
    }

}
