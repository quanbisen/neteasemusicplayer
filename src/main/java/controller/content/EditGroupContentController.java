package controller.content;

import controller.main.LeftController;
import controller.main.MainController;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import pojo.Group;
import service.UpdateGroupService;
import javax.annotation.Resource;
import java.io.File;

@Controller
public class EditGroupContentController {

    @FXML
    private ImageView ivAlbum;

    @FXML
    private Label labChoseImageTips;

    @FXML
    private TextField tfGroupName;

    @FXML
    private Label labTips;

    @FXML
    private TextArea taDescription;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    /**存储歌单组的对象信息*/
    private Group group;

    @Resource
    private LeftController leftController;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private MainController mainController;

    private File choseImageFile;

    public ImageView getIvAlbum() {
        return ivAlbum;
    }

    public TextField getTfGroupName() {
        return tfGroupName;
    }

    public Label getLabTips() {
        return labTips;
    }

    public TextArea getTaDescription() {
        return taDescription;
    }

    public Group getGroup() {
        return group;
    }

    public File getChoseImageFile() {
        return choseImageFile;
    }

    public void initialize() throws Exception {
        if (choseImageFile != null){    //如果不为null，重置为null
            choseImageFile = null;
        }
        group = (Group) leftController.getContextMenuShownTab().getUserData();
        tfGroupName.setText(group.getName());
        taDescription.setText(group.getDescription());
        //设置专辑图片
        if (group.getImageURL() != null){
            Image image = new Image(group.getImageURL(),210,210,true,true);
            System.out.println(group);
            if (!image.isError()){
                ivAlbum.setImage(image);
            }else {
                ivAlbum.setImage(new Image("/image/DefaultAlbumImage_200.png",210,210,true,true));
            }
        }else {
            ivAlbum.setImage(new Image("/image/DefaultAlbumImage_200.png",210,210,true,true));
        }


        btnSave.setOpacity(0.8);
        btnSave.setMouseTransparent(true);

        tfGroupName.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (observable.getValue().trim().equals("")){
                labTips.setText("歌单名不能为空");
            }else if (!observable.getValue().equals(group.getName())){
                btnSave.setMouseTransparent(false);
                btnSave.setOpacity(1);
            }else {
                btnSave.setOpacity(0.8);
                btnSave.setMouseTransparent(true);
            }
        }));
        taDescription.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (!observable.getValue().trim().equals("") && !observable.getValue().equals(group.getDescription())){
                btnSave.setMouseTransparent(false);
                btnSave.setOpacity(1);
            }else {
                btnSave.setOpacity(0.8);
                btnSave.setMouseTransparent(true);
            }
            if (!taDescription.lookup(".scroll-bar:vertical").isDisable()){ //去除textArea的滚动条
                taDescription.lookup(".scroll-bar:vertical").setDisable(true);
            }
        }));
    }

    /**专辑封面的事件处理*/
    @FXML
    public synchronized void onClickedAlbumImage(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JPG","*.jpg"),
                    new FileChooser.ExtensionFilter("PNG","*.png")
            );
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            choseImageFile = fileChooser.showOpenDialog(ivAlbum.getScene().getWindow());
            //chose file here
            if (choseImageFile != null){
                if (choseImageFile.length() / 1024 / 1024 > 1){ //选择的文件大小大于1m
                    labChoseImageTips.setText("图片文件大于1M");
                    choseImageFile = null;
                }else {
                    //更新UI
                    ivAlbum.setImage(new Image("file:" + choseImageFile.getPath(),210,210,true,true));
                    btnSave.setMouseTransparent(false);
                    btnSave.setOpacity(1);
                }
            }
        }
    }

    /**“取消”按钮的事件处理*/
    @FXML
    public void onClickedCancel(ActionEvent actionEvent) {
        leftController.setSelectedTab(leftController.getSelectedTab());
        fireEvent(leftController.getSelectedTab());
    }

    private void fireEvent(Node node){
        Event.fireEvent(node, new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 5, true, true, true, true,
                true, true, true, true, true, true, null));
    }

    /**“保存”按钮的事件处理*/
    @FXML
    public void onClickedSave(ActionEvent actionEvent) {
        applicationContext.getBean(UpdateGroupService.class).start();   //启动更新
        onClickedCancel(actionEvent);
    }
}
