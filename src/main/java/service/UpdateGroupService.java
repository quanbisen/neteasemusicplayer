package service;

import com.alibaba.fastjson.JSON;
import controller.content.EditGroupContentController;
import controller.main.CenterController;
import controller.main.LeftController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import mediaplayer.Config;
import mediaplayer.PlayerStatus;
import mediaplayer.UserStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.Group;
import util.HttpClientUtils;
import util.WindowUtils;

import javax.annotation.Resource;
import java.nio.charset.Charset;

@Service
@Scope("prototype")
public class UpdateGroupService extends javafx.concurrent.Service<Void> {

    @Resource
    private EditGroupContentController editGroupContentController;

    @Resource
    private Config config;

    @Resource
    private UserStatus userStatus;

    @Resource
    private CenterController centerController;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private LeftController leftController;

    @Override
    protected Task<Void> createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String url = config.getGroupURL() + "/update";
                String name = editGroupContentController.getTfGroupName().getText();
                String description = editGroupContentController.getTaDescription().getText();
                String userID = userStatus.getUser().getId();
                try {
                    Group group = new Group();
                    group.setId(editGroupContentController.getGroup().getId());
                    group.setName(name);
                    group.setDescription(description);
                    group.setUserID(userID);
                    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().addTextBody("group",JSON.toJSONString(group), ContentType.create("application/json", Charset.forName("UTF-8")));
                    if (editGroupContentController.getChoseImageFile() != null){    //如果选择了专辑图片文件
                        multipartEntityBuilder.addBinaryBody("image",editGroupContentController.getChoseImageFile());
                    }
                    String responseString = HttpClientUtils.executePost(url,multipartEntityBuilder.build());
                    Platform.runLater(()->{
                        WindowUtils.toastInfo(centerController.getStackPane(),new Label(responseString));
                        SynchronizeGroupService synchronizeGroupService = applicationContext.getBean(SynchronizeGroupService.class);
                        synchronizeGroupService.restart();    //启动同步歌单服务
                        EventHandler<WorkerStateEvent> workerStateEventEventHandler = new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent event) {
                                Integer groupID = editGroupContentController.getGroup().getId();
                                if ( groupID != null){
                                    HBox selectedTab = leftController.getSelectedTab();
                                    if (selectedTab.getUserData() != null && groupID == ((Group)selectedTab.getUserData()).getId()){
                                        System.out.println("update");
                                        Event.fireEvent(selectedTab, new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                                                0, 0, 0, MouseButton.PRIMARY, 5, true, true, true, true,
                                                true, true, true, true, true, true, null));
                                    }
                                }
                                synchronizeGroupService.removeEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,this);

                                synchronizeGroupService.cancel();
                            }
                        };
                        synchronizeGroupService.setOnSucceeded(workerStateEventEventHandler);

                    });
                }catch (Exception e){e.printStackTrace();}

                return null;
            }
        };
        return task;
    }
}
