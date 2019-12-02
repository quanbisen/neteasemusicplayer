package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.springframework.stereotype.Controller;
import application.SpringFXMLLoader;

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

    public BorderPane getBorderPane(){ return borderPane; }

}
