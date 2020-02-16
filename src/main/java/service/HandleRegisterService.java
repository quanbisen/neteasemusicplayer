package service;

import application.SpringFXMLLoader;
import controller.authentication.RegisterInputController;
import controller.authentication.RegisterVerifyController;
import dao.RegisterDao;
import dao.UserDao;
import javafx.application.Platform;
import javafx.concurrent.Task;
import mediaplayer.Config;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.Register;
import pojo.User;
import util.MD5Utils;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;

/**
 * @author super lollipop
 * @date 20-2-16
 */
@Service
@Scope("prototype")
public class HandleRegisterService extends javafx.concurrent.Service<Void> {

    @Resource
    private RegisterDao registerDao;

    @Resource
    private UserDao userDao;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private RegisterVerifyController registerVerifyController;

    @Resource
    private RegisterInputController registerInputController;

    @Override
    protected Task<Void> createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Date createTime = registerDao.query(applicationContext.getBean(Config.class).getRegister()).getCreateTime();
                Date nowTime = registerDao.queryDate();
                if ( (nowTime.getTime() - createTime.getTime()) / 1000 <= 60 ){ //时间合法
                    Register register = registerDao.query(applicationContext.getBean(Config.class).getRegister());  //查询数据库的register对象
                    applicationContext.getBean(Config.class).setRegister(register);     //更新applicationContext的register对象
                    if (register.getCode().equals(registerVerifyController.getTfCode().getText())){    //如果验证码也正确
                        User user = new User(register.getId(), register.getPassword(), MD5Utils.getMD5(register.getId()).substring(0, 6), MD5Utils.getMD5(register.getPassword()));         //创建用户对象，设置属性为输入的TextField文本内容
                        try {
                            int row = userDao.insertUser(user);
                            if (row == 1) {
                                Platform.runLater(()->{
                                    registerVerifyController.getTimeSchedule().cancel();    //取消倒计时服务
                                    try {
                                        registerInputController.getVisualPane().setBottom(applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/authentication/register-success.fxml").load());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                Platform.runLater(()->{
                                    registerVerifyController.getLabVerifyMessage().setText("注册失败");
                                });
                            }
                        } catch (PersistenceException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Platform.runLater(()->{
                            registerVerifyController.getLabVerifyMessage().setText("验证码输入错误");
                        });
                    }
                }else { //否则,时间过期了
                    Platform.runLater(()->{
                        registerVerifyController.getLabVerifyMessage().setText("注册失败");
                    });
                }
                return null;
            }
        };
        return task;
    }
}
