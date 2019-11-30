package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class CenterController {
    @FXML
    private TabPane tabPane;
    @Resource
    private MainController mainController;
    public void initialize(){
        /*BorderPane borderPane = (BorderPane) tabPane.getParent();
        Pane pane = (Pane) borderPane.getCenter();
        tabPane.prefWidthProperty().bind(pane.widthProperty());
        tabPane.prefHeightProperty().bind(pane.heightProperty());*/
    }
}
