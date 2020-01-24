package dao;

import model.OnlineSong;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SongDao {

    List<OnlineSong> queryByName(String name);
    List<OnlineSong> queryBySinger(String singer);
    void insert(OnlineSong onlineSong);
    void delete(int id);

}
