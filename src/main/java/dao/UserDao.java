package dao;

import pojo.User;
import org.springframework.stereotype.Repository;

/**
 * @author super lollipop
 * @date 19-12-6
 */
@Repository(value = "userDao")
public interface UserDao {
    User queryUserByIdAndPassword(User user);
    User queryUserByIdToken(User user);
    int insertUser(User user);
    int updateLoginTime(String id);
}
