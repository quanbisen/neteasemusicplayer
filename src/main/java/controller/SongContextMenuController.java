package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.springframework.stereotype.Controller;
import javax.annotation.Resource;
import java.io.IOException;

@Controller
public class SongContextMenuController {

    @Resource
    private LocalMusicContentController localMusicContentController;

    public void initialize(){

    }

    @FXML
    public void onClickedNextPlay(ActionEvent actionEvent) throws IOException {
    }
}
