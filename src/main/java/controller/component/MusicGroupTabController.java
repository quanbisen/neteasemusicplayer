package controller.component;

import controller.main.LeftController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
@Scope("prototype")
public class MusicGroupTabController {

    @FXML
    private HBox hBoxMusicGroup;

    /**注入左边放置标签的容器控制器*/
    @Resource
    LeftController leftController;


    @FXML
    public void onClickedMusicGroupTab(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            System.out.println(((Label)hBoxMusicGroup.getChildren().get(1)).getText());
            leftController.setSelectedTab(hBoxMusicGroup);
        }
    }
}
