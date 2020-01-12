package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import application.SpringFXMLLoader;
import service.LoadSongService;
import util.StageUtils;
import util.UserUtils;
import util.WindowUtils;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
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
    MainController mainController;

    /**注入窗体根容器（BorderPane）的中间容器的控制器*/
    @Resource
    CenterController centerController;

    /**注入Spring上下文工具类*/
    @Resource
    private ApplicationContext applicationContext;

    /**播放器登录文件的存放路径*/
    private String Login_CONFIG_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "config" + File.separator + "login-config.properties";

    /**播放器登录配置文件*/
    private File LOGIN_CONFIG_FILE;

    /**"搜索"标签的内容容器*/
    private Parent searchParent;

    /**"发现音乐"标签的内容容器*/
    private Parent exploreMusicParent;

    /**"本地音乐"标签的内容容器*/
    private Parent localMusicParent;

    /**"最近播放"标签的内容容器*/
    private Parent recentPlayParent;

    /**"我喜欢的音乐"标签的内容容器*/
    private Parent myFavorMusicParent;

    @Resource
    private LocalMusicContentController localMusicContentController;

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

    public File getLOGIN_CONFIG_FILE() {
        return LOGIN_CONFIG_FILE;
    }

    public void initialize() throws IOException {

        tabList = new ArrayList<>();
        tabList.add(hBoxSearchTab);
        tabList.add(hBoxExploreMusicTab);
        tabList.add(hBoxLocalMusicTab);
        tabList.add(hBoxRecentPlayTab);
        tabList.add(hBoxMyFavorMusicTab);

        this.setSelectedTab(hBoxExploreMusicTab);   //初始化“发现音乐”为选择的标签

        LOGIN_CONFIG_FILE = new File(Login_CONFIG_PATH);  //初始化播放器登录文件

        if (LOGIN_CONFIG_FILE.exists()){   //如果登录配置文件存在，读取设置，显示登录用户的信息
            User user = UserUtils.parseUser(LOGIN_CONFIG_FILE);  //解析用户对象
            String urlString = user.getImage();
            String imageName = urlString.substring(urlString.lastIndexOf("/")+1);
            String USER_IMAGE_PATH = "/config" + File.separator + user.getId() + File.separator + imageName;
            ImageView imageView = new ImageView(new Image(USER_IMAGE_PATH));
            imageView.setFitWidth(38);
            imageView.setFitHeight(38);
            labUserImage.setGraphic(imageView);   //设置用户头像
            labUserName.setText(user.getName());  //设置用户名称

        }
    }

    /**单击“搜索”标签事件处理*/
    @FXML
    public void onClickedSearchTab(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            this.setSelectedTab(hBoxSearchTab);  //设置当前选择的为“搜索”标签
            if (searchParent == null){
                FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/tab-search-input.fxml");
                searchParent = fxmlLoader.load();
            }
            centerController.getBorderPane().setCenter(searchParent);
        }
    }

    /**单击“发现音乐”标签事件处理*/
    @FXML
    public void onClickedExplorerMusicTab(MouseEvent mouseEvent) {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            this.setSelectedTab(hBoxExploreMusicTab);
            if (exploreMusicParent == null){
                exploreMusicParent = new Label("发现音乐");
            }
            centerController.getBorderPane().setCenter(exploreMusicParent);
        }
    }

    /**单击“本地音乐”标签事件处理*/
    @FXML
    public void onClickedLocalMusicTab(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            this.setSelectedTab(hBoxLocalMusicTab);
            if (localMusicParent == null){
                FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/tab-localmusic-content.fxml");
                localMusicParent = fxmlLoader.load();
                /*LoadSongService loadSongService = applicationContext.getBean(LoadSongService.class);  //获取服务对象
                localMusicContentController.getTableViewSong().itemsProperty().bind(loadSongService.valueProperty());  //搜搜结果显示表格的内容绑定
                localMusicContentController.getProgressIndicator().visibleProperty().bind(loadSongService.runningProperty());
                loadSongService.start();  //开始服务*/
            }

            centerController.getBorderPane().setCenter(localMusicParent);   //设置容器的中间容器内容
        }
    }

    /**单击“最近播放”标签事件处理*/
    @FXML
    public void onClickedRecentPlayTab(MouseEvent mouseEvent) {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            this.setSelectedTab(hBoxRecentPlayTab);
            if (recentPlayParent == null){
                recentPlayParent = new Label("最近播放");
            }
            centerController.getBorderPane().setCenter(recentPlayParent);
        }
    }

    /**单击“我喜欢的音乐”标签事件处理*/
    @FXML
    public void onClickedMyFavorMusicTab(MouseEvent mouseEvent) {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            this.setSelectedTab(hBoxMyFavorMusicTab);
            if (myFavorMusicParent == null){
                myFavorMusicParent = new Label("我喜欢的音乐");
            }
            centerController.getBorderPane().setCenter(myFavorMusicParent);
        }
    }

    /**设置选择的标签背景颜色的函数*/
    public void setSelectedTab(HBox selectedTab){
        //首先重置所有的标签的背景颜色，我这里的HBox标签背景颜色是由另外一个HBox包裹做背景颜色显示的，所以需要getParent，设置parent的样式
        for (HBox hBoxTab:tabList){
            ((HBox)hBoxTab.getParent()).getStyleClass().remove("selectedHBox");  //移除parent的css类名
        }
        //然后给当前选中的标签的parent容器添加css类名
        ((HBox)selectedTab.getParent()).getStyleClass().add("selectedHBox");
    }

    /**单击添加歌单图标事件处理*/
    @FXML
    public void onClickedCreateMusicGroup(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton()==MouseButton.PRIMARY){  //鼠标左击
            if (!LOGIN_CONFIG_FILE.exists()){   //判断用户是否登录过了。
                WindowUtils.toastInfo(centerController.getStackPane(),new Label("请先登录！"));
            }
            else{
                FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/create-musicgroup.fxml");  //加载添加音乐歌单的fxml文件
                Stage primaryStage = ((Stage)hBoxSearchTab.getScene().getWindow());              //获取主窗体的stage对象primaryStage
                Stage createMusicGroupStage = StageUtils.getStage(primaryStage,fxmlLoader.load());  //使用自定义工具获取Stage对象
                StageUtils.synchronizeCenter(primaryStage,createMusicGroupStage);   //设置createMusicGroupStage对象居中到primaryStage
                WindowUtils.blockBorderPane(mainController.getBorderPane());         //设置borderPane不响应鼠标事件和改变透明度
                createMusicGroupStage.showAndWait();  //显示并且等待
            }
        }
    }

    /**单击“用户头像”HBox容器的事件处理*/
    @FXML
    public void onClickedHBoxUserInfo(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            if (centerController.getStackPane().getChildren().size()==1){
                if (!LOGIN_CONFIG_FILE.exists()){  //如果登录配置文件不存在，则没有登录
                    FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/right-slide-unlogin.fxml");
                    BorderPane borderPaneRoot = fxmlLoader.load();

                    StackPane stackPane = centerController.getStackPane();
                    stackPane.getChildren().add(borderPaneRoot);  //添加进stackPane
                }
                else {  //如果登录配置文件存在，则已经登录过了。
                    FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/right-slide-logined.fxml");
                    BorderPane borderPaneRoot = fxmlLoader.load();

                    StackPane stackPane = centerController.getStackPane();
                    stackPane.getChildren().add(borderPaneRoot);  //添加进stackPane
                }
            }
        }
    }
}
