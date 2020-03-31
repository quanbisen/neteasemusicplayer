package controller.popup;

import controller.main.BottomController;
import controller.main.MainController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Callback;
import mediaplayer.MyMediaPlayer;
import model.LocalSong;
import model.PlayListSong;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.stereotype.Controller;
import util.ImageUtils;
import util.WindowUtils;
import javax.annotation.Resource;
import java.io.IOException;

@Controller
public class PlayListController {


    @FXML
    private AnchorPane anchorPane;

    @FXML
    private BorderPane borderPanePlayList;

    @FXML
    private TableView tableViewPlayList;

    @FXML
    private TableColumn<LocalSong,String> nameColumn;

    @FXML
    private TableColumn<LocalSong,String> singerColumn;

    @FXML
    private TableColumn<LocalSong,String> totalTimeColumn;

    @FXML
    private TableColumn labRemoveIcon;

    @Resource
    private MainController mainController;

    @Resource
    private MyMediaPlayer myMediaPlayer;

    @Resource
    private BottomController bottomController;

    public TableView getTableViewPlayList() {
        return tableViewPlayList;
    }

    public void initialize() {

        /**宽度高度属性监听，同步坐标*/
        anchorPane.minWidthProperty().addListener(((observable, oldValue, newValue) -> {
            double offSetX = 0;
            if (WindowUtils.isWindowsPlatform() && !((Stage)mainController.getStackPane().getScene().getWindow()).isMaximized()){  //如果是Windows平台，需要考虑阴影效果设置的像素大小10px
                offSetX = offSetX - 10;
            }
            borderPanePlayList.setLayoutX(observable.getValue().doubleValue()-borderPanePlayList.getWidth() + offSetX);
        }));
        anchorPane.minHeightProperty().addListener(((observable, oldValue, newValue) -> {
            double offSetY = -45;
            if (WindowUtils.isWindowsPlatform() && !((Stage)mainController.getStackPane().getScene().getWindow()).isMaximized()){  //如果是Windows平台，需要考虑阴影效果设置的像素大小10px
                offSetY = offSetY - 10;
            }
            borderPanePlayList.setLayoutY(observable.getValue().doubleValue()-borderPanePlayList.getHeight() + offSetY);
        }));

        /***/
        Platform.runLater(()->{
            //宽度高度绑定
            anchorPane.minWidthProperty().bind(((StackPane)anchorPane.getParent()).widthProperty());
            anchorPane.minHeightProperty().bind(((StackPane)anchorPane.getParent()).heightProperty());

            double offSetX = 0;
            double offSetY = -45;
            if (WindowUtils.isWindowsPlatform() && !((Stage)mainController.getStackPane().getScene().getWindow()).isMaximized()){   //如果是Windows平台，需要考虑阴影效果设置的像素大小10px
                offSetX = offSetX - 10;
                offSetY = offSetY - 10;
            }

            //设置布局X，Y
            borderPanePlayList.setLayoutX(anchorPane.getMinWidth() - borderPanePlayList.getMinWidth() + offSetX);
            borderPanePlayList.setLayoutY(anchorPane.getMinHeight() - borderPanePlayList.getMinHeight() + offSetY);

        });

        /**裁剪成只剩下borderPanePlayList的部分*/
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(borderPanePlayList.getMinWidth());
        rectangle.setHeight(borderPanePlayList.getMinHeight());
        rectangle.xProperty().bind(borderPanePlayList.layoutXProperty());
        rectangle.yProperty().bind(borderPanePlayList.layoutYProperty());
        anchorPane.setClip(rectangle);

        mainController.getBorderPane().getCenter().setMouseTransparent(true);
        mainController.getBorderPane().getBottom().setMouseTransparent(true);

//        EventHandler<MouseEvent> eventEventHandler = new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                if (event.getButton() == MouseButton.PRIMARY){ //鼠标左击
//                    mainController.getStackPane().getChildren().remove(anchorPane);   //移除面板
//                    mainController.getBorderPane().getCenter().setMouseTransparent(false);
//                    mainController.getBorderPane().getBottom().setMouseTransparent(false);
//                    mainController.getBorderPane().removeEventHandler(MouseEvent.MOUSE_CLICKED,this);
//                }
//            }
//        };
//        mainController.getBorderPane().addEventHandler(MouseEvent.MOUSE_CLICKED,eventEventHandler);


        //列属性绑定
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        singerColumn.setCellValueFactory(new PropertyValueFactory<>("singer"));
        totalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("totalTime"));
        labRemoveIcon.setCellValueFactory(new PropertyValueFactory<>("labRemoveIcon"));

