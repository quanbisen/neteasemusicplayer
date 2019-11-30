package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.springframework.stereotype.Controller;

/**
 * @author super lollipop
 * @date 19-11-30
 */
@Controller
public class AddMusicGroupController{

    /**“新建”按钮组件*/
    @FXML
    private Button btnCreate;

    /**"取消"按钮组件*/
    @FXML
    private Button btnCancel;

    /**初始化函数，自动调用，以前需要实现接口Initializable,从JavaFX2.0开始就自动会调用了，不需要实现接口*/
    public void initialize(){

    }
}
