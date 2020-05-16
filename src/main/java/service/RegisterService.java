package service;

import application.SpringFXMLLoader;
import com.alibaba.fastjson.JSON;
import controller.user.RegisterInputController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;
import mediaplayer.Config;
import mediaplayer.UserStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import response.RegisterResponse;
import util.HttpClientUtils;
import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author super lollipop
 * @date 19-12-7
 */
@Service
@Scope("prototype")
public class RegisterService extends javafx.concurrent.Service<Void> {

    /**注入注册页面的控制器*/
    @Resource
    private RegisterInputController registerInputController;

    @Resource
    private ApplicationContext applicationContext;

    @Override
    protected Task<Void> createTask() {
        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                try {
                    //取出账号密码
                    String email = registerInputController.getTfAccountID().getText();
                    String password = registerInputController.getPfPassword().getText();
                    String url = applicationContext.getBean(Config.class).getUserURL() + "/sendAuthenticationCode";
                    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().addTextBody("email",email, ContentType.create("text/pain", Charset.forName("UTF-8"))).
                            addTextBody("password",password,ContentType.create("text/pain",Charset.forName("UTF-8")));
                    String responseString = HttpClientUtils.executePost(url,multipartEntityBuilder.build());
                    RegisterResponse registerResponse = JSON.parseObject(responseString,RegisterResponse.class);
                    applicationContext.getBean(UserStatus.class).setRegisterResponse(registerResponse);     //存储注册响应对象到applicationContext管理的Config.class Bean中
                    if (registerResponse.getMessage().equals("验证码发送成功")){
                        Platform.runLater(()->{
                            try {
                                registerInputController.getVisualPane().setBottom(applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/user/register-verify.fxml").load());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }else if(registerResponse.getMessage().equals("操作太快")){
                        Platform.runLater(()->{
                            registerInputController.getLabRegisterInformation().setTextFill(Color.rgb(181, 44, 46));
                            registerInputController.getLabRegisterInformation().setText("你的注册速度过快，请稍等一分钟后再操作");
                        });
                    }else if (registerResponse.getMessage().equals("用户已注册")){
                        Platform.runLater(()->{
                            registerInputController.getLabRegisterInformation().setTextFill(Color.rgb(181, 44, 46));
                            registerInputController.getLabRegisterInformation().setText("用户已注册");
                        });
                    }
                    System.out.println(responseString);
                }catch (Exception e){
                    e.printStackTrace();
                }

                return null;
            }
        };
        return task;
    }
}
