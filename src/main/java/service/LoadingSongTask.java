package service;

import javafx.concurrent.Task;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * @author super lollipop
 * @date 19-12-2
 */
@Component
@Scope("prototype")
public class LoadingSongTask extends Task<Boolean> {
    @Override
    protected Boolean call() throws Exception {
        Thread.sleep(500);
        return true;
    }
}
