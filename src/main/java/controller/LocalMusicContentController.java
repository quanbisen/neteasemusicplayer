package controller;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import mediaplayer.MyMediaPlayer;
import mediaplayer.PlayMode;
import model.Song;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import application.SpringFXMLLoader;
import service.LoadSongService;
import util.StageUtils;
import util.WindowUtils;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class LocalMusicContentController {

    /**本地音乐中间面板的根容器*/
    @FXML
    private TabPane tabPane;

    /**本地音乐中间面板的根容器tabPane的Tab1*/
    @FXML
    private BorderPane localMusicContentContainer;

    /**根容器的中间内容容器*/
    @FXML
    public BorderPane borderPane;

    /**歌曲名称列组件*/
    @FXML
    private TableColumn<Song,String> nameColumn;

    /**歌曲歌手列组件*/
    @FXML
    private TableColumn<Song,String> singerColumn;

    /**歌曲专辑列组件*/
    @FXML
    private TableColumn<Song,String> albumColumn;

    /**歌曲总时间列组件*/
    @FXML
    private TableColumn<Song,String> totalTimeColumn;

    /**歌曲大小列组件*/
    @FXML
    private TableColumn<Song,String> sizeColumn;

    /**“选择目录”的HBox容器*/
    @FXML
    private HBox hBoxChoseFolder;

    /**"歌曲"的HBox容器*/
    @FXML
    private HBox hBoxSong;

    /**"歌手"的HBox容器*/
    @FXML
    private HBox hBoxSinger;

    /**"专辑"的HBox容器*/
    @FXML
    private HBox hBoxAlbum;

    /**"歌曲"的计数Label*/
    @FXML
    private Label labSongCount;

    /**"歌曲"的计数Label容器*/
    @FXML
    private Label labSingerCount;

    /**"歌曲"的计数Label容器*/
    @FXML
    private Label labAlbumCount;

    /**装标签的集合tabList*/
    private List<HBox> tabList;

    /**显示进度的指示器*/
    @FXML
    private ProgressIndicator progressIndicator;

    /**存放显示歌曲的表格*/
    @FXML
    private TableView<Song> tableViewSong;

    /**注入窗体根容器（BorderPane）的控制类*/
    @Resource
    MainController mainController;

    /**注入Spring上下文类*/
    @Resource
    private ApplicationContext applicationContext;

    /**注入“选择目录”舞台的controller*/
    @Resource
    private ChoseFolderController choseFolderController;

    /**注入播放器*/
    @Resource
    private MyMediaPlayer myMediaPlayer;

    /**注入窗体底部进度条的控制器*/
    @Resource
    private BottomController bottomController;

    public TabPane getTabPane() {
        return tabPane;
    }

    public TableView<Song> getTableViewSong() {
        return tableViewSong;
    }

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    public Label getLabSongCount() {
        return labSongCount;
    }

    public BorderPane getBorderPane() {
        return borderPane;
    }

    public void initialize() throws Exception {
        tabList = new ArrayList<>();
        tabList.add(hBoxSong);
        tabList.add(hBoxSinger);
        tabList.add(hBoxAlbum);

        progressIndicator.setVisible(false);   //初始化进度指示器为不可见
        borderPane.setVisible(false);   //初始化不可见，在service中加载歌曲后控制可见性

        this.setSelectedTab(hBoxSong);  //设置初始选中为格式标签
        //添加css名称.在CSS文件定制样式
        nameColumn.getStyleClass().add("nameColumn");
        singerColumn.getStyleClass().add("singerColumn");
        albumColumn.getStyleClass().add("albumColumn");
        totalTimeColumn.getStyleClass().add("totalTimeColumn");
        sizeColumn.getStyleClass().add("sizeColumn");
        //列属性绑定
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        singerColumn.setCellValueFactory(new PropertyValueFactory<>("singer"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
        totalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("totalTime"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));

        //关闭表格"头"列的左右拖拽移动重新排列行为
        tableViewSong.widthProperty().addListener((source, oldWidth, newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tableViewSong.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((observable, oldValue, newValue) -> header.setReordering(false));
        });

        //设置表格列的宽度随这个borderPane的宽度而动态改变
        borderPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            nameColumn.setPrefWidth(newValue.doubleValue()/6.5*2);
            singerColumn.setPrefWidth(newValue.doubleValue()/6.5*1);
            albumColumn.setPrefWidth(newValue.doubleValue()/6.5*1.5);
            totalTimeColumn.setPrefWidth(newValue.doubleValue()/6.5*1);
            sizeColumn.setPrefWidth(newValue.doubleValue()/6.5*1);
        });

        /**加载歌曲服务*/
        LoadSongService loadSongService = applicationContext.getBean(LoadSongService.class);  //获取服务对象
        tableViewSong.itemsProperty().bind(loadSongService.valueProperty());  //搜搜结果显示表格的内容绑定
        progressIndicator.visibleProperty().bind(loadSongService.runningProperty());
        loadSongService.start();  //启动服务

//        /*******Fixed some resize bug here.*/
//        localMusicContentContainer.setCursor(Cursor.DEFAULT);
//        /*******Fixed some resize bug here.*/
    }

    /**“选择目录”按钮按下事件处理*/
    @FXML
    public void onClickedChoseFolder(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){  //鼠标左击
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/chose-musicfolder.fxml");  //获取被Spring工厂接管的FXMLLoader对象
            Stage choseFolderStage = StageUtils.getStage((Stage) hBoxChoseFolder.getScene().getWindow(),fxmlLoader.load());

            StageUtils.synchronizeCenter((Stage) hBoxChoseFolder.getScene().getWindow(),choseFolderStage);   //设置addMusicGroupStage对象居中到primaryStage
            WindowUtils.blockBorderPane(mainController.getBorderPane());         //设置borderPane不响应鼠标事件和改变透明度

            choseFolderStage.showAndWait();  //显示并且等待
        }
    }

    /**单击"歌曲"标签的事件处理*/
    public void onClickedHBoxSong(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            this.setSelectedTab(hBoxSong);
        }
    }



    /**单击"歌手"标签的事件处理*/
    public void onClickedHBoxSinger(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            this.setSelectedTab(hBoxSinger);
        }
    }

    /**单击"专辑"标签的事件处理*/
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
            myMediaPlayer.playLocal(tableViewSong.getSelectionModel().getSelectedItem());      //播放选中的歌曲
            myMediaPlayer.setPlaySongList(tableViewSong.getItems());     //设置当前播放列表
            myMediaPlayer.setCurrentPlayIndex(tableViewSong.getSelectionModel().getFocusedIndex());  //设置当前播放的歌曲在表格中的位置
            //设置右下角"歌单文本提示"显示数量
            bottomController.getLabPlayListCount().setText(String.valueOf(myMediaPlayer.getPlaySongList().size()));

        }
    }

    /**"播放全部"按钮的鼠标单击事件处理*/
    @FXML
    public void onClickedPlayAll(MouseEvent mouseEvent) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            if (myMediaPlayer.getPlayMode() == PlayMode.SHUFFLE){   //如果当前播放模式为"随机播放"
                //生成一个随机数，执行播放
                int randomIndex=new Random().nextInt(tableViewSong.getItems().size());
                myMediaPlayer.setCurrentPlayIndex(randomIndex);     //设置当前播放的索引为生成的随机索引
                myMediaPlayer.playLocal(tableViewSong.getItems().get(myMediaPlayer.getCurrentPlayIndex()));      //播放生成的随机索引歌曲

            }
            else {  //否则,不是"随机播放"模式,这些都是播放表格中的第一首歌曲
                myMediaPlayer.setCurrentPlayIndex(0);  //设置当前播放的歌曲为表格第一首歌曲
                myMediaPlayer.playLocal(tableViewSong.getItems().get(0));      //播放表格中第一首歌曲
            }
            myMediaPlayer.setPlaySongList(tableViewSong.getItems());     //设置当前播放列表
            //设置右下角"歌单文本提示"显示数量
            bottomController.getLabPlayListCount().setText(String.valueOf(myMediaPlayer.getPlaySongList().size()));
        }
    }

    public void onScrollTableView(ScrollEvent scrollEvent) {
        System.out.println("scrolling");
    }

    public void onScrollToColumn(ScrollToEvent<TableColumn<Song,?>> tableColumnScrollToEvent) {
        System.out.println("scrollToColumn");
    }

    public void onScrollFinished(ScrollEvent scrollEvent) {
        System.out.println("scrollFinished");
    }

    public void onScrollStarted(ScrollEvent scrollEvent) {
        System.out.println("scrollStarted");
    }


//    /**中间面板的鼠标移动事件，修复鼠标形状*/
//    @FXML
//    public void onBorderPaneMouseMoved(MouseEvent mouseEvent) {
//
////        localMusicContentContainer.setCursor(Cursor.DEFAULT);
//    }
}
