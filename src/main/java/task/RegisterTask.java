package task;

import controller.RegisterController;
import dao.UserDao;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;
import model.User;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
        User user1 = new User();         //创建用户对象，设置属性为输入的TextField文本内容
        user1.setId(registerController.getTfAccountID().getText());
        user1.setPassword(registerController.getPfPassword().getText());
        try {
            int row = userDao.addUser(user1);
            if (row==1){
                Thread.sleep(400);
                Platform.runLater(()->{
                    registerController.getLabRegisterInformation().setTextFill(Color.BLACK);
                    registerController.getLabRegisterInformation().setText("注册成功");
                    registerController.getBtnRegister().setText("转到登录页面");
                });
                return true;
            }
        }catch (PersistenceException e){
            Thread.sleep(200);
            Platform.runLater(()->{
                registerController.getLabRegisterInformation().setTextFill(Color.rgb(181,44,46));
                registerController.getLabRegisterInformation().setText("注册失败");
            });
            return false;
        }
        return false;
    }
}
