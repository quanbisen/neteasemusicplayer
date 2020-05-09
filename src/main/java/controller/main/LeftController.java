package controller.main;

import controller.component.GroupTabController;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import mediaplayer.Config;
import mediaplayer.UserStatus;
import org.dom4j.DocumentException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import application.SpringFXMLLoader;
import pojo.Group;
import pojo.User;
import util.ImageUtils;
import util.XMLUtils;
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

    /**记录选择的标签索引*/
    private int selectedTabIndex = -1;

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

    public int getSelectedTabIndex() {
        return selectedTabIndex;
    }

    public void initialize() throws IOException {

        tabList = new LinkedList<>();
        tabList.add(hBoxSearchTab);
        tabList.add(hBoxExploreMusicTab);
        tabList.add(hBoxLocalMusicTab);
        tabList.add(hBoxRecentPlayTab);

        this.setSelectedTab(hBoxExploreMusicTab);   //初始化“发现音乐”为选择的标签

        User user = applicationContext.getBean(UserStatus.class).getUser();
        if (user != null){
            labUserName.setText(user.getName());  //设置用户名称*/
        }

        labUserImage.setClip(new Circle(19,19,19)); //切割用户头像
    }



    /**单击“搜索”标签事件处理*/
    @FXML
    public void onClickedSearchTab(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            if (getSelectedTab() != hBoxSearchTab){
                this.setSelectedTab(hBoxSearchTab);  //设置当前选择的为“搜索”标签
                FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/content/tab-searchinput-content.fxml");
                centerController.getBorderPane().setCenter(fxmlLoader.load());
            }else if (mouseEvent.getClickCount() == 5){
                this.setSelectedTab(hBoxSearchTab);  //设置当前选择的为“搜索”标签
                FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/content/tab-searchinput-content.fxml");
                centerController.getBorderPane().setCenter(fxmlLoader.load());
            }

        }
    }

    /**单击“发现音乐”标签事件处理*/
    @FXML
    public void onClickedExplorerMusicTab(MouseEvent mouseEvent) {
        if (mouseEvent.getButton()== MouseButton.PRIMARY){  //鼠标左击
            if (getSelectedTab() != hBoxExploreMusicTab){   //普通单击时,不是当前选中的tab
                this.setSelectedTab(hBoxExploreMusicTab);
                centerController.getBorderPane().setCenter(new Label("敬请期待"));
            }else if (mouseEvent.getClickCount() == 5){     //用作fireEvent操作
                this.setSelectedTab(hBoxExploreMusicTab);
                centerController.getBorderPane().setCenter(new Label("敬请期待"));
            }
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
            if (getSelectedTab() != hBoxRecentPlayTab){     //普通单击时,不是当前选中的tab
                this.setSelectedTab(hBoxRecentPlayTab);
                FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/content/tab-recentplay-content.fxml");
                centerController.getBorderPane().setCenter(fxmlLoader.load());
            }else if (mouseEvent.getClickCount() == 5){ //用作fireEvent操作
                this.setSelectedTab(hBoxRecentPlayTab);
                FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/content/tab-recentplay-content.fxml");
                centerController.getBorderPane().setCenter(fxmlLoader.load());
            }
        }
    }

    /**单击“用户头像”HBox容器的事件处理*/
    @FXML
    public void onClickedHBoxUserInfo(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            if (centerController.getStackPane().getChildren().size()==1){
                FXMLLoader fxmlLoader;
                if (applicationContext.getBean(UserStatus.class).getUser() == null){  //如果登录配置文件不存在，则没有登录
                    fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/user/right-slide-unlogin.fxml");
                }
                else {  //如果登录对象存在，则已经登录过了。
                    fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/user/right-slide-login.fxml");
                }
                BorderPane borderPaneRoot = fxmlLoader.load();
                StackPane stackPane = centerController.getStackPane();
                stackPane.getChildren().add(borderPaneRoot);  //添加进stackPane
            }
        }
    }

    /**根据user对象更新左侧UI的显示，包括歌单名称、用户信息等
     * @param user 用户对象
     */
    public void updateUserAndGroupList(User user) throws IOException {
        updateUserInformation(user);
        updateUserGroupList(user);
    }

    /**用户呢称和头像文件UI更新
     * @param user 用户对象*/
    public void updateUserInformation(User user){
        labUserName.setText(user.getName());
        new javafx.concurrent.Service<Void>() {     //创建一个加载在线图片资源的服务。
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        if (user.getImageURL() != null){
                            Image image = new Image(user.getImageURL(),38,38,true,true);
                            if (!image.isError()){
                                ((ImageView)labUserImage.getGraphic()).setImage(image);
                            }else if (user.getLocalImagePath() != null && new File(user.getLocalImagePath().substring(5)).exists()){   //如果本地存储的图片文件存在，设置图片显示
                                ((ImageView)labUserImage.getGraphic()).setImage(new Image(user.getLocalImagePath(),38,38,true,true));
                            } else {
                                ((ImageView)labUserImage.getGraphic()).setImage(new Image("image/UserDefaultImage.png",38,38,true,true));
                            }
                        }
                        return null;
                    }
                };
            }
        }.start();  //启动服务
    }

    public void updateGroupTabData(Group group){

    }

    /**更新用户创建的歌单
     * @param user 用户对象*/
    private void updateUserGroupList(User user) throws IOException {
        //加载歌单指示器和"我喜欢的音乐"tab标签
        removeAllGroupTab(); //先移除先前的歌单tab
        vBoxTabContainer.getChildren().add(applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/component/group-indicator.fxml").load());    //歌单指示器组件
        List<Group> groupList = user.getGroupList();
        if (groupList != null){
            for (int i = 0; i < groupList.size(); i++) {    //遍历集合添加列表
                addGroupTab(groupList.get(i));
            }
        }
    }

    /**设置选择的标签背景颜色的函数*/
    public void setSelectedTab(HBox selectedTab){
        this.resetSelectedTab();
        //然后给当前选中的标签的parent容器添加css类名
        selectedTab.getStyleClass().add("selectedHBox");
        selectedTabIndex = tabList.indexOf(selectedTab);
    }

    public HBox getSelectedTab(){
        return tabList.get(selectedTabIndex);
    }

    public void resetSelectedTab(){
        //重置所有的标签的背景颜色，我这里的HBox标签背景颜色是由另外一个HBox包裹做背景颜色显示的，所以需要getParent，设置parent的样式
        for (HBox hBoxTab:tabList){
            hBoxTab.getStyleClass().remove("selectedHBox");  //移除parent的css类名
        }
    }

    /**添加歌单标签的函数
     * @param group 歌单对象*/
    public void addGroupTab(Group group) throws IOException {
        if (group.getFavor() == 1) { //如果是"我喜欢的音乐"歌单
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/component/favorgroup-tab.fxml");    //"我喜欢的音乐"tab
            vBoxTabContainer.getChildren().add(fxmlLoader.load());
            GroupTabController groupTabController = fxmlLoader.getController();
            tabList.add(groupTabController.getHBoxGroup());
        } else {
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/component/group-tab.fxml");    //歌单tab
            vBoxTabContainer.getChildren().add(fxmlLoader.load());
            GroupTabController groupTabController = fxmlLoader.getController();
            groupTabController.getLabGroupName().setText(group.getName());
            tabList.add(groupTabController.getHBoxGroup());
        }
        tabList.get(tabList.size() - 1).setUserData(group); //存储对象
    }

    /**根据歌单对象移除歌单标签的函数
     * @param group 歌单对象*/
    public void removeGroupTab(Group group) throws DocumentException {
        for (int i = 5; i < tabList.size(); i++) {
            if (((Label)tabList.get(i).getChildren().get(0)).getText().equals(group.getName())){  //如果歌单名称相等，移除
                tabList.get(i).setUserData(null);
                vBoxTabContainer.getChildren().remove(tabList.get(i));
                tabList.remove(i);
                XMLUtils.removeGroup(applicationContext.getBean(Config.class).getGroupsSongFile(),group);  //删除xml文件存储的歌单信息
            }
        }
    }

    /**当登录用户身份验证失败时，移除歌单tab组件的函数*/
    public void removeAllGroupTab(){
        if (vBoxTabContainer.getChildren().size() > 5){ //如果vBoxTabContainer容器的孩子大于5，证明存在歌单tab.
            vBoxTabContainer.getChildren().remove(5,vBoxTabContainer.getChildren().size());
            while (tabList.size() > 4){
                tabList.remove(tabList.size() - 1);
            }
        }
    }

    /**获取歌单tab的对象数据
     * @param id 歌单id*/
    private Group getGroupTabData(int id){
        for (int i = 4; i < tabList.size(); i++) {
            Group group = (Group) tabList.get(i).getUserData();
            if (group.getId() == id){
                return group;
            }
        }
        return null;
    }

    /**根据ID设置group tab的的数据
     * @param group 歌单分组信息
     * @param id 歌单分组ID*/
    public void setGroupTabData(Group group,int id){
        HBox hBox = getGroupTabByID(id);
        hBox.setUserData(group);
        Label labTabName = (Label)hBox.getChildren().get(0);  //获取显示歌单名称Label组件
        if (!labTabName.getText().equals(group.getName())){ //如果名称修改了，更新左侧的歌单名称tab
            labTabName.setText(group.getName());
        }

        return;
    }

    /**重新加载歌单分组标签对应的内容*/
    public void reloadGroupTabContent(int id){
        HBox hBox = getGroupTabByID(id);
        Event.fireEvent(hBox,new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 5, true, true, true, true,
                true, true, true, true, true, true, null));
    }

    /**根据歌单分组的id获取歌单分组标签HBox容器*/
    private HBox getGroupTabByID(int id){
        for (int i = 4; i < tabList.size(); i++) {
            if (((Group) tabList.get(i).getUserData()).getId() == id){
                return tabList.get(i);
            }
        }
        return null;
    }

    /**传入歌单集合对象,更新歌单分组的UI及数据
     * @param groupList 歌单对象集合*/
    public void updateGroup(List<Group> groupList) throws DocumentException {
        for (int i = 0; i < groupList.size(); i++) {
            Group group = getGroupTabData(groupList.get(i).getId());
            if (group != null){    //如果当前存在歌单名称,还需判断里面的信息是否改变
                if (!group.toString().equals(groupList.get(i).toString())){
                    setGroupTabData(groupList.get(i),group.getId());
                }
            }else { //否则,移除这个歌单信息
                removeGroupTab(groupList.get(i));
            }
        }
    }

    /**判断groupList是否存在歌单名称为groupName的歌单，存在返回true，否则返回false
     * @param groupList
     * @param groupName
     * @return boolean*/
    private boolean exist(List<Group> groupList,String groupName){
        for (int i = 0; i < groupList.size(); i++) {
            if (groupList.get(i).getName().equals(groupName)){
                return true;
            }
        }
        return false;
    }

    /**获取目前选中的是哪一个标签的名称,如果没有找到，会抛出异常
     * */
    public HBox getContextMenuShownTab() throws Exception {
        int size = tabList.size();
        if (size >= 5){
            for (int i = 5; i < tabList.size(); i++) {
                if (tabList.get(i).isMouseTransparent()){
                    return tabList.get(i);
                }
            }
        }
        throw new Exception();
    }

    /**判断当前的歌单是否存在名称为tabName
     * @param tabName 歌单名称*/
    public boolean exist(String tabName){
        for (int i = 4; i < tabList.size(); i++) {
            if (((Label)tabList.get(i).getChildren().get(0)).getText().equals(tabName)){
                return true;
            }
        }
        return false;
    }
}
