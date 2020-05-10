package service;

import controller.user.RegisterVerifyController;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
@Scope("singleton")
public class ScheduledCountDownService extends ScheduledService<Number> {

    private int time;

    @Resource
    private RegisterVerifyController registerVerifyController;

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    protected Task<Number> createTask() {

        Task<Number> task=new Task<Number>() {

            @Override
            protected void updateValue(Number value) {
                super.updateValue(value);
                if (value.intValue() <= 0){
                    ScheduledCountDownService.this.cancel();
                    System.out.println("任务取消");
                    Platform.runLater(()->{
                        registerVerifyController.getLabTimeOrResend().getStyleClass().remove("labTime");
                        registerVerifyController.getLabTimeOrResend().getStyleClass().add("labResend");
                        registerVerifyController.getLabTimeOrResend().setText("重发");
                    });
                }
            }

            @Override
            protected Number call() throws Exception {
                time--;
                System.out.println(time);
                Platform.runLater(()->{
                    registerVerifyController.getLabTimeOrResend().setText(String.valueOf(time));
                });
                return time;
            }
        };
        return task;
    }
}
