package service;

import com.alibaba.fastjson.JSON;
import controller.content.EditGroupContentController;
import controller.main.CenterController;
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
import java.nio.charset.Charset;

@Service
@Scope("prototype")
public class UpdateGroupService extends javafx.concurrent.Service<Void> {

    @Resource
    private EditGroupContentController editGroupContentController;

    @Resource
    private Config config;

    @Resource
    private CenterController centerController;

    @Resource
    private ApplicationContext applicationContext;

    @Override
    protected Task<Void> createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String url = config.getGroupURL() + "/update";
                String name = editGroupContentController.getTfGroupName().getText();
                String description = editGroupContentController.getTaDescription().getText();
                String userID = config.getUser().getId();
                try {
                    Group group = editGroupContentController.getGroup();
                    group.setName(name);
                    group.setDescription(description);
                    group.setUserID(userID);
                    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().addTextBody("group",JSON.toJSONString(group), ContentType.create("application/json", Charset.forName("UTF-8")));
                    if (editGroupContentController.getChoseImageFile() != null){    //如果选择了专辑图片文件
                        multipartEntityBuilder.addBinaryBody("image",editGroupContentController.getChoseImageFile());
                    }
                    String responseString = HttpClientUtils.executePost(url,multipartEntityBuilder.build());
                    System.out.println(responseString);
                    Platform.runLater(()->{
                        WindowUtils.toastInfo(centerController.getStackPane(),new Label(responseString));
                        applicationContext.getBean(SynchronizeGroupService.class).restart();    //启动同步歌单服务
                    });
                }catch (Exception e){e.printStackTrace();}

                return null;
            }
        };
        return task;
    }
}
