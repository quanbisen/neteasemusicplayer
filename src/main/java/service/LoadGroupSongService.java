package service;

import controller.component.GroupTabController;
import controller.content.GroupContentController;
import controller.main.LeftController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import mediaplayer.Config;
import model.GroupSong;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.Group;
import util.XMLUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
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

    @Override
    protected Task<ObservableList<GroupSong>> createTask() {
        Task<ObservableList<GroupSong>> task = new Task<ObservableList<GroupSong>>() {
            @Override
            protected ObservableList<GroupSong> call() throws Exception {
                System.out.println(leftController.getSelectedTab().getUserData());
                Group group = (Group) leftController.getSelectedTab().getUserData();
                ObservableList<GroupSong> observableList = FXCollections.observableArrayList();
                List<GroupSong> groupSongs = XMLUtils.getGroupSongs(config.getGroupsSongFile(),group);
                observableList.addAll(groupSongs);
                //设置添加喜欢,移除喜欢的label图标

                Platform.runLater(()->{
                    //更新UI
                    groupContentController.getLabGroupName().setText(group.getName());
                    groupContentController.getLabUserName().setText(config.getUser().getName());
                    groupContentController.getLabDescription().setText(group.getDescription());
                    groupContentController.getLabCreateTime().setText(new SimpleDateFormat("yyyy-MM-dd").format(group.getCreateTime()));
                    if (group.getImageURL() != null){   //如果图片的资源不为空,加载图片

                    }
                    groupContentController.getTableViewGroupSong().setMinHeight(observableList.size() * 40);
                    groupContentController.getScrollPaneContainer().setVisible(true);
                    groupContentController.getLabBlur().setVisible(true);
                });
                return observableList;
            }
        };
        return task;
    }
}
