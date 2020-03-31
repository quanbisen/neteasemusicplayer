package mediaplayer;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.PlayListSong;
import java.util.LinkedList;
import java.util.List;

/**
 * @author super lollipop
 * @date 20-2-17
 */
@Data
@NoArgsConstructor
public class PlayerStatus {

    /**
     * 播放器音量
     * */
    protected double volume = 0.25;

    /**
     * 是否静音
     * */
    protected boolean mute = false;

    /**
     * 播放列表歌曲集合
     * */
    protected List<PlayListSong> playListSongs = new LinkedList<>();

    /**
     * 当前播放的歌曲在播放列表中位置索引
     * */
    protected int currentPlayIndex = -1;

    /**
     * 定义播放模式枚举类型,默认为顺序播放
     */
    protected PlayMode playMode = PlayMode.SEQUENCE;

    public PlayerStatus(double volume, boolean mute, List<PlayListSong> playListSongs, int currentPlayIndex, PlayMode playMode) {
        this.volume = volume;
        this.mute = mute;
        for (int i = 0; i < playListSongs.size(); i++) {
            if (playListSongs.get(i).getLabRemoveIcon() != null){
                playListSongs.get(i).setLabRemoveIcon(null);
            }
        }
        this.playListSongs = playListSongs;
        this.currentPlayIndex = currentPlayIndex;
        this.playMode = playMode;
    }
}
