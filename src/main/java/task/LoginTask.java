package task;

import controller.authentication.LoginController;
import dao.UserDao;
import javafx.concurrent.Task;
import pojo.User;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
@Scope("prototype")
public class LoginTask extends Task<User> {

    /**注入登录页面的控制器*/
    @Resource
    private LoginController loginController;

    /**注入用户的操作类*/
    @Resource
    private UserDao userDao;

    @Override
    protected User call() {

        String accountID = loginController.getTfAccountID().getText();  //取出输入的账号
        String password = loginController.getPfPassword().getText();    //取出输入的密码
        User user = new User();                    //创建用用持久化对象
        user.setId(accountID);
        user.setPassword(password);
        try {
            user = userDao.findUserByIdAndPassword(user);
            System.out.println(user);
        }catch (Exception e){
            e.printStackTrace();
        }
        return user == null ? new User(null,null,null,null) : user;
    }
}
