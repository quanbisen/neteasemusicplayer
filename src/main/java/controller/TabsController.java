package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;
import util.SpringFXMLLoader;
import util.StageUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TabsController {
/*    *//**左侧标签的VBox容器*//*
    @FXML
    private VBox vBoxTabContainer;*/
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
    /**注入窗体根容器（BorderPane）的控制类*/
    @Resource
    MainController mainController;

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
        }
    }

    /**单击“发现音乐”标签事件处理*/
    @FXML
    public void onClickedExplorerMusicTab(MouseEvent mouseEvent) {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            this.setSelectedTab(hBoxExploreMusicTab);
        }
    }

    /**单击“本地音乐”标签事件处理*/
    @FXML
    public void onClickedLocalMusicTab(MouseEvent mouseEvent) {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            this.setSelectedTab(hBoxLocalMusicTab);
        }
    }

    /**单击“最近播放”标签事件处理*/
    @FXML
    public void onClickedRecentPlayTab(MouseEvent mouseEvent) {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            this.setSelectedTab(hBoxRecentPlayTab);
        }
    }

    /**单击“我喜欢的音乐”标签事件处理*/
    @FXML
    public void onClickedMyFavorMusicTab(MouseEvent mouseEvent) {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            this.setSelectedTab(hBoxMyFavorMusicTab);
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
            FXMLLoader fxmlLoader = SpringFXMLLoader.getLoader();  //加载添加音乐歌单的fxml文件
            fxmlLoader.setLocation(this.getClass().getResource("/fxml/add-musicgroup.fxml"));
            Stage primaryStage = ((Stage)hBoxSearchTab.getScene().getWindow());              //获取主窗体的stage对象primaryStage
            Stage addMusicGroupStage = StageUtils.getStage(primaryStage,fxmlLoader.load());  //使用自定义工具获取Stage对象
            StageUtils.syncCenter(primaryStage,addMusicGroupStage);   //设置addMusicGroupStage对象居中到primaryStage
            this.blockBorderPane();            //设置borderPane不响应鼠标事件和改变透明度
            addMusicGroupStage.showAndWait();  //显示并且等待

        }
    }

    /**阻止主舞台的borderPane响应鼠标事件和改变不透明度的函数*/
    private void blockBorderPane(){
        BorderPane borderPane = mainController.getBorderPane();  //通过Spring注入的mainContainer获取主舞台的根容器borderPane
        //设置主舞台界面borderPane除了顶部的titleBar部分外，其它的部分都不响应鼠标事件
        borderPane.getCenter().setMouseTransparent(true);
        borderPane.getBottom().setMouseTransparent(true);
        //顺便设置不透明色，方便提示查看
        borderPane.getCenter().setOpacity(0.4);
        borderPane.getBottom().setOpacity(0.4);
    }
}
