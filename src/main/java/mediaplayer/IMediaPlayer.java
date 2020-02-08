package mediaplayer;

import javafx.collections.ObservableList;
import model.LocalSong;
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
    void playSong(PlayListSong playListSong) throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException, ParseException;
    void playAll(ObservableList tableItems) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException;
    void playAll(ObservableList tableItems,int index) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException;
    void destroy();
    void pause();
}
