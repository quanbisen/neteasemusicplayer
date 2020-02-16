package task;

import controller.authentication.RegisterController;
import dao.RegisterDao;
import dao.UserDao;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;
import mediaplayer.Config;
import org.apache.logging.log4j.core.appender.rewrite.MapRewritePolicy;
import org.springframework.context.ApplicationContext;
import pojo.Register;
import pojo.User;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import util.EmailUtils;
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

    @Resource
    private RegisterDao registerDao;

    @Resource
    private ApplicationContext applicationContext;

    @Override
    protected Boolean call() throws Exception {
        //取出账号密码
        String id = registerController.getTfAccountID().getText();
        String password = registerController.getPfPassword().getText();
        //生成邮箱验证码发送
        String code = EmailUtils.generateCode();
        EmailUtils.sendEmail(id,code);
        //创建持久化对象，存储到applicationContext用作验证，然后尝试插入数据库
        Register register = new Register(id,password,code);
        applicationContext.getBean(Config.class).setRegister(register);   //保存到applicationContext
        try {
            int row = registerDao.insert(register);
            if (row == 1){
                return true;
            }else {
                return false;
            }
        }catch (PersistenceException e){
            Platform.runLater(()->{
                registerController.getLabRegisterInformation().setTextFill(Color.rgb(181,44,46));
                registerController.getLabRegisterInformation().setText("账户已注册");
            });
        }
        return null;

        /*User user = new User(id,password,MD5Utils.getMD5(id).substring(0,6),MD5Utils.getMD5(password));         //创建用户对象，设置属性为输入的TextField文本内容
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
        }*/
    }
}
