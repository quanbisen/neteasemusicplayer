package controller;

import application.SpringFXMLLoader;
import dao.SongDao;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import model.Song;
import org.dom4j.DocumentException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import service.SearchSongService;
import util.XMLUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author super lollipop
 * @date 19-12-10
 */
@Component
public class SearchInputController {

    /**搜索页面的根容器*/
    @FXML
    private BorderPane searchInputContainer;

    /**搜索页面的根容器searchInputContainer的Center内容容器*/
    @FXML
    public BorderPane searchInputCenter;

    /**滚动容器存储VBox，然后VBox容器放置一条条搜索记录*/
    @FXML
    private ScrollPane scrollPane;

    /**VBox容器放置一条条搜索记录*/
    @FXML
    private VBox vBoxHistoryContainer;

    /**清除输入的搜索内容的label图标*/
    @FXML
    private Label labClearIcon;

    /**输入搜索内容的文本框TextField*/
    @FXML
    private TextField tfSearchText;

    /**注入歌曲的数据库操作类*/
    @Resource
    private SongDao songDao;

    /**注入Spring上下文对象*/
    @Resource
    private ConfigurableApplicationContext applicationContext;

    /**显示搜索结果的容器*/
    private Parent parent;

    @Resource
    private SearchResultController searchResultController;

    @Resource
    private SearchHistoryRecordController searchHistoryRecordController;

    /**播放器搜索历史的记录文件存放路径*/
    private String SEARCH_HISTORY_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "config" + File.separator + "search-history.xml";

    /**播放器搜索历史的记录文件*/
    private File SEARCH_HISTORY_FILE;

    public BorderPane getSearchInputContainer() {
        return searchInputContainer;
    }

    public TextField getTfSearchText() {
        return tfSearchText;
    }

    public void initialize() throws DocumentException, IOException {
        //初始化宽度、高度绑定
        vBoxHistoryContainer.prefWidthProperty().bind(scrollPane.widthProperty());
        vBoxHistoryContainer.prefHeightProperty().bind(scrollPane.heightProperty());

        labClearIcon.setVisible(false);  //初始化不可见

        SEARCH_HISTORY_FILE = new File(SEARCH_HISTORY_PATH);
        if (SEARCH_HISTORY_FILE.exists()){  //如果文件存在，读取记录
            List<String> searchRecordList = XMLUtils.getAllRecord(SEARCH_HISTORY_FILE,"SearchRecord","text");
            if (searchRecordList != null && searchRecordList.size()>0){
                for (String oneRecord:searchRecordList){
                    FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/search-history-record.fxml");
                    vBoxHistoryContainer.getChildren().add(fxmlLoader.load());
                    searchHistoryRecordController.getLabRecordText().setText(oneRecord);
                }

            }
        }

        tfSearchText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.equals("")){
                    labClearIcon.setVisible(true);
                }
                else {
                    labClearIcon.setVisible(false);
                    searchInputContainer.setCenter(searchInputCenter);
                }
            }
        });

    }

    /**单击"清除"图标的事件处理*/
    @FXML
    public void onClickedClearIcon(MouseEvent mouseEvent) {
        if(mouseEvent.getButton() == MouseButton.PRIMARY){
            tfSearchText.setText("");
        }
    }

    /**单击"搜索"图标的事件处理*/
    @FXML
    public void onClickedSearchIcon(MouseEvent mouseEvent) throws IOException, DocumentException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            if (!tfSearchText.getText().trim().equals("")) {     //有输入文本内容才执行
                this.saveSearchText(tfSearchText.getText());    //保存当前文本
                this.startSearchService();                      //启动服务
            }
        }
    }

    /**搜索文本框的键盘按下事件处理*/
    @FXML
    public void onKeyPressedSearchTextField(KeyEvent keyEvent) throws IOException, DocumentException {
        if (keyEvent.getCode() == KeyCode.ENTER){  //如果按下的enter回车键才执行
            if (!tfSearchText.getText().trim().equals("")){     //有输入文本内容才执行
                this.saveSearchText(tfSearchText.getText());    //保存当前文本
                this.startSearchService();                      //启动服务
            }
        }
    }

    /**启动搜索服务的函数*/
    private void startSearchService() throws IOException {
        if (parent == null){    //容器对象为空时才加载
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/search-result.fxml");
            parent = fxmlLoader.load();
        }
        SearchSongService searchSongService = applicationContext.getBean(SearchSongService.class);  //获取服务对象
        searchResultController.getTableViewSong().itemsProperty().bind(searchSongService.valueProperty());  //搜搜结果显示表格的内容绑定
        searchResultController.getProgressIndicator().visibleProperty().bind(searchSongService.runningProperty());  //加载指示器可见性绑定
        searchSongService.start();  //开始服务
        searchInputContainer.setCenter(parent); //设置容器的中间容器内容
    }

    /**保存搜索的记录的文件*/
    private void saveSearchText(String text) throws DocumentException {
        if (!SEARCH_HISTORY_FILE.exists()){     //如果存储搜索历史的文件不存在
            XMLUtils.createXML(SEARCH_HISTORY_FILE,"SearchRecordList"); //创建一个新的XML文件
        }
        List<String> searchRecordList = XMLUtils.getAllRecord(SEARCH_HISTORY_FILE,"SearchRecord","text");   //获取文件中已经存储的记录
        if(searchRecordList == null){   //如果searchRecordList为空，证明没有记录，直接添加
            XMLUtils.addOneRecord(SEARCH_HISTORY_FILE,"SearchRecord","text",text);
        }
        else {  //否则，还需要进一步判断
            if (!searchRecordList.contains(text)){  //如果原来存储的记录没有本记录，才添加进去
                XMLUtils.addOneRecord(SEARCH_HISTORY_FILE,"SearchRecord","text",text);
            }
        }
    }
}
