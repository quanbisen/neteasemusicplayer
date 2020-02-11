package dao;

import pojo.Song;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
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
    public List<Song> queryByName(String name) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        List<Song> songList = sqlSession.selectList("model.OnlineSongMapper.queryByName",name);
        sqlSession.close();
        return songList;
    }

    @Override
    public List<Song> queryBySinger(String singer) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        List<Song> localSongList = sqlSession.selectList("model.OnlineSongMapper.queryBySinger",singer);
        sqlSession.close();
        return localSongList;
    }

    @Override
    public void insert(Song localSong) {

    }

    @Override
    public void delete(int id) {

    }
}
