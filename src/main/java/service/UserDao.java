package service;

import model.User;
import org.springframework.stereotype.Repository;

/**
 * @author super lollipop
 * @date 19-12-6
 */
@Repository
public interface UserDao {
    public User findUserByIdAndPassword(User user);
    public int addUser(User user);
}
