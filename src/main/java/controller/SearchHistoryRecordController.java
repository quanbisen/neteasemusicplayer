package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import util.XMLUtils;

import javax.annotation.Resource;

@Controller
@Scope("prototype")
public class SearchHistoryRecordController {

    @FXML
    private BorderPane oneHistoryContainer;

    @FXML
    private Label labRecordText;

    @FXML
    private Label labDelete;

    @Resource
    private SearchInputController searchInputController;

    public BorderPane getOneHistoryContainer() {
        return oneHistoryContainer;
    }

    public void setOneHistoryContainer(BorderPane oneHistoryContainer) {
        this.oneHistoryContainer = oneHistoryContainer;
    }

    public Label getLabRecordText() {
        return labRecordText;
    }

    public void setLabRecordText(Label labRecordText) {
        this.labRecordText = labRecordText;
    }

    public Label getLabDelete() {
        return labDelete;
    }

    public void setLabDelete(Label labDelete) {
        this.labDelete = labDelete;
    }

    @FXML
    public void onClickedClear(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            System.out.println(labRecordText.getText());
            searchInputController.getvBoxHistoryContainer().getChildren().remove(labDelete.getParent());    //GUI更新，移除本容器
            if (searchInputController.getSEARCH_HISTORY_FILE().exists()){   //如果存储的文件存在
                XMLUtils.removeOneRecord(searchInputController.getSEARCH_HISTORY_FILE(),"text",labRecordText.getText());    //移除本条记录
            }
        }
    }
}
