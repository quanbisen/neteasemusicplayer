package service;

import application.SpringFXMLLoader;
import com.alibaba.fastjson.JSON;
import controller.authentication.LoginController;
import controller.component.GroupTabController;
import controller.main.CenterController;
import controller.main.LeftController;
import controller.main.MainController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import mediaplayer.Config;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.User;
import util.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Scope("prototype")
public class LoginService extends javafx.concurrent.Service<Void> {

    /**注入登录页面的控制器*/
    @Resource
    private LoginController loginController;

    @Resource
    private MainController mainController;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private LeftController leftController;

    @Resource
    private CenterController centerController;

    @Override
    protected Task<Void> createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws IOException {
                String url = applicationContext.getBean(Config.class).getUserURL() + "/login";
                String accountID = loginController.getTfAccountID().getText();  //取出输入的账号
                String password = loginController.getPfPassword().getText();    //取出输入的密码
                User user = new User();                    //创建用用持久化对象
                user.setId(accountID);
                user.setPassword(password);
                String userString = JSON.toJSONString(user);
                StringEntity entity = new StringEntity(userString, ContentType.create("application/json", Charset.forName("UTF-8")));
                String responseString = HttpClientUtils.executePost(url,entity);
                if (responseString.equals("fail")){ //返回失败，认证失败
                    Platform.runLater(()->{
                        loginController.getPfPassword().setText("");
                        loginController.getLabLoginInformation().setText("登录账号或密码错误");
                    });

                }else { //否则，认证通过
                    user = JSON.parseObject(responseString,User.class);
                    applicationContext.getBean(Config.class).setUser(user); //保存User对象到applicationContext，user对象有token信息

                    Path imagePath = applicationContext.getBean(Config.class).getCachePath().resolve("image");
                    Files.createDirectories(imagePath);
                    System.out.println();
                    File imageFile = new File(imagePath.toString() + File.separator + TimeUtils.formatDate(user.getLoginTime(),"yyyyMMddHHmmss") + user.getImageURL().substring(user.getImageURL().lastIndexOf("."))); //用用户的用户名作为图片命名
                    HttpClientUtils.download(user.getImageURL(),imageFile);  //下载用户的头像文件，保存供下次打开播放器使用
                    user.setImageURL("file:"+imageFile.getPath());
                    //保存登录信息到本地文件
                    File loginConfigFile = applicationContext.getBean(Config.class).getLoginConfigFile();
                    loginConfigFile.delete();
                    loginConfigFile.createNewFile();  //创建新的文件
                    JSONObjectUtils.saveObject(user,loginConfigFile);  //调用存储的函数，写入到文件
                    //关闭登录界面，并且开始同步歌单
                    Platform.runLater(()->{
                        //加载用户创建的歌单
                        applicationContext.getBean(SynchronizeGroupService.class).start();
//                        applicationContext.getBean(ScheduledQueryUserService.class).restart();    //重新启动定时任务

                        ((Stage)loginController.getPfPassword().getScene().getWindow()).close();      //关闭窗口
                        WindowUtils.releaseBorderPane(mainController.getBorderPane());  //释放中间的面板，可以接受鼠标事件和改变透明度
                        leftController.getLabUserImage().setGraphic(ImageUtils.createImageView(applicationContext.getBean(Config.class).getUser().getImageURL(),38,38));  //设置用户头像图片
                        leftController.getLabUserName().setText(applicationContext.getBean(Config.class).getUser().getName());  //设置用户名称
                        WindowUtils.toastInfo(centerController.getStackPane(),new Label("登录成功"));

                        //加载歌单指示器和"我喜欢的音乐"tab标签
                        try {
                            leftController.getVBoxTabContainer().getChildren().add(applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/component/group-indicator.fxml").load());   //歌单指示器组件
//                            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/component/favorgroup-tab.fxml");    //"我喜欢的音乐"tab
//                            leftController.getVBoxTabContainer().getChildren().add(fxmlLoader.load());
//                            GroupTabController groupTabController = fxmlLoader.getController();
//                            leftController.getTabList().add(groupTabController.getHBoxGroup());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                }
                return null;

            }
        };
        return task;
    }
}
