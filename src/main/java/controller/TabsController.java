package controller;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import application.SpringFXMLLoader;
import util.StageUtils;
import util.WindowUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TabsController {

    /**左侧”搜索“标签Tab*/
    @FXML
    private HBox hBoxSearchTab;

    /**左侧”发现音乐“标签Tab*/
    @FXML
    private HBox hBoxExploreMusicTab;

    /**左侧本地音乐标签Tab*/
    @FXML
    private HBox hBoxLocalMusicTab;

    /**左侧"最近播放"标签Tab*/
    @FXML
    private HBox hBoxRecentPlayTab;

    /**左侧"我喜欢的音乐“标签Tab*/
    @FXML
    private HBox hBoxMyFavorMusicTab;

    /**装标签的集合tabList*/
    private List<HBox> tabList;

    @FXML
    private HBox hBoxUserInfo;

    /**注入窗体根容器（BorderPane）的控制类*/
    @Resource
    MainController mainController;

    /**注入窗体根容器（BorderPane）的中间容器的控制器*/
    @Resource
    CenterController centerController;

    /**注入window工具类*/
    @Resource
    private WindowUtils windowUtils;

    /**注入Spring上下文工具类*/
    @Resource
    private ConfigurableApplicationContext applicationContext;

    /**注入舞台工具*/
    @Resource
    private StageUtils stageUtils;


    @Resource
    private SpringFXMLLoader springFXMLLoader;

    public void initialize(){
        tabList = new ArrayList<>();
        tabList.add(hBoxSearchTab);
        tabList.add(hBoxExploreMusicTab);
        tabList.add(hBoxLocalMusicTab);
        tabList.add(hBoxRecentPlayTab);
        tabList.add(hBoxMyFavorMusicTab);
    }

    /**单击“搜索”标签事件处理*/
    @FXML
    public void onClickedSearchTab(MouseEvent mouseEvent) {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            this.setSelectedTab(hBoxSearchTab);  //设置当前选择的为“搜索”标签
            centerController.getBorderPane().setCenter(new Label("搜索"));
        }
    }

    /**单击“发现音乐”标签事件处理*/
    @FXML
    public void onClickedExplorerMusicTab(MouseEvent mouseEvent) {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            this.setSelectedTab(hBoxExploreMusicTab);
            centerController.getBorderPane().setCenter(new Label("发现音乐"));
        }
    }

    /**单击“本地音乐”标签事件处理*/
    @FXML
    public void onClickedLocalMusicTab(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            this.setSelectedTab(hBoxLocalMusicTab);
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/tab-localmusic-content.fxml");
            centerController.getBorderPane().setCenter(fxmlLoader.load());
        }
    }

    /**单击“最近播放”标签事件处理*/
    @FXML
    public void onClickedRecentPlayTab(MouseEvent mouseEvent) {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            this.setSelectedTab(hBoxRecentPlayTab);
            centerController.getBorderPane().setCenter(new Label("最近播放"));
        }
    }

    /**单击“我喜欢的音乐”标签事件处理*/
    @FXML
    public void onClickedMyFavorMusicTab(MouseEvent mouseEvent) {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            this.setSelectedTab(hBoxMyFavorMusicTab);
            centerController.getBorderPane().setCenter(new Label("我喜欢的音乐"));
        }
    }

    /**设置选择的标签背景颜色的函数*/
    private void setSelectedTab(HBox selectedTab){
        //首先重置所有的标签的背景颜色，我这里的HBox标签背景颜色是由另外一个HBox包裹做背景颜色显示的，所以需要getParent，设置parent的样式
        for (HBox hBoxTab:tabList){
            ((HBox)hBoxTab.getParent()).getStyleClass().remove("selectedHBox");  //移除parent的css类名
        }
        //然后给当前选中的标签的parent容器添加css类名
        ((HBox)selectedTab.getParent()).getStyleClass().add("selectedHBox");
    }

    /**单击添加歌单图标事件处理*/
    @FXML
    public void onClickedAddMusicGroup(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton()==MouseButton.PRIMARY){  //鼠标左击
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/create-musicgroup.fxml");  //加载添加音乐歌单的fxml文件
            Stage primaryStage = ((Stage)hBoxSearchTab.getScene().getWindow());              //获取主窗体的stage对象primaryStage
            Stage addMusicGroupStage = stageUtils.getStage(primaryStage,fxmlLoader.load());  //使用自定义工具获取Stage对象
            stageUtils.syncCenter(primaryStage,addMusicGroupStage);   //设置addMusicGroupStage对象居中到primaryStage
            windowUtils.blockBorderPane(mainController.getBorderPane());         //设置borderPane不响应鼠标事件和改变透明度
            addMusicGroupStage.showAndWait();  //显示并且等待

        }
    }

    /**单击“用户头像”HBox容器的事件处理*/
    @FXML
    public void onClickedHBoxUserInfo(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            if (centerController.getStackPane().getChildren().size()==1){

                FXMLLoader fxmlLoader = springFXMLLoader.getLoader("/fxml/right-slide.fxml");
                BorderPane borderPaneRoot = fxmlLoader.load();

                StackPane stackPane = centerController.getStackPane();
                stackPane.getChildren().add(borderPaneRoot);  //添加进stackPane
            }
        }
    }
}
