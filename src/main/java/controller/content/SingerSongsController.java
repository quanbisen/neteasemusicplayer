package controller.content;

import controller.main.CenterController;
import controller.main.LeftController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import mediaplayer.MyMediaPlayer;
import model.LocalAlbum;
import model.LocalSinger;
import model.LocalSong;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.stereotype.Controller;
import util.SongUtils;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author super lollipop
 * @date 20-2-8
 */
@Controller
public class SingerSongsController {

    /**根容器*/
    @FXML
    private BorderPane root;

    @FXML
    private Label labSingerOrAlbumName;

    @FXML
    private HBox hBoxPlayAll;

    @FXML
    private Button btnPlayAll;

    @FXML
    private BorderPane tableViewContainer;

    @FXML
    private TableView<LocalSong> tableViewSong;

    @FXML
    private TableColumn<LocalSong, String> indexColumn;

    @FXML
    private TableColumn<LocalSong, String> nameColumn;

    @FXML
    private TableColumn<LocalSong, String> singerColumn;

    @FXML
    private TableColumn<LocalSong, String> albumColumn;

    @FXML
    private TableColumn<LocalSong, String> totalTimeColumn;

    @FXML
    private TableColumn<LocalSong, String> sizeColumn;

    /**注入中间容器的控制类*/
    @Resource
    private CenterController centerController;

    /**注入左侧标签tag的控制器类*/
    @Resource
    private LeftController leftController;

    @Resource
    private MyMediaPlayer myMediaPlayer;

    @Resource
    private LocalMusicContentController localMusicContentController;

    public void initialize(){
        //添加css名称.在CSS文件定制样式
        indexColumn.getStyleClass().add("indexColumn");
        totalTimeColumn.getStyleClass().add("totalTimeColumn");
        sizeColumn.getStyleClass().add("sizeColumn");
        nameColumn.getStyleClass().add("nameColumn");
        singerColumn.getStyleClass().add("singerColumn");
        albumColumn.getStyleClass().add("albumColumn");

        //列属性绑定
        indexColumn.setCellValueFactory(new PropertyValueFactory<>("index"));
        totalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("totalTime"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        singerColumn.setCellValueFactory(new PropertyValueFactory<>("singer"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));

        //设置表格列的宽度随这个borderPane的宽度而动态改变
        tableViewContainer.widthProperty().addListener((observable, oldValue, newValue) -> {
            totalTimeColumn.setPrefWidth((observable.getValue().doubleValue()-indexColumn.getMaxWidth())/6.5*1);
            sizeColumn.setPrefWidth((observable.getValue().doubleValue()-indexColumn.getMaxWidth())/6.5*1);
            nameColumn.setPrefWidth((observable.getValue().doubleValue()-indexColumn.getMaxWidth())/6.5*2);
            singerColumn.setPrefWidth((observable.getValue().doubleValue()-indexColumn.getMaxWidth())/6.5*1);
            albumColumn.setPrefWidth((observable.getValue().doubleValue()-indexColumn.getMaxWidth())/6.5*1.5);
        });

        if (((Label)localMusicContentController.getTabPane().getSelectionModel().getSelectedItem().getGraphic()).getText().equals("歌手")){ //如果是”歌手“tag
            LocalSinger selectedLocalSinger = localMusicContentController.getTableViewSinger().getSelectionModel().getSelectedItem();
            labSingerOrAlbumName.setText(selectedLocalSinger.getLabSinger().getText());  //设置显示歌手名称
            tableViewSong.setItems(SongUtils.getObservableLocalSongListBySingerOrAlbum(localMusicContentController.getTableViewSong().getItems(),selectedLocalSinger));
        }else if (((Label)localMusicContentController.getTabPane().getSelectionModel().getSelectedItem().getGraphic()).getText().equals("专辑")){   //如果是"专辑"tag
            LocalAlbum selectedLocalAlbum = localMusicContentController.getTableViewAlbum().getSelectionModel().getSelectedItem();
            labSingerOrAlbumName.setText(selectedLocalAlbum.getLabAlbum().getText());  //设置显示歌手名称
            tableViewSong.setItems(SongUtils.getObservableLocalSongListBySingerOrAlbum(localMusicContentController.getTableViewSong().getItems(),selectedLocalAlbum));
        }

    }


    /**“播放全部按钮的事件
     * @param mouseEvent”*/
    @FXML
    public void onClickedPlayAll(MouseEvent mouseEvent) throws ReadOnlyFileException, IOException, TagException, InvalidAudioFrameException, CannotReadException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            myMediaPlayer.playAll(tableViewSong.getItems());  //执行媒体播放其播放全部操作
        }
    }

    /**"返回"按钮的事件*/
    @FXML
    public void onClickedBack(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            centerController.getBorderPane().setCenter(leftController.getLocalMusicParent());
        }
    }

    /**表格的点击事件*/
    public void onClickedTableViewSong(MouseEvent mouseEvent) throws ReadOnlyFileException, IOException, TagException, InvalidAudioFrameException, CannotReadException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2){  //鼠标左双击
            myMediaPlayer.playAll(tableViewSong.getItems(),tableViewSong.getSelectionModel().getSelectedIndex());  //执行媒体播放其播放全部操作
        }
    }
}
