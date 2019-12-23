package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import application.SpringFXMLLoader;
import util.WindowUtils;
import javax.annotation.Resource;

@Controller
public class MainController{

    /**根容器borderPane*/
    @FXML
    private BorderPane borderPane;

    /**注入Spring上下文对象*/
    @Resource
    private ApplicationContext applicationContext;

    public BorderPane getBorderPane() {
        return borderPane;
    }

    public void initialize() throws IOException {

        if (WindowUtils.isWindowsPlatform()){   //如果是windows平台,加载自定义设计的标题栏
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/main-top.fxml");
            borderPane.setTop(fxmlLoader.load());
        }

    }
}
