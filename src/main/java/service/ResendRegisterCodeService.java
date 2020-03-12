package service;

import com.alibaba.fastjson.JSON;
import javafx.concurrent.Task;
import mediaplayer.Config;
import mediaplayer.PlayerState;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import response.RegisterResponse;
import util.HttpClientUtils;

import javax.annotation.Resource;
import java.nio.charset.Charset;

/**
 * @author super lollipop
 * @date 20-2-16
 */
@Service
@Scope(value = "prototype")
public class ResendRegisterCodeService extends javafx.concurrent.Service<ScheduledCountDownService> {


    @Resource
    private ApplicationContext applicationContext;

    @Override
    protected Task<ScheduledCountDownService> createTask() {
        Task<ScheduledCountDownService> task = new Task<ScheduledCountDownService>() {
            @Override
            protected ScheduledCountDownService call() throws Exception {
                try {
                    //取出账号密码
                    RegisterResponse registerResponse = applicationContext.getBean(PlayerState.class).getRegisterResponse();
                    String email = registerResponse.getId();
                    String password = registerResponse.getPassword();
                    String url = applicationContext.getBean(Config.class).getUserURL() + "/sendAuthenticationCode";
                    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().addTextBody("email",email, ContentType.create("text/pain", Charset.forName("UTF-8"))).
                            addTextBody("password",password,ContentType.create("text/pain",Charset.forName("UTF-8")));
                    String responseString = HttpClientUtils.executePost(url,multipartEntityBuilder.build());
                    if (registerResponse.getMessage().equals("验证码发送成功")){
                        applicationContext.getBean(PlayerState.class).setRegisterResponse(JSON.parseObject(responseString,RegisterResponse.class));
                        ScheduledCountDownService scheduledCountDownService = applicationContext.getBean(ScheduledCountDownService.class);
                        scheduledCountDownService.setTime(registerResponse.getExpireSecond());
                        return scheduledCountDownService;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
        return task;
    }
}
