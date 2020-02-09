package service;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import model.LocalSinger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import task.LoadLocalSingerImageTask;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 20-2-9
 */
@Service
@Scope("prototype")
public class LoadLocalSingerImageService extends javafx.concurrent.Service<Boolean> {

    @Resource
    private LoadLocalSingerImageTask loadLocalSingerImageTask;

    @Override
    protected Task<Boolean> createTask() {
        return loadLocalSingerImageTask;
    }
}
