package service;

import javafx.concurrent.Task;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import task.LoadUserTask;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 20-2-11
 */
@Service
@Scope("prototype") //prototype
public class LoadUserService extends javafx.concurrent.Service<Void> {

    @Resource
    private LoadUserTask loadUserTask;

    @Override
    protected Task<Void> createTask() {
        return loadUserTask;
    }
}
