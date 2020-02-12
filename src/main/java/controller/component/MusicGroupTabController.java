package controller.component;

import controller.main.CenterController;
import controller.main.LeftController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import javax.annotation.Resource;

@Controller
@Scope("prototype")
public class MusicGroupTabController {

    /**歌单tag的容器*/
    @FXML
    private HBox hBoxMusicGroup;

    /**歌单tag的图标*/
    @FXML
    public ImageView ivMusicGroupIcon;

    /**显示歌单名称的Label组件*/
    @FXML
    public Label labGroupName;

    /**注入左边放置标签的容器控制器*/
    @Resource
    private LeftController leftController;

    @Resource
    private CenterController centerController;

    public HBox getHBoxMusicGroup() {
        return hBoxMusicGroup;
    }

    public ImageView getIvMusicGroupIcon() {
        return ivMusicGroupIcon;
    }

    public Label getLabGroupName() {
        return labGroupName;
    }

    @FXML
    public void onClickedMusicGroupTab(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            leftController.setSelectedTab(hBoxMusicGroup);  //设置当前选中的tab
            if(labGroupName.getText().equals("我喜欢的音乐")){
                centerController.getBorderPane().setCenter(new Label("我喜欢的音乐"));
            }else {
                centerController.getBorderPane().setCenter(new Label(labGroupName.getText()));
            }
        }
    }
}
