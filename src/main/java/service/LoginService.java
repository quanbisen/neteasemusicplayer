package service;

import javafx.concurrent.Task;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Scope("prototype")
public class LoginService extends javafx.concurrent.Service<Boolean> {

    @Resource
    private LoginTask loginTask;

    @Override
    protected Task<Boolean> createTask() {
        return loginTask;
    }
}
