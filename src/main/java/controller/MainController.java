package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

import org.springframework.stereotype.Controller;
import util.SpringFXMLLoader;


@Controller
public class MainController{

    @FXML
    private BorderPane borderPane;

    public BorderPane getBorderPane() {
        return borderPane;
    }

    public void initialize() throws IOException{
        borderPane.setTop(this.getTop());
        borderPane.setCenter(this.getCenter());
        borderPane.setBottom(this.getBottom());
    }



    /**加载窗体顶部的标题栏容器及组件函数*/
    private Node getTop() throws IOException {
        FXMLLoader fxmlLoader = SpringFXMLLoader.getLoader();  //使用自定义Spring工具获取一个设置了Spring代理的FXMLLoader对象
        fxmlLoader.setLocation(this.getClass().getResource("/fxml/main-top.fxml"));
        return fxmlLoader.load();
    }

    private Node getLeft() throws IOException{
        FXMLLoader fxmlLoader = SpringFXMLLoader.getLoader();  //使用自定义Spring工具获取一个设置了Spring代理的FXMLLoader对象
        fxmlLoader.setLocation(this.getClass().getResource("/fxml/tabs-container.fxml"));
        return fxmlLoader.load();
    }

    /**加载窗体中间StackPane容器及组件的函数*/
    private Node getCenter() throws IOException{
        FXMLLoader fxmlLoader = SpringFXMLLoader.getLoader();  //使用自定义Spring工具获取一个设置了Spring代理的FXMLLoader对象
        fxmlLoader.setLocation(this.getClass().getResource("/fxml/main-center.fxml"));
        return fxmlLoader.load();
    }

    /**加载窗体底部播放控制容器及组件函数*/
    private Node getBottom() throws IOException{
        FXMLLoader fxmlLoader = SpringFXMLLoader.getLoader();  //使用自定义Spring工具获取一个设置了Spring代理的FXMLLoader对象
        fxmlLoader.setLocation(this.getClass().getResource("/fxml/main-bottom.fxml"));
        return fxmlLoader.load();
    }

}
