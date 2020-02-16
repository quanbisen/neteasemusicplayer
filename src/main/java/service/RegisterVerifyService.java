package service;

import org.springframework.stereotype.Service;
import javafx.concurrent.Task;
import org.springframework.context.annotation.Scope;
import task.RegisterVerifyTask;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 20-2-15
 */
@Service
@Scope("prototype")
public class RegisterVerifyService extends javafx.concurrent.Service<Void> {

    @Resource
    private RegisterVerifyTask registerVerifyTask;

    @Override
    protected Task<Void> createTask() {
        return registerVerifyTask;
    }
}
