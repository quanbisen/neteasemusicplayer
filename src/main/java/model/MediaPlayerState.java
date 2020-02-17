package model;

import lombok.Data;
import mediaplayer.PlayMode;
import java.util.List;

/**
 * @author super lollipop
 * @date 20-2-17
 */
@Data
public class MediaPlayerState {
    private double volume;
    private List playListSongs;
    private int currentPlayIndex;
    private PlayMode playMode;
}
