package dao;

import model.Singer;
import org.springframework.stereotype.Repository;

/**
 * @author super lollipop
 * @date 20-2-8
 */
@Repository(value = "singerDao")
public interface SingerDao {
    Singer querySinger(String singer);
}
