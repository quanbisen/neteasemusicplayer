package controller.contextmenu;

import application.SpringFXMLLoader;
import controller.main.CenterController;
import controller.main.LeftController;
import controller.main.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import service.DeleteGroupService;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author super lollipop
 * @date 20-2-19
 */
@Controller
public class GroupTabContextMenuController {

    @Resource
    private LeftController leftController;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private CenterController centerController;

    @FXML
    public void onClickedPlayNow(ActionEvent actionEvent) {
    }

    /**"编辑"歌单事件处理*/
    @FXML
    public void onClickedEdit(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/content/edit-group-content.fxml");
        leftController.resetSelectedTab();
        centerController.getBorderPane().setCenter(fxmlLoader.load());
    }

    /**”删除“歌单事件处理*/
    @FXML
    public void onClickedDeleteGroup(ActionEvent actionEvent) throws Exception {
        applicationContext.getBean(DeleteGroupService.class).restart();
    }


}
