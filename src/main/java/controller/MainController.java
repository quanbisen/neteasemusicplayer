package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import util.SpringUtil;


@Controller
public class MainController{

    @FXML
    private BorderPane borderPane;

    public BorderPane getBorderPane() {
        return borderPane;
    }

    public void initialize() throws IOException{
        borderPane.setTop(this.getTop());
        borderPane.setBottom(this.getBottom());
        System.out.println("borderPane = " + borderPane);
    }

    /**加载窗体顶部的标题栏组件函数*/
    private Node getTop() throws IOException {
        FXMLLoader fxmlLoader = SpringUtil.getFXMLLoaderWithSpring();  //使用自定义Spring工具获取一个设置了Spring代理的FXMLLoader对象
        fxmlLoader.setLocation(MainController.class.getResource("/fxml/main-top.fxml"));
        return fxmlLoader.load();
    }

    /**加载窗体底部播放控制组件函数*/
    private Node getBottom() throws IOException{
        FXMLLoader fxmlLoader = SpringUtil.getFXMLLoaderWithSpring();
        fxmlLoader.setLocation(this.getClass().getResource("/fxml/main-bottom.fxml"));
        return fxmlLoader.load();
    }

/*    @FXML
    public void onClicked(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/config/application-context.xml");
        MainTopController mainTopController =(MainTopController) applicationContext.getBean("mainTopController");
        System.out.println(mainTopController);
    }*/
}
