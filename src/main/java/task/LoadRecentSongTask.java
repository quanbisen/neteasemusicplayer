package task;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import mediaplayer.MyMediaPlayer;
import model.RecentSong;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import util.XMLUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author super lollipop
 * @date 20-1-29
 */
@Component
@Scope("prototype")
public class LoadRecentSongTask extends Task<ObservableList<RecentSong>> {

    @Resource
    private MyMediaPlayer myMediaPlayer;

    @Override
    protected ObservableList<RecentSong> call() throws Exception {
        List<RecentSong> recentSongs = XMLUtils.getRecentPlaySongs(myMediaPlayer.getRecentPlayStorageFile(),"PlayedSong");
        Collections.reverse(recentSongs);   //倒序集合
        ObservableList<RecentSong> observableList = FXCollections.observableArrayList();
        observableList.addAll(recentSongs);
        return observableList;
    }
}
