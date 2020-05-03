package service;

import controller.main.BottomController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import mediaplayer.Config;
import mediaplayer.PlayerStatus;
import mediaplayer.MyMediaPlayer;
import model.PlayListSong;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import util.ImageUtils;
import util.JSONObjectUtils;
import javax.annotation.Resource;
import java.io.File;

/**
 * @author super lollipop
 * @date 20-2-17
 */
@Service
@Scope("prototype")
public class LoadPlayerStateService extends javafx.concurrent.Service<Void> {

    @Resource
    private MyMediaPlayer myMediaPlayer;

    @Resource
    private BottomController bottomController;

    @Resource
    private ApplicationContext applicationContext;

    @Override
    protected Task<Void> createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                /**播放状态加载*/
                System.out.println("load player status service start");
                File playerStatusFile = applicationContext.getBean(Config.class).getPlayerStatusFile();   //获取媒体播放器状态保存的文件句柄
                if (playerStatusFile.exists()) { //如果存储媒体播放器状态的文件存在
                    PlayerStatus playerStatus = JSONObjectUtils.parseMediaPlayerState(playerStatusFile);    //解析配置文件，转换成对象
                    if (playerStatus != null) {
                        if (playerStatus.getPlayListSongs() != null && playerStatus.getPlayListSongs().size() > 0) {
                            //创建播放列表，并且设置自定义媒体播放器对象myMediaPlayer的播放列表
                            ObservableList<PlayListSong> playListSongs = FXCollections.observableArrayList();
                            playListSongs.addAll(playerStatus.getPlayListSongs());
                            myMediaPlayer.setPlayListSongs(playListSongs);
                        }
                        myMediaPlayer.setCurrentPlayIndex(playerStatus.getCurrentPlayIndex());  //设置当前播放的索引
                        myMediaPlayer.setPlayMode(playerStatus.getPlayMode());  //设置播放播放
                        myMediaPlayer.setVolume(playerStatus.getVolume());
                        Platform.runLater(() -> {
                            if (myMediaPlayer.getPlayListSongs() != null && myMediaPlayer.getPlayListSongs().size() > 0) {
                                bottomController.getLabPlayListCount().setText(String.valueOf(myMediaPlayer.getPlayListSongs().size()));
                            }
                            bottomController.getSliderVolume().setValue(playerStatus.getVolume());
                            if (myMediaPlayer.getPlayListSongs() != null && myMediaPlayer.getPlayListSongs().size() > 0) {
                                try {
                                    myMediaPlayer.prepareGUI(); //设置准备当前索引的歌曲
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            switch (playerStatus.getPlayMode()) {
                                case SEQUENCE_LOOP: {
                                    bottomController.getLabPlayModeIcon().setGraphic(ImageUtils.createImageView("image/NeteaseSequenceLoopMode.png", 24, 24));
                                    break;
                                }
                                case SEQUENCE: {
                                    bottomController.getLabPlayModeIcon().setGraphic(ImageUtils.createImageView("image/NeteaseSequencePlayMode.png", 24, 24));
                                    break;
                                }
                                case SINGLE_LOOP: {
                                    bottomController.getLabPlayModeIcon().setGraphic(ImageUtils.createImageView("image/NeteaseSingleRoopIcon.png", 24, 24));
                                    break;
                                }
                                case SHUFFLE: {
                                    bottomController.getLabPlayModeIcon().setGraphic(ImageUtils.createImageView("image/NeteaseShufflePlayMode.png", 24, 24));
                                    break;
                                }
                                default:
                            }
                        });
                    }
                }
                return null;
            }
        };
        return task;
    }
}
