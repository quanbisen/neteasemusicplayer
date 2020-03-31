package controller.contextmenu;

import application.SpringFXMLLoader;
import controller.content.LocalMusicContentController;
import controller.main.BottomController;
import controller.main.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import mediaplayer.MyMediaPlayer;
import model.LocalSong;
import model.PlayListSong;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import util.SongUtils;
import util.StageUtils;
import util.WindowUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Controller
public class LocalSongContextMenuController {

    @Resource
    private LocalMusicContentController localMusicContentController;

    @Resource
    private MainController mainController;

    @Resource
    private MyMediaPlayer myMediaPlayer;

    @Resource
    private BottomController bottomController;

    @Resource
    private ApplicationContext applicationContext;


    public void initialize(){

    }

    /**“下一首播放”contextmenu事件处理*/
    @FXML
    public void onClickedNextPlay(ActionEvent actionEvent) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        LocalSong selectedLocalSong = localMusicContentController.getTableViewSong().getSelectionModel().getSelectedItem(); //获取表格选中的歌曲
        PlayListSong playListSong = SongUtils.toPlayListSong(selectedLocalSong);    //转变成播放列表歌曲模型对象
        myMediaPlayer.addToPlayList(playListSong);
    }

    /**“删除文件”contextmenu事件处理*/
    @FXML
    public void onClickedDelete(ActionEvent actionEvent) throws IOException {

        FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/dialog/delete-localmusic-confirm-dialog.fxml");  //获取被Spring工厂接管的FXMLLoader对象
        Stage deleteConfirmDialog = StageUtils.getStage((Stage) localMusicContentController.getBorderPane().getScene().getWindow(),fxmlLoader.load());

        StageUtils.synchronizeCenter((Stage) localMusicContentController.getBorderPane().getScene().getWindow(),deleteConfirmDialog);   //设置deleteConfirmDialog对象居中到primaryStage
        WindowUtils.blockBorderPane(mainController.getBorderPane());         //设置borderPane不响应鼠标事件和改变透明度
        deleteConfirmDialog.showAndWait();  //显示并且等待
    }

    /**"收藏"contextmenu事件处理*/
    @FXML
    public void onClickedCollection(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/popup/chose-group.fxml");  //获取被Spring工厂接管的FXMLLoader对象
        Stage choseGroupStage = StageUtils.getStage((Stage) localMusicContentController.getBorderPane().getScene().getWindow(),fxmlLoader.load());

        StageUtils.synchronizeCenter((Stage) localMusicContentController.getBorderPane().getScene().getWindow(),choseGroupStage);   //设置choseGroupStage对象居中到primaryStage
        WindowUtils.blockBorderPane(mainController.getBorderPane());         //设置borderPane不响应鼠标事件和改变透明度
        choseGroupStage.showAndWait();  //显示并且等待
    }
}
