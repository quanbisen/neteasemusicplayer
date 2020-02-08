package controller.content;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.springframework.stereotype.Controller;

/**
 * @author super lollipop
 * @date 20-2-8
 */
@Controller
public class LocalMusicSingerController {

    @FXML
    private HBox hBoxPlayAll;

    @FXML
    private Button btnPlayAll;

    @FXML
    private BorderPane tableViewContainer;

    @FXML
    private TableView<?> tableViewSong;

    @FXML
    private TableColumn<?, ?> nameColumn;

    @FXML
    private TableColumn<?, ?> singerColumn;

    @FXML
    private TableColumn<?, ?> albumColumn;

    @FXML
    private TableColumn<?, ?> totalTimeColumn;

    @FXML
    private TableColumn<?, ?> sizeColumn;

    public void initialize(){
        //添加css名称.在CSS文件定制样式
        totalTimeColumn.getStyleClass().add("totalTimeColumn");
        sizeColumn.getStyleClass().add("sizeColumn");
        nameColumn.getStyleClass().add("nameColumn");
        singerColumn.getStyleClass().add("singerColumn");
        albumColumn.getStyleClass().add("albumColumn");

        //列属性绑定
        totalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("totalTime"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        singerColumn.setCellValueFactory(new PropertyValueFactory<>("singer"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));

        //设置表格列的宽度随这个borderPane的宽度而动态改变
        tableViewContainer.widthProperty().addListener((observable, oldValue, newValue) -> {
            totalTimeColumn.setPrefWidth(observable.getValue().doubleValue()/6.5*1);
            sizeColumn.setPrefWidth(observable.getValue().doubleValue()/6.5*1);
            nameColumn.setPrefWidth(observable.getValue().doubleValue()/6.5*2);
            singerColumn.setPrefWidth(observable.getValue().doubleValue()/6.5*1);
            albumColumn.setPrefWidth(observable.getValue().doubleValue()/6.5*1.5);
        });
    }


    /**“播放全部按钮的事件
     * @param mouseEvent”*/
    public void onClickedPlayAll(MouseEvent mouseEvent) {
    }
}
