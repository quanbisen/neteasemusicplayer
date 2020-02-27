package dao;

import org.springframework.stereotype.Repository;
import pojo.Singer;
import java.util.List;
import java.util.Map;

/**
 * @author super lollipop
 * @date 20-2-8
 */
@Repository(value = "singerDao")
public interface SingerDao {
    Singer querySinger(String singer);
    Map<String,String> querySingerMap(List<String> singerList);
}
