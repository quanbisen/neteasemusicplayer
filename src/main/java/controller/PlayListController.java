package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Controller;

@Controller
public class PlayListController {

    @FXML
    private Label labCloseIcon;

    public void initialize(){

    }

    /**关闭图标按钮的鼠标事件*/
    @FXML
    public void onClickedCloseIcon(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){ //鼠标左击
            labCloseIcon.getScene().getWindow().hide();     //隐藏窗体
        }
    }
}
