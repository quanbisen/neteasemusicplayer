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
import org.springframework.stereotype.Controller;
import util.ImageUtils;

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

    public void initialize(){
        recentPlayContentContainer.setCursor(Cursor.DEFAULT);
        progressIndicator.setVisible(false);    //初始化不可见

        /**属性绑定*/
        indexColumn.setCellValueFactory(new PropertyValueFactory<RecentSong,String>("index"));
        addFavorColumn.setCellValueFactory(new PropertyValueFactory<RecentSong, Label>("labAddFavor"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<RecentSong,String>("name"));
        singerColumn.setCellValueFactory(new PropertyValueFactory<RecentSong,String>("singer"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<RecentSong,String>("album"));
        totalTimeColumn.setCellValueFactory(new PropertyValueFactory<RecentSong,String>("totalTime"));

        borderPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                System.out.println("change");
                nameColumn.setPrefWidth((observable.getValue().doubleValue() - indexColumn.getMaxWidth() - addFavorColumn.getMaxWidth())/4.5*2);
                singerColumn.setPrefWidth((observable.getValue().doubleValue() - indexColumn.getMaxWidth() - addFavorColumn.getMaxWidth())/4.5*1);
                albumColumn.setPrefWidth((observable.getValue().doubleValue() - indexColumn.getMaxWidth() - addFavorColumn.getMaxWidth())/4.5*1);
                totalTimeColumn.setPrefWidth((observable.getValue().doubleValue() - indexColumn.getMaxWidth() - addFavorColumn.getMaxWidth())/4.5*0.5);
            }
        });
        ObservableList<RecentSong> observableList = FXCollections.observableArrayList();
        ImageView imageView = ImageUtils.createImageView("/image/FavorTabIcon.png",20,20);
        Label label = new Label("",imageView);
        label.setOnMouseClicked(event -> {
            System.out.println("clicked ...");
        });
        observableList.add(new RecentSong("01", label,"1","1","1","1",null));
        tableViewRecentPlaySong.setItems(observableList);
    }
}
