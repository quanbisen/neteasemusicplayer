package controller.content;

import controller.main.BottomController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import mediaplayer.MyMediaPlayer;
import pojo.Singer;
import pojo.Song;
import model.PlayListSong;
import org.springframework.stereotype.Component;
import util.SongUtils;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author super lollipop
 * @date 19-12-10
 */
@Component
public class SearchResultContentController {

    /**显示搜索结果的容器TabPane*/
    @FXML
    private TabPane tabPaneSearchResult;

    /**查询进度指示器组件*/
    @FXML
    private ProgressIndicator progressIndicator;

    /**歌曲名称列组件*/
    @FXML
    private TableColumn<Song,String> columnName;

    /**歌曲歌手列组件*/
    @FXML
    private TableColumn<Song, Singer> columnSinger;

    /**歌曲专辑列组件*/
    @FXML
    private TableColumn<Song,String> columnAlbum;

    /**歌曲总时间列组件*/
    @FXML
    private TableColumn<Song,String> columnTotalTime;

    /**显示歌曲的表格组件*/
    @FXML
    private TableView<Song> tableViewSong;

    /**注入搜索输入页面的控制器*/
    @Resource
    private SearchInputContentController searchInputContentController;

    /**注入自定义媒体播放器*/
    @Resource
    private MyMediaPlayer myMediaPlayer;

    /**注入底部显示音乐进度条信息的控制器*/
    @Resource
    private BottomController bottomController;

    public TableView<Song> getTableViewSong() {
        return tableViewSong;
    }

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    public void initialize() {

        //宽度和高度绑定为父容器的宽度
        tabPaneSearchResult.prefWidthProperty().bind(((Region) searchInputContentController.getSearchInputContainer().getCenter()).widthProperty());
        tabPaneSearchResult.prefHeightProperty().bind(((Region) searchInputContentController.getSearchInputContainer().getCenter()).heightProperty());

        progressIndicator.setVisible(false);       //初始化加载指示器不可见

        //添加css名称.在CSS文件定制样式
        columnName.getStyleClass().add("nameColumn");
        columnSinger.getStyleClass().add("singerColumn");
        columnAlbum.getStyleClass().add("albumColumn");
        columnTotalTime.getStyleClass().add("totalTimeColumn");
        //列属性绑定
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnSinger.setCellValueFactory(param -> {
            StringBuilder singer = new StringBuilder();
            for (int i = 0; i < param.getValue().getSingerList().size(); i++) {
                singer.append(param.getValue().getSingerList().get(i).getName());
                if (i != param.getValue().getSingerList().size() -1){
                    singer.append("/");
                }
            }
            SimpleObjectProperty simpleObjectProperty = new SimpleObjectProperty(singer.toString());
            return simpleObjectProperty;
        });
        columnAlbum.setCellValueFactory(new PropertyValueFactory<>("albumName"));
        columnTotalTime.setCellValueFactory(new PropertyValueFactory<>("totalTime"));

        //设置表格列的初始化宽度
        columnName.setMinWidth(searchInputContentController.getSearchInputContainer().getWidth()/5*2);
        columnSinger.setMinWidth(searchInputContentController.getSearchInputContainer().getWidth()/5*1);
        columnAlbum.setMinWidth(searchInputContentController.getSearchInputContainer().getWidth()/5*1);
        columnTotalTime.setMinWidth(searchInputContentController.getSearchInputContainer().getWidth()/5*1);
        //设置表格列的宽度随这个borderPane的宽度而动态改变
        searchInputContentController.getSearchInputContainer().widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                System.out.println(newValue);
                columnName.setMinWidth(newValue.doubleValue()/5*2);
                columnSinger.setMinWidth(newValue.doubleValue()/5*1);
                columnAlbum.setMinWidth(newValue.doubleValue()/5*1);
                columnTotalTime.setMinWidth(newValue.doubleValue()/5*1);
            }
        });

    }


    /**单击表格歌曲的容器事件处理*/
    @FXML
    public void onClickedTableView(MouseEvent mouseEvent) throws Exception{
        if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2){  //鼠标双击执行
            if (myMediaPlayer.getPlayListSongs() == null){
                myMediaPlayer.setPlayListSongs(FXCollections.observableArrayList());
            }
            PlayListSong newPlaySong = SongUtils.toPlayListSong(tableViewSong.getSelectionModel().getSelectedItem());
            List<PlayListSong> playListSongs = myMediaPlayer.getPlayListSongs();
            if (playListSongs.size() == 0){
                playListSongs.add(newPlaySong);
            }else {
                playListSongs.add(myMediaPlayer.getCurrentPlayIndex()+1,newPlaySong);  //添加到播放列表后面
            }
            myMediaPlayer.setCurrentPlayIndex(playListSongs.indexOf(newPlaySong));    //更新当前播放的索引值
            myMediaPlayer.playSong();      //播放选中的歌曲
            bottomController.getLabPlayListCount().setText(String.valueOf(playListSongs.size())); //更新右下角歌单数量的显示文本 林
        }
    }
}


