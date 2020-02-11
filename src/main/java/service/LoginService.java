package service;

import javafx.concurrent.Task;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.User;
import task.LoginTask;

import javax.annotation.Resource;

@Service
@Scope("prototype")
public class LoginService extends javafx.concurrent.Service<User> {

    @Resource
    private LoginTask loginTask;

    @Override
    protected Task<User> createTask() {
        return loginTask;
    }
}
