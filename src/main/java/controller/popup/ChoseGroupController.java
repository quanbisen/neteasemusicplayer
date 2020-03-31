package controller.popup;

import application.SpringFXMLLoader;
import controller.component.GroupCandidateController;
import controller.component.GroupIndicatorController;
import controller.main.MainController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mediaplayer.UserStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import pojo.Group;
import util.ImageUtils;
import util.StageUtils;
import util.WindowUtils;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author super lollipop
 * @date 20-3-8
 */
@Controller
public class ChoseGroupController {

    @FXML
    private BorderPane actualPane;

    @FXML
    private Label labCloseIcon;

    @FXML
    private VBox vWrapGroupCandidate;

    /**记录单击的歌单名称*/
    private String choseGroupName;

    @Resource
    private MainController mainController;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private GroupIndicatorController groupIndicatorController;

    public BorderPane getActualPane() {
        return actualPane;
    }

    public String getChoseGroupName() {
        return choseGroupName;
    }

    public Label getLabCloseIcon() {
        return labCloseIcon;
    }

    public void setChoseGroupName(String choseGroupName) {
        this.choseGroupName = choseGroupName;
    }

    public void initialize() throws IOException {
        List<Group> groupList = applicationContext.getBean(UserStatus.class).getUser().getGroupList();
        for (int i = 0; i < groupList.size(); i++) {
            addGroupCandidate(groupList.get(i));
        }
    }

    public void addGroupCandidate(Group group) throws IOException {
        FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/component/group-candidate.fxml");   //获取loader
        HBox hBoxGroupCandidate = fxmlLoader.load();    //加载容器组件
        GroupCandidateController groupCandidateController = fxmlLoader.getController(); //获取控制器
        groupCandidateController.getLabGroupName().setText(group.getName()); //设置显示歌单名称的Label组件文本
        if (group.getImageURL() != null){        //如果存在图片URL
            Image image = new Image(group.getImageURL(),50,50,true,true);    //创建图片资源
            if (!image.isError()){  //如果没发生错误.
                groupCandidateController.getLabGroupName().setGraphic(ImageUtils.createImageView(image,50,50)); //设置图片
            }
        }
        vWrapGroupCandidate.getChildren().add(hBoxGroupCandidate);
    }

    @FXML
    public void onClickedCloseIcon(MouseEvent mouseEvent) {
        labCloseIcon.getScene().getWindow().hide();
        WindowUtils.releaseBorderPane(mainController.getBorderPane());
    }

    @FXML
    public void onClickedCreateMusicGroup(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            actualPane.setOpacity(0.4); //设置不透明度
            actualPane.setMouseTransparent(true);   //设置不响应鼠标事件
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/popup/create-musicgroup.fxml");  //加载添加音乐歌单的fxml文件
            Stage stage = ((Stage)labCloseIcon.getScene().getWindow());              //获取窗体的stage对象
            Stage createMusicGroupStage = StageUtils.getStage(stage,fxmlLoader.load());  //使用自定义工具获取Stage对象
            StageUtils.synchronizeCenter(stage,createMusicGroupStage);   //设置createMusicGroupStage对象居中到primaryStage
            createMusicGroupStage.showAndWait();  //显示并且等待
        }
    }
}
