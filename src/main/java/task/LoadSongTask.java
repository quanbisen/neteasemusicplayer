package task;

import controller.ChoseFolderController;
import controller.LocalMusicContentController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import model.Song;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import util.SongUtils;
import util.XMLUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * @author super lollipop
 * @date 19-12-2
 */
@Component
@Scope("prototype")
public class LoadSongTask extends Task<ObservableList<Song>> {

    /**
     * 注入选择目录的控制器
     */
    @Resource
    private ChoseFolderController choseFolderController;

    /**
     * 注入"本地音乐"中间面板的控制器
     */
    @Resource
    private LocalMusicContentController localMusicContentController;

    @Override
    protected ObservableList<Song> call() throws Exception {

        File CHOSE_FOLDER_FILE = new File("src" + File.separator + "main" + File.separator + "resources" + File.separator + "config" + File.separator + "chose-folder.xml");
        if (CHOSE_FOLDER_FILE.exists()) {
            List<String> folderList = XMLUtils.getAllRecord(CHOSE_FOLDER_FILE, "Folder", "path");
            if (folderList.size() > 0) {   //如果选择的文件目录存在，即记录数大于0，获取目录下的歌曲信息集合
                ObservableList<Song> observableSongList = SongUtils.getObservableSongList(folderList);
                Platform.runLater(() -> {   //设置"显示歌曲"数量的标签为扫描到的歌曲数目
                    localMusicContentController.getLabSongCount().setText(String.valueOf(SongUtils.getSongCount(observableSongList)));
                    localMusicContentController.getBorderPane().setVisible(true);
                });
                return observableSongList;
            } else {
                Platform.runLater(() -> {
                    localMusicContentController.getLabSongCount().setText("0");
                    localMusicContentController.getBorderPane().setVisible(false);
                });
                return null;
            }
        } else {
            return null;
        }
    }


}
