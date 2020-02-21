package dao;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import pojo.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;
import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 19-12-6
 */
@Repository(value = "userDao")
public class UserDaoImpl implements UserDao{

    /**注入MyBatis的SqlSessionFactory对象*/
    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public User queryUserByIdAndPassword(User user) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        User validUser = sqlSession.selectOne("pojo.UserMapper.findUserByIdAndPassword",user);
        sqlSession.close();
        return validUser;
    }

    @Override
    public User queryUserByIdToken(User user) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        User validUser = sqlSession.selectOne("pojo.UserMapper.findUserByIdToken",user);
        sqlSession.close();
        return validUser;
    }

    @Override
    public int insertUser(User user) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        int row = sqlSession.insert("pojo.UserMapper.addUser",user);
        sqlSession.close();
        return row;
    }

    @Override
    public int updateLoginTime(String id) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        int row = sqlSession.update("pojo.UserMapper.updateLoginTime",id);
        sqlSession.close();
        return row;
    }

    @Test
    public void test(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/config/application-context.xml");
        applicationContext.getBean(UserDao.class).updateLoginTime("1769128867@qq.com");
    }
}
