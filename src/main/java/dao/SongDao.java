package dao;

import model.Song;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongDao {

    List<Song> queryByName(String name);
    List<Song> queryBySinger(String singer);
    void insert(Song song);
    void delete(int id);

}
