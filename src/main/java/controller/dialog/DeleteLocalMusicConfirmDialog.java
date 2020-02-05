package controller.dialog;

import controller.content.LocalMusicContentController;
import controller.main.MainController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.LocalSong;
import org.springframework.stereotype.Controller;
import util.Pinyin4jUtils;
import util.SongUtils;
import util.WindowUtils;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author super lollipop
 * @date 20-2-5
 */
@Controller
public class DeleteLocalMusicConfirmDialog {

    /**“取消”按钮组件*/
    @FXML
    private Button btnCancel;

    @Resource
    private LocalMusicContentController localMusicContentController;

    @Resource
    private MainController mainController;


    /**
     * "确定"按钮的事件处理
     */
    @FXML
    public void onClickedConfirm(ActionEvent actionEvent) {
        LocalSong selectedLocalSong = localMusicContentController.getTableViewSong().getSelectionModel().getSelectedItem();
        String resource = selectedLocalSong.getResource();
        String name = selectedLocalSong.getName();
        char character = Pinyin4jUtils.getFirstPinYinHeadChar(name);    //获取选中的需要删除的歌曲的字母分类
        if (resource!=null && !resource.equals("")){
            File file = new File(resource); //创建文件句柄
            file.delete();  //删除文件
            ObservableList<LocalSong> tableViewItems = localMusicContentController.getTableViewSong().getItems();
            tableViewItems.remove(selectedLocalSong);    //GUI更新，移除选中的歌曲行
            /**判断是否需要移除字母的分类行 start*/
            int index = 0;  //记录字母分类行的索引
            for (int i = 0; i < tableViewItems.size(); i++) {   //遍历找到对应的字母分类行索引
                if (tableViewItems.get(i).getName().equals(String.valueOf(character))){
                    index = i;
                    break;
                }
            }
            if (index+1 > tableViewItems.size()-1 || Pinyin4jUtils.getFirstPinYinHeadChar(tableViewItems.get(index+1).getName()) != character){
                tableViewItems.remove(index);
            }
            /**判断是否需要移除字母的分类行 end*/
            if (tableViewItems.size() == 0){    //如果表格已经没有内容行，设置tabPane面板不可见，隐藏
                localMusicContentController.getLabSongCount().setText("0");
                localMusicContentController.getBorderPane().setVisible(false);
            }else { //否则，可以更新歌曲数目的显示
                localMusicContentController.getLabSongCount().setText(String.valueOf(SongUtils.getSongCount(tableViewItems)));  //更新歌曲数目统计
            }
            this.onClickedCancel(actionEvent);  //隐藏dialog
            WindowUtils.toastInfo(mainController.getStackPane(),new Label("删除歌曲成功"));

        }
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
