package controller.component;

import application.SpringFXMLLoader;
import controller.main.CenterController;
import controller.main.MainController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import mediaplayer.Config;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import util.StageUtils;
import util.WindowUtils;
import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author super lollipop
 * @date 20-2-12
 */
@Controller
public class MusicGroupIndicatorController {

    @FXML
    private ImageView ivAddMusicGroupIcon;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private CenterController centerController;

    @Resource
    private MainController mainController;

    /**单击添加歌单图标事件处理*/
    @FXML
    public void onClickedCreateMusicGroup(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            if (applicationContext.getBean(Config.class).getUser() == null){   //判断用户是否登录过了。
                WindowUtils.toastInfo(centerController.getStackPane(),new Label("请先登录！"));
            }
            else{
                FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/popup/create-musicgroup.fxml");  //加载添加音乐歌单的fxml文件
                Stage primaryStage = ((Stage)ivAddMusicGroupIcon.getScene().getWindow());              //获取主窗体的stage对象primaryStage
                Stage createMusicGroupStage = StageUtils.getStage(primaryStage,fxmlLoader.load());  //使用自定义工具获取Stage对象
                StageUtils.synchronizeCenter(primaryStage,createMusicGroupStage);   //设置createMusicGroupStage对象居中到primaryStage
                WindowUtils.blockBorderPane(mainController.getBorderPane());         //设置borderPane不响应鼠标事件和改变透明度
                createMusicGroupStage.showAndWait();  //显示并且等待
            }
        }
    }
}
