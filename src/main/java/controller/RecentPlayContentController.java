package controller;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.layout.BorderPane;
import org.springframework.stereotype.Controller;

@Controller
public class RecentPlayContentController {
    /**“最近播放”标签内容的根容器*/
    @FXML
    private BorderPane recentPlayContentContainer;

    public void initialize(){
        recentPlayContentContainer.setCursor(Cursor.DEFAULT);
    }
}
