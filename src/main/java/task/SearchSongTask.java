package task;

import controller.SearchInputController;
import dao.SongDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import model.Song;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author super lollipop
 * @date 19-12-10
 */
@Component
@Scope("prototype")
public class SearchSongTask extends Task<ObservableList<Song>> {

    /**注入歌曲的数据库操作类*/
    @Resource
    private SongDao songDao;

    @Resource
    private SearchInputController searchInputController;

    @Override
    protected ObservableList<Song> call() {
        List<Song> byNameSongs = songDao.queryByName(searchInputController.getTfSearchText().getText());    //查询歌名匹配的记录
        List<Song> bySingerSongs = songDao.queryBySinger(searchInputController.getTfSearchText().getText());//查询歌手匹配的记录
        ObservableList<Song> songObservableList = FXCollections.observableArrayList();  //获取FX可视化化集合对象
        songObservableList.addAll(byNameSongs);
        songObservableList.addAll(bySingerSongs);
        return songObservableList;
    }
}
