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
        SqlSession sqlSession = sqlSessionFactory.openSession();
        int row = sqlSession.insert("pojo.GroupMapper.insert",group);
        sqlSession.close();
        return row;
    }

    @Test
    public void testInsert(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/config/application-context.xml");
        Group group = new Group();
        group.setName("林俊杰");
        group.setUserID("1769128867@qq.com");
        System.out.println(applicationContext.getBean(GroupDao.class).insert(group));
    }

    @Override
    public List<Group> queryAll(String userID) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        List<Group> groupList = sqlSession.selectList("pojo.GroupMapper.query",userID);
        sqlSession.close();
        return groupList;
    }

    @Test
    public void testQuery(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/config/application-context.xml");
        System.out.println(applicationContext.getBean(GroupDao.class).queryAll("1769128867@qq.com"));
    }

    @Override
    public int delete(Group group) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        int row = sqlSession.delete("pojo.GroupMapper.delete",group);
        sqlSession.close();
        return row;
    }

    @Test
    public void testDelete(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/config/application-context.xml");
        Group group = new Group();
        group.setName("邓紫棋");
        group.setUserID("1769128867@qq.com");
        System.out.println(applicationContext.getBean(GroupDao.class).delete(group));
    }

    @Override
    public int update(Group group) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        int row = sqlSession.update("pojo.GroupMapper.update",group);
        sqlSession.close();
        return row;
    }

    @Test
    public void testUpdate(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/config/application-context.xml");
        Group group = new Group();
        group.setId(1);
        group.setName("邓紫棋");
        group.setUserID("1769128867@qq.com");
        System.out.println(applicationContext.getBean(GroupDao.class).update(group));
    }
}
