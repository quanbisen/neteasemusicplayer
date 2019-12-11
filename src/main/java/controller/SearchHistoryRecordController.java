package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.springframework.stereotype.Controller;

@Controller
public class SearchHistoryRecordController {

    @FXML
    private BorderPane oneHistoryContainer;

    @FXML
    private Label labRecordText;

    @FXML
    private Label labDelete;

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
}
