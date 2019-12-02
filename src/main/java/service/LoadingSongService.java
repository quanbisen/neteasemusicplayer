package service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.springframework.context.annotation.Scope;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 19-12-2
 */
//@org.springframework.stereotype.Service("loadingSongService")
//@Scope("prototype")
public class LoadingSongService extends Service<Boolean> {

//    @Resource
//    private LoadingSongTask loadingSongTask;

    @Override
    protected Task<Boolean> createTask() {
//        return loadingSongTask;
        return new LoadingSongTask();
    }
}
