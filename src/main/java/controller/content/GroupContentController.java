package controller.content;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import model.GroupSong;
import model.RecentSong;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import service.LoadGroupSongService;
import util.ImageUtils;

import javax.annotation.Resource;

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
    private Label labBlur;

    @FXML
    private VBox vBoxContainer;

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

    public StackPane getGroupContentContainer() {
        return groupContentContainer;
    }

    public ScrollPane getScrollPaneContainer() {
        return scrollPaneContainer;
    }

    public Label getLabBlur() {
        return labBlur;
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

    public ImageView getIvUserImage() {
        return ivUserImage;
    }

    public Label getLabUserName() {
        return labUserName;
    }

    public Label getLabCreateTime() {
        return labCreateTime;
    }

    public HBox gethBoxDescription() {
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
        vBoxContainer.prefWidthProperty().bind(groupContentContainer.widthProperty());
        vBoxContainer.prefHeightProperty().bind(groupContentContainer.heightProperty());

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
            Platform.runLater(()->{
                labBlur.setMaxWidth(observable.getValue().doubleValue() - 150);
            });
        });

        ivUserImage.setClip(new Circle(15,15,15));  //切割用户头像

        //宽高度绑定,然后设置模糊效果
        ImageView ivBlur = new ImageView(ivAlbumImage.getImage());
        ivBlur.fitWidthProperty().bind(labBlur.maxWidthProperty());
        ivBlur.fitHeightProperty().bind(labBlur.maxHeightProperty());
        labBlur.setGraphic(ivBlur);
        labBlur.setOpacity(0.8);
        labBlur.setEffect(new BoxBlur(150,150,2));

        LoadGroupSongService loadGroupSongService = applicationContext.getBean(LoadGroupSongService.class);
        progressIndicator.visibleProperty().bind(loadGroupSongService.runningProperty());
        tableViewGroupSong.itemsProperty().bind(loadGroupSongService.valueProperty());
        loadGroupSongService.start();

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
                            if (item.getLabFavor().getUserData().equals("like")){
                                item.getLabFavor().setGraphic(ImageUtils.createImageView(new Image("/image/FavoredIcon_16.png"),16,16));
                            }else {
                                item.getLabFavor().setGraphic(ImageUtils.createImageView(new Image("/image/FavorIcon_16.png"),16,16));
                            }
                        }
                    }
                };
            }
        });
//        ObservableList<GroupSong> observableList = FXCollections.observableArrayList();
//        for (int i = 0; i < 30; i++) {
//            Label label = new Label("", ImageUtils.createImageView(new Image("/image/LocalFlag_10.png"),10,10));
//            label.setMinWidth(14);
//            label.setMinHeight(14);
//            Label labFavor = new Label("",ImageUtils.createImageView(new Image("/image/FavoredIcon_16.png"),16,16));
//            label.setStyle("-fx-background-color: #88D2F2;-fx-background-radius: 14;-fx-alignment: center");
//            observableList.add(new GroupSong(String.valueOf(111),labFavor,"test",label,"test","test","test","test"));
//        }
//        tableViewGroupSong.setItems(observableList);
//        tableViewGroupSong.setMinHeight(tableViewGroupSong.getItems().size() * 40);
    }
}
