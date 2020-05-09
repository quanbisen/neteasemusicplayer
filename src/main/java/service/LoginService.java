package service;

import application.SpringFXMLLoader;
import com.alibaba.fastjson.JSON;
import controller.user.LoginController;
import controller.main.CenterController;
import controller.main.LeftController;
import controller.main.MainController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import mediaplayer.Config;
import mediaplayer.UserStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.Group;
import pojo.User;
import util.*;
import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

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
                    LocalPersistenceUtils.saveUserStatus(applicationContext.getBean(Config.class),user);  //使用本地持久化工具保存用户对象
                    //查询用户创建的歌单对象集合，开始同步歌单，并且关闭登录界面
                    applicationContext.getBean(UserStatus.class).setUser(user); //保存User对象到applicationContext，user对象有token信息
                    List<Group> groupList = JSON.parseArray(HttpClientUtils.executeGet(applicationContext.getBean(Config.class).getGroupURL() + "/query/" + user.getToken()),Group.class);  //执行查询歌单
                    Platform.runLater(()->{
                        ((Stage)loginController.getPfPassword().getScene().getWindow()).close();      //关闭窗口
                        WindowUtils.releaseBorderPane(mainController.getBorderPane());  //释放中间的面板，可以接受鼠标事件和改变透明度
                        leftController.getLabUserImage().setGraphic(ImageUtils.createImageView(applicationContext.getBean(UserStatus.class).getUser().getImageURL(),38,38));  //设置用户头像图片
                        leftController.getLabUserName().setText(applicationContext.getBean(UserStatus.class).getUser().getName());  //设置用户名称
                        WindowUtils.toastInfo(centerController.getStackPane(),new Label("登录成功"));

                        //加载歌单指示器和"我喜欢的音乐"及用户创建的歌单tab标签
                        try {
                            leftController.getVBoxTabContainer().getChildren().add(applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/component/group-indicator.fxml").load());   //歌单指示器组件
                            for (int i = 0; i < groupList.size(); i++) {       //遍历歌单集合，添加到UI界面上
                                leftController.addGroupTab(groupList.get(i));
                            }
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
