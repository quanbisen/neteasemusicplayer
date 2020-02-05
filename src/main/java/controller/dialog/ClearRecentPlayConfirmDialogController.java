package controller.dialog;

import controller.main.MainController;
import controller.content.RecentPlayContentController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import mediaplayer.MyMediaPlayer;
import model.RecentSong;
import org.springframework.stereotype.Controller;
import util.WindowUtils;
import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 20-2-5
 */
@Controller
public class ClearRecentPlayConfirmDialogController {

    /**
     * “取消”按钮组件
     */
    @FXML
    private Button btnCancel;

    /**
     * 注入窗体根容器（BorderPane）的控制类
     */
    @Resource
    private MainController mainController;

    /**
     * 注入最近播放内容的控制类
     */
    @Resource
    private RecentPlayContentController recentPlayContentController;

    /**注入自定义的媒体播放类*/
    @Resource
    private MyMediaPlayer myMediaPlayer;

    /**
     * "确定"按钮的事件处理
     */
    @FXML
    public void onClickedConfirm(ActionEvent actionEvent) {
        if (myMediaPlayer.getRecentPlayStorageFile().exists()){ //如果存储文件存在，删除
            myMediaPlayer.getRecentPlayStorageFile().delete();
        }
        ObservableList<RecentSong> recentSongs = recentPlayContentController.getTableViewRecentPlaySong().getItems();
        if (recentSongs != null && recentSongs.size() > 0){
            recentSongs.clear();    //清除最近播放表格的内容
        }
        this.onClickedCancel(actionEvent);  //最后，调用“取消”按钮事件，关闭对话框
    }

    /**
     * “取消”按钮的事件处理
     */
    @FXML
    public void onClickedCancel(ActionEvent actionEvent) {
        btnCancel.getScene().getWindow().hide();  //隐藏窗口
        WindowUtils.releaseBorderPane(mainController.getBorderPane());
    }
}
