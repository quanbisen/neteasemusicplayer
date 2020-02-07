package controller.content;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import controller.main.BottomController;
import controller.main.MainController;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import mediaplayer.MyMediaPlayer;
import model.LocalSinger;
import model.LocalSong;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import application.SpringFXMLLoader;
import service.LoadLocalSongService;
import util.ImageUtils;
import util.SongUtils;
import util.StageUtils;
import util.WindowUtils;
import javax.annotation.Resource;
import java.io.IOException;

@Controller
public class LocalMusicContentController {

    /**本地音乐中间面板的根容器*/
    @FXML
    private BorderPane localMusicContentContainer;

    /**本地音乐中间面板的根容器tabPane的Tab1*/
    @FXML
    private TabPane tabPane;

    /**“歌曲”标签的内容容器*/
    @FXML
    public BorderPane tabSongContent;

    /**歌曲名称列组件*/
    @FXML
    private TableColumn<LocalSong,String> nameColumn;

    /**歌曲歌手列组件*/
    @FXML
    private TableColumn<LocalSong,String> singerColumn;

    /**歌曲专辑列组件*/
    @FXML
    private TableColumn<LocalSong,String> albumColumn;

    /**歌曲总时间列组件*/
    @FXML
    private TableColumn<LocalSong,String> totalTimeColumn;

    /**歌曲大小列组件*/
    @FXML
    private TableColumn<LocalSong,String> sizeColumn;

    /**“选择目录”的HBox容器*/
    @FXML
    private HBox hBoxChoseFolder;

    /**显示进度的指示器*/
    @FXML
    private ProgressIndicator progressIndicator;

    /**存放显示歌曲的表格*/
    @FXML
    private TableView<LocalSong> tableViewSong;

    /**搜索框的TextField组件，用作输入文本*/
    @FXML
    private TextField tfSearch;

    /**搜索框的图标图片组件*/
    @FXML
    private ImageView ivSearchIcon;

    /**歌曲表格内容的集合变量*/
    private ObservableList<LocalSong> observableItems;

    /**歌手表格的BorderPane容器*/
    @FXML
    private BorderPane tabSingerContent;

    /**歌手图片姓名表格列组件*/
    @FXML
    private TableColumn singerInformationColumn;

    /**歌手对应的歌曲数目表格列组件*/
    @FXML
    private TableColumn songCountColumn;

    /**歌手表格组件*/
    @FXML
    private TableView tableViewSinger;

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

    public TableView<LocalSong> getTableViewSong() {
        return tableViewSong;
    }

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    public BorderPane getBorderPane() {
        return tabSongContent;
    }

