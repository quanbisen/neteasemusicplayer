package controller.content;

import application.SpringFXMLLoader;
import controller.main.BottomController;
import controller.main.MainController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import mediaplayer.MyMediaPlayer;
import model.PlayListSong;
import model.RecentSong;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import service.LoadRecentSongService;
import util.ImageUtils;
import util.SongUtils;
import util.StageUtils;
import util.WindowUtils;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Controller
public class RecentPlayContentController {

    /**“最近播放”标签内容的根容器*/
    @FXML
    private BorderPane recentPlayContentContainer;

    /**进度加载指示器*/
    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private TabPane tabPane;

    /**“全部清空”的HBox容器*/
    @FXML
    private HBox hBoxClearAll;

    /**歌曲id索引列*/
    @FXML
    private TableColumn<RecentSong,String> indexColumn;

    /**歌曲“添加喜欢”列*/
    @FXML
    private TableColumn<RecentSong,Label> addFavorColumn;

    /**歌曲名称列*/
    @FXML
    private TableColumn<RecentSong,String> nameColumn;

    /**歌曲歌手列*/
    @FXML
    private TableColumn<RecentSong,String> singerColumn;

    /**歌曲专辑列*/
    @FXML
    private TableColumn<RecentSong,String> albumColumn;

    /**歌曲总时长列*/
    @FXML
    private TableColumn<RecentSong,String> totalTimeColumn;

    /**最近播放歌曲的表格组件*/
    @FXML
    private TableView<RecentSong> tableViewRecentPlaySong;

    /**表格组件的容器borderPane*/
    @FXML
    private BorderPane borderPane;

    /**注入Spring上下文类*/
    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private MainController mainController;

    @Resource
    private MyMediaPlayer myMediaPlayer;

    @Resource
    private BottomController bottomController;

    public TabPane getTabPane() {
        return tabPane;
    }

    public TableView getTableViewRecentPlaySong() {
        return tableViewRecentPlaySong;
    }

    public void initialize(){
        /*******Fixed some resize cursor bug here.********/
        recentPlayContentContainer.setCursor(Cursor.DEFAULT);
        /*******Fixed some resize cursor bug here.********/

        progressIndicator.setVisible(false);    //初始化不可见

        /**属性绑定*/
        indexColumn.setCellValueFactory(new PropertyValueFactory<RecentSong,String>("index"));
        addFavorColumn.setCellValueFactory(new PropertyValueFactory<RecentSong, Label>("labAddFavor"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<RecentSong,String>("name"));
        singerColumn.setCellValueFactory(new PropertyValueFactory<RecentSong,String>("singer"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<RecentSong,String>("album"));
        totalTimeColumn.setCellValueFactory(new PropertyValueFactory<RecentSong,String>("totalTime"));

        borderPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            nameColumn.setPrefWidth((observable.getValue().doubleValue() - indexColumn.getMaxWidth() - addFavorColumn.getMaxWidth())/4.5*2);
            singerColumn.setPrefWidth((observable.getValue().doubleValue() - indexColumn.getMaxWidth() - addFavorColumn.getMaxWidth())/4.5*1);
            albumColumn.setPrefWidth((observable.getValue().doubleValue() - indexColumn.getMaxWidth() - addFavorColumn.getMaxWidth())/4.5*1);
            totalTimeColumn.setPrefWidth((observable.getValue().doubleValue() - indexColumn.getMaxWidth() - addFavorColumn.getMaxWidth())/4.5*0.5);
        });

        LoadRecentSongService loadRecentSongService = applicationContext.getBean(LoadRecentSongService.class);  //获取服务对象
        tableViewRecentPlaySong.itemsProperty().bind(loadRecentSongService.valueProperty());
        progressIndicator.visibleProperty().bind(loadRecentSongService.runningProperty());
        loadRecentSongService.restart();
        loadRecentSongService.setOnSucceeded(event -> {
            this.updateRecentPlayPane(); //更新最近播放面板的GUI
        });
    }

    /**“全部清空”图标的事件处理*/
    @FXML
    public void onClickedClearAll(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){ //鼠标左击
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/dialog/clear-recentplay-confirm-dialog.fxml");
            Stage primaryStage = ((Stage)hBoxClearAll.getScene().getWindow());              //获取主窗体的stage对象primaryStage
            Stage dialogStage = StageUtils.getStage((Stage) hBoxClearAll.getScene().getWindow(),fxmlLoader.load());
            StageUtils.synchronizeCenter(primaryStage,dialogStage);
            WindowUtils.blockBorderPane(mainController.getBorderPane());
            dialogStage.showAndWait();  //显示对话框
        }
    }

    /**“播放全部“按钮的事件处理*/
    @FXML
    public void onClickedPlayAll(MouseEvent mouseEvent) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){ //鼠标左击
            if (tableViewRecentPlaySong.getItems() != null && tableViewRecentPlaySong.getItems().size() > 0){
                myMediaPlayer.playAll(tableViewRecentPlaySong.getItems());  //执行媒体播放其播放全部操作
            }
        }
    }

    /**更新最近播放面板的GUI的函数
     * */
    public void updateRecentPlayPane(){
        List<RecentSong> tableItems = tableViewRecentPlaySong.getItems();
        tabPane.getTabs().get(0).setText(String.valueOf(tableItems.size()));    //更新歌曲数目显示
        Image imageFavor = new Image("/image/FavorIcon_16.png",16.0,16.0,true,true);
        for (int i = 0; i < tableItems.size(); i++) {
            if (tableItems.get(i).getLabAddFavor() == null){
                ImageView imageView = ImageUtils.createImageView(imageFavor,16.0,16.0);
                Label labAddFavor = new Label("",imageView);
                labAddFavor.setOnMouseClicked(event -> {
                    System.out.println("clicked ...");
                });
                tableItems.get(i).setLabAddFavor(labAddFavor);
            }

            StringBuffer stringBuffer = new StringBuffer();
            if (i+1<10){
                stringBuffer.append("0");
            }
            String index = stringBuffer.append(i+1).toString();
            tableItems.get(i).setIndex(index);
        }
    }

    /**”最近播放“表格的鼠标事件*/
    @FXML
    public void onClickedTableViewRecentSong(MouseEvent mouseEvent) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2){ //鼠标左键双击
            RecentSong selectedRecentSong = tableViewRecentPlaySong.getSelectionModel().getSelectedItem();  //取出选中的最近播放歌曲行
            PlayListSong playListSong = SongUtils.toPlayListSong(selectedRecentSong);   //转换成播放列表模型
            if (!myMediaPlayer.isPlayingThis(playListSong)){    //如果选中的歌曲不是正在播放的歌曲
                myMediaPlayer.addToPlayList(playListSong);
                myMediaPlayer.playNext();
                tableViewRecentPlaySong.getSelectionModel().clearSelection();   //清除选中模式
            }
        }
    }
}
