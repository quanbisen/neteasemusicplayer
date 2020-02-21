package service;

import controller.main.LeftController;
import controller.main.MainController;
import dao.GroupDao;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import lombok.SneakyThrows;
import mediaplayer.Config;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.Group;
import util.WindowUtils;

import javax.annotation.Resource;

@Service
@Scope("prototype")
public class DeleteGroupService extends javafx.concurrent.Service<Void> {

    @Resource
    private LeftController leftController;

    @Resource
    private GroupDao groupDao;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private MainController mainController;

    @SneakyThrows
    @Override
    protected Task<Void> createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Group group = (Group) leftController.getContextMenuShownTab().getUserData();
                int row = groupDao.delete(group);
                if (row == 1){
                    Platform.runLater(()->{
                        leftController.removeGroupTab(group.getName());
                        WindowUtils.toastInfo(mainController.getStackPane(),new Label("删除歌单成功"));
                    });
                }else {
                    Platform.runLater(()->{
                        WindowUtils.toastInfo(mainController.getStackPane(),new Label("删除歌单失败"));
                    });
                }
                return null;
            }
        };
        return task;
    }
}
