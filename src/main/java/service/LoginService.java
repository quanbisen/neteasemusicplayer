package service;

import application.SpringFXMLLoader;
import controller.authentication.LoginController;
import controller.component.MusicGroupTabController;
import controller.main.CenterController;
import controller.main.LeftController;
import controller.main.MainController;
import dao.UserDao;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mediaplayer.Config;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.User;
import util.ImageUtils;
import util.WindowUtils;

import javax.annotation.Resource;
import java.io.IOException;

@Service
@Scope("prototype")
public class LoginService extends javafx.concurrent.Service<Boolean> {

    /**注入登录页面的控制器*/
    @Resource
    private LoginController loginController;

    /**注入用户的操作类*/
    @Resource
    private UserDao userDao;

    @Resource
    private MainController mainController;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private LeftController leftController;

    @Resource
    private CenterController centerController;

    @Override
    protected Task<Boolean> createTask() {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() {

                String accountID = loginController.getTfAccountID().getText();  //取出输入的账号
                String password = loginController.getPfPassword().getText();    //取出输入的密码
                User user = new User();                    //创建用用持久化对象
                user.setId(accountID);
                user.setPassword(password);
                applicationContext.getBean(Config.class).setUser(userDao.queryUserByIdAndPassword(user));

                if (applicationContext.getBean(Config.class).getUser() != null){
                    Platform.runLater(()->{
                        ((Stage)loginController.getPfPassword().getScene().getWindow()).close();      //关闭窗口
                        WindowUtils.releaseBorderPane(mainController.getBorderPane());  //释放中间的面板，可以接受鼠标事件和改变透明度
                        leftController.getLabUserImage().setGraphic(ImageUtils.createImageView(applicationContext.getBean(Config.class).getUser().getImageURL(),38,38));  //设置用户头像图片
                        leftController.getLabUserName().setText(applicationContext.getBean(Config.class).getUser().getName());  //设置用户名称
                        WindowUtils.toastInfo(centerController.getStackPane(),new Label("登录成功"));

                        //加载歌单指示器和"我喜欢的音乐"tab标签
                        try {
                            leftController.getVBoxTabContainer().getChildren().add(applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/component/musicgroup-indicator.fxml").load());   //歌单指示器组件
                            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/component/musicgroup-tab.fxml");    //"我喜欢的音乐"tab
                            leftController.getVBoxTabContainer().getChildren().add(fxmlLoader.load());
                            Image imageFavorTabIcon = new Image("/image/FavorTabIcon.png",20,20,true,true);
                            MusicGroupTabController musicGroupTabController = fxmlLoader.getController();
                            musicGroupTabController.getIvMusicGroupIcon().setImage(imageFavorTabIcon);
                            musicGroupTabController.getLabGroupName().setText("我喜欢的音乐");
                            leftController.getTabList().add(musicGroupTabController.getHBoxMusicGroup());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //加载用户创建的歌单

                    });
                    return true;
                }else {
                    Platform.runLater(()->{
                        loginController.getPfPassword().setText("");
                        loginController.getLabLoginInformation().setText("登录账号或密码错误");
                    });
                    return false;
                }

            }
        };
        return task;
    }
}
