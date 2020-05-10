package controller.content;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import mediaplayer.MyMediaPlayer;
import model.GroupSong;
import model.RecentSong;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import service.LoadGroupSongService;
import util.ImageUtils;
import util.WindowUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author super lollipop
 * @date 20-3-7
 */
@Controller
public class GroupContentController {

    @FXML
    private StackPane groupContentContainer;

    @FXML
    private ScrollPane scrollPaneContainer;

    @FXML
    private StackPane stackPane;

    @FXML
    private VBox vBoxContainer;

    @FXML
    private Label labBlur;

    @FXML
    private BorderPane borderPaneGroupInfo;

    @FXML
    private Label labGroupName;

    @FXML
    private ImageView ivAlbumImage;

    @FXML
    private ImageView ivUserImage;

    @FXML
    private Label labUserName;

    @FXML
    private Label labCreateTime;

    @FXML
    private HBox hBoxDescription;

    @FXML
    private Label labDescription;

    @FXML
    private HBox hBoxPlayAll;

    @FXML
    private Button btnPlayAll;

    @FXML
    private TextField tfSearch;

    @FXML
    private ImageView ivSearchIcon;

    @FXML
    private TableView<GroupSong> tableViewGroupSong;

    @FXML
    private TableColumn<GroupSong, String> indexColumn;

    @FXML
    private TableColumn<GroupSong, Label> addFavorColumn;

    @FXML
    private TableColumn<GroupSong, String> nameColumn;

    @FXML
    private TableColumn<GroupSong,Label> localFlagColumn;

    @FXML
    private TableColumn<GroupSong, String> singerColumn;

    @FXML
    private TableColumn<GroupSong, String> albumColumn;

    @FXML
    private TableColumn<GroupSong, String> totalTimeColumn;

    @FXML
    private ProgressIndicator progressIndicator;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private MyMediaPlayer myMediaPlayer;

    public StackPane getGroupContentContainer() {
        return groupContentContainer;
    }

    public ScrollPane getScrollPaneContainer() {
        return scrollPaneContainer;
    }

    public VBox getVBoxContainer() {
        return vBoxContainer;
    }

    public BorderPane getBorderPaneGroupInfo() {
        return borderPaneGroupInfo;
    }

    public Label getLabGroupName() {
        return labGroupName;
    }

    public ImageView getIvAlbumImage() {
        return ivAlbumImage;
    }

    public Label getLabBlur() {
        return labBlur;
    }

    public ImageView getIvUserImage() {
        return ivUserImage;
    }

    public Label getLabUserName() {
        return labUserName;
    }

    public Label getLabCreateTime() {
        return labCreateTime;
    }

    public HBox getHBoxDescription() {
        return hBoxDescription;
    }

    public Label getLabDescription() {
        return labDescription;
    }

    public HBox getHBoxPlayAll() {
        return hBoxPlayAll;
    }

    public Button getBtnPlayAll() {
        return btnPlayAll;
    }

    public TextField getTfSearch() {
        return tfSearch;
    }

    public ImageView getIvSearchIcon() {
        return ivSearchIcon;
    }

    public TableView<GroupSong> getTableViewGroupSong() {
        return tableViewGroupSong;
    }

