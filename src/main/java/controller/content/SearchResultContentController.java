package controller.content;

import controller.content.SearchInputContentController;
import controller.main.BottomController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import mediaplayer.MyMediaPlayer;
import model.OnlineSong;
import model.PlayListSong;
import org.springframework.stereotype.Component;
import util.SongUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    /**"歌曲"的HBox容器*/
    @FXML
    private HBox hBoxSong;

    /**"歌手"的HBox容器*/
    @FXML
    private HBox hBoxSinger;

    /**"专辑"的HBox容器*/
    @FXML
    private HBox hBoxAlbum;

    /**装标签的集合tabList*/
    private List<HBox> tabList;

    /**查询进度指示器组件*/
    @FXML
    private ProgressIndicator progressIndicator;

    /**歌曲名称列组件*/
    @FXML
    private TableColumn<OnlineSong,String> nameColumn;

    /**歌曲歌手列组件*/
    @FXML
    private TableColumn<OnlineSong,String> singerColumn;

    /**歌曲专辑列组件*/
    @FXML
    private TableColumn<OnlineSong,String> albumColumn;

    /**歌曲总时间列组件*/
    @FXML
    private TableColumn<OnlineSong,String> totalTimeColumn;

    /**显示歌曲的表格组件*/
    @FXML
    private TableView tableViewSong;

    /**表格tableViewSong的容器BorderPane*/
    @FXML
    private BorderPane borderPane;

    /**注入搜索输入页面的控制器*/
    @Resource
    private SearchInputContentController searchInputContentController;

    /**注入自定义媒体播放器*/
    @Resource
    private MyMediaPlayer myMediaPlayer;

    /**注入底部显示音乐进度条信息的控制器*/
    @Resource
    private BottomController bottomController;

    public TableView getTableViewSong() {
        return tableViewSong;
    }

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    public void initialize() {
        tabList = new ArrayList<>();
        tabList.add(hBoxSong);
        tabList.add(hBoxSinger);
        tabList.add(hBoxAlbum);
        this.setSelectedTab(hBoxSong);
        //宽度和高度绑定为父容器的宽度
        tabPaneSearchResult.prefWidthProperty().bind(((Region) searchInputContentController.getSearchInputContainer().getCenter()).widthProperty());
        tabPaneSearchResult.prefHeightProperty().bind(((Region) searchInputContentController.getSearchInputContainer().getCenter()).heightProperty());

        progressIndicator.setVisible(false);       //初始化加载指示器不可见

        //添加css名称.在CSS文件定制样式
        nameColumn.getStyleClass().add("nameColumn");
        singerColumn.getStyleClass().add("singerColumn");
        albumColumn.getStyleClass().add("albumColumn");
        totalTimeColumn.getStyleClass().add("totalTimeColumn");
        //列属性绑定
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        singerColumn.setCellValueFactory(new PropertyValueFactory<>("singer"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
        totalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("totalTime"));

        //设置表格列的初始化宽度
        nameColumn.setMinWidth(searchInputContentController.getSearchInputContainer().getWidth()/5*2);
        singerColumn.setMinWidth(searchInputContentController.getSearchInputContainer().getWidth()/5*1);
        albumColumn.setMinWidth(searchInputContentController.getSearchInputContainer().getWidth()/5*1);
        totalTimeColumn.setMinWidth(searchInputContentController.getSearchInputContainer().getWidth()/5*1);
        //设置表格列的宽度随这个borderPane的宽度而动态改变
        searchInputContentController.getSearchInputContainer().widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                System.out.println(newValue);
                nameColumn.setMinWidth(newValue.doubleValue()/5*2);
                singerColumn.setMinWidth(newValue.doubleValue()/5*1);
                albumColumn.setMinWidth(newValue.doubleValue()/5*1);
                totalTimeColumn.setMinWidth(newValue.doubleValue()/5*1);
            }
        });

    }

    /**单击"歌曲"标签的事件处理*/
    @FXML
    public void onClickedHBoxSong(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            this.setSelectedTab(hBoxSong);
        }
    }



    /**单击"歌手"标签的事件处理*/
    @FXML
    public void onClickedHBoxSinger(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            this.setSelectedTab(hBoxSinger);
        }
    }

    /**单击"专辑"标签的事件处理*/
    @FXML
    public void onClickedHBoxAlbum(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            this.setSelectedTab(hBoxAlbum);
        }
    }

    /**设置选择的标签背景颜色的函数*/
    private void setSelectedTab(HBox selectedTab){
        //首先重置所有的标签的背景颜色，我这里的HBox标签背景颜色是由另外一个HBox包裹做背景颜色显示的，所以需要getParent，设置parent的样式
        for (HBox hBoxTab:tabList){
            hBoxTab.getStyleClass().remove("selectedHBox");  //移除parent的css类名
        }
        //然后给当前选中的标签的parent容器添加css类名
        selectedTab.getStyleClass().add("selectedHBox");
    }

    /**单击表格存储歌曲的容器事件处理*/
    @FXML
    public void onClickedTableView(MouseEvent mouseEvent) throws Exception{
        if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2){  //鼠标双击执行
            if (myMediaPlayer.getPlayListSongs() == null){
                myMediaPlayer.setPlayListSongs(FXCollections.observableArrayList());
            }
            PlayListSong newPlaySong = SongUtils.toPlayListSong((OnlineSong) tableViewSong.getSelectionModel().getSelectedItem());
            ObservableList<PlayListSong> playListSongs = myMediaPlayer.getPlayListSongs();
            if (playListSongs.size() == 0){
                playListSongs.add(newPlaySong);
            }else {
                playListSongs.add(myMediaPlayer.getCurrentPlayIndex()+1,newPlaySong);  //添加到播放列表后面
            }
            myMediaPlayer.setCurrentPlayIndex(playListSongs.indexOf(newPlaySong));    //更新当前播放的索引值
            myMediaPlayer.playSong(playListSongs.get(myMediaPlayer.getCurrentPlayIndex()));      //播放选中的歌曲
            bottomController.getLabPlayListCount().setText(String.valueOf(playListSongs.size())); //更新右下角歌单数量的显示文本 林
        }
    }
}


