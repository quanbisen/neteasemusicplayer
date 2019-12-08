package service;

import controller.ChoseFolderController;
import controller.LocalMusicContentController;
import javafx.application.Platform;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import model.Song;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import util.SongUtils;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 19-12-2
 */
@Component
@Scope("prototype")
public class LoadingSongTask extends Task<ObservableList<Song>> {

    /**注入选择目录的控制器*/
    @Resource
    private ChoseFolderController choseFolderController;

    /**注入"本地音乐"中间面板的控制器*/
    @Resource
    private LocalMusicContentController localMusicContentController;

    @Override
    protected ObservableList<Song> call() throws Exception {
        ObservableList<Song> observableSongList = SongUtils.getObservableSongList(choseFolderController.getSelectedPaths());
        Platform.runLater(()->{   //设置"显示歌曲"数量的标签为扫描到的歌曲数目
            localMusicContentController.getLabSongCount().setText(String.valueOf(observableSongList.size()));
        });
        return observableSongList;
    }
}
