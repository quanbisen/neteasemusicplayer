package mediaplayer;

import javafx.collections.ObservableList;
import lombok.Data;
import model.PlayListSong;
import org.springframework.stereotype.Component;
import pojo.User;
import response.RegisterResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author super lollipop
 * @date 20-2-17
 */
@Component
@Data
public class PlayerState {

    /**
     * 播放器合法的登录用户对象
     * */
    private User user;

    /**
     * 注册时的临时对象
     * */
    private RegisterResponse registerResponse;

    /**
     * 播放器音量
     * */
    protected double volume;

    /**
     * 播放列表歌曲集合
     * */
    protected List<PlayListSong> playListSongs;

    /**
     * 当前播放的歌曲在播放列表中位置索引
     * */
    protected int currentPlayIndex;

    /**
     * 定义播放模式枚举类型,默认为顺序播放
     */
    protected PlayMode playMode = PlayMode.SEQUENCE;

    public void setPlayListSongs(List<PlayListSong> observableList) {
        if (playListSongs == null){
            playListSongs = new ArrayList<>();
        }
        for (int i = 0; i < observableList.size(); i++) {
            PlayListSong observableSong = new PlayListSong(observableList.get(i).getName(),observableList.get(i).getSinger(),observableList.get(i).getAlbum(),observableList.get(i).getTotalTime(),observableList.get(i).getResource(),observableList.get(i).getLyricURL(),observableList.get(i).getImageURL());
            playListSongs.add(observableSong);
        }
    }
}
