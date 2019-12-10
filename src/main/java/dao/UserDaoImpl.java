package dao;

import model.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;
import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 19-12-6
 */
@Repository
public class UserDaoImpl implements UserDao{

    /**注入MyBatis的SqlSessionFactory对象*/
    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public User findUserByIdAndPassword(User user) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        User validUser = sqlSession.selectOne("model.UserMapper.findUserByIdAndPassword",user);
        return validUser;
    }

    @Override
    public int addUser(User user) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        int row = sqlSession.insert("model.UserMapper.addUser",user);
        return row;
    }
}
