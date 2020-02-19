package controller.contextmenu;

import controller.main.LeftController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author super lollipop
 * @date 20-2-19
 */
@Controller
public class GroupTabContextMenuController {

    @Resource
    private LeftController leftController;

    @FXML
    public void onClickedPlayNow(ActionEvent actionEvent) {
    }

    /**”删除“歌单事件处理*/
    @FXML
    public void onClickedDeleteGroup(ActionEvent actionEvent) throws Exception {
        leftController.removeGroupTab(leftController.getContextMenuShowingTabName());
    }
}
