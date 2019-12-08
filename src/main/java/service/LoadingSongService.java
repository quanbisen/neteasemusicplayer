package service;

import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 19-12-2
 */
@Component
@Scope("prototype")
public class LoadingSongService extends Service<ObservableList<Song>> {

    @Resource
    private LoadingSongTask loadingSongTask;

    @Override
    protected Task<ObservableList<Song>> createTask() {
        return loadingSongTask;
    }
}
