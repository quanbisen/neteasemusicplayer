package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import util.SpringUtil;

@Controller
public class MainController{

    @FXML
    private BorderPane borderPane;

    public void initialize() throws IOException{
        borderPane.setTop(this.getTop());
    }

    /**加载窗体顶部的标题栏组件函数*/
    private Node getTop() throws IOException {
        FXMLLoader fxmlLoader = SpringUtil.getFXMLLoaderWithSpring();  //使用自定义Spring工具获取一个设置了Spring代理的FXMLLoader对象
        fxmlLoader.setLocation(MainController.class.getResource("/fxml/main-top.fxml"));
        return fxmlLoader.load();
    }

/*    @FXML
    public void onClicked(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/config/application-context.xml");
        MainTopController mainTopController =(MainTopController) applicationContext.getBean("mainTopController");
        System.out.println(mainTopController);
    }*/
}
