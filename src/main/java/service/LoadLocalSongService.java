package service;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import model.LocalSong;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import task.LoadLocalSongTask;
import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 19-12-2
 */
@Service
@Scope("prototype")
public class LoadLocalSongService extends javafx.concurrent.Service<ObservableList<LocalSong>>{

    @Resource
    private LoadLocalSongTask loadLocalSongTask;

    @Override
    protected Task<ObservableList<LocalSong>> createTask() {
        return loadLocalSongTask;
    }
}
