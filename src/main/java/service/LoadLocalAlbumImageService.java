package service;

import javafx.concurrent.Task;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import task.LoadLocalAlbumImageTask;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 20-2-10
 */
@Service
@Scope("prototype")
public class LoadLocalAlbumImageService extends javafx.concurrent.Service<Boolean> {

    @Resource
    private LoadLocalAlbumImageTask loadLocalAlbumImageTask;

    @Override
    protected Task<Boolean> createTask() {
        return loadLocalAlbumImageTask;
    }
}
