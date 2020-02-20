package service;

import controller.main.LeftController;
import controller.main.MainController;
import controller.popup.CreateGroupController;
import dao.GroupDao;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import mediaplayer.Config;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import pojo.Group;
import util.WindowUtils;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 20-2-20
 */
@Service
public class CreateGroupService extends javafx.concurrent.Service<Void> {

    @Resource
    private CreateGroupController createGroupController;

    @Resource
    private GroupDao groupDao;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private MainController mainController;

    @Resource
    private LeftController leftController;

    @Override
    protected Task<Void> createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Group group = new Group(null,createGroupController.getTfInput().getText(),applicationContext.getBean(Config.class).getUser().getId());
                int row = groupDao.insert(group);
                if (row == 1){  //插入成功的提示
                    Platform.runLater(()->{
                        System.out.println("添加成功");
                        WindowUtils.toastInfo(mainController.getStackPane(),new Label("添加成功"));
                    });
                }else { //插入失败，需要移除
                    Platform.runLater(()->{
                        System.out.println("添加失败");
                        WindowUtils.toastInfo(mainController.getStackPane(),new Label("添加失败"));
                        leftController.removeGroupTab(createGroupController.getTfInput().getText());
                    });
                }
                return null;
            }
        };
        return task;
    }
}
