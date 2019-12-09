package media;

import controller.BottomController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import model.Song;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;
import org.springframework.stereotype.Component;
import util.SecondUtils;

import javax.annotation.Resource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        MP3File mp3File = new MP3File(song.getResource());
        if (mp3File.hasID3v2Tag()){
            AbstractID3v2Frame abstractID3v2Frame = (AbstractID3v2Frame) mp3File.getID3v2Tag().getFrame("APIC");
            FrameBodyAPIC frameBodyAPIC = (FrameBodyAPIC) abstractID3v2Frame.getBody();
            byte[] imageData = frameBodyAPIC.getImageData();
            ImageView albumImageView = new ImageView(new Image(new ByteArrayInputStream(imageData)));
            albumImageView.setFitWidth(58);
            albumImageView.setFitHeight(58);
            bottomController.getLabAlbum().setGraphic(albumImageView);
        }
        else {
            ImageView albumImageView = new ImageView(new Image("image/NeteaseDefaultAlbumWhiteBackground.png"));
            albumImageView.setFitWidth(58);
            albumImageView.setFitHeight(58);
            bottomController.getLabAlbum().setGraphic(albumImageView);
        }
        //2."播放、暂停"按钮图片
        ImageView playingImageView = new ImageView(new Image("image/NeteasePlaying.png"));
        playingImageView.setFitHeight(32);
        playingImageView.setFitWidth(32);
        bottomController.getLabPlay().setGraphic(playingImageView);
        //3.歌曲名称、歌手、歌曲总时间
        bottomController.getLabMusicName().setText(song.getName());
        bottomController.getLabMusicSinger().setText(song.getSinger());
        bottomController.getLabTotalTime().setText(song.getTotalTime());
        //
        bottomController.getSliderSong().setMax(SecondUtils.toSeconds(song.getTotalTime()));
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                if (!bottomController.getSliderSong().isPressed()){  //没有被鼠标按下时
                    bottomController.getSliderSong().setValue(newValue.toSeconds());
                }
            }
        });
//        bottomController.getSliderSong().setMax();
//        bottomController.getSliderSong().valueProperty().bind(mediaPlayer.currentTimeProperty());
    }


    @Override
    public void destroy() {
        this.mediaPlayer.dispose();
        this.mediaPlayer = null;
        System.gc();
    }

    @Override
    public void pause() {

    }
}
