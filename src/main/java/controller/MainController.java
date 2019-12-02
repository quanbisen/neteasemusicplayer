package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

import org.springframework.stereotype.Controller;
import application.SpringFXMLLoader;


@Controller
public class MainController{

    /**根容器borderPane*/
    @FXML
    private BorderPane borderPane;

    public BorderPane getBorderPane() {
        return borderPane;
    }

}
