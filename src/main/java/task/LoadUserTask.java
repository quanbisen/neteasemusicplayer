package task;

import controller.main.LeftController;
import dao.UserDao;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import mediaplayer.Config;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pojo.User;
import util.ImageUtils;
import util.UserUtils;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author super lollipop
 * @date 20-2-11
 */
@Component
@Scope("prototype")
public class LoadUserTask extends Task<Boolean> {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private UserDao userDao;

    @Resource
    private LeftController leftController;

    @Override
    protected Boolean call() throws Exception {
        File loginConfigFile = applicationContext.getBean(Config.class).getLoginConfigFile();  //播放器登录文件

        if (loginConfigFile.exists()){
            try {
                applicationContext.getBean(Config.class).setUser(UserUtils.parseUser(loginConfigFile));  //设置文件读取到的用户信息
                User user = userDao.queryUserByIdToken(applicationContext.getBean(Config.class).getUser());
                if (user != null){ //不为null，证明有合法用户
                    applicationContext.getBean(Config.class).setUser(user); //存储合法用户对象
                    Platform.runLater(()->{
                        Image image = new Image(applicationContext.getBean(Config.class).getUser().getImageURL(),38,38,true,true);
                        if (!image.isError()){
                            leftController.getLabUserImage().setGraphic(ImageUtils.createImageView(image,38,38));   //设置用户头像
                        }
                        leftController.getLabUserName().setText(applicationContext.getBean(Config.class).getUser().getName());  //设置用户名称
                    });
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }else {
            return false;
        }
    }
}
