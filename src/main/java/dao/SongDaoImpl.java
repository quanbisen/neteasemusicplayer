package dao;

import model.Song;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author super lollipop
 * @date 19-12-10
 */
@Repository
public class SongDaoImpl implements SongDao {

    /**注入MyBatis的SqlSessionFactory对象*/
    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public List<Song> queryByName(String name) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        List<Song> songList = sqlSession.selectList("model.SongMapper.queryByName",name);
        sqlSession.close();
        return songList;
    }


    @Override
    public List<Song> queryBySinger(String singer) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        List<Song> songList = sqlSession.selectList("model.SongMapper.queryBySinger",singer);
        sqlSession.close();
        return songList;
    }

    @Override
    public void insert(Song song) {

    }

    @Override
    public void delete(int id) {

    }
}
