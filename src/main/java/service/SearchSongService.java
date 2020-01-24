package service;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import model.LocalSong;
import model.OnlineSong;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import task.SearchSongTask;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 19-12-10
 */
@Service
@Scope("prototype")
public class SearchSongService extends javafx.concurrent.Service<ObservableList<OnlineSong>> {

    @Resource
    private SearchSongTask searchSongTask;

    @Override
    protected Task<ObservableList<OnlineSong>> createTask() {
        return searchSongTask;
    }
}
