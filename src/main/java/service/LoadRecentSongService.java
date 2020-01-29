package service;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.RecentSong;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import task.LoadRecentSongTask;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 20-1-29
 */
@Component
@Scope("prototype")
public class LoadRecentSongService extends Service<ObservableList<RecentSong>> {

    @Resource
    private LoadRecentSongTask loadRecentSongTask;

    @Override
    protected Task<ObservableList<RecentSong>> createTask() {
        return loadRecentSongTask;
    }
}
