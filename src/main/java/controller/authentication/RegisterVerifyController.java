package controller.authentication;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import service.ScheduledCountDownService;
import service.HandleRegisterService;
import service.ResendRegisterCodeService;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 20-2-15
 */
@Controller
public class RegisterVerifyController {

    @FXML
    private TextField tfCode;

    @FXML
    private Label labTimeOrResend;

    @FXML
    private ProgressIndicator verifyProgressIndicator;

    @FXML
    private Label labVerifyMessage;

    @Resource
    private ApplicationContext applicationContext;

    /**定时服务*/
    private ScheduledCountDownService timeSchedule;

    public TextField getTfCode() {
        return tfCode;
    }

    public Label getLabVerifyMessage() {
        return labVerifyMessage;
    }

    public ScheduledCountDownService getTimeSchedule() {
        return timeSchedule;
    }

    public Label getLabTimeOrResend() {
        return labTimeOrResend;
    }

    public void setLabTimeOrResend(Label labTimeOrResend) {
        this.labTimeOrResend = labTimeOrResend;
    }

    public void initialize(){
        verifyProgressIndicator.setVisible(false);    //初始化不可见

        /**启动倒计时定时服务*/
        timeSchedule = applicationContext.getBean(ScheduledCountDownService.class);
        timeSchedule.setPeriod(Duration.seconds(1));
        timeSchedule.start();


        /*handleRegisterService.setOnSucceeded(event -> {
            Register register = handleRegisterService.getValue();
            applicationContext.getBean(Config.class).setRegister(register); //更新register对象
            System.out.println(register.getCreateTime());
            System.out.println((register.getCreateTime().getTime() / 1000) - (new Date().getTime() / 1000) + 60);

        });*/
    }

    /**“确定”按钮的事件处理*/
    public void onClickedConfirm(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY && !tfCode.getText().equals("")){
            HandleRegisterService handleRegisterService = applicationContext.getBean(HandleRegisterService.class);
            verifyProgressIndicator.visibleProperty().bind(handleRegisterService.runningProperty());
            handleRegisterService.start();
        }
    }

    @FXML
    public void onClickedLabelTimeOrResend(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY && labTimeOrResend.getText().equals("重发")){
            //切换样式选择器名更换显示样式
            labTimeOrResend.getStyleClass().remove("labResend");
            labTimeOrResend.getStyleClass().add("labTime");
            //清空原先输入的code
            tfCode.setText("");
            //获取重发验证码的服务，并启动服务
            ResendRegisterCodeService resendRegisterCodeService = applicationContext.getBean(ResendRegisterCodeService.class);
            verifyProgressIndicator.visibleProperty().bind(resendRegisterCodeService.runningProperty());
            resendRegisterCodeService.setOnSucceeded(event -> {
                timeSchedule = resendRegisterCodeService.getValue();
                timeSchedule.setPeriod(Duration.seconds(1));
                timeSchedule.start();
            });
            resendRegisterCodeService.start();
        }
    }
}