        //设置表格列的宽度随这个borderPane的宽度而动态改变
        borderPanePlayList.widthProperty().addListener((observable, oldValue, newValue) -> {
            nameColumn.setPrefWidth((observable.getValue().doubleValue()-labRemoveIcon.getMaxWidth())/4*2);
            singerColumn.setPrefWidth((observable.getValue().doubleValue()-labRemoveIcon.getMaxWidth())/4*1.2);
            totalTimeColumn.setPrefWidth((observable.getValue().doubleValue()-labRemoveIcon.getMaxWidth())/4*0.8);
        });


        ObservableList<PlayListSong> observablePlayListSongs = FXCollections.observableArrayList();
        observablePlayListSongs.addAll(myMediaPlayer.getPlayListSongs());
        tableViewPlayList.setItems(observablePlayListSongs);   //设置播放列表的表格items
        tableViewPlayList.scrollTo(myMediaPlayer.getCurrentPlayIndex());   //滚动到播放的行
        /**更新表格行Row*/
        tableViewPlayList.setRowFactory(new Callback<TableView<LocalSong>, TableRow<LocalSong>>() {
            @Override
            public TableRow call(TableView param) {
                return new TableRow(){
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty){
                            if (getIndex() == myMediaPlayer.getCurrentPlayIndex()){ //如果当前的为播放的索音，设置一些样式（字体变红），see PlayListStyle.css文件的“.table-view .table-row-cell.highlightedRow .table-cell”
                                getStyleClass().add("highlightedRow");
                            } else {    //否则，移除
                                getStyleClass().remove("highlightedRow");
                            }
                        }
                    }

                    @Override
                    public void updateSelected(boolean selected) {
                        super.updateSelected(selected);
                        if (selected){
                            Label labRemoveIcon = new Label("", ImageUtils.createImageView("/image/RemovePlayListSongIcon.png",16,16));
                            labRemoveIcon.getStyleClass().add("labRemoveIcon");
//                            /**移除按钮的事件处理 start*/
                            labRemoveIcon.setOnMouseClicked(event -> {
                                try {
                                    myMediaPlayer.removeFromPlayList(getIndex());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
//                            /**移除按钮的事件处理 end*/
                            ((PlayListSong)this.getItem()).setLabRemoveIcon(labRemoveIcon);
                        }else {
                            if (getItem() != null){     //if no judgement,it will throw null exception.
                                ((PlayListSong)this.getItem()).setLabRemoveIcon(null);
                            }
                        }
                        getTableView().refresh();
                    }
                };
            }
        });
    }

    /**关闭图标按钮的鼠标事件*/
    @FXML
    public void onClickedCloseIcon(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){ //鼠标左击
            mainController.getStackPane().getChildren().remove(anchorPane);   //移除面板
            mainController.getBorderPane().getCenter().setMouseTransparent(false);
            mainController.getBorderPane().getBottom().setMouseTransparent(false);
        }
    }

    /**"清空"播放列表的事件*/
    @FXML
    public void onClickedClearAll(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){ //鼠标左击
            myMediaPlayer.destroy();
            if (tableViewPlayList.getItems() != null && tableViewPlayList.getItems().size() > 0){   //如果表格存在items
                tableViewPlayList.getItems().clear();   //清除表格
            }
        }
    }

    /**播放列表表格的事件*/
    @FXML
    public void onClickedTableViewPlayList(MouseEvent mouseEvent) throws Exception{
        if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2){  //鼠标左键双击
            int index = tableViewPlayList.getSelectionModel().getSelectedIndex();   //获取选中的索引
            myMediaPlayer.setCurrentPlayIndex(index);   //设置播放索引
            myMediaPlayer.playSong(); //播放当前的索引歌曲
            tableViewPlayList.refresh();    //刷新表格
        }
    }
}
