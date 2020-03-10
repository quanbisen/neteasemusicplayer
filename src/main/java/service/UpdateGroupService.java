package service;

import controller.content.EditGroupContentController;
import controller.main.CenterController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import mediaplayer.Config;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.Group;
import util.WindowUtils;

import javax.annotation.Resource;

@Service
@Scope("prototype")
public class UpdateGroupService extends javafx.concurrent.Service<Void> {

    @Resource
    private EditGroupContentController editGroupContentController;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private CenterController centerController;



    @Override
    protected Task<Void> createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
//                String name = editGroupContentController.getTfGroupName().getText();
//                String description = editGroupContentController.getTaDescription().getText();
//                String userID = applicationContext.getBean(Config.class).getUser().getId();
//                try {
//                    Group group = editGroupContentController.getGroup();
//                    group.setName(name);
//                    group.setDescription(description);
//                    group.setUserID(userID);
//                    int row = groupDao.update(group);
//                    if (row == 1){
//                        Platform.runLater(()->{
//                            WindowUtils.toastInfo(centerController.getStackPane(),new Label("保存成功"));
//                        });
//                    }else {
//                        Platform.runLater(()->{
//                            WindowUtils.toastInfo(centerController.getStackPane(),new Label("保存失败"));
//                        });
//                    }
//                }catch (Exception e){e.printStackTrace();}

                return null;
            }
        };
        return task;
    }
}
