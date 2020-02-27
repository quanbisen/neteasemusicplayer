package service;

import com.alibaba.fastjson.JSON;
import controller.main.LeftController;
import controller.main.MainController;
import controller.popup.CreateGroupController;
import dao.GroupDao;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import mediaplayer.Config;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.Group;
import util.HttpClientUtils;
import util.WindowUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author super lollipop
 * @date 20-2-20
 */
@Service
@Scope("prototype")
public class CreateGroupService extends javafx.concurrent.Service<Void> {

    @Resource
    private CreateGroupController createGroupController;

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
                String url = applicationContext.getBean(Config.class).getGroupURL() + "/insert";
                String groupName = createGroupController.getTfInput().getText();
                String token = applicationContext.getBean(Config.class).getUser().getToken();
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().
                        addTextBody("name", groupName, ContentType.create("text/pain", Charset.forName("UTF-8"))).
                        addTextBody("token", token, ContentType.create("text/pain", Charset.forName("UTF-8")));
                String responseString = HttpClientUtils.executePost(url, multipartEntityBuilder.build());
                Platform.runLater(() -> {
                    if (responseString.equals("fail")) {
                        WindowUtils.toastInfo(mainController.getStackPane(), new Label("新建歌单失败"));
                    } else if (responseString.equals("身份不合法")) {
                        WindowUtils.toastInfo(mainController.getStackPane(), new Label(responseString));
                    } else if (responseString.equals("身份过期")) {
                        WindowUtils.toastInfo(mainController.getStackPane(), new Label(responseString));
                    } else { //最后一种情况，创建成功，返回JSONString
                        Group group = JSON.parseObject(responseString, Group.class);
                        try {
                            //UI更新部分
                            leftController.addGroupTab(groupName);
                            leftController.getTabList().get(leftController.getTabList().size() - 1).setUserData(group);
                            WindowUtils.toastInfo(mainController.getStackPane(), new Label("新建歌单成功"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return null;
            }
        };
        return task;
    }
}
