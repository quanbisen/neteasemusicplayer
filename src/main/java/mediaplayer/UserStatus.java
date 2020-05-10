package mediaplayer;

import lombok.Data;
import org.springframework.stereotype.Component;
import pojo.Group;
import pojo.User;
import response.RegisterResponse;

import java.util.List;

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

    public void updateGroup(Group group,int groupID){
        List<Group> groupList = user.getGroupList();
        for (int i = 0; i < groupList.size(); i++) {
            if (groupList.get(i).getId() == groupID){
                groupList.set(i,group);
                break;
            }
        }
    }
}
