package service;

import controller.content.SearchInputContentController;
import dao.SongDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import pojo.Song;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author super lollipop
 * @date 19-12-10
 */
@Service
@Scope("prototype")
public class SearchSongService extends javafx.concurrent.Service<ObservableList<Song>> {

    /**注入歌曲的数据库操作类*/
    @Resource
    private SongDao songDao;

    @Resource
    private SearchInputContentController searchInputContentController;

    @Override
    protected Task<ObservableList<Song>> createTask() {
        Task<ObservableList<Song>> task = new Task<ObservableList<Song>>() {
            @Override
            protected ObservableList<Song> call() {
                System.out.println(songDao);
                List<Song> byNameLocalSongs = songDao.queryByName(searchInputContentController.getTfSearchText().getText());    //查询歌名匹配的记录
                System.out.println(byNameLocalSongs.size());
                List<Song> bySingerLocalSongs = songDao.queryBySinger(searchInputContentController.getTfSearchText().getText());//查询歌手匹配的记录
                ObservableList<Song> localSongObservableList = FXCollections.observableArrayList();  //获取FX可视化化集合对象
                localSongObservableList.addAll(byNameLocalSongs);
                localSongObservableList.addAll(bySingerLocalSongs);
                return localSongObservableList;
            }
        };
        return task;
    }
}
