package service;

import javafx.concurrent.Task;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @author super lollipop
 * @date 5/3/20
 */
@Service
@Scope("prototype")
public class UpdateUserService extends javafx.concurrent.Service<Void>{
    @Override
    protected Task<Void> createTask() {
        return null;
    }
}
