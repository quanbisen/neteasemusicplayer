package service;

import controller.content.LocalMusicContentController;
import javafx.concurrent.Task;
import javafx.scene.control.ScrollBar;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 20-2-28
 */
@Service
@Scope("prototype")
public class HideScrollerBarService extends javafx.concurrent.ScheduledService<Void> {

    @Resource
    private LocalMusicContentController localMusicContentController;

    private ScrollBar scrollBar;

    public ScrollBar getScrollBar() {
        return scrollBar;
    }

    public void setScrollBar(ScrollBar scrollBar) {
        this.scrollBar = scrollBar;
    }

    @Override
    protected Task<Void> createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("schedule");
                if (!scrollBar.isDisable()){
                    scrollBar.setDisable(true);
                }
                return null;
            }

        };
        return task;

    }
}
