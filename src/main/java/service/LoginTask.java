package service;

import controller.CenterController;
import controller.LoginController;
import controller.TabsController;
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

    @Resource
    private LoginController loginController;

    @Resource
    private TabsController tabsController;

    @Resource
    private CenterController centerController;

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
                    ImageView userImage = new ImageView(new Image(validUser.getImage()));  //创建用户头像图片对象
                    userImage.setFitHeight(38);  //设置宽度、高度
                    userImage.setFitWidth(38);
                    tabsController.getLabUserImage().setGraphic(userImage);  //设置用户头像图片
                    tabsController.getLabUserName().setText(validUser.getName());  //设置用户名称
                    WindowUtils.toastInfo(centerController.getStackPane(),new Label("登录成功"));

                    //存储登录成功的用户对象到本地文件
                    tabsController.getLOGIN_CONFIG_FILE().delete();
                    try {
                        tabsController.getLOGIN_CONFIG_FILE().createNewFile();  //创建新的文件
                        UserUtils.saveUser(validUser,tabsController.getLOGIN_CONFIG_FILE());  //调用存储的函数，写入到文件

                        String urlString = validUser.getImage();
                        String imageName = urlString.substring(urlString.lastIndexOf("/")+1);
                        String USER_IMAGE_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "config" + File.separator + validUser.getId();
                        File path = new File(USER_IMAGE_PATH);
                        path.mkdirs();              //创建目录
                        System.out.println(USER_IMAGE_PATH);
                        File imageFile = new File(USER_IMAGE_PATH + File.separator + imageName);
                        ImageUtils.download(validUser.getImage(),imageFile);  //下载用户的头像文件，保存供下次打开播放器使用
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
