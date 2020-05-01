package controller.content;

import application.SpringFXMLLoader;
import controller.component.SearchHistoryRecordController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
import mediaplayer.Config;
import org.dom4j.DocumentException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import service.SearchSongService;
import util.XMLUtils;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author super lollipop
 * @date 19-12-10
 */
@Component
public class SearchInputContentController {

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

    /**注入Spring上下文对象*/
    @Resource
    private ApplicationContext applicationContext;

    /**显示搜索结果的容器*/
    private Parent parent;

    @Resource
    private SearchResultContentController searchResultContentController;

    private List<Node> searchHistoryRecordList;

    public BorderPane getSearchInputContainer() {
        return searchInputContainer;
    }

    public TextField getTfSearchText() {
        return tfSearchText;
    }

    public VBox getvBoxHistoryContainer() {
        return vBoxHistoryContainer;
    }


    public void initialize() throws DocumentException, IOException {
        //初始化宽度、高度绑定
        vBoxHistoryContainer.prefWidthProperty().bind(scrollPane.widthProperty());
        vBoxHistoryContainer.prefHeightProperty().bind(scrollPane.heightProperty());

        labClearIcon.setVisible(false);  //初始化不可见

        searchHistoryRecordList = FXCollections.observableArrayList();    //初始化
        File searchHistoryFile = applicationContext.getBean(Config.class).getSearchHistoryFile();   //获取保存文件的句柄
        if (searchHistoryFile.exists()){  //如果文件存在，读取记录
            List<String> searchRecordList = XMLUtils.getAllRecord(searchHistoryFile,"SearchRecord","text");   //使用自定义XML工具获取SearchRecord元素下的所有属性为text对应的Value值
            if (searchRecordList != null && searchRecordList.size()>0){ //如果有记录
                for (String oneRecord:searchRecordList){    //遍历读取到的所有的记录“字符串类型”
                    FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/component/search-history-record.fxml");   //获取fxml文件加载器
                    searchHistoryRecordList.add(fxmlLoader.load()); //加载容器并添加到集合中去
                    SearchHistoryRecordController searchHistoryRecordController = fxmlLoader.getController();   //获取控制器
                    searchHistoryRecordController.getLabRecordText().setText(oneRecord);    //设置文本
                }
                Collections.reverse(searchHistoryRecordList);  //集合反转
                vBoxHistoryContainer.getChildren().addAll(searchHistoryRecordList); //添加到容器中去
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
                saveSearchText(tfSearchText.getText());    //保存当前文本
                startSearchService();                      //启动服务
            }
        }
    }

    /**搜索文本框的键盘按下事件处理*/
    @FXML
    public void onKeyPressedSearchTextField(KeyEvent keyEvent) throws IOException, DocumentException {
        if (keyEvent.getCode() == KeyCode.ENTER){  //如果按下的enter回车键才执行
            if (!tfSearchText.getText().trim().equals("")){     //有输入文本内容才执行
                saveSearchText(tfSearchText.getText());    //保存当前文本
                startSearchService();                      //启动服务
            }
        }
    }

    /**启动搜索服务的函数*/
    public void startSearchService() throws IOException {
        if (parent == null){    //容器对象为空时才加载
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/content/search-result-content.fxml");
            parent = fxmlLoader.load();
        }
        SearchSongService searchSongService = applicationContext.getBean(SearchSongService.class);  //获取服务对象
        searchResultContentController.getTableViewSong().itemsProperty().bind(searchSongService.valueProperty());  //搜索结果显示表格的内容绑定
        searchResultContentController.getProgressIndicator().visibleProperty().bind(searchSongService.runningProperty());  //加载指示器可见性绑定
        searchSongService.start();  //开始服务
        searchInputContainer.setCenter(parent); //设置容器的中间容器内容
    }

    /**保存搜索的记录的函数*/
    private void saveSearchText(String text) throws DocumentException, IOException {
        //获取已经保存的文本集合，存储在list中
        List<Node> vBoxChildren = vBoxHistoryContainer.getChildren();
        List<String> list = new ArrayList<>();
        for (Node node:vBoxChildren){
            list.add(((Label)((BorderPane)node).getCenter()).getText());
        }
        if (!list.contains(text)){  //如果保存的记录没有包含text文本
            //先更新GUI的
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/component/search-history-record.fxml");   //获取fxml文件加载器
            vBoxHistoryContainer.getChildren().add(0,fxmlLoader.load());  //加载容器并添加到搜索历史的容器vBoxHistoryContainer
            ((SearchHistoryRecordController)fxmlLoader.getController()).getLabRecordText().setText(text);    //设置文本
            //后保存记录到存储文件
            File searchHistoryFile = applicationContext.getBean(Config.class).getSearchHistoryFile();   //获取保存文件的句柄
            if (!searchHistoryFile.exists()){     //如果存储搜索历史的文件不存在
                XMLUtils.createXML(searchHistoryFile,"SearchRecordList"); //创建一个新的XML文件
            }
            XMLUtils.addOneRecord(searchHistoryFile,"SearchRecord","text",text);
        }
        else {  //否则，证明已有，需要把从第2个开始的，包含text文本的记录移除
            List<Node> recordNodeList = vBoxHistoryContainer.getChildren();
            for(int i=1;i<recordNodeList.size()-1;i++){
                if (((Label)((BorderPane)recordNodeList.get(i)).getCenter()).getText().equals(text)){
                    vBoxHistoryContainer.getChildren().remove(recordNodeList.get(i));
                    break;
                }
            }
        }

    }

    /**单击“垃圾”图标的事件处理，清除所有的存储记录*/
    @FXML
    public void onClickedTrashBin(MouseEvent mouseEvent) {
        if(mouseEvent.getButton() == MouseButton.PRIMARY){
            vBoxHistoryContainer.getChildren().remove(0,vBoxHistoryContainer.getChildren().size()); //删除GUI的记录
            File searchHistoryFile = applicationContext.getBean(Config.class).getSearchHistoryFile();   //获取保存文件的句柄
            if (searchHistoryFile.exists()){  //如果保存搜索记录的文件存在
                searchHistoryFile.delete();   //删除它
            }
        }
    }
}
