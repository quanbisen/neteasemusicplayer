package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 19-12-10
 */
@Component
public class SearchContentController {


    /**根容器BorderPane*/
    @FXML
    private BorderPane searchContentContainer;

    /**搜索栏的HBox容器*/
    @FXML
    private HBox hBoxSearch;

    @FXML
    private ScrollPane scrollPaneHistory;

    @Resource
    private MainController mainController;


    public void initialize(){
        searchContentContainer.prefHeightProperty().bind(((Pane)mainController.getBorderPane().getCenter()).heightProperty());
        searchContentContainer.prefWidthProperty().bind(((Pane)mainController.getBorderPane().getCenter()).widthProperty());
        hBoxSearch.prefWidthProperty().bind(((Pane)mainController.getBorderPane().getCenter()).widthProperty());
    }
}
