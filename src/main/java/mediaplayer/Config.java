package mediaplayer;

import org.springframework.stereotype.Component;
import pojo.User;
import response.RegisterResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

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
    private RegisterResponse registerResponse;

    private String server;

    public String getSongURL() {
        return server + "/song";
    }

    public String getSingerURL(){
        return server + "/singer";
    }

    public String getUserURL(){
        return server + "/user";
    }

    public String getGroupURL(){
        return server + "/group";
    }

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

    public Path getCachePath() throws IOException {
        Path cachePath;
        if (!configPath.resolve("cache").toFile().exists()){
            cachePath = Files.createDirectories(configPath.resolve("cache"));
        }else {
            cachePath = configPath.resolve("cache");
        }
        return cachePath;
    }

    public Path getLyricPath() throws IOException {
        Path lyricPath;
        if (!configPath.resolve("lyric").toFile().exists()){
            lyricPath = Files.createDirectories(configPath.resolve("lyric"));
        }else {
            lyricPath = configPath.resolve("lyric");
        }
        return lyricPath;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RegisterResponse getRegisterResponse() {
        return registerResponse;
    }

    public void setRegisterResponse(RegisterResponse registerResponse) {
        this.registerResponse = registerResponse;
    }

    public Config() throws IOException {
        /**server part*/
        BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("config/server-config.properties")));
        Properties properties = new Properties();
        properties.load(in);
        server = properties.getProperty("server");

        /**local config part*/
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
