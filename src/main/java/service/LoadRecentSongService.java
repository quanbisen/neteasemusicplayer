package service;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import model.RecentSong;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import task.LoadRecentSongTask;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 20-1-29
 */
@Service
@Scope("prototype")
public class LoadRecentSongService extends javafx.concurrent.Service<ObservableList<RecentSong>> {

    @Resource
    private LoadRecentSongTask loadRecentSongTask;

    @Override
    protected Task<ObservableList<RecentSong>> createTask() {
        return loadRecentSongTask;
    }
}