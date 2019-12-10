package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 19-12-10
 */
@Component
public class SearchContentController {

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

    @Resource
    private MainController mainController;


    public void initialize(){
        //初始化宽度、高度绑定
        vBoxHistoryContainer.prefWidthProperty().bind(scrollPane.widthProperty());
        vBoxHistoryContainer.prefHeightProperty().bind(scrollPane.heightProperty());

        labClearIcon.setVisible(false);  //初始化不可见

        tfSearchText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.equals("")){
                    labClearIcon.setVisible(true);
                }
                else {
                    labClearIcon.setVisible(false);
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
    public void onClickedSearchIcon(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            if (!tfSearchText.getText().trim().equals("")) {     //有输入文本内容才执行
                System.out.println("need to search.");
            }
        }
    }

    /**搜索文本框的键盘按下事件处理*/
    @FXML
    public void onKeyPressedSearchTextField(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER){  //如果按下的enter回车键才执行
            if (!tfSearchText.getText().trim().equals("")){     //有输入文本内容才执行
                System.out.println("press enter");
            }
        }
    }
}
