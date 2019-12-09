package media;

import controller.BottomController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import model.Song;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.stereotype.Component;
import util.ImageUtils;
import util.TimeUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * @author super lollipop
 * @date 19-12-8
 */
@Component
public class MyMediaPlayer implements IMediaPlayer {

    private MediaPlayer mediaPlayer;

    @Resource
    private BottomController bottomController;

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @Override
    public void play(Song song) throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException, ParseException {
        if (mediaPlayer!=null){  //如果当前的媒体播放器不为空,销毁它
            this.destroy();
        }
        /**创建MediaPlayer播放*/
        mediaPlayer = new MediaPlayer(new Media(new File(song.getResource()).toURI().toString()));
        mediaPlayer.volumeProperty().bind(bottomController.getSliderVolume().valueProperty());  //绑定音量条组件的音量
        mediaPlayer.play();
        /**设置播放时底部的UI组件显示*/
        //1.专辑图片
        bottomController.getLabAlbum().setGraphic(ImageUtils.getAlbumImage(song.getResource()));
        //2."播放、暂停"按钮图片
        bottomController.getLabPlay().setGraphic(ImageUtils.createImageView("image/NeteasePlaying.png",32,32));
        //3.歌曲名称、歌手、歌曲总时间
        bottomController.getLabMusicName().setText(song.getName());
        bottomController.getLabMusicSinger().setText(song.getSinger());
        bottomController.getLabTotalTime().setText(song.getTotalTime());
        //
        bottomController.getSliderSong().setMax(TimeUtils.toSeconds(song.getTotalTime()));  //设置歌曲滑动条的最大值为歌曲的秒数
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                if (!bottomController.getSliderSong().isPressed()){  //没有被鼠标按下时
                    bottomController.getSliderSong().setValue(newValue.toSeconds());
                }
            }
        });
    }


    @Override
    public void destroy() {
        this.mediaPlayer.dispose();
        this.mediaPlayer = null;
        System.gc();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
        bottomController.getLabPlay().setGraphic(ImageUtils.createImageView("image/NeteasePause.png",32,32));  //"播放、暂停"按钮图片
    }

    @Override
    public void play() {
        mediaPlayer.play();
        bottomController.getLabPlay().setGraphic(ImageUtils.createImageView("image/NeteasePlaying.png",32,32));  //"播放、暂停"按钮图片
    }
}
