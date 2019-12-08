package media;

import controller.BottomController;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import model.Song;
import org.junit.Test;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author super lollipop
 * @date 19-12-8
 */
@Component
public class MyMediaPlayer implements IMediaPlayer {

    private MediaPlayer mediaPlayer;

    @Resource
    private BottomController bottomController;

    @Override
    public void play(Song song) {
        if (mediaPlayer!=null){  //如果当前的媒体播放器不为空,销毁它
            this.destroy();
        }
        mediaPlayer = new MediaPlayer(new Media(new File(song.getResource()).toURI().toString()));
        mediaPlayer.play();
        bottomController.getLabMusicName().setText(song.getName());
        bottomController.getLabMusicSinger().setText(song.getSinger());
        bottomController.getLabTotalTime().setText(song.getTotalTime());
    }


    @Override
    public void destroy() {

    }
}
