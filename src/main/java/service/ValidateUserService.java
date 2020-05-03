package service;

import application.SpringFXMLLoader;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import controller.main.LeftController;
import controller.main.MainController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mediaplayer.Config;
import mediaplayer.UserStatus;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.User;
import util.HttpClientUtils;
import util.ImageUtils;
import util.JSONObjectUtils;
import util.WindowUtils;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author super lollipop
 * @date 20-2-11
 */
@Service
@Scope("singleton")
public class ValidateUserService extends javafx.concurrent.Service<Void> {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private LeftController leftController;

    @Resource
    private MainController mainController;

    @Resource
    private UserStatus userStatus;

    @Override
    protected Task<Void> createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                /**登录状态加载*/
                File userStatusFile = applicationContext.getBean(Config.class).getUserStatusFile();
                if (userStatusFile.exists()) {    //用户状态文件存在，或者网络不OK（网络不OK就要尝试连接）
                    System.out.println("validate user service start...");
                    try {
                        User user = JSONObjectUtils.parseUser(userStatusFile);
                        File imageFile = null;
                        if (user != null){  //如果用户对象不为空才执行
                            if (user.getLocalImagePath() != null){
                                imageFile = new File(user.getLocalImagePath().substring(5)); //本地存储的用户头像文件句柄
                            }
                            updateUser(user);   //根据user对象更新左侧用户信息及歌单
                            //验证服务器user的状态是否合法
                            String url = applicationContext.getBean(Config.class).getUserURL() + "/validate";
                            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().addTextBody("token", user.getToken(), ContentType.create("text/pain", Charset.forName("UTF-8")));
                            try {
                                String responseString = HttpClientUtils.executePost(url, multipartEntityBuilder.build());
                                user = JSON.parseObject(responseString, User.class);
                                if (user != null) {  //如果user不为null，证明服务器验证通过。
                                    if (imageFile != null && imageFile.exists()) { //如果原来存储的头像文件存在，删除它
                                        imageFile.delete();
                                        HttpClientUtils.download(user.getImageURL(), imageFile); //下载新的头像图片保存
                                    }
                                    user.setLocalImagePath("file:" + imageFile.getPath());  //设置本地存储的图片位置
                                    JSONObjectUtils.saveObject(user, applicationContext.getBean(Config.class).getUserStatusFile());   //保存更新的用户信息到本地存储文件
                                    updateUser(user);
                                }
                            } catch (JSONException e) {
                                System.out.println("身份验证不通过");
                                applicationContext.getBean(Config.class).getUserStatusFile().delete();
                                File userImageFile = new File(user.getImageURL().substring(5));
                                userImageFile.delete();
                                Platform.runLater(() -> {
                                    WindowUtils.toastInfo(mainController.getStackPane(), new Label("身份验证不通过"));
                                });
                            } catch (HttpHostConnectException e) {
                                System.out.println("无网络连接");
                                cancel();   //取消定时服务
                                Platform.runLater(()->{
                                    WindowUtils.toastInfo(mainController.getStackPane(),new Label("无网络连接"));
                                });

                            }
                        }
                    }catch (JSONException e){
                        System.out.println("登录信息失效");
                        userStatusFile.delete(); //删除存储用户状态的文件
                        Platform.runLater(() -> {
                            WindowUtils.toastInfo(mainController.getStackPane(), new Label("登录信息失效"));
                        });
                    }
                }
                return null;
            }

            /**根据user对象更新左侧UI的显示，包括歌单名称、用户信息等
             * @param user 用户对象
             */
            private void updateUser(User user) {
                userStatus.setUser(user); //保存用户信息到applicationContext的UserState
                //加载歌单指示器和"我喜欢的音乐"tab标签
                Platform.runLater(() -> {
                    try {
                        leftController.removeAllGroupTab(); //先移除先前的歌单tab
                        //用户呢称和头像文件UI更新
                        leftController.getLabUserName().setText(user.getName());
                        if (user.getLocalImagePath() != null && new File(user.getLocalImagePath().substring(5)).exists()){   //如果本地存储的图片文件存在，设置图片显示
                            leftController.getLabUserImage().setGraphic(ImageUtils.createImageView(user.getLocalImagePath(), 38, 38));
                        }
                        new javafx.concurrent.Service<Void>() {     //创建一个加载在线图片资源的服务。
                            @Override
                            protected Task<Void> createTask() {
                                return new Task<Void>() {
                                    @Override
                                    protected Void call() throws Exception {
                                        if (user.getImageURL() != null){
                                            Image image = new Image(user.getImageURL(),38,38,true,true);
                                            if (!image.isError()){
                                                ((ImageView)leftController.getLabUserImage().getGraphic()).setImage(image);
                                            }else {
                                                ((ImageView)leftController.getLabUserImage().getGraphic()).setImage(new Image("image/UserDefaultImage.png",38,38,true,true));
                                            }
                                        }
                                        return null;
                                    }
                                };
                            }
                        }.start();  //启动服务
                        leftController.getVBoxTabContainer().getChildren().add(applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/component/group-indicator.fxml").load());    //歌单指示器组件
                        user.getGroupList().forEach(group -> {
                            try {
                                leftController.addGroupTab(group);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        };
        return task;
    }
}
