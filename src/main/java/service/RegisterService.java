package service;

import javafx.concurrent.Task;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import task.RegisterTask;
import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 19-12-7
 */
@Service
@Scope("prototype")
public class RegisterService extends javafx.concurrent.Service<Boolean> {

    @Resource
    private RegisterTask registerTask;

    @Override
    protected Task createTask() {
        return registerTask;
    }
}
