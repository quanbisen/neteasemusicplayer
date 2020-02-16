package service;

import controller.content.LocalMusicContentController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import model.LocalSong;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import util.SongUtils;
import util.XMLUtils;
import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * @author super lollipop
 * @date 19-12-2
 */
@Service
@Scope("prototype")
public class LoadLocalSongService extends javafx.concurrent.Service<ObservableList<LocalSong>>{

    /**
     * 注入"本地音乐"中间面板的控制器
     */
    @Resource
    private LocalMusicContentController localMusicContentController;

    @Override
    protected Task<ObservableList<LocalSong>> createTask() {
        Task<ObservableList<LocalSong>> task = new Task<ObservableList<LocalSong>>() {

            @Override
            protected ObservableList<LocalSong> call() throws Exception {

                File CHOSE_FOLDER_FILE = new File("src" + File.separator + "main" + File.separator + "resources" + File.separator + "config" + File.separator + "chose-folder.xml");
                if (CHOSE_FOLDER_FILE.exists()) {
                    List<String> folderList = XMLUtils.getAllRecord(CHOSE_FOLDER_FILE, "Folder", "path");
                    if (folderList != null && folderList.size() > 0) {   //如果选择的文件目录存在，即记录数大于0，获取目录下的歌曲信息集合
                        ObservableList<LocalSong> observableLocalSongList = SongUtils.getObservableLocalSongList(folderList);
                        if (observableLocalSongList.size()>0){
                            Platform.runLater(() -> {   //设置"显示歌曲"数量的标签为扫描到的歌曲数目
                                localMusicContentController.getTabPane().getTabs().get(0).setText(String.valueOf(SongUtils.getSongCount(observableLocalSongList)));
                                localMusicContentController.getTabPane().getTabs().get(1).setText(String.valueOf(SongUtils.getSingerCount(observableLocalSongList)));
                                localMusicContentController.getTabPane().getTabs().get(2).setText(String.valueOf(SongUtils.getAlbumCount(observableLocalSongList)));
                                localMusicContentController.getBorderPane().setVisible(true);
                            });
                            return observableLocalSongList;
                        }else {
                            return null;
                        }

                    } else {
                        Platform.runLater(() -> {
                            localMusicContentController.getTabPane().getTabs().get(0).setText("0");
                            localMusicContentController.getTabPane().getTabs().get(1).setText("0");
                            localMusicContentController.getTabPane().getTabs().get(2).setText("0");
                            localMusicContentController.getBorderPane().setVisible(false);
                        });
                        return null;
                    }
                } else {
                    return null;
                }
            }
        };
        return task;
    }
}
