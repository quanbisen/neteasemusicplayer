package mediaplayer;

import javafx.collections.ObservableList;
import model.PlayListSong;
import org.springframework.stereotype.Component;
import pojo.Register;
import pojo.User;
import java.io.File;
import java.util.List;

/**
 * @author super lollipop
 * @date 20-2-11
 */
@Component
public class Config {

    /**播放器合法的登录用户对象*/
    private User user;

    /**播放器配置文件的存放路径*/
    private String configPath = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "config" + File.separator;

    /**注册时的临时对象*/
    private Register register;

    /**播放器的音量*/
    private Double volume;

    /**播放器播放模式*/
    private PlayMode playMode;

    /**播放列表歌曲*/
    private ObservableList<PlayListSong> playListSongs;

    /**当前播放歌曲在playListSongs中的位置*/
    private int currentPlayIndex;

    /**获取登录配置存储的文件句柄*/
    public File getLoginConfigFile() {
        return new File(configPath + "login-config.properties");
    }

    /**获取选择的目录存储的文件句柄*/
    public File getChoseFolderConfigFile(){
        return new File(configPath + "chose-folder.xml");
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
        this.register = register;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }

    public ObservableList<PlayListSong> getPlayListSongs() {
        return playListSongs;
    }

    public void setPlayListSongs(ObservableList<PlayListSong> playListSongs) {
        this.playListSongs = playListSongs;
    }

    public int getCurrentPlayIndex() {
        return currentPlayIndex;
    }

    public void setCurrentPlayIndex(int currentPlayIndex) {
        this.currentPlayIndex = currentPlayIndex;
    }
}
