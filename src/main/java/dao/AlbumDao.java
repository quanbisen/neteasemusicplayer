package dao;

import pojo.Album;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author super lollipop
 * @date 20-2-10
 */
@Repository(value = "albumDao")
public interface AlbumDao {
    void insert(Album album);
    Album queryAlbumByName(String name);
    Map<String,String> queryAlbumMap(List<String> albumList);
}
