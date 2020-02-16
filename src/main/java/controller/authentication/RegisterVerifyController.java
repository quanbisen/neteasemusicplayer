package controller.authentication;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Controller;

/**
 * @author super lollipop
 * @date 20-2-15
 */
@Controller
public class RegisterVerifyController {

    @FXML
    private Button btnConfirm;

    @FXML
    private TextField tfCode;

    @FXML
    private Label labTime;

    @FXML
    private ProgressIndicator progressIndicator;

    public Button getBtnConfirm() {
        return btnConfirm;
    }

    public void setBtnConfirm(Button btnConfirm) {
        this.btnConfirm = btnConfirm;
    }

    public TextField getTfCode() {
        return tfCode;
    }

    public void setTfCode(TextField tfCode) {
        this.tfCode = tfCode;
    }

    public Label getLabTime() {
        return labTime;
    }

    public void setLabTime(Label labTime) {
        this.labTime = labTime;
    }

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    public void setProgressIndicator(ProgressIndicator progressIndicator) {
        this.progressIndicator = progressIndicator;
    }

    public void initialize(){
        progressIndicator.setVisible(false);    //初始化不可见
    }

    /**“确定”按钮的事件处理*/
    public void onClickedConfirm(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY && !tfCode.getText().equals("")){

        }
    }
}
