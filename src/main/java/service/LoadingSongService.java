package service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
public class LoadingSongService extends Service<Boolean> {

    @Resource
    private LoadingSongTask loadingSongTask;

    @Override
    protected Task<Boolean> createTask() {
        return loadingSongTask;
    }
}
