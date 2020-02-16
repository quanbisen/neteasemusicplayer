package service;

import dao.RegisterDao;
import javafx.concurrent.Task;
import mediaplayer.Config;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.Register;
import util.EmailUtils;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 20-2-16
 */
@Service
@Scope(value = "prototype")
public class ResendRegisterCodeService extends javafx.concurrent.Service<ScheduledCountDownService> {

    @Resource
    private RegisterDao registerDao;

    @Resource
    private ApplicationContext applicationContext;

    @Override
    protected Task<ScheduledCountDownService> createTask() {
        Task<ScheduledCountDownService> task = new Task<ScheduledCountDownService>() {
            @Override
            protected ScheduledCountDownService call() throws Exception {
                Register register = applicationContext.getBean(Config.class).getRegister();
                //生成邮箱验证码
                String code = EmailUtils.generateCode();
                register.setCode(code);
                EmailUtils.sendEmail(register.getId(),code);
                //更新数据库此用户的创建时间和验证码
                int row = registerDao.updateDateAndCode(register);
                if (row == 1){
                    return applicationContext.getBean(ScheduledCountDownService.class);
                }else {
                    return null;
                }
            }
        };
        return task;
    }
}