    public void initialize() throws Exception {

        progressIndicator.setVisible(false);   //初始化进度指示器为不可见
        tabSongContent.setVisible(false);   //初始化不可见，在service中加载歌曲后控制可见性


        /******************/
        /**“歌曲”tab start*/
        /******************/
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
        tabSongContent.widthProperty().addListener((observable, oldValue, newValue) -> {
            nameColumn.setPrefWidth(observable.getValue().doubleValue()/6.5*2);
            singerColumn.setPrefWidth(observable.getValue().doubleValue()/6.5*1);
            albumColumn.setPrefWidth(observable.getValue().doubleValue()/6.5*1.5);
            totalTimeColumn.setPrefWidth(observable.getValue().doubleValue()/6.5*1);
            sizeColumn.setPrefWidth(observable.getValue().doubleValue()/6.5*1);
        });

        /**加载歌曲服务*/
        LoadLocalSongService loadLocalSongService = applicationContext.getBean(LoadLocalSongService.class);  //获取服务对象
        tableViewSong.itemsProperty().bind(loadLocalSongService.valueProperty());  //搜搜结果显示表格的内容绑定
        progressIndicator.visibleProperty().bind(loadLocalSongService.runningProperty());
        loadLocalSongService.start();  //启动服务


        /**单击表格歌曲行的事件处理，注意：歌曲行为非字母分类行*/
        EventHandler<MouseEvent> onClickedTableSongRow = mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2){  //鼠标双击执行
                LocalSong selectedLocalSong = tableViewSong.getSelectionModel().getSelectedItem();
                if (myMediaPlayer.getPlayListSongs() == null || myMediaPlayer.getPlayListSongs().size() ==0){
                    myMediaPlayer.setPlayListSongs(SongUtils.getPlayListSongs(tableViewSong.getItems()));     //设置当前播放列表
                    myMediaPlayer.setCurrentPlayIndex(myMediaPlayer.getPlayListSongs().indexOf(SongUtils.toPlayListSong(selectedLocalSong)));  //设置当前播放的歌曲在播放列表playList中的位置
                } else if (myMediaPlayer.getCurrentPlaySong().equals(SongUtils.toPlayListSong(selectedLocalSong))) {
                    return;
                } else {
                    myMediaPlayer.setPlayListSongs(SongUtils.getPlayListSongs(tableViewSong.getItems()));     //设置当前播放列表
                    myMediaPlayer.setCurrentPlayIndex(myMediaPlayer.getPlayListSongs().indexOf(SongUtils.toPlayListSong(selectedLocalSong)));  //设置当前播放的歌曲在播放列表playList中的位置
                }
                System.out.println(myMediaPlayer.getCurrentPlayIndex());
                try {
                    myMediaPlayer.playSong(myMediaPlayer.getPlayListSongs().get(myMediaPlayer.getCurrentPlayIndex()));      //播放选中的歌曲
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //设置右下角"歌单文本提示"显示数量
                bottomController.getLabPlayListCount().setText(String.valueOf(myMediaPlayer.getPlayListSongs().size()));
            }
        };
        /**设置表格行的行为
         * start*/
        loadLocalSongService.setOnSucceeded(event -> {
            tableViewSong.setRowFactory(new Callback<TableView<LocalSong>, TableRow<LocalSong>>() {
//                int index=0;    //用作字母分类的记录基数偶数行的标记index
                @Override
                public TableRow<LocalSong> call(TableView<LocalSong> param) {
                    return new TableRow<LocalSong>(){
                        @Override
                        protected void updateItem(LocalSong item, boolean empty) {
                            super.updateItem(item, empty);
                            if (!empty){
                                /**设置表格行的样式*/
                                if (SongUtils.isCharacterCategory(item.getName())){
//                                    index = 0;
//                                    this.setStyle("-fx-background-color: #FAFAFC;");
                                    this.setMouseTransparent(true);
                                    this.removeEventHandler(MouseEvent.MOUSE_CLICKED,onClickedTableSongRow);
                                }else {
//                                    index++;
//                                    if (index%2 != 0){
//                                        this.setStyle("-fx-background-color:#F4F4F6;"); //字母行下的基数行样式
//                                    }else {
//                                        this.setStyle("-fx-background-color: #FAFAFC;");//字母行下的偶数行样式
//                                    }
                                    this.setMouseTransparent(false);
                                    this.setOnMouseClicked(onClickedTableSongRow);
                                }
                            }
                        }
                        @Override
                        public void updateSelected(boolean selected) {
                            super.updateSelected(selected);
                            if (selected){  //如果选中了
                                if (!SongUtils.isCharacterCategory(this.getItem().getName())){  //选择的是实际上的歌曲行才执行
//                                    this.getTableView().refresh();  //刷新表格
//                                    this.setStyle("-fx-background-color: #DEDEE0;");    //设置背景
                                    /**设置表格行的右键菜单contextMenu*/
                                    try {
                                        this.setContextMenu(applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/contextmenu/song-contextmenu.fxml").load());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else {
                                this.setContextMenu(null);
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
            nameColumn.setCellFactory(new Callback<TableColumn<LocalSong, String>, TableCell<LocalSong, String>>() {
                @Override
                public TableCell<LocalSong, String> call(TableColumn<LocalSong, String> param) {
                    return new TableCell<LocalSong,String>(){
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty){ //如果为空的，设置为空
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


        /*******Fixed some resize cursor bug here.********/
        localMusicContentContainer.setCursor(Cursor.DEFAULT);
        /*******Fixed some resize cursor bug here.********/

        /**为搜索本地歌曲的文本输入框添加text文本事件监听器*/
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if (observableItems == null){   //如果存储表格内容的变量为空，才指向表格的内容
                observableItems = tableViewSong.getItems(); //取出表格的内容东西，指向它
            }
            if (tableViewSong.itemsProperty().isBound()){
                tableViewSong.itemsProperty().unbind(); //解除items绑定
            }
            ObservableList<LocalSong> observableResultList = FXCollections.observableArrayList();   //定义匹配关键字的Observable集合
            if (!observable.getValue().trim().equals("")){   //如果有内容
                ivSearchIcon.setImage(new Image("/image/CloseIcon.png"));   //设置图标为清除图标
                for (LocalSong localSong : SongUtils.getLocalSongList(observableItems)){   //遍历可播放歌曲，查找包含搜索关键字的行
                    if (localSong.toStringContent().contains(observable.getValue())){    //如果歌曲的歌名、歌手和专辑字符串包含搜索的关键字
                        observableResultList.add(localSong);     //添加到搜索结果集合
                    }
                }
                tableViewSong.setItems(observableResultList);   //设置表格内容为搜索到的结果集合
            } else {  //否则，还原搜索图标和表格内容
                ivSearchIcon.setImage(new Image("/image/SearchIcon-16.png"));
                observableResultList.removeAll();   //清除所有的搜索结果
                tableViewSong.setItems(observableItems);
                if (tableViewSong.getSelectionModel().getSelectedIndex() != -1){    //如果表格有选择的行，清除
                    tableViewSong.getSelectionModel().clearSelection();
                }
            }
        });

        tabPane.getSelectionModel().getSelectedItem().setText("120");
        /******************/
        /**“歌曲”tab end*/
        /******************/

        /******************/
        /**“歌手”tab start*/
        /******************/
        singerInformationColumn.setCellValueFactory(new PropertyValueFactory<>("labSinger"));
        songCountColumn.setCellValueFactory(new PropertyValueFactory<>("songCount"));
        songCountColumn.getStyleClass().add("songCountColumn");
  /*      Label label = new Label("林俊杰", ImageUtils.createImageView("/image/林俊杰.png",48,48));
        label.setGraphicTextGap(15);
        tableViewSinger.getItems().addAll(new LocalSinger(label,"2首"),
                new LocalSinger(new Label("singer"),"2首"),
                new LocalSinger(new Label("singer"),"2首"),
                new LocalSinger(new Label("singer"),"2首"),
                new LocalSinger(new Label("singer"),"2首"));*/
        //设置表格列的宽度随这个borderPane的宽度而动态改变
        tabSingerContent.widthProperty().addListener((observable, oldValue, newValue) -> {
            singerInformationColumn.setPrefWidth(observable.getValue().doubleValue()/6.5*5.5);
            songCountColumn.setPrefWidth(observable.getValue().doubleValue()/6.5*1);
        });
        /******************/
        /**“歌手”tab end*/
        /******************/

        /******************/
        /**“专辑”tab start*/
        /******************/
        /******************/
        /**“专辑”tab end*/
        /******************/

        /**为TabPane切换内容添加淡入淡出动画*/
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Node oldContent = oldValue.getContent();
            Node newContent = newValue.getContent();
            newValue.setContent(oldContent);
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.25),oldContent);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.25),newContent);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeOut.setOnFinished(event -> newValue.setContent(newContent));
            SequentialTransition sequentialTransition = new SequentialTransition(fadeOut,fadeIn);
            sequentialTransition.play();

            if (newValue == tabPane.getTabs().get(1)){
                tableViewSinger.getItems().addAll(SongUtils.getObservableLocalSingerList(tableViewSong.getItems()));
            }
        });
    }

    /**“选择目录”按钮按下事件处理*/
    @FXML
    public void onClickedChoseFolder(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){  //鼠标左击
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/popup/chose-musicfolder.fxml");  //获取被Spring工厂接管的FXMLLoader对象
            Stage choseFolderStage = StageUtils.getStage((Stage) hBoxChoseFolder.getScene().getWindow(),fxmlLoader.load());

            StageUtils.synchronizeCenter((Stage) hBoxChoseFolder.getScene().getWindow(),choseFolderStage);   //设置addMusicGroupStage对象居中到primaryStage
            WindowUtils.blockBorderPane(mainController.getBorderPane());         //设置borderPane不响应鼠标事件和改变透明度

            choseFolderStage.showAndWait();  //显示并且等待
        }
    }

    /**"播放全部"按钮的鼠标单击事件处理*/
    @FXML
    public void onClickedPlayAll(MouseEvent mouseEvent) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            myMediaPlayer.playAll(tableViewSong.getItems());  //执行媒体播放其播放全部操作
        }
    }

    public void onScrollTableView(ScrollEvent scrollEvent) {

        System.out.println("scrolling");
//        tableViewSong.refresh();
    }

    public void onScrollToColumn(ScrollToEvent<TableColumn<LocalSong,?>> tableColumnScrollToEvent) {
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
