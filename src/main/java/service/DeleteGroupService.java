package service;

import com.alibaba.fastjson.JSON;
import controller.main.LeftController;
import controller.main.MainController;
import dao.GroupDao;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import lombok.SneakyThrows;
import mediaplayer.Config;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
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
                String url = applicationContext.getBean(Config.class).getGroupURL() + "/delete";
                Group group = (Group) leftController.getContextMenuShownTab().getUserData();
                String groupString = JSON.toJSONString(group);
                StringEntity entity = new StringEntity(groupString, ContentType.create("application/json", Charset.forName("UTF-8")));
                String responseString = HttpClientUtils.executePost(url,entity);
                Platform.runLater(()->{
                    if (responseString.equals("删除歌单成功")){
                        leftController.removeGroupTab(group.getName());
                    }
                    WindowUtils.toastInfo(mainController.getStackPane(),new Label(responseString));
                });
                return null;
            }
        };
        return task;
    }
}
