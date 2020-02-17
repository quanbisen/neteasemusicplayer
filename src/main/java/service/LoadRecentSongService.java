package service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import mediaplayer.Config;
import mediaplayer.MyMediaPlayer;
import model.RecentSong;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import util.XMLUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author super lollipop
 * @date 20-1-29
 */
@Service
@Scope("prototype")
public class LoadRecentSongService extends javafx.concurrent.Service<ObservableList<RecentSong>> {

    @Resource
    private Config config;

    @Override
    protected Task<ObservableList<RecentSong>> createTask() {
        Task<ObservableList<RecentSong>> task = new Task<ObservableList<RecentSong>>() {
            @Override
            protected ObservableList<RecentSong> call() throws Exception {
                List<RecentSong> recentSongs = XMLUtils.getRecentPlaySongs(config.getRecentPlayFile(),"PlayedSong");
                Collections.reverse(recentSongs);   //倒序集合
                ObservableList<RecentSong> observableList = FXCollections.observableArrayList();
                observableList.addAll(recentSongs);
                return observableList;
            }
        };
        return task;
    }
}
