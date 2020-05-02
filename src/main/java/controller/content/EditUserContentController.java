package controller.content;

import controller.main.LeftController;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import service.UpdateUserService;
import javax.annotation.Resource;
import java.io.File;

/**
 * @author super lollipop
 * @date 5/2/20
 */
@Controller
public class EditUserContentController {

    @FXML
    private Label labChoseImageTips;

    @FXML
    private ImageView ivImage;

    @FXML
    private TextField tfPetName;

    @FXML
    private Label labTips;

    @FXML
    private TextArea taDescription;

    @FXML
    private ComboBox<?> cbSex;

    @FXML
    private DatePicker dpBirthday;

    @FXML
    private ComboBox<?> cbProvince;

    @FXML
    private ComboBox<?> cbCity;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    @Resource
    private LeftController leftController;

    @Resource
    private ApplicationContext applicationContext;

    private File choseImageFile;

    public void initialize(){
        if (choseImageFile != null){    //如果不为null，重置为null
            choseImageFile = null;
        }
        btnSave.setOpacity(0.8);
        btnSave.setMouseTransparent(true);
    }

    /**用户头像图片的鼠标事件*/
    @FXML
    public void onClickedImage(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG","*.jpg"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG","*.png"));
            choseImageFile = fileChooser.showOpenDialog(ivImage.getScene().getWindow());
            if (choseImageFile != null){
                if (choseImageFile.length() / 1024 / 1024 <= 1){
                    ivImage.setImage(new Image("file:" + choseImageFile.getPath(), 210,210,true,true));
                    btnSave.setMouseTransparent(false);
                    btnSave.setOpacity(1);
                }else {
                    labChoseImageTips.setText("图片文件大于1M");
                    choseImageFile = null;
                }
            }
        }
    }

    /**“保存”按钮的事件*/
    @FXML
    public void onClickedSave(ActionEvent actionEvent) {
        applicationContext.getBean(UpdateUserService.class).start();   //启动更新
        onClickedCancel(actionEvent);
    }

    /**“取消”按钮的事件*/
    @FXML
    public void onClickedCancel(ActionEvent actionEvent) {
        leftController.setSelectedTab(leftController.getSelectedTab());
        Event.fireEvent(leftController.getSelectedTab(), new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 5, true, true, true, true,
                true, true, true, true, true, true, null));     //fire选中的tab的鼠标事件
    }

}
