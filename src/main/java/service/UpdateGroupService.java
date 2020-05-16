package service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import controller.content.EditGroupContentController;
import controller.main.CenterController;
import controller.main.LeftController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import mediaplayer.Config;
import mediaplayer.UserStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
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
                    try {
                        group = JSONObject.parseObject(responseString,Group.class);
                        int groupID = group.getId();
                        leftController.setGroupTabData(group,groupID); //更新group的信息
                        userStatus.updateGroup(group,groupID);
                        Platform.runLater(()->{
                            if (leftController.getSelectedTab().getUserData() != null &&
                                    groupID == ((Group)leftController.getSelectedTab().getUserData()).getId()){ //如果更新的歌单是当前显示中的歌单，需要重新加载
                                leftController.reloadGroupTabContent(groupID);
                            }
                            WindowUtils.toastInfo(centerController.getStackPane(),new Label("保存成功"));
                        });
                    }catch (JSONException e){   //catch 解析异常，证明保存失败，responseString服务器返回的是“保存失败”
                        Platform.runLater(()->WindowUtils.toastInfo(centerController.getStackPane(),new Label(responseString)));
                    }

                }catch (Exception e){e.printStackTrace();}

                return null;
            }
        };
        return task;
    }
}
