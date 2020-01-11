package task;

import controller.ChoseFolderController;
import controller.LocalMusicContentController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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

    /**注入选择目录的控制器*/
    @Resource
    private ChoseFolderController choseFolderController;

    /**注入"本地音乐"中间面板的控制器*/
    @Resource
    private LocalMusicContentController localMusicContentController;

    @Override
    protected ObservableList<Song> call() throws Exception {

        File CHOSE_FOLDER_FILE = new File("src" + File.separator + "main" + File.separator + "resources" + File.separator + "config" + File.separator + "chose-folder.xml");
        if (CHOSE_FOLDER_FILE.exists()){
            List<String> folderList = XMLUtils.getAllRecord(CHOSE_FOLDER_FILE,"Folder","path");
            if (folderList.size()>0){
                ObservableList<Song> observableSongList = SongUtils.getObservableSongList(folderList);
                Platform.runLater(()->{   //设置"显示歌曲"数量的标签为扫描到的歌曲数目
                    localMusicContentController.getLabSongCount().setText(String.valueOf(observableSongList.size()));
                    localMusicContentController.getTabPane().setVisible(true);
                });
                return observableSongList;
            }
            else {
                return null;
            }

        }
        else {
            return null;
        }
    }
}
