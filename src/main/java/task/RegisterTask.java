package task;

import controller.authentication.RegisterController;
import dao.UserDao;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;
import pojo.User;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import util.MD5Utils;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 19-12-7
 */
@Component
@Scope("prototype")
public class RegisterTask extends Task<Boolean> {

    /**注入注册页面的控制器*/
    @Resource
    private RegisterController registerController;

    /**注入用户的操作类*/
    @Resource
    private UserDao userDao;

    @Override
    protected Boolean call() throws Exception {
        String id = registerController.getTfAccountID().getText();
        String password = registerController.getPfPassword().getText();
        User user = new User(id,password,MD5Utils.getMD5(id).substring(0,6),MD5Utils.getMD5(password));         //创建用户对象，设置属性为输入的TextField文本内容
        try {
            int row = userDao.addUser(user);
            if (row==1){
                return true;
            }else{
                return false;
            }
        }catch (PersistenceException e){
            Platform.runLater(()->{
                registerController.getLabRegisterInformation().setTextFill(Color.rgb(181,44,46));
                registerController.getLabRegisterInformation().setText("账户已注册");
            });
            return null;
        }
    }
}
