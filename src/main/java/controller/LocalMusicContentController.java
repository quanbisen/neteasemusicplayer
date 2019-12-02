package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import application.SpringFXMLLoader;
import util.StageUtils;
import util.WindowUtils;
import javax.annotation.Resource;
import java.io.IOException;

@Controller
public class LocalMusicContentController {

    /**“选择目录”的HBox容器*/
    @FXML
    private HBox hBoxChoseFolder;

    /**注入窗体根容器（BorderPane）的控制类*/
    @Resource
    MainController mainController;

    /**注入window工具类*/
    @Resource
    private WindowUtils windowUtils;

    /**注入Spring上下文类*/
    @Resource
    private ApplicationContext applicationContext;

    /**注入舞台工具*/
    @Resource
    private StageUtils stageUtils;

    /**“选择目录”按钮按下事件处理*/
    @FXML
    public void onClickedChoseFolder(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){  //鼠标左击
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/chose-folder.fxml");  //获取被Spring工厂接管的FXMLLoader对象
            Stage choseFolderStage = stageUtils.getStage((Stage) hBoxChoseFolder.getScene().getWindow(),fxmlLoader.load());
            stageUtils.syncCenter((Stage) hBoxChoseFolder.getScene().getWindow(),choseFolderStage);   //设置addMusicGroupStage对象居中到primaryStage
            windowUtils.blockBorderPane(mainController.getBorderPane());         //设置borderPane不响应鼠标事件和改变透明度
            choseFolderStage.showAndWait();  //显示并且等待
        }
    }
}
