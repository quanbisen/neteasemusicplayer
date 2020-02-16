package controller.main;

import controller.component.MusicGroupTabController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import mediaplayer.Config;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import application.SpringFXMLLoader;
import util.ImageUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Controller
public class LeftController {

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

    /**装标签的集合tabList*/
    private List<HBox> tabList;

    /**存储标签的VBox容器*/
    @FXML
    private VBox vBoxTabContainer;

    /**显示用户头像的Label组件*/
    @FXML
    private Label labUserImage;

    /**显示用户名称的Label组件*/
    @FXML
    private Label labUserName;

    /**注入窗体根容器（BorderPane）的控制类*/
    @Resource
    private MainController mainController;

    /**注入窗体根容器（BorderPane）的中间容器的控制器*/
    @Resource
    private CenterController centerController;

    /**注入Spring上下文工具类*/
    @Resource
    private ApplicationContext applicationContext;

    /**"本地音乐"标签的内容容器*/
    private Parent localMusicParent;

    public VBox getVBoxTabContainer() {
        return vBoxTabContainer;
    }

    public List<HBox> getTabList() {
        return tabList;
    }

    public Label getLabUserImage() {
        return labUserImage;
    }

    public Label getLabUserName() {
        return labUserName;
    }

    public Parent getLocalMusicParent() {
        return localMusicParent;
    }

    public void initialize() throws IOException {

//        LoadUserService loadUserService = applicationContext.getBean(LoadUserService.class);  //启动加载用户的服务
//        loadUserService.setPeriod(Duration.seconds(2));
//        loadUserService.setMaximumFailureCount(10);
//        loadUserService.setOnSucceeded(event -> System.out.println("success"));
//        loadUserService.setOnFailed(event -> System.out.println("fail"));
//        loadUserService.setOnCancelled(event -> System.out.println("cancel"));
//        loadUserService.setOnReady(event -> System.out.println("ready"));
//        loadUserService.setOnRunning(event -> System.out.println("running"));
////        loadUserService.setDelay(Duration.seconds(2));
//        loadUserService.start();


        tabList = new LinkedList<>();
        tabList.add(hBoxSearchTab);
        tabList.add(hBoxExploreMusicTab);
        tabList.add(hBoxLocalMusicTab);
        tabList.add(hBoxRecentPlayTab);

        this.setSelectedTab(hBoxExploreMusicTab);   //初始化“发现音乐”为选择的标签

        if (applicationContext.getBean(Config.class).getUser() != null){
            labUserImage.setGraphic(ImageUtils.createImageView(File.separator + "cache"+File.separator+applicationContext.getBean(Config.class).getUser().getCache(),38,38));
            labUserName.setText(applicationContext.getBean(Config.class).getUser().getName());  //设置用户名称*/

            //加载歌单指示器和"我喜欢的音乐"tab标签
            vBoxTabContainer.getChildren().add(applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/component/musicgroup-indicator.fxml").load());   //歌单指示器组件
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/component/musicgroup-tab.fxml");    //"我喜欢的音乐"tab
            vBoxTabContainer.getChildren().add(fxmlLoader.load());
            Image imageFavorTabIcon = new Image("/image/FavorTabIcon.png",20,20,true,true);
            MusicGroupTabController musicGroupTabController = fxmlLoader.getController();
            musicGroupTabController.getIvMusicGroupIcon().setImage(imageFavorTabIcon);
            musicGroupTabController.getLabGroupName().setText("我喜欢的音乐");
            tabList.add(musicGroupTabController.getHBoxMusicGroup());

            //加载用户创建的歌单tab标签
            FXMLLoader loader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/component/musicgroup-tab.fxml");    //"我喜欢的音乐"tab
            vBoxTabContainer.getChildren().add(loader.load());
            MusicGroupTabController musicGroupTabController1 = loader.getController();
            musicGroupTabController1.getLabGroupName().setText("test");
            tabList.add(musicGroupTabController1.getHBoxMusicGroup());
        }
    }

    /**单击“搜索”标签事件处理*/
    @FXML
    public void onClickedSearchTab(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            this.setSelectedTab(hBoxSearchTab);  //设置当前选择的为“搜索”标签
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/content/tab-searchinput-content.fxml");
            centerController.getBorderPane().setCenter(fxmlLoader.load());
        }
    }

    /**单击“发现音乐”标签事件处理*/
    @FXML
    public void onClickedExplorerMusicTab(MouseEvent mouseEvent) {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            this.setSelectedTab(hBoxExploreMusicTab);

//                exploreMusicParent = new Label("发现音乐");

            Slider slider = new Slider(0, 1, 0.5);
            slider.setShowTickMarks(true);
            slider.setShowTickLabels(true);
            slider.setMajorTickUnit(0.2f);
            slider.setContextMenu(null);
            slider.setBlockIncrement(0.1f);
            centerController.getBorderPane().setCenter(slider);
        }
    }

    /**单击“本地音乐”标签事件处理*/
    @FXML
    public void onClickedLocalMusicTab(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            this.setSelectedTab(hBoxLocalMusicTab);
            if (localMusicParent == null){
                FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/content/tab-localmusic-content.fxml");
                localMusicParent = fxmlLoader.load();
            }
            centerController.getBorderPane().setCenter(localMusicParent);   //设置容器的中间容器内容
        }
    }

    /**单击“最近播放”标签事件处理*/
    @FXML
    public void onClickedRecentPlayTab(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            this.setSelectedTab(hBoxRecentPlayTab);
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/content/tab-recentplay-content.fxml");
            centerController.getBorderPane().setCenter(fxmlLoader.load());
        }
    }

    /**设置选择的标签背景颜色的函数*/
    public void setSelectedTab(HBox selectedTab){
        //首先重置所有的标签的背景颜色，我这里的HBox标签背景颜色是由另外一个HBox包裹做背景颜色显示的，所以需要getParent，设置parent的样式
        for (HBox hBoxTab:tabList){
            hBoxTab.getStyleClass().remove("selectedHBox");  //移除parent的css类名
        }
        //然后给当前选中的标签的parent容器添加css类名
        selectedTab.getStyleClass().add("selectedHBox");
    }

    /**单击“用户头像”HBox容器的事件处理*/
    @FXML
    public void onClickedHBoxUserInfo(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            if (centerController.getStackPane().getChildren().size()==1){
                if (applicationContext.getBean(Config.class).getUser() == null){  //如果登录配置文件不存在，则没有登录
                    FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/authentication/right-slide-unlogin.fxml");
                    BorderPane borderPaneRoot = fxmlLoader.load();

                    StackPane stackPane = centerController.getStackPane();
                    stackPane.getChildren().add(borderPaneRoot);  //添加进stackPane
                }
                else {  //如果登录对象存在，则已经登录过了。
                    FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/authentication/right-slide-login.fxml");
                    BorderPane borderPaneRoot = fxmlLoader.load();

                    StackPane stackPane = centerController.getStackPane();
                    stackPane.getChildren().add(borderPaneRoot);  //添加进stackPane
                }
            }
        }
    }
}
