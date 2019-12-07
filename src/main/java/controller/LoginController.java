package controller;

import com.alibaba.fastjson.JSON;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.User;
import org.apache.ibatis.exceptions.PersistenceException;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import service.LoginService;
import service.UserDao;
import util.ImageUtils;
import util.UserUtils;
import util.WindowUtils;

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
    MainController mainController;

    /**注入“导航去登录、注册”面板的控制器Controller*/
    @Resource
    private NavigateLoginOrRegisterController navigateLoginOrRegisterController;

    /**注入userDao类*/
    @Resource
    private UserDao userDao;

    /**注入左侧的标签控制器*/
    @Resource
    private TabsController tabsController;

    /**注入窗体根容器（BorderPane）的中间容器的控制器*/
    @Resource
    CenterController centerController;

    /**注入Spring上下文类*/
    @Resource
    private ApplicationContext applicationContext;

    private MouseEvent mouseEvent;

    public TextField getTfAccountID() {
        return tfAccountID;
    }

    public PasswordField getPfPassword() {
        return pfPassword;
    }

    public Label getLabLoginInformation() {
        return labLoginInformation;
    }

    public MouseEvent getMouseEvent() {
        return mouseEvent;
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
            if (newValue == true && !tfAccountID.getText().equals("")){
                labClearIcon.setVisible(true);
            }
            else {
                labClearIcon.setVisible(false);
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
    public void onClickedLoginButton(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){  //鼠标左击
            labLoginInformation.setText("");  //清空文本信息
            this.mouseEvent = mouseEvent;
            LoginService loginService = applicationContext.getBean(LoginService.class);
            loginProgressIndicator.visibleProperty().bind(loginService.runningProperty());
            loginService.start();

//            String accountID = tfAccountID.getText();  //取出输入的账号
//            String password = pfPassword.getText();    //取出输入的密码
//            User user = new User();                    //创建用用持久化对象
//            user.setId(accountID);
//            user.setPassword(password);
//            try{
//                User validUser = userDao.findUserByIdAndPassword(user);  //查询用户
//                if (validUser==null){
//                    labLoginInformation.setText("登录账号或密码错误");
//                }
//                else if (validUser.getId().equals(accountID)
//                        && validUser.getPassword().equals(password)){  //数据库查询到此记录，进行登录成功处理
//                    this.onClickedCloseIcon(mouseEvent);   //关闭当前登录窗口
//                    ImageView userImage = new ImageView(new Image(validUser.getImage()));  //创建用户头像图片对象
//                    userImage.setFitHeight(38);  //设置宽度、高度
//                    userImage.setFitWidth(38);
//                    tabsController.getLabUserImage().setGraphic(userImage);  //设置用户头像图片
//                    tabsController.getLabUserName().setText(validUser.getName());  //设置用户名称
//                    WindowUtils.toastInfo(centerController.getStackPane(),new Label("登录成功"));
//
//                    //存储登录成功的用户对象到本地文件
//                    tabsController.getLOGIN_CONFIG_FILE().delete();
//                    tabsController.getLOGIN_CONFIG_FILE().createNewFile();  //创建新的文件
//                    UserUtils.saveUser(validUser,tabsController.getLOGIN_CONFIG_FILE());  //调用存储的函数，写入到文件
//
//                    String urlString = validUser.getImage();
//                    String imageName = urlString.substring(urlString.lastIndexOf("/")+1);
//                    String USER_IMAGE_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "config" + File.separator + validUser.getId();
//                    File path = new File(USER_IMAGE_PATH);
//                    path.mkdirs();              //创建目录
//                    System.out.println(USER_IMAGE_PATH);
//                    File imageFile = new File(USER_IMAGE_PATH + File.separator + imageName);
//                    ImageUtils.download(validUser.getImage(),imageFile);  //下载用户的头像文件，保存供下次打开播放器使用
//                }
//            } catch (PersistenceException e){
//                labLoginInformation.setText("登录失败");
//            }
        }
    }

}
