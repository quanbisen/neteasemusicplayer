package service;

import com.alibaba.fastjson.JSON;
import controller.content.LocalMusicContentController;
import controller.main.LeftController;
import controller.main.MainController;
import controller.popup.ChoseGroupController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import mediaplayer.Config;
import mediaplayer.PlayerStatus;
import mediaplayer.UserStatus;
import model.LocalSong;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.Group;
import pojo.Song;
import util.HttpClientUtils;
import util.WindowUtils;
import util.XMLUtils;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author super lollipop
 * @date 20-3-9
 */
@Service
@Scope("prototype")
public class CollectSongService extends javafx.concurrent.Service<Void> {

    @Resource
    private LocalMusicContentController localMusicContentController;

    @Resource
    private LeftController leftController;

    @Resource
    private Config config;

    @Resource
    private PlayerStatus playerStatus;

    @Resource
    private UserStatus userStatus;

    @Resource
    private ChoseGroupController choseGroupController;

    @Resource
    private MainController mainController;

    @Override
    protected Task<Void> createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                //找出与选中歌单名称匹配的Group
                List<Group> groupList = userStatus.getUser().getGroupList();
                Group selectedGroup = null;
                for (int i = 0; i < groupList.size(); i++) {
                    if (groupList.get(i).getName().equals(choseGroupController.getChoseGroupName())){
                        selectedGroup = groupList.get(i);
                        break;
                    }
                }
                if (selectedGroup != null) { //找到了匹配的Group
                    if (leftController.getSelectedTabIndex() == 2 && localMusicContentController.getTableViewSong().getSelectionModel().getSelectedItem() != null){ //2为"本地音乐"tab
                        LocalSong localSong = localMusicContentController.getTableViewSong().getSelectionModel().getSelectedItem(); //取出选中的本地歌曲
                        //存储添加歌曲的记录到本地文件,注意:原则上设计是本地文件的优先级比在线资源的高
                        File groupsSongFile = config.getGroupsSongFile();
                        if (!groupsSongFile.exists()){  //如果文件不存在,创建新文件
                            groupsSongFile.createNewFile();
                            XMLUtils.createXML(groupsSongFile,"GroupList"); //创建根元素"GroupList"
                        }
                        String string = XMLUtils.addOneRecord(groupsSongFile,selectedGroup,localSong);  //添加到xml文件存储
                        Platform.runLater(()->{
                            Event.fireEvent(choseGroupController.getLabCloseIcon(), new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                                    0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                                    true, true, true, true, true, true, null)); //关闭舞台
                            WindowUtils.toastInfo(mainController.getStackPane(),new Label(string)); //提示信息
                        });

                        Song song = new Song(); //创建song对象
                        song.setName(localSong.getName());
                        song.setSinger(localSong.getSinger());
                        song.setAlbum(localSong.getAlbum());
                        //尝试插入数据到数据库中,实现云同步.注意,后端数据库中找到了与song的名称,歌手,专辑相同的才进行插入
                        String responseStr = addPersistenceGroupSong(selectedGroup,song);
                        System.out.println(responseStr);
                    }
                }
                return null;
            }
        };
        return task;
    }

    /**尝试插入数据到数据库中,注意,后端数据库中找到了与song的名称,歌手,专辑相同的才进行插入
     * @param group
     * @param song
     * @return String*/
    private String addPersistenceGroupSong(Group group,Song song) throws IOException {
        String url = config.getGroupSongURL() + "/insert";
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
                .addTextBody("song",JSON.toJSONString(song),ContentType.create("application/json",Charset.forName("UTF-8")))
                .addTextBody("group",JSON.toJSONString(group),ContentType.create("application/json",Charset.forName("UTF-8")));
        return HttpClientUtils.executePost(url,multipartEntityBuilder.build());
    }

}
