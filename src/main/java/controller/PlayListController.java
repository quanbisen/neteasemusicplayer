package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;
import util.WindowUtils;

@Controller
public class PlayListController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private BorderPane borderPanePlayList;

    @FXML
    private Label labCloseIcon;

    public void initialize(){

        anchorPane.prefWidthProperty().addListener(((observable, oldValue, newValue) -> {
            double offSetX = 0;
            if (WindowUtils.isWindowsPlatform() && !((Stage)anchorPane.getScene().getWindow()).isMaximized()){
                offSetX = offSetX + 10;
            }
            borderPanePlayList.setLayoutX(observable.getValue().doubleValue()-borderPanePlayList.getWidth()- offSetX);
        }));
        anchorPane.prefHeightProperty().addListener(((observable, oldValue, newValue) -> {
            double offSetY = 40;
            if (WindowUtils.isWindowsPlatform() && !((Stage)anchorPane.getScene().getWindow()).isMaximized()){
                offSetY = offSetY + 10;
            }
            borderPanePlayList.setLayoutY(observable.getValue().doubleValue()-borderPanePlayList.getHeight()- offSetY);
        }));
        Platform.runLater(()->{
            //宽度高度绑定
            anchorPane.prefWidthProperty().bind(((StackPane)anchorPane.getParent()).widthProperty());
            anchorPane.prefHeightProperty().bind(((StackPane)anchorPane.getParent()).heightProperty());
            Platform.runLater(()->{
                double offSetX = 0;
                double offSetY = 40;
                if (WindowUtils.isWindowsPlatform()){
                    offSetX = offSetX + 10;
                    offSetY = offSetY + 10;
                }
                borderPanePlayList.setLayoutX(anchorPane.getWidth()-borderPanePlayList.getWidth()- offSetX);
                borderPanePlayList.setLayoutY(anchorPane.getHeight()-borderPanePlayList.getHeight()-offSetY);
            });
        });
    }

    /**关闭图标按钮的鼠标事件*/
    @FXML
    public void onClickedCloseIcon(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){ //鼠标左击
            labCloseIcon.getScene().getWindow().hide();     //隐藏窗体
        }
    }
}
