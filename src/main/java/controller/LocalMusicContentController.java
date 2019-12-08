package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Song;
import org.dom4j.DocumentException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import application.SpringFXMLLoader;
import service.LoadingSongService;
import util.SongUtils;
import util.StageUtils;
import util.WindowUtils;
import util.XMLUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LocalMusicContentController {

    @FXML
    public BorderPane borderPane;
    @FXML
    private TableColumn<Song,String> nameColumn;
    @FXML
    private TableColumn<Song,String> singerColumn;
    @FXML
    private TableColumn<Song,String> albumColumn;
    @FXML
    private TableColumn<Song,String> totalTimeColumn;
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
    ChoseFolderController choseFolderController;

    public TableView<Song> getTableViewSong() {
        return tableViewSong;
    }

    public void initialize() throws Exception {
        tabList = new ArrayList<>();
        tabList.add(hBoxSong);
        tabList.add(hBoxSinger);
        tabList.add(hBoxAlbum);

        this.setSelectedTab(hBoxSong);  //设置初始选中为格式标签

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        singerColumn.setCellValueFactory(new PropertyValueFactory<>("singer"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
        totalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("totalTime"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));

        //设置表格列的宽度随这个borderPane的宽度而动态改变
        borderPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                nameColumn.setPrefWidth(newValue.doubleValue()/6.5*2);
                singerColumn.setPrefWidth(newValue.doubleValue()/6.5*1);
                albumColumn.setPrefWidth(newValue.doubleValue()/6.5*1.5);
                totalTimeColumn.setPrefWidth(newValue.doubleValue()/6.5*1);
                sizeColumn.setPrefWidth(newValue.doubleValue()/6.5*1);
            }
        });

        File CHOSE_FOLDER_FILE = new File("src" + File.separator + "main" + File.separator + "resources" + File.separator + "config" + File.separator + "chose-folder.xml");
        if (CHOSE_FOLDER_FILE.exists()){
            List<String> folderList = XMLUtils.getAllRecord(CHOSE_FOLDER_FILE,"Folder","path");
            if (folderList.size()>0){
                ObservableList<Song> songs = SongUtils.getObservableSongList(folderList);
                tableViewSong.setItems(songs);
            }

        }

        progressIndicator.setVisible(false);
    }

    /**“选择目录”按钮按下事件处理*/
    @FXML
    public void onClickedChoseFolder(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){  //鼠标左击
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/chose-musicfolder.fxml");  //获取被Spring工厂接管的FXMLLoader对象
            Stage choseFolderStage = StageUtils.getStage((Stage) hBoxChoseFolder.getScene().getWindow(),fxmlLoader.load());

            StageUtils.syncCenter((Stage) hBoxChoseFolder.getScene().getWindow(),choseFolderStage);   //设置addMusicGroupStage对象居中到primaryStage
            WindowUtils.blockBorderPane(mainController.getBorderPane());         //设置borderPane不响应鼠标事件和改变透明度

            choseFolderStage.showAndWait();  //显示并且等待
//            if (choseFolderController.loadProperty().getValue()){  //如果是按下了“确定”按钮
//                //逻辑乱了的部分，先实现吧。
//
////                LoadingSongService loadingSongService = new LoadingSongService();
////                LoadingSongService loadingSongService = applicationContext.getBean(LoadingSongService.class);
////                progressIndicator.visibleProperty().bind(loadingSongService.runningProperty());
////                vBoxSongContainer.visibleProperty().bind(loadingSongService.valueProperty());
////                loadingSongService.start();
//            }
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
}
