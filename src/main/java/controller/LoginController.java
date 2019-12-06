package controller;

import com.alibaba.fastjson.JSON;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.User;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import service.UserDao;
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

    /**播放器登录文件的存放路径*/
    private String Login_CONFIG_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "config" + File.separator + "login-config.properties";

    /**播放器登录配置文件*/
    private File LOGIN_CONFIG_FILE;

    public File getLOGIN_CONFIG_FILE() {
        return LOGIN_CONFIG_FILE;
    }

    public void initialize(){
        LOGIN_CONFIG_FILE = new File(Login_CONFIG_PATH);  //初始化播放器登录文件

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
            String accountID = tfAccountID.getText();  //取出输入的账号
            String password = pfPassword.getText();    //取出输入的密码
            User user = new User();                    //创建用用持久化对象
            user.setId(accountID);
            user.setPassword(password);
            User validUser = userDao.findUserByIdAndPassword(user);  //查询用户
            if (validUser==null){
                labLoginInformation.setText("登录账号或密码错误");
            }
            else if (validUser.getId().equals(accountID)
                    && validUser.getPassword().equals(password)){  //数据库查询到此记录，进行登录成功处理
                this.onClickedCloseIcon(mouseEvent);   //关闭当前登录窗口
                ImageView userImage = new ImageView(new Image(validUser.getImage()));  //创建用户头像图片对象
                userImage.setFitHeight(38);  //设置宽度、高度
                userImage.setFitWidth(38);
                tabsController.getLabUserImage().setGraphic(userImage);  //设置用户头像图片
                tabsController.getLabUserName().setText(validUser.getName());  //设置用户名称
                WindowUtils.toastInfo(centerController.getStackPane(),new Label("登录成功"));
                //存储登录成功的用户对象到本地文件

                LOGIN_CONFIG_FILE.delete();
                //写入到文件
                LOGIN_CONFIG_FILE = new File(Login_CONFIG_PATH);
                LOGIN_CONFIG_FILE.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(LOGIN_CONFIG_FILE);
                fileOutputStream.write(JSON.toJSONString(validUser).getBytes());
                fileOutputStream.close();
            }
            else {
                labLoginInformation.setText("登录失败");
            }
        }
    }
    @Test
    public void test() throws IOException {
        LOGIN_CONFIG_FILE = new File(Login_CONFIG_PATH);
        FileInputStream fileInputStream = new FileInputStream(LOGIN_CONFIG_FILE);
        int n = 0;
        StringBuffer stringBuffer = new StringBuffer();
        while (n!=-1){
            n=fileInputStream.read();//读取文件的一个字节(8个二进制位),并将其由二进制转成十进制的整数返回

            char by=(char) n; //转成字符

            stringBuffer.append(by);
        }
        String str = stringBuffer.substring(0,stringBuffer.length()-1);
        System.out.println(str);
        User user = JSON.parseObject(str,User.class);
        System.out.println(user.getName());
//
//        FileOutputStream fileOutputStream = new FileOutputStream(LOGIN_CONFIG_FILE);
//        User user = new User();
//        user.setId("122");
//        user.setName("t");
//        user.setPassword("1wd2");
//        user.setImage("tasdas");
//        fileOutputStream.write(JSON.toJSONString(user).getBytes());
    }

}
