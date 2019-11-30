package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.springframework.stereotype.Controller;
import util.SpringFXMLLoader;

import javax.annotation.Resource;
import java.io.IOException;

@Controller
public class CenterController {
    /**中间的StackPane容器*/
    @FXML
    private StackPane stackPane;

    /**中间StackPane第0层的borderPane，我这里定义StackPane层数为0,1,2...*/
    @FXML
    private BorderPane borderPane;

    @Resource
    private MainController mainController;

    public StackPane getStackPane(){
        return stackPane;
    }

    public void initialize() throws IOException{
        borderPane.setLeft(this.getLeft());
        /*BorderPane borderPane = (BorderPane) tabPane.getParent();
        Pane pane = (Pane) borderPane.getCenter();
        tabPane.prefWidthProperty().bind(pane.widthProperty());
        tabPane.prefHeightProperty().bind(pane.heightProperty());*/
    }

    /**加载获取左侧标签项容器的函数*/
    private Node getLeft() throws IOException {
        FXMLLoader fxmlLoader = SpringFXMLLoader.getLoader();
        fxmlLoader.setLocation(this.getClass().getResource("/fxml/tabs-container.fxml"));
        return fxmlLoader.load();
    }
}
