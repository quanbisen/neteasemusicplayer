package task;

import controller.authentication.RegisterVerifyController;
import javafx.concurrent.Task;
import mediaplayer.Config;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pojo.Register;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 20-2-15
 */
@Component
@Scope("prototype")
public class RegisterVerifyTask extends Task<Void> {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private RegisterVerifyController registerVerifyController;

    @Override
    protected Void call() throws Exception {
        Register register = applicationContext.getBean(Config.class).getRegister(); //取出存储的注册账号
        if (register.getCode().equals(registerVerifyController.getTfCode().getText())){ //如果验证码相同，还需再次判断时间是否超时

        }
        return null;
    }
}
