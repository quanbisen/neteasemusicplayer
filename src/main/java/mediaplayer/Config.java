package mediaplayer;

import org.springframework.stereotype.Component;
import pojo.Register;
import pojo.User;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author super lollipop
 * @date 20-2-11
 */
@Component
public class Config {

    /**播放器合法的登录用户对象*/
    private User user;

    /**播放器配置文件的存放路径*/
    private Path configPath;

    /**注册时的临时对象*/
    private Register register;

    /**获取登录配置存储的文件句柄*/
    public File getLoginConfigFile() {
        return configPath.resolve("login-config.properties").toFile();
    }

    /**获取选择的目录存储的文件句柄*/
    public File getChoseFolderConfigFile(){
        return configPath.resolve("chose-folder.xml").toFile();
    }

    /**获取最近播放记录文件句柄*/
    public File getRecentPlayFile(){
        return configPath.resolve("recent-play.xml").toFile();
    }

    public File getMediaPlayerStateFile(){
        return configPath.resolve("media-player-state.properties").toFile();
    }

    public File getSearchHistoryFile(){
        return configPath.resolve("search-history.xml").toFile();
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

    public Config() throws IOException {
        Path configParent;
        Path configDir;
        String appData = System.getenv("APPDATA");
        System.out.println(appData);
        if (appData != null) {
            configParent = Paths.get(appData);
            configDir = configParent.resolve("neteasemusicplayer");
        } else {
            configParent = Paths.get(System.getProperty("user.home"));
            configDir = configParent.resolve(".neteasemusicplayer");
        }
        configPath = Files.createDirectories(configDir);
    }

}
