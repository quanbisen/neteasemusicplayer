package service;

import application.SpringFXMLLoader;
import controller.authentication.RegisterInputController;
import dao.RegisterDao;
import dao.UserDao;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;
import mediaplayer.Config;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.Register;
import util.EmailUtils;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author super lollipop
 * @date 19-12-7
 */
@Service
@Scope("prototype")
public class RegisterService extends javafx.concurrent.Service<Boolean> {

    /**注入注册页面的控制器*/
    @Resource
    private RegisterInputController registerInputController;

    /**注入用户的操作类*/
    @Resource
    private UserDao userDao;

    @Resource
    private RegisterDao registerDao;

    @Resource
    private ApplicationContext applicationContext;

    @Override
    protected Task<Boolean> createTask() {
        Task<Boolean> task = new Task<Boolean>() {

            @Override
            protected Boolean call() throws Exception {
                //取出账号密码
                String id = registerInputController.getTfAccountID().getText();
                String password = registerInputController.getPfPassword().getText();
                //生成邮箱验证码
                String code = EmailUtils.generateCode();
                //创建持久化对象，存储到applicationContext用作验证，然后尝试插入数据库
                Register register = new Register(id,password,code);
                applicationContext.getBean(Config.class).setRegister(register);   //保存到applicationContext
                try {
                    int row = registerDao.insert(register);
                    if (row == 1){
                        EmailUtils.sendEmail(id,code);
                        Platform.runLater(()->{
                            try {
                                registerInputController.getVisualPane().setBottom(applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/authentication/register-verify.fxml").load());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        /*labRegisterInformation.setTextFill(Color.BLACK);
                        labRegisterInformation.setText("注册成功");
                        btnRegister.setText("转到登录页面");*/

                        return true;
                    }else {
                        Platform.runLater(()->{
                            registerInputController.getLabRegisterInformation().setTextFill(Color.rgb(181, 44, 46));
                            registerInputController.getLabRegisterInformation().setText("注册失败");
                        });
                        return false;
                    }
                } catch (PersistenceException e){
                    e.printStackTrace();
                    Platform.runLater(()->{
                        registerInputController.getLabRegisterInformation().setTextFill(Color.rgb(181,44,46));
                        registerInputController.getLabRegisterInformation().setText("账户已注册");
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
        };
        return task;
    }
}
