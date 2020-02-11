package service;

import javafx.concurrent.Task;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.User;
import task.LoadUserTask;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 20-2-11
 */
@Service
@Scope("prototype")
public class LoadUserService extends javafx.concurrent.Service<Boolean> {

    @Resource
    private LoadUserTask loadUserTask;

    @Override
    protected Task<Boolean> createTask() {
        return loadUserTask;
    }
}
