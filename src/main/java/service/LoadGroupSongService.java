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
import model.GroupSong;
import org.dom4j.DocumentException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.Group;
import util.ImageUtils;
import util.SongUtils;
import util.WindowUtils;
import util.XMLUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * @author super lollipop
 * @date 20-3-10
 */
@Service
@Scope("prototype")
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
                    groupContentController.getLabUserName().setText(config.getUser().getName());
                    groupContentController.getLabDescription().setText(group.getDescription());
                    groupContentController.getLabCreateTime().setText(new SimpleDateFormat("yyyy-MM-dd").format(group.getCreateTime()));
                    groupContentController.getScrollPaneContainer().setVisible(true);
                    groupContentController.getLabBlur().setVisible(true);
                });
                List<GroupSong> groupSongs = XMLUtils.getGroupSongs(config.getGroupsSongFile(), group);
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
                    } else { //否则,再次判断
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

                    Platform.runLater(() -> {
                        //面板的专辑图片是最新添加的歌曲的专辑图
                        if (!observableList.get(0).getResourceURL().contains("http://")) {   //如果最新的歌曲是本地音乐文件,获取文件的专辑图,设置成文件的专辑图
                            try {
                                groupContentController.getIvAlbumImage().setImage(ImageUtils.getAlbumImage(observableList.get(0).getResourceURL(), 200, 200));
                            } catch (Exception e) {
                                groupContentController.getIvAlbumImage().setImage(new Image("/image/DefaultAlbumImage_200.png", 200, 200, true, true));
                            }
                        }
                        if (group.getImageURL() != null) {   //如果图片的资源不为空,加载图片

                        }
                        groupContentController.getTableViewGroupSong().setMinHeight(observableList.size() * 40);    //设置表格的高度

                    });
                    return observableList;
                }else {
                    return null;
                }
            }
        };
        return task;
    }
}
