package service;

import controller.component.GroupTabController;
import controller.content.GroupContentController;
import controller.main.LeftController;
import controller.main.MainController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import mediaplayer.Config;
import mediaplayer.PlayerStatus;
import mediaplayer.UserStatus;
import model.GroupSong;
import org.dom4j.DocumentException;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.Group;
import util.ImageUtils;
import util.SongUtils;
import util.WindowUtils;
import util.XMLUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * @author super lollipop
 * @date 20-3-10
 */
@Service
@Scope("singleton")
public class LoadGroupSongService extends javafx.concurrent.Service<ObservableList<GroupSong>> {

    @Resource
    private GroupTabController groupTabController;

    @Resource
    private GroupContentController groupContentController;

    @Resource
    private LeftController leftController;

    @Resource
    private Config config;

    @Resource
    private PlayerStatus playerStatus;

    @Resource
    private UserStatus userStatus;

    @Resource
    private MainController mainController;

    private EventHandler<MouseEvent> onClickedFavorIcon;

    private EventHandler<MouseEvent> onClickedFavoredIcon;


    @Override
    protected Task<ObservableList<GroupSong>> createTask() {
        Task<ObservableList<GroupSong>> task = new Task<ObservableList<GroupSong>>() {
            @Override
            protected ObservableList<GroupSong> call() throws Exception {

                System.out.println(leftController.getSelectedTab().getUserData());

                Group group = (Group) leftController.getSelectedTab().getUserData();
                Platform.runLater(()->{
                    //更新UI
                    groupContentController.getLabGroupName().setText(group.getName());
                    groupContentController.getLabUserName().setText(userStatus.getUser().getName());
                    groupContentController.getLabDescription().setText(group.getDescription());
                    groupContentController.getLabCreateTime().setText(new SimpleDateFormat("yyyy-MM-dd").format(group.getCreateTime()));
                    groupContentController.getScrollPaneContainer().setVisible(true);
                    groupContentController.getLabBlur().setVisible(true);
                });
                List<GroupSong> groupSongs = XMLUtils.getGroupSongs(config.getGroupsSongFile(), group); //获取本地存储的歌单歌曲记录

                if (groupSongs != null) {
                    ObservableList<GroupSong> observableList = FXCollections.observableArrayList(); //创建表格内容集合
                    observableList.addAll(groupSongs);

                    //设置喜欢的图标
                    Image favoredImage = new Image("/image/FavoredIcon_16.png", 16, 16, true, true);
                    Image favorImage = new Image("/image/FavorIcon_16.png", 16, 16, true, true);
                    Group favorGroup = (Group) leftController.getTabList().get(4).getUserData();    //取出"我喜欢的音乐"歌单对象
                    /**添加喜欢的事件处理*/
                    onClickedFavorIcon = mouseEvent -> {
                        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                            try {
                                GroupSong selectedGroupSong = groupContentController.getTableViewGroupSong().getSelectionModel().getSelectedItem(); //取出选中的行(歌曲)
                                String string = XMLUtils.addOneRecord(config.getGroupsSongFile(), favorGroup, selectedGroupSong);
                                WindowUtils.toastInfo(mainController.getStackPane(), new Label(string));
                                selectedGroupSong.getLabFavor().setGraphic(ImageUtils.createImageView(favoredImage, 16, 16)); //更换图标
                                //更换图标事件处理
                                selectedGroupSong.getLabFavor().removeEventHandler(MouseEvent.MOUSE_CLICKED, onClickedFavorIcon);
                                selectedGroupSong.getLabFavor().addEventHandler(MouseEvent.MOUSE_CLICKED, onClickedFavoredIcon);
                                System.out.println(string);
                            } catch (DocumentException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    /**取消喜欢的事件处理*/
                    onClickedFavoredIcon = mouseEvent -> {
                        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                            try {
                                GroupSong selectedGroupSong = groupContentController.getTableViewGroupSong().getSelectionModel().getSelectedItem(); //取出选中的行(歌曲)
                                String string = XMLUtils.removeOneRecord(config.getGroupsSongFile(), favorGroup, selectedGroupSong);
                                WindowUtils.toastInfo(mainController.getStackPane(), new Label(string));
                                selectedGroupSong.getLabFavor().setGraphic(ImageUtils.createImageView(favorImage, 16, 16));   //更换图标
                                //更换图标事件处理
                                selectedGroupSong.getLabFavor().removeEventHandler(MouseEvent.MOUSE_CLICKED, onClickedFavoredIcon);
                                selectedGroupSong.getLabFavor().addEventHandler(MouseEvent.MOUSE_CLICKED, onClickedFavorIcon);
                                System.out.println(string);
                            } catch (DocumentException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    if (group.getName().equals("我喜欢的音乐")) {  //如果是"我喜欢的音乐"歌单,直接就是"喜欢"图标
                        for (int i = 0; i < observableList.size(); i++) {
                            Label labFavoredIcon = new Label("", ImageUtils.createImageView(favoredImage, 16, 16));
                            observableList.get(i).setLabFavor(labFavoredIcon);
                            observableList.get(i).getLabFavor().addEventHandler(MouseEvent.MOUSE_CLICKED, onClickedFavoredIcon);
                        }
                    } else{ //否则,再次判断
                        for (int i = 0; i < observableList.size(); i++) {
                            if (SongUtils.isFavorSong(XMLUtils.getGroupSongs(config.getGroupsSongFile(), favorGroup), observableList.get(i))) {
                                Label labFavoredIcon = new Label("", ImageUtils.createImageView(favoredImage, 16, 16));
                                observableList.get(i).setLabFavor(labFavoredIcon);
                                observableList.get(i).getLabFavor().addEventHandler(MouseEvent.MOUSE_CLICKED, onClickedFavoredIcon);
                            } else {
                                Label labFavorIcon = new Label("", ImageUtils.createImageView(favorImage, 16, 16));
                                observableList.get(i).setLabFavor(labFavorIcon);
                                observableList.get(i).getLabFavor().addEventHandler(MouseEvent.MOUSE_CLICKED, onClickedFavorIcon);
                            }
                        }
                    }
                    Collections.sort(observableList, (o1, o2) -> {  //按添加时间排序
                        if (o2.getAddTime().getTime() > o1.getAddTime().getTime()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    });
                    Platform.runLater(()->{
                        //如果获取歌单信息的UIL图片(在线资源)失败，加载本地文件的专辑图片
                        if (!setupOnlineImage(group)){
                            try {
                                Image image = getOptimizedImage(observableList,200,200);
                                if (image != null){
                                    groupContentController.getIvAlbumImage().setImage(image);
                                }else {
                                    groupContentController.getIvAlbumImage().setImage(new Image("/image/DefaultAlbumImage_200.png",200,200,true,true));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    return observableList;
                }else {
                    Platform.runLater(()->{
                        setupOnlineImage(group);
                    });
                    return null;
                }
            }

            private boolean setupOnlineImage(Group group){
                if (group.getImageURL() != null) {   //如果图片的资源不为空,加载图片
                    Image image = new Image(group.getImageURL(), 200, 200, true, true);
                    if (!image.isError()) {
                        groupContentController.getIvAlbumImage().setImage(image);
                        return true;
                    }else {
                        return false;
                    }
                }
                return false;
            }

            /**获取歌单专辑图片函数
             * @param groupSongList 歌单歌曲集合
             * @return*/
            private Image getOptimizedImage(List<GroupSong> groupSongList, double width, double height) throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException {
                //面板的专辑图片是最新添加的歌曲的专辑图
                for (int i = 0; i < groupSongList.size(); i++) {
                    if (!groupSongList.get(i).getResourceURL().contains("http://")){   //没有包含"http字样",那就是本地歌曲了.
                        Image imageData = ImageUtils.getAlbumImage(groupSongList.get(i).getResourceURL(), width, height);
                        if (imageData != null) return imageData;
                    }else{
                        Image image = new Image(groupSongList.get(i).getImageURL(),width,height,true,true);
                        if (!image.isError()){
                            return image;
                        }
                    }
                }
                return null;
            }
        };
        return task;
    }
}
