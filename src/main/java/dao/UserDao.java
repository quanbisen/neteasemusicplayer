package dao;

import model.User;
import org.springframework.stereotype.Repository;

/**
 * @author super lollipop
 * @date 19-12-6
 */
@Repository(value = "userDao")
public interface UserDao {
    User findUserByIdAndPassword(User user);
    int addUser(User user);
}
