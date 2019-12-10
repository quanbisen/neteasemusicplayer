package mediaplayer;

import model.Song;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;

/**
 * @author super lollipop
 * @date 19-12-8
 */
@Component
public interface IMediaPlayer {
    void play();
    void play(Song song) throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException, ParseException;
    void destroy();
    void pause();
}
