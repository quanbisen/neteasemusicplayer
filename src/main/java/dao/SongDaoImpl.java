package dao;

import model.OnlineSong;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.springframework.stereotype.Repository;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author super lollipop
 * @date 19-12-10
 */
@Repository(value = "songDao")
public class SongDaoImpl implements SongDao {

    /**注入MyBatis的SqlSessionFactory对象*/
    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public List<OnlineSong> queryByName(String name) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        List<OnlineSong> onlineSongList = sqlSession.selectList("model.OnlineSongMapper.queryByName",name);
        sqlSession.close();
        return onlineSongList;
    }

    @Override
    public List<OnlineSong> queryBySinger(String singer) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        List<OnlineSong> localSongList = sqlSession.selectList("model.OnlineSongMapper.queryBySinger",singer);
        sqlSession.close();
        return localSongList;
    }

    @Override
    public void insert(OnlineSong localSong) {

    }

    @Override
    public void delete(int id) {

    }
}
