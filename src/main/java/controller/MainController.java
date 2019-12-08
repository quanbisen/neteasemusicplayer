package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import application.SpringFXMLLoader;
import util.WindowUtils;

import javax.annotation.Resource;


@Controller
public class MainController{

    /**根容器borderPane*/
    @FXML
    private BorderPane borderPane;

    @Resource
    private ConfigurableApplicationContext applicationContext;

    public BorderPane getBorderPane() {
        return borderPane;
    }

    public void initialize() throws IOException {

        if (WindowUtils.isWindowsPlatform()){   //如果是windows平台
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/main-top.fxml");
            borderPane.setTop(fxmlLoader.load());
            Platform.runLater(()->{
                Stage primaryStage = (Stage) borderPane.getScene().getWindow();
                primaryStage.initStyle(StageStyle.UNDECORATED);   //去掉Windows自带的标题栏
                WindowUtils.addResizable(primaryStage,860,570);  //为primaryStage添加自由缩放
                WindowUtils.addFixedCode(primaryStage);  //为primaryStage添加一些GUI的修复代码
                WindowUtils.addWindowsPlatformTaskBarIconifyBehavior();  //为primaryStage添加Windows平台显示窗体时单击任务栏图标可以最小化
            });
        }

    }
}
