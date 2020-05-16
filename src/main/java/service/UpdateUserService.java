package service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import controller.content.EditUserContentController;
import controller.main.LeftController;
import controller.main.MainController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import mediaplayer.Config;
import mediaplayer.UserStatus;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pojo.User;
import util.HttpClientUtils;
import util.LocalPersistenceUtils;
import util.WindowUtils;
import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author super lollipop
 * @date 5/3/20
 */
@Service
@Scope("prototype")
public class UpdateUserService extends javafx.concurrent.Service<Void>{

    @Resource
    private EditUserContentController editUserContentController;

    @Resource
    private Config config;

    @Resource
    private UserStatus userStatus;

    @Resource
    private MainController mainController;

    @Resource
    private LeftController leftController;

    @Override
    protected Task<Void> createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String url = config.getUserURL() + "/update";
                String name = editUserContentController.getTfPetName().getText();
                String description = editUserContentController.getTaDescription().getText();
                String sex = editUserContentController.getCbSex().getValue();
                Date birthday = null;
                if (editUserContentController.getDpBirthday().getValue() != null){
                    birthday = Date.from(editUserContentController.getDpBirthday().getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                }
                String province = editUserContentController.getCbProvince().getValue();
                String city = editUserContentController.getCbCity().getValue();
                User user = new User();
                user.setId(userStatus.getUser().getId());
                if (!StringUtils.isEmpty(name)){
                    user.setName(name);
                }
                if (!StringUtils.isEmpty(description)){
                    user.setDescription(description);
                }
                if (!StringUtils.isEmpty(sex)){
                    user.setSex(sex);
                }
                if (birthday != null){
                    user.setBirthday(birthday);
                }
                if (!StringUtils.isEmpty(province)){
                    user.setProvince(province);
                }
                if (!StringUtils.isEmpty(city)){
                    user.setCity(city);
                }
                try {
                    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().addTextBody("user", JSON.toJSONString(user), ContentType.create("application/json", Charset.forName("UTF-8")));
                    if (editUserContentController.getChoseImageFile() != null){    //如果选择了专辑图片文件
                        multipartEntityBuilder.addBinaryBody("image",editUserContentController.getChoseImageFile());
                    }
                    String responseString = HttpClientUtils.executePost(url,multipartEntityBuilder.build());
                    try {
                        user = JSON.parseObject(responseString,User.class); //解析用户对象
                        userStatus.setUser(user);   //保存用户对象到记录应用状态这里
                        LocalPersistenceUtils.saveUserStatus(config,userStatus.getUser());  //保存到本地文件
                        Platform.runLater(()->{
                            leftController.updateUserInformation(userStatus.getUser());
                            WindowUtils.toastInfo(mainController.getStackPane(),new Label("保存成功"));
                        });
                    }catch (JSONException e){   //异常更新失败
                        System.out.println("解析responseString导致异常，保存用户信息失败");
                        Platform.runLater(()-> WindowUtils.toastInfo(mainController.getStackPane(),new Label("保存失败")));
                    }
                }catch (HttpHostConnectException e){
                    Platform.runLater(()-> WindowUtils.toastInfo(mainController.getStackPane(),new Label("无网络")));
                }catch (Exception e){
                    Platform.runLater(()-> WindowUtils.toastInfo(mainController.getStackPane(),new Label("保存失败")));
                    e.printStackTrace();
                }
                return null;
            }
        };
        return task;
    }
}
