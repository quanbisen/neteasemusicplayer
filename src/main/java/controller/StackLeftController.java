package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;

@Controller
public class StackLeftController {
    /**左侧标签的VBox容器*/
    @FXML
    private VBox vBoxTabContainer;
    Label label;

    public Label getLabel() {
        return label;
    }
}
