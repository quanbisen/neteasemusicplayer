package dao;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;
import pojo.Singer;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author super lollipop
 * @date 20-2-8
 */
@Repository(value = "singerDao")
public class SingerDaoImpl implements SingerDao{

    /**注入MyBatis的SqlSessionFactory对象*/
    @Resource
    private SqlSessionFactory sqlSessionFactory;

    /**根据名称查找数据库的歌手记录
     * @param name 歌手名称
     * @return Singer 对象*/
    @Override
    public Singer querySinger(String name) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Singer singer = sqlSession.selectOne("model.SingerMapper.querySinger",name);
        sqlSession.close();
        return singer;
    }


    @Override
    public Map<String, String> querySingerMap(List<String> singerList) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Map<String, HashMap<String,String>> map = sqlSession.selectMap("model.SingerMapper.querySingerMap",singerList,"name");
        sqlSession.close();
        Map<String,String> singerMap = new HashMap<>();
        map.keySet().forEach(s -> {
            singerMap.put(s,map.get(s).get("image_url"));
        });
        return singerMap;
    }

    @Test
    public void testQuerySingerMap(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/config/application-context.xml");
        List<String> list = new ArrayList<>();
        list.add("G.E.M.邓紫棋");
        list.add("林俊杰");
        Map<String,String> singerListMap = applicationContext.getBean(SingerDao.class).querySingerMap(list);
        Set<String> set = singerListMap.keySet();
        set.forEach(s -> {
            System.out.println(s + "-->" + singerListMap.get(s));
        });
    }
}
