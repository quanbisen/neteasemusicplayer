package controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import mediaplayer.MyMediaPlayer;
import model.Song;
import org.springframework.stereotype.Controller;
import util.WindowUtils;
import javax.annotation.Resource;

@Controller
public class PlayListController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private BorderPane borderPanePlayList;

    @FXML
    private Label labCloseIcon;

    @FXML
    private TableView tableViewPlayListSong;

    @FXML
    private TableColumn<Song,String> nameColumn;

    @FXML
    private TableColumn<Song,String> singerColumn;

    @FXML
    private TableColumn<Song,String> totalTimeColumn;

    @Resource
    private MainController mainController;

    @Resource
    private MyMediaPlayer myMediaPlayer;

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

        //设置表格列的宽度随这个borderPane的宽度而动态改变
        borderPanePlayList.widthProperty().addListener((observable, oldValue, newValue) -> {
            nameColumn.setPrefWidth(newValue.doubleValue()/4*2);
            singerColumn.setPrefWidth(newValue.doubleValue()/4*1);
            totalTimeColumn.setPrefWidth(newValue.doubleValue()/4*1);
        });


        tableViewPlayListSong.setItems(myMediaPlayer.getPlaySongList());

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
}
