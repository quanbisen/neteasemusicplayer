package dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Repository;
import pojo.Group;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author super lollipop
 * @date 20-2-18
 */
@Repository(value = "groupDao")
public class GroupDaoImpl implements GroupDao{

    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public int insert(Group group) {
        try {
            SqlSession sqlSession = sqlSessionFactory.openSession();
            int row = sqlSession.insert("pojo.GroupMapper.insert",group);
            sqlSession.close();
            return row;
        }catch (Exception e){e.printStackTrace();}
        return 0;
    }

    @Test
    public void testInsert(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/config/application-context.xml");
        Group group = new Group(null,"林俊杰","1769128867@qq.com");
        System.out.println(applicationContext.getBean(GroupDao.class).insert(group));
    }

    @Override
    public List<Group> queryAll(String userID) {
        try {
            SqlSession sqlSession = sqlSessionFactory.openSession();
            List<Group> groupList = sqlSession.selectList("pojo.GroupMapper.query",userID);
            sqlSession.close();
            return groupList;
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    @Test
    public void testQuery(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/config/application-context.xml");
        System.out.println(applicationContext.getBean(GroupDao.class).queryAll("1769128867@qq.com"));
    }

    @Override
    public int delete(Group group) {
        try {
            SqlSession sqlSession = sqlSessionFactory.openSession();
            int row = sqlSession.delete("pojo.GroupMapper.delete",group);
            sqlSession.close();
            return row;
        }catch (Exception e){e.printStackTrace();}
        return 0;

    }

    @Test
    public void testDelete(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/config/application-context.xml");
        Group group = new Group(null,"邓紫棋","1769128867@qq.com");
        System.out.println(applicationContext.getBean(GroupDao.class).delete(group));
    }

    @Override
    public int update(Group group) {
        try {
            SqlSession sqlSession = sqlSessionFactory.openSession();
            int row = sqlSession.update("pojo.GroupMapper.update",group);
            sqlSession.close();
            return row;
        }catch (Exception e){e.printStackTrace();}
        return 0;
    }

    @Test
    public void testUpdate(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/config/application-context.xml");
        Group group = new Group(1,"邓紫棋","1769128867@qq.com");
        group.setId(1);
        System.out.println(applicationContext.getBean(GroupDao.class).update(group));
    }
}
