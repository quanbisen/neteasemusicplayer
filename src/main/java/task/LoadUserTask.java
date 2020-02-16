package task;

import controller.main.LeftController;
import controller.main.MainController;
import dao.UserDao;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import mediaplayer.Config;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pojo.User;
import util.ImageUtils;
import util.UserUtils;
import util.WindowUtils;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author super lollipop
 * @date 20-2-11
 */
@Component
@Scope("prototype") //prototype
public class LoadUserTask extends Task<Void> {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private UserDao userDao;

    @Resource
    private LeftController leftController;

    @Resource
    private MainController mainController;

    @Override
    protected Void call() throws Exception {
        File loginConfigFile = applicationContext.getBean(Config.class).getLoginConfigFile();  //播放器登录文件

        if (loginConfigFile.exists()) {
            try {
                applicationContext.getBean(Config.class).setUser(UserUtils.parseUser(loginConfigFile));  //设置文件读取到的用户信息
                User user = userDao.queryUserByIdToken(applicationContext.getBean(Config.class).getUser());
                if (user != null) { //不为null，证明有合法用户
                    Platform.runLater(() -> {
                        applicationContext.getBean(Config.class).setUser(user); //存储合法用户对象
                        Image image = new Image(applicationContext.getBean(Config.class).getUser().getImageURL(), 38, 38, true, true);
                        if (!image.isError()) {
                            leftController.getLabUserImage().setGraphic(ImageUtils.createImageView(image, 38, 38));   //设置用户头像
                        }
                        leftController.getLabUserName().setText(applicationContext.getBean(Config.class).getUser().getName());  //设置用户名称
                    });
                } else {
                    Platform.runLater(() -> {
                        applicationContext.getBean(Config.class).setUser(null); //存储合法用户对象
                        WindowUtils.toastInfo(mainController.getStackPane(), new Label("登录身份过期"));
                        leftController.getLabUserImage().setGraphic(ImageUtils.createImageView("/image/UnLoginImage.png", 38, 38));   //设置用户头像
                        leftController.getLabUserName().setText("未登录");  //设置用户名称
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
