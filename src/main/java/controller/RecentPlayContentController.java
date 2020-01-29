package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import model.RecentSong;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import service.LoadLocalSongService;
import service.LoadRecentSongService;
import util.ImageUtils;

import javax.annotation.Resource;

@Controller
public class RecentPlayContentController {



    /**“最近播放”标签内容的根容器*/
    @FXML
    private BorderPane recentPlayContentContainer;

    /**进度加载指示器*/
    @FXML
    private ProgressIndicator progressIndicator;

    /**歌曲id索引列*/
    @FXML
    private TableColumn indexColumn;

    /**歌曲“添加喜欢”列*/
    @FXML
    private TableColumn addFavorColumn;

    /**歌曲名称列*/
    @FXML
    private TableColumn nameColumn;

    /**歌曲歌手列*/
    @FXML
    private TableColumn singerColumn;

    /**歌曲专辑列*/
    @FXML
    private TableColumn albumColumn;

    /**歌曲总时长列*/
    @FXML
    private TableColumn totalTimeColumn;

    /**最近播放歌曲的表格组件*/
    @FXML
    private TableView tableViewRecentPlaySong;

    /**表格组件的容器borderPane*/
    @FXML
    private BorderPane borderPane;

    /**显示当前列表的歌曲数量的label组件*/
    @FXML
    private Label labSongCount;

    /**注入Spring上下文类*/
    @Resource
    private ApplicationContext applicationContext;

    public Label getLabSongCount() {
        return labSongCount;
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
        loadRecentSongService.start();
    }
}
