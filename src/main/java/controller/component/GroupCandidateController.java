package controller.component;

import controller.popup.ChoseGroupController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import service.CollectSongService;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 20-3-8
 */
@Controller
@Scope("prototype")
public class GroupCandidateController {

    @FXML
    private Label labGroupName;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private ChoseGroupController choseGroupController;

    public Label getLabGroupName() {
        return labGroupName;
    }

    /**单击其中一个候选"歌单"的事件*/
    @FXML
    public void onClickedGroupCandidate(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            choseGroupController.setChoseGroupName(labGroupName.getText());
            applicationContext.getBean(CollectSongService.class).start();
        }
    }
}
