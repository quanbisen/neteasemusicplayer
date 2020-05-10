package mediaplayer;

import javafx.util.Duration;
import model.PlayListSong;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * @author super lollipop
 * @date 19-12-8
 */
@Component
public interface IMediaPlayer {
    void play();
    void pause();
    void prepareGUI() throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException;
    void prepareMediaPlayer();
    void playOrPause() throws IOException;
    void playSong() throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException, ParseException;
    void playAll(List tableItems) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException;
    void playAll(List tableItems,int index) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException;
    void playLast() throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException;
    void playNext() throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException;
    void addToPlayList(PlayListSong playListSong) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException;
    void removeFromPlayList(int index) throws ReadOnlyFileException, IOException, TagException, InvalidAudioFrameException, CannotReadException;
    void addFavor();
    void destroy();
    void seek(Duration duration);
    void setMute(boolean mute);
    void switchMute();
    void switchPlayMode();
    boolean isPlayingThis(PlayListSong playListSong);
}
