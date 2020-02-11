package dao;

import pojo.Album;
import org.springframework.stereotype.Repository;

/**
 * @author super lollipop
 * @date 20-2-10
 */
@Repository(value = "albumDao")
public interface AlbumDao {
    void insert(Album album);
    Album queryAlbumByName(String name);
}
