package media;

import model.Song;
import org.springframework.stereotype.Component;

/**
 * @author super lollipop
 * @date 19-12-8
 */
@Component
public interface IMediaPlayer {
    void play(Song song);
    void destroy();
}
