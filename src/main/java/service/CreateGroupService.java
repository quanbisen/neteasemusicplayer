package service;

import com.alibaba.fastjson.JSON;
import controller.main.LeftController;
import controller.main.MainController;
import controller.popup.ChoseGroupController;
import controller.popup.CreateGroupController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import mediaplayer.Config;
import mediaplayer.PlayerState;
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

    @Resource
    private ChoseGroupController choseGroupController;

    @Override
    protected Task<Void> createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String url = applicationContext.getBean(Config.class).getGroupURL() + "/insert";
                String groupName = createGroupController.getTfInput().getText();
                String token = applicationContext.getBean(PlayerState.class).getUser().getToken();
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
                            if (((Stage)createGroupController.getTfInput().getScene().getWindow()).getOwner() == mainController.getStackPane().getScene().getWindow()){ //是单击左侧的"+"图标调用的创建歌单stage,释放主控器的borderPane
                                WindowUtils.toastInfo(mainController.getStackPane(), new Label("新建歌单成功"));
                            }else { //否则,就是"选择收藏歌单"面板调用创建歌单stage,释放"选择收藏歌单"面板
                                choseGroupController.getActualPane().setOpacity(1);
                                choseGroupController.getActualPane().setMouseTransparent(false);
                                //添加新的歌单候选HBox
                                choseGroupController.addGroupCandidate(group);  //添加候选歌单HBox
                            }
                            leftController.addGroupTab(group);  //在左侧的tab标签添加歌单
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
