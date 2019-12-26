package service;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import model.Song;
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
public class SearchSongService extends javafx.concurrent.Service<ObservableList<Song>> {

    @Resource
    private SearchSongTask searchSongTask;

    @Override
    protected Task<ObservableList<Song>> createTask() {
        return searchSongTask;
    }
}
