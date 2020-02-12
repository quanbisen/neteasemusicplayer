package controller.dialog;

import controller.content.LocalMusicContentController;
import controller.main.MainController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.LocalAlbum;
import model.LocalSinger;
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

        if (resource!=null && !resource.equals("")){
            File file = new File(resource); //创建文件句柄
            file.delete();  //删除文件
            ObservableList<LocalSong> tableViewSongItems = localMusicContentController.getTableViewSong().getItems();
            localMusicContentController.getTableViewSong().getSelectionModel().clearSelection();   //清除选中状态
            //tableViewSinger part
            ObservableList<LocalSinger> tableViewSingerItems = localMusicContentController.getTableViewSinger().getItems();
            if (tableViewSingerItems != null && tableViewSingerItems.size() > 0){
                if (SongUtils.getSongCountBySinger(tableViewSongItems,selectedLocalSong.getSinger()) == 1){
                    tableViewSingerItems.remove(SongUtils.getIndex(tableViewSingerItems,selectedLocalSong.getSinger()));
                    char singerFirstPinYinHeadChar = Pinyin4jUtils.getFirstPinYinHeadChar(selectedLocalSong.getSinger());
                    int index = 0;
                    for (int i = 0; i < tableViewSingerItems.size(); i++) {
                        if (tableViewSingerItems.get(i).getLabSinger().getText().equals(String.valueOf(singerFirstPinYinHeadChar))){
                            index = i;
                            break;
                        }
                    }
                    if (index+1 > tableViewSingerItems.size()-1 || //越界或者
                            Pinyin4jUtils.getFirstPinYinHeadChar(tableViewSingerItems.get(index+1).getLabSinger().getText()) != singerFirstPinYinHeadChar){
                        tableViewSingerItems.remove(index);
                    }
                }else { //否则，更新歌手的数目-1
                    String songCountStr = tableViewSingerItems.get(SongUtils.getIndex(tableViewSingerItems,selectedLocalSong.getSinger())).getSongCount();
                    int songCount = Integer.parseInt(songCountStr.substring(0,songCountStr.indexOf("首")));
                    tableViewSingerItems.get(SongUtils.getIndex(tableViewSingerItems,selectedLocalSong.getSinger())).setSongCount(songCount-1 + "首");
                }
            }
            //tableViewAlbum part
            ObservableList<LocalAlbum> tableViewAlbumItems = localMusicContentController.getTableViewAlbum().getItems();
            if (tableViewAlbumItems != null && tableViewAlbumItems.size() > 0){
                int selectedIndex = SongUtils.getIndex(tableViewAlbumItems,selectedLocalSong.getAlbum());   //获取选中的歌曲的专辑在专辑表格的位置
                if (SongUtils.getSongCountByAlbum(tableViewSongItems,selectedLocalSong.getAlbum()) == 1){
                    tableViewAlbumItems.remove(selectedIndex);
                    char albumFirstPinYinHeadChar = Pinyin4jUtils.getFirstPinYinHeadChar(selectedLocalSong.getAlbum());
                    int index = 0;
                    for (int i = 0; i < tableViewAlbumItems.size(); i++) {
                        if (tableViewAlbumItems.get(i).getLabAlbum().getText().equals(String.valueOf(albumFirstPinYinHeadChar))){
                            index = i;
                            break;
                        }
                    }
                    if (index+1 > tableViewAlbumItems.size()-1 || //越界或者
                            Pinyin4jUtils.getFirstPinYinHeadChar(tableViewAlbumItems.get(index+1).getLabAlbum().getText()) != albumFirstPinYinHeadChar){
                        tableViewAlbumItems.remove(index);
                    }
                }else { //否则，更新歌手的数目-1
                    String songCountStr = tableViewAlbumItems.get(selectedIndex).getSongCount();
                    int songCount = Integer.parseInt(songCountStr.substring(0,songCountStr.indexOf("首")));
                    tableViewAlbumItems.get(selectedIndex).setSongCount(songCount-1 + "首");
                }
            }
            //tableViewSong part
            tableViewSongItems.remove(selectedLocalSong);    //GUI更新，移除选中的歌曲行
            /**判断是否需要移除字母的分类行 start*/
            char songFirstPinYinHeadChar = Pinyin4jUtils.getFirstPinYinHeadChar(selectedLocalSong.getName());    //获取选中的需要删除的歌曲的字母分类
            int index = 0;  //记录字母分类行的索引
            for (int i = 0; i < tableViewSongItems.size(); i++) {   //遍历找到对应的字母分类行索引
                if (tableViewSongItems.get(i).getName().equals(String.valueOf(songFirstPinYinHeadChar))){
                    index = i;
                    break;
                }
            }
            if (index+1 > tableViewSongItems.size()-1 || //越界或者
                    Pinyin4jUtils.getFirstPinYinHeadChar(tableViewSongItems.get(index+1).getName()) != songFirstPinYinHeadChar){
                tableViewSongItems.remove(index);
            }
            /**判断是否需要移除字母的分类行 end*/

            if (tableViewSongItems.size() == 0){    //如果表格已经没有内容行，设置tabPane面板不可见，隐藏
                localMusicContentController.getTabPane().getTabs().get(0).setText("0");
                localMusicContentController.getTabPane().getTabs().get(1).setText("0");
                localMusicContentController.getTabPane().getTabs().get(2).setText("0");
                tableViewSingerItems.clear();
                tableViewAlbumItems.clear();
                localMusicContentController.getBorderPane().setVisible(false);
            }else { //否则，可以更新歌曲数目的显示
                localMusicContentController.getTabPane().getTabs().get(0).setText(String.valueOf(SongUtils.getSongCount(tableViewSongItems)));  //更新歌曲数目统计
                localMusicContentController.getTabPane().getTabs().get(1).setText(String.valueOf(SongUtils.getSingerCount(tableViewSongItems)));
                localMusicContentController.getTabPane().getTabs().get(2).setText(String.valueOf(SongUtils.getAlbumCount(tableViewSongItems)));
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
