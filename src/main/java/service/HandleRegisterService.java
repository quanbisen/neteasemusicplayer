package service;

import application.SpringFXMLLoader;
import com.alibaba.fastjson.JSON;
import controller.authentication.RegisterInputController;
import controller.authentication.RegisterVerifyController;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
 * @date 20-2-16
 */
@Service
@Scope("prototype")
public class HandleRegisterService extends javafx.concurrent.Service<Void> {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private RegisterVerifyController registerVerifyController;

    @Resource
    private RegisterInputController registerInputController;

    @Override
    protected Task<Void> createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                String url = applicationContext.getBean(Config.class).getUserURL() + "/register";
                RegisterResponse registerResponse = applicationContext.getBean(UserStatus.class).getRegisterResponse();
                String id = registerResponse.getId();
                String password = registerResponse.getPassword();
                String code = registerVerifyController.getTfCode().getText();

                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().
                        addTextBody("email",id, ContentType.create("text/pain", Charset.forName("UTF-8"))).
                        addTextBody("password",password,ContentType.create("text/pain",Charset.forName("UTF-8"))).
                        addTextBody("code",code,ContentType.create("text/pain",Charset.forName("UTF-8")));
                String responseString = HttpClientUtils.executePost(url,multipartEntityBuilder.build());

                String message = JSON.parseObject(responseString,RegisterResponse.class).getMessage();
                Platform.runLater(()->{
                    registerVerifyController.getLabVerifyMessage().setText(message);
                    if (registerVerifyController.getLabVerifyMessage().getText().equals("注册成功")){
                        applicationContext.getBean(UserStatus.class).setRegisterResponse(null); //清空注册临时对象
                        registerVerifyController.getTimeSchedule().cancel();    //取消倒计时服务
                        try {
                            registerInputController.getVisualPane().setBottom(applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/authentication/register-success.fxml").load());
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