    public void initialize(){
        //容器布局绑定
        WindowUtils.absolutelyBind(vBoxContainer,groupContentContainer);
        WindowUtils.absolutelyBind(stackPane,groupContentContainer);

        groupContentContainer.widthProperty().addListener(((observable, oldValue, newValue) -> {
            labBlur.setPrefWidth(observable.getValue().doubleValue() - 150);
        }));

        /**属性绑定*/
        indexColumn.setCellValueFactory(new PropertyValueFactory<>("index"));
        addFavorColumn.setCellValueFactory(new PropertyValueFactory<>("labFavor"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        localFlagColumn.setCellValueFactory(new PropertyValueFactory<>("labLocalFlag"));
        singerColumn.setCellValueFactory(new PropertyValueFactory<>("singer"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
        totalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("totalTime"));

        vBoxContainer.widthProperty().addListener((observable, oldValue, newValue) -> {
            nameColumn.setPrefWidth((observable.getValue().doubleValue() - indexColumn.getMaxWidth() - addFavorColumn.getMaxWidth() - localFlagColumn.getMaxWidth())/4.5*1.8);
            singerColumn.setPrefWidth((observable.getValue().doubleValue() - indexColumn.getMaxWidth() - addFavorColumn.getMaxWidth() - localFlagColumn.getMaxWidth())/4.5*1);
            albumColumn.setPrefWidth((observable.getValue().doubleValue() - indexColumn.getMaxWidth() - addFavorColumn.getMaxWidth() - localFlagColumn.getMaxWidth())/4.5*1);
            totalTimeColumn.setPrefWidth((observable.getValue().doubleValue() - indexColumn.getMaxWidth() - addFavorColumn.getMaxWidth() - localFlagColumn.getMaxWidth())/4.5*0.7);
        });

        borderPaneGroupInfo.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (observable.getValue().doubleValue() / 100 * 23 > 200){
                ivAlbumImage.setFitHeight(200);
                ivAlbumImage.setFitWidth(200);
            }else {
                Platform.runLater(()->{ //using runLater to fix BorderPane Left child resize
                    ivAlbumImage.setFitHeight(observable.getValue().doubleValue() / 100 * 23);
                    ivAlbumImage.setFitWidth(observable.getValue().doubleValue() / 100 * 23);
                });
            }
        });

        ivUserImage.setClip(new Circle(15,15,15));  //切割用户头像

        //宽高度绑定,然后设置模糊效果
        ivAlbumImage.imageProperty().addListener((observable, oldValue, newValue) -> {
            ImageView ivBlur = new ImageView(observable.getValue());
            ivBlur.fitWidthProperty().bind(labBlur.prefWidthProperty());
            ivBlur.fitHeightProperty().bind(labBlur.prefHeightProperty());
            labBlur.setGraphic(ivBlur);
        });
        labBlur.setOpacity(0.8);
        labBlur.setEffect(new BoxBlur(150,150,2));


        LoadGroupSongService loadGroupSongService = applicationContext.getBean(LoadGroupSongService.class);
        progressIndicator.visibleProperty().bind(loadGroupSongService.runningProperty());
        tableViewGroupSong.itemsProperty().bind(loadGroupSongService.valueProperty());
        loadGroupSongService.restart();
        loadGroupSongService.setOnSucceeded(event -> {
            if (tableViewGroupSong.getItems() != null && tableViewGroupSong.getItems().size() > 0){
                tableViewGroupSong.setMinHeight(tableViewGroupSong.getItems().size() * 40);    //设置表格的高度
                tableViewGroupSong.setRowFactory(new Callback<TableView<GroupSong>, TableRow<GroupSong>>() {
                    @Override
                    public TableRow<GroupSong> call(TableView<GroupSong> param) {
                        return new TableRow<GroupSong>(){
                            @Override
                            protected void updateItem(GroupSong item, boolean empty) {
                                super.updateItem(item, empty);
                                if (!empty){
                                    //设置索引列
                                    if ( 0 <= getIndex() && getIndex() < 9){
                                        item.setIndex("0" + (getIndex() + 1));
                                    }else {
                                        item.setIndex(String.valueOf(getIndex() + 1));
                                    }
                                    //设置本地资源图标标识
                                    if (!item.getResourceURL().contains("http://")){    //没有包含http,那么就是本地资源了.添加本地资源的图标标识
                                        Label labLocalFlag = new Label("",ImageUtils.createImageView(new Image("/image/LocalFlag_10.png"),10,10));
                                        labLocalFlag.getStyleClass().add("labLocalFlag");
                                        item.setLabLocalFlag(labLocalFlag);
                                    }
                                }
                            }
                        };
                    }
                });
            }
        });

    }


    /**"播放全部"事件处理*/
    @FXML
    public void onClickedPlayAll(ActionEvent actionEvent) throws ReadOnlyFileException, IOException, TagException, InvalidAudioFrameException, CannotReadException {
        myMediaPlayer.playAll(tableViewGroupSong.getItems());
    }

    /**单击表格的事件处理*/
    @FXML
    public void onClickedTableViewSong(MouseEvent mouseEvent) throws ReadOnlyFileException, IOException, TagException, InvalidAudioFrameException, CannotReadException {
        if(mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2){
            myMediaPlayer.playAll(tableViewGroupSong.getItems(),tableViewGroupSong.getSelectionModel().getSelectedIndex());
        }
    }

}
