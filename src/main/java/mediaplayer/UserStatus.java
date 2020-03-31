package mediaplayer;

import lombok.Data;
import org.springframework.stereotype.Component;
import pojo.User;
import response.RegisterResponse;

/**
 * @author super lollipop
 * @date 20-3-30
 */
@Component
@Data
public class UserStatus {

    /**
     * 播放器合法的登录用户对象
     * */
    private User user;

    /**
     * 注册时的临时对象
     * */
    private RegisterResponse registerResponse;
}
