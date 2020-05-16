package controller.component;

import controller.content.SearchInputContentController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import mediaplayer.Config;
import org.dom4j.DocumentException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import util.XMLUtils;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

@Controller
@Scope("prototype")
public class SearchHistoryRecordController {

    @FXML
    private BorderPane oneHistoryContainer;

    @FXML
    private Label labRecordText;

    @FXML
    private ImageView ivDelete;

    @Resource
    private SearchInputContentController searchInputContentController;

    @Resource
    private ApplicationContext applicationContext;

    public Label getLabRecordText() {
        return labRecordText;
    }

    /**初始化，最先执行这里...*/
    public void initialize(){
        labRecordText.prefWidthProperty().bind(oneHistoryContainer.widthProperty());
    }

    /**单击记录清除图标的事件处理*/
    @FXML
    public void onClickedClear(MouseEvent mouseEvent) throws DocumentException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            searchInputContentController.getvBoxHistoryContainer().getChildren().remove(ivDelete.getParent());    //GUI更新，移除本容器
            File searchHistoryFile = applicationContext.getBean(Config.class).getSearchHistoryFile();   //获取保存文件的句柄
            if (searchHistoryFile.exists()){   //如果存储的文件存在
                XMLUtils.removeOneRecord(searchHistoryFile,"text",labRecordText.getText());    //移除本条记录
            }
        }
    }

    /**单击搜索记录的事件处理*/
    @FXML
    public void onClickedRecordSearch(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            searchInputContentController.getTfSearchText().setText(labRecordText.getText());
            searchInputContentController.startSearchService();
        }
    }
}
