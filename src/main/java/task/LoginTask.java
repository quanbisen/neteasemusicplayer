package task;

import controller.main.CenterController;
import controller.authentication.LoginController;
import controller.main.LeftController;
import dao.UserDao;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.User;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import util.ImageUtils;
import util.UserUtils;
import util.WindowUtils;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

@Component
@Scope("prototype")
public class LoginTask extends Task<Boolean> {

    /**注入登录页面的控制器*/
    @Resource
    private LoginController loginController;

    /**注入左侧便签的容器页面的控制器*/
    @Resource
    private LeftController leftController;

    /**注入中间显示的页面的控制器*/
    @Resource
    private CenterController centerController;

    /**注入用户的操作类*/
    @Resource
    private UserDao userDao;

    @Override
    protected Boolean call() throws Exception {

        String accountID = loginController.getTfAccountID().getText();  //取出输入的账号
        String password = loginController.getPfPassword().getText();    //取出输入的密码
        User user = new User();                    //创建用用持久化对象
        user.setId(accountID);
        user.setPassword(password);
        try{
            User validUser = userDao.findUserByIdAndPassword(user);  //查询用户
            if (validUser==null){
                Thread.sleep(200);  //休眠两百毫秒
                Platform.runLater(()->{
                    loginController.getLabLoginInformation().setText("登录账号或密码错误");
                });
                return false;
            }
            else if (validUser.getId().equals(accountID)
                    && validUser.getPassword().equals(password)){  //数据库查询到此记录，进行登录成功处理
                Platform.runLater(()->{
                    loginController.onClickedCloseIcon(loginController.getMouseEvent());   //关闭当前登录窗口
                    ImageView userImage = new ImageView(new Image(validUser.getImageURL()));  //创建用户头像图片对象
                    userImage.setFitHeight(38);  //设置宽度、高度
                    userImage.setFitWidth(38);
                    leftController.getLabUserImage().setGraphic(userImage);  //设置用户头像图片
                    leftController.getLabUserName().setText(validUser.getName());  //设置用户名称
                    WindowUtils.toastInfo(centerController.getStackPane(),new Label("登录成功"));

                    //存储登录成功的用户对象到本地文件
                    leftController.getLOGIN_CONFIG_FILE().delete();
                    try {
                        leftController.getLOGIN_CONFIG_FILE().createNewFile();  //创建新的文件
                        UserUtils.saveUser(validUser, leftController.getLOGIN_CONFIG_FILE());  //调用存储的函数，写入到文件

                        String urlString = validUser.getImageURL();
                        String imageName = urlString.substring(urlString.lastIndexOf("/")+1);
                        String USER_IMAGE_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "config" + File.separator + validUser.getId();
                        File path = new File(USER_IMAGE_PATH);
                        path.mkdirs();              //创建目录
                        System.out.println(USER_IMAGE_PATH);
                        File imageFile = new File(USER_IMAGE_PATH + File.separator + imageName);
                        ImageUtils.download(validUser.getImageURL(),imageFile);  //下载用户的头像文件，保存供下次打开播放器使用
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                return true;
            }
        } catch (PersistenceException e){
            Platform.runLater(()->{
                loginController.getLabLoginInformation().setText("登录失败");
            });
            return false;
        }
        return false;
    }
}
