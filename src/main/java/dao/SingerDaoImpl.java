package dao;

import pojo.Singer;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

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
        Singer singer = sqlSession.selectOne("model.SingerMapper.findSinger",name);
        sqlSession.close();
        return singer;
    }
}
