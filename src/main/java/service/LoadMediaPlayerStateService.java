package service;

import controller.main.BottomController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import mediaplayer.Config;
import model.MediaPlayerState;
import mediaplayer.MyMediaPlayer;
import model.PlayListSong;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import util.ImageUtils;
import util.JSONObjectUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author super lollipop
 * @date 20-2-17
 */
@Service
@Scope("prototype")
public class LoadMediaPlayerStateService extends javafx.concurrent.Service<Void> {

    @Resource
    private MyMediaPlayer myMediaPlayer;

    @Resource
    private Config config;

    @Resource
    private BottomController bottomController;

    @Override
    protected Task<Void> createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                File mediaPlayerStageFile = config.getMediaPlayerStateFile();   //获取媒体播放器状态保存的文件句柄
                if (mediaPlayerStageFile.exists()){ //如果文件存在
                    MediaPlayerState mediaPlayerState = JSONObjectUtils.parseMediaPlayerStage(mediaPlayerStageFile);    //解析配置文件，转换成对象
                    //创建播放列表，并且设置自定义媒体播放器对象myMediaPlayer的播放列表
                    ObservableList<PlayListSong> playListSongs = FXCollections.observableArrayList();
                    for (int i = 0; i < mediaPlayerState.getPlayListSongs().size(); i++) {
                        playListSongs.add(JSONObjectUtils.parsePlayListSong(mediaPlayerState.getPlayListSongs().get(i)));
                    }
                    myMediaPlayer.setPlayListSongs(playListSongs);

                    myMediaPlayer.setCurrentPlayIndex(mediaPlayerState.getCurrentPlayIndex());  //设置当前播放的索引
                    myMediaPlayer.setPlayMode(mediaPlayerState.getPlayMode());  //设置播放播放

                    Platform.runLater(() -> {
                        bottomController.updatePlayListIcon();
                        bottomController.getSliderVolume().setValue(mediaPlayerState.getVolume());
                        try {
                            myMediaPlayer.playSong(myMediaPlayer.getCurrentPlaySong()); //播放当前索引的歌曲
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        myMediaPlayer.pause();  //暂停
                        switch (mediaPlayerState.getPlayMode()) {
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
                return null;
            }
        };
        return task;
    }
}
