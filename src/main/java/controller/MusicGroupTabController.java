package controller;

import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class MusicGroupTabController {

    @FXML
    private HBox hBoxMusicGroup;

    /**注入左边放置标签的容器控制器*/
    @Resource
    TabsController tabsController;


    @FXML
    public void onClickedMusicGroupTab(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            tabsController.setSelectedTab(hBoxMusicGroup);
        }
    }
}
