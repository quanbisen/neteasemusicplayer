package dao;

import org.springframework.stereotype.Repository;
import pojo.Group;

import java.util.List;

/**
 * @author super lollipop
 * @date 20-2-18
 */
@Repository(value = "groupDao")
public interface GroupDao {
    int insert(Group group);
    List<Group> queryAll(String userID);
    int delete(Group group);
    int update(Group group);
}
