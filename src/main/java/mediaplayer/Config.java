package mediaplayer;

import org.springframework.stereotype.Component;
import pojo.Register;
import pojo.User;

import java.io.File;

/**
 * @author super lollipop
 * @date 20-2-11
 */
@Component
public class Config {

    /**播放器合法的登录用户对象*/
    private User user;

    /**播放器配置文件的存放路径*/
    private String configPath = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "config" + File.separator;

    /**注册时的临时对象*/
    private Register register;

    /**获取登录配置文件*/
    public File getLoginConfigFile() {
        return new File(configPath + "login-config.properties");
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
        this.register = register;
    }
}
