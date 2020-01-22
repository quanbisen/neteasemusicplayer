package controller;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
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
import util.SongUtils;
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
    private BorderPane localMusicContentContainer;

    /**本地音乐中间面板的根容器tabPane的Tab1*/
    @FXML
    private TabPane tabPane;

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

    /**搜索框的TextField组件，用作输入文本*/
    @FXML
    private TextField tfSearch;

    /**搜索框的图标图片组件*/
    @FXML
    private ImageView ivSearchIcon;

    /**表格内容的集合变量*/
    private ObservableList<Song> observableItems;

    /**注入窗体根容器（BorderPane）的控制类*/
    @Resource
    private MainController mainController;

    /**注入Spring上下文类*/
    @Resource
    private ApplicationContext applicationContext;

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

        /**加载歌曲信息成功后执行定制的表格样式，使用Java代码设置样式，css设置样式存在更新的不同步的问题
         * start*/
        loadSongService.setOnSucceeded(event -> {
            tableViewSong.setRowFactory(new Callback<TableView<Song>, TableRow<Song>>() {
                int index=0;    //用作字母分类的记录基数偶数行的标记index
                @Override
                public TableRow<Song> call(TableView<Song> param) {
                    return new TableRow<Song>(){
                        @Override
                        protected void updateItem(Song item, boolean empty) {
                            super.updateItem(item, empty);
                            if (!empty){
                                /**设置表格行的样式*/
                                if (SongUtils.isCharacterCategory(item.getName())){
                                    index = 0;
                                    this.setStyle("-fx-background-color: #FAFAFC;");
                                }else {
                                    index++;
                                    if (index%2 != 0){
                                        this.setStyle("-fx-background-color:#F4F4F6;"); //字母行下的基数行样式
                                    }else {
                                        this.setStyle("-fx-background-color: #FAFAFC;");//字母行下的偶数行样式
                                    }
                            /*        this.setOnMouseEntered(event1 -> {
                                        if (!this.isSelected()){
                                            this.setStyle("-fx-background-color: #F2F2F3;");
                                        }
                                    });
                                    this.setOnMouseExited(event1 -> {
                                        getTableView().refresh();
                                    });*/
                                    /**设置表格行的右键菜单contextMenu*/
                                    try {
                                        this.setContextMenu(applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/song-contextmenu.fxml").load());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        @Override
                        public void updateSelected(boolean selected) {
                            super.updateSelected(selected);
                            if (selected){  //如果选中了
                                if (!SongUtils.isCharacterCategory(this.getItem().getName())){  //选择的是实际上的歌曲行才执行
                                    this.setStyle("-fx-background-color: #DEDEE0;");    //设置背景
                                    this.getTableView().refresh();  //刷新表格
                                }
                            }
                        }
                    };
                }
            });
            /*tableViewSong.setRowFactory(new Callback<TableView<Song>, TableRow<Song>>() {
                @Override
                public TableRow<Song> call(TableView<Song> param) {
                    return new TableRow<Song>(){
                        @Override
                        protected void updateItem(Song item, boolean empty) {
                            if (!empty && !SongUtils.isCharacterCategory(item.getName())){
                                try {
                                    getStylesheets().add("/css/LocalMusicContentStyle.css");
                                    setContextMenu(applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/song-contextmenu.fxml").load());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                }
            });*/
            nameColumn.setCellFactory(new Callback<TableColumn<Song, String>, TableCell<Song, String>>() {
                @Override
                public TableCell<Song, String> call(TableColumn<Song, String> param) {
                    return new TableCell<Song,String>(){
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item==null||empty){ //如果为空的，设置为空
                                this.setText(null);
                                this.setStyle("");
                            }
                            else {  //否则，单元格存在内容，执行下一步
                                setText(item);  //设置表格内容为item的字符串
                                if (SongUtils.isCharacterCategory(item)){   //如果字符串是类别字母，设置添加定制
                                    setStyle("-fx-text-fill: #999999;" +
                                            "-fx-font-size: 18px;");
                                }else {
                                    setStyle("-fx-text-fill: black;" +
                                            "-fx-font-size: 12px;");
                                }
                            }
                        }
                    };
                }
            });
        });
        /**end*/


        /*******Fixed some resize bug here.********/
        localMusicContentContainer.setCursor(Cursor.DEFAULT);
        /*******Fixed some resize bug here.********/

        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if (observableItems == null){   //如果存储表格内容的变量为空，才指向表格的内容
                observableItems = tableViewSong.getItems(); //取出表格的内容东西，指向它
            }
            if (tableViewSong.itemsProperty().isBound()){
                tableViewSong.itemsProperty().unbind(); //解除items绑定
            }
            if (!observable.getValue().trim().equals("")){   //如果有内容
                ivSearchIcon.setImage(new Image("/image/CloseIcon.png"));   //设置图标为清除图标
                ObservableList<Song> observableResultList = FXCollections.observableArrayList();   //定义匹配关键字的Observable集合
                for (Song song : SongUtils.getActualList(observableItems)){   //遍历可播放歌曲，查找包含搜索关键字的行
                    if (song.toStringContent().contains(observable.getValue())){    //如果歌曲的歌名、歌手和专辑字符串包含搜索的关键字
                        observableResultList.add(song);     //添加到搜索结果集合
                    }
                }
                tableViewSong.setItems(observableResultList);   //设置表格内容为搜索到的结果集合
            } else {  //否则，还原搜索图标和表格内容
                ivSearchIcon.setImage(new Image("/image/SearchIcon-16.png"));
                tableViewSong.setItems(observableItems);
            }
        });

//        tableViewSong.setContextMenu(applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/song-contextmenu.fxml").load());
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
            if (!SongUtils.isCharacterCategory(tableViewSong.getSelectionModel().getSelectedItem().getName())){ //如果双击播放的歌曲不是字母分类的行，才执行以下播放
                myMediaPlayer.playLocal(tableViewSong.getSelectionModel().getSelectedItem());      //播放选中的歌曲
                myMediaPlayer.setPlaySongList(SongUtils.getActualList(tableViewSong.getItems()));     //设置当前播放列表
                myMediaPlayer.setCurrentPlayIndex(myMediaPlayer.getPlaySongList().indexOf(tableViewSong.getSelectionModel().getSelectedItem()));  //设置当前播放的歌曲在播放列表playList中的位置
                //设置右下角"歌单文本提示"显示数量
                bottomController.getLabPlayListCount().setText(String.valueOf(myMediaPlayer.getPlaySongList().size()));
//                System.out.println(tableViewSong.getSelectionModel().getSelectedIndex());
//                System.out.println(myMediaPlayer.getCurrentPlayIndex());
            }
        }
    }

    /**"播放全部"按钮的鼠标单击事件处理*/
    @FXML
    public void onClickedPlayAll(MouseEvent mouseEvent) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            myMediaPlayer.setPlaySongList(SongUtils.getActualList(tableViewSong.getItems()));     //设置当前播放列表
            //设置右下角"歌单文本提示"显示数量
            bottomController.getLabPlayListCount().setText(String.valueOf(myMediaPlayer.getPlaySongList().size()));
            if (myMediaPlayer.getPlayMode() == PlayMode.SHUFFLE){   //如果当前播放模式为"随机播放"
                //生成一个随机数，执行播放
                int randomIndex=new Random().nextInt(myMediaPlayer.getPlaySongList().size());
                myMediaPlayer.setCurrentPlayIndex(randomIndex);     //设置当前播放的索引为生成的随机索引
                myMediaPlayer.playLocal(myMediaPlayer.getPlaySongList().get(myMediaPlayer.getCurrentPlayIndex()));      //播放生成的随机索引歌曲

            }
            else {  //否则,不是"随机播放"模式,这些都是播放播放列表中的第一首歌曲
                myMediaPlayer.setCurrentPlayIndex(0);  //设置当前播放的歌曲为播放列表第一首歌曲
                myMediaPlayer.playLocal(myMediaPlayer.getPlaySongList().get(0));      //播放表格中第一首歌曲
            }

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

    /**清除图标的鼠标事件*/
    @FXML
    public void onClickedClearIcon(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){ //鼠标左击
            tfSearch.setText("");   //清空文本
        }
    }

}
