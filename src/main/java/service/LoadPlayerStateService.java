package service;

import application.SpringFXMLLoader;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import controller.main.BottomController;
import controller.main.LeftController;
import controller.main.MainController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import mediaplayer.Config;
import mediaplayer.PlayerStatus;
import mediaplayer.MyMediaPlayer;
import mediaplayer.UserStatus;
import model.PlayListSong;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.User;
import util.HttpClientUtils;
import util.ImageUtils;
import util.JSONObjectUtils;
import util.WindowUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author super lollipop
 * @date 20-2-17
 */
@Service
@Scope("prototype")
public class LoadPlayerStateService extends javafx.concurrent.Service<Void> {

    @Resource
    private MyMediaPlayer myMediaPlayer;

    @Resource
    private LeftController leftController;

    @Resource
    private MainController mainController;

    @Resource
    private BottomController bottomController;

    @Resource
    private ApplicationContext applicationContext;

    @Override
    protected Task<Void> createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                /**播放状态加载*/
                File playerStatusFile = applicationContext.getBean(Config.class).getPlayerStatusFile();   //获取媒体播放器状态保存的文件句柄
                if (playerStatusFile.exists()) { //如果存储媒体播放器状态的文件存在
                    PlayerStatus playerStatus = JSONObjectUtils.parseMediaPlayerState(playerStatusFile);    //解析配置文件，转换成对象
                    if (playerStatus != null) {
                        if (playerStatus.getPlayListSongs() != null && playerStatus.getPlayListSongs().size() > 0) {
                            //创建播放列表，并且设置自定义媒体播放器对象myMediaPlayer的播放列表
                            ObservableList<PlayListSong> playListSongs = FXCollections.observableArrayList();
                            playListSongs.addAll(playerStatus.getPlayListSongs());
                            myMediaPlayer.setPlayListSongs(playListSongs);
                        }
                        myMediaPlayer.setCurrentPlayIndex(playerStatus.getCurrentPlayIndex());  //设置当前播放的索引
                        myMediaPlayer.setPlayMode(playerStatus.getPlayMode());  //设置播放播放
                        myMediaPlayer.setVolume(playerStatus.getVolume());
                        Platform.runLater(() -> {
                            if (myMediaPlayer.getPlayListSongs() != null && myMediaPlayer.getPlayListSongs().size() > 0) {
                                bottomController.getLabPlayListCount().setText(String.valueOf(myMediaPlayer.getPlayListSongs().size()));
                            }
                            bottomController.getSliderVolume().setValue(playerStatus.getVolume());
                            if (myMediaPlayer.getPlayListSongs() != null && myMediaPlayer.getPlayListSongs().size() > 0) {
                                try {
                                    myMediaPlayer.prepareGUI(); //设置准备当前索引的歌曲
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            switch (playerStatus.getPlayMode()) {
                                case SEQUENCE_LOOP: {
                                    bottomController.getLabPlayModeIcon().setGraphic(ImageUtils.createImageView("image/NeteaseSequenceLoopMode.png", 24, 24));
                                    break;
                                }
                                case SEQUENCE: {
                                    bottomController.getLabPlayModeIcon().setGraphic(ImageUtils.createImageView("image/NeteaseSequencePlayMode.png", 24, 24));
                                    break;
                                }
                                case SINGLE_LOOP: {
                                    bottomController.getLabPlayModeIcon().setGraphic(ImageUtils.createImageView("image/NeteaseSingleRoopIcon.png", 24, 24));
                                    break;
                                }
                                case SHUFFLE: {
                                    bottomController.getLabPlayModeIcon().setGraphic(ImageUtils.createImageView("image/NeteaseShufflePlayMode.png", 24, 24));
                                    break;
                                }
                                default:
                            }
                        });
                    }
                }
                /**登录状态加载*/
                File userStatusFile = applicationContext.getBean(Config.class).getUserStatusFile();
                if (userStatusFile.exists()) {
                    try {
                        User user = JSONObjectUtils.parseUser(userStatusFile);
                        if (user != null){  //如果用户对象不为空才执行
                            File imageFile = new File(user.getImageURL().substring(5)); //本地存储的用户头像文件句柄
                            updateUser(user);   //根据user对象更新左侧用户信息的UI显示
                            //验证服务器user的状态是否合法
                            String url = applicationContext.getBean(Config.class).getUserURL() + "/validate";
                            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().addTextBody("token", user.getToken(), ContentType.create("text/pain", Charset.forName("UTF-8")));
                            try {
                                String responseString = HttpClientUtils.executePost(url, multipartEntityBuilder.build());
                                user = JSON.parseObject(responseString, User.class);
                                if (user != null) {  //如果user不为null，证明服务器验证通过。
                                    if (imageFile.exists()) { //如果原来存储的头像文件存在，删除它
                                        imageFile.delete();
                                        HttpClientUtils.download(user.getImageURL(), imageFile); //下载新的头像图片保存
                                    }
                                    JSONObjectUtils.saveObject(user, applicationContext.getBean(Config.class).getUserStatusFile());   //保存更新的用户信息到本地存储文件
                                    updateUser(user);
                                }
                            } catch (JSONException e) {
                                System.out.println("身份验证不通过");
                                applicationContext.getBean(Config.class).getUserStatusFile().delete();
                                File userImageFile = new File(user.getImageURL().substring(5));
                                userImageFile.delete();
                                Platform.runLater(() -> {
                                    WindowUtils.toastInfo(mainController.getStackPane(), new Label("身份验证不通过"));
                                });
                            } catch (HttpHostConnectException e) {
                                System.out.println("无网络连接");
                                Platform.runLater(()->{
                                    WindowUtils.toastInfo(mainController.getStackPane(),new Label("无网络连接"));
                                });
                            }
                        }
                    }catch (JSONException e){
                        System.out.println("登录信息失效");
                        userStatusFile.delete(); //删除存储用户状态的文件
                        Platform.runLater(() -> {
                            WindowUtils.toastInfo(mainController.getStackPane(), new Label("登录信息失效"));
                        });
                    }
                }
                return null;
            }
        };
        return task;
    }

    /**根据user对象更新左侧UI的显示，包括歌单名称、用户信息等
     * @param user 用户对象
     */
    private void updateUser(User user) {
        applicationContext.getBean(UserStatus.class).setUser(user); //保存用户信息到applicationContext的UserState
        //加载歌单指示器和"我喜欢的音乐"tab标签
        Platform.runLater(() -> {
            try {
                leftController.removeAllGroupTab(); //先移除先前的歌单tab
                //用户呢称和头像文件UI更新
                leftController.getLabUserName().setText(user.getName());
                leftController.getLabUserImage().setGraphic(ImageUtils.createImageView(user.getImageURL(), 38, 38));
                leftController.getVBoxTabContainer().getChildren().add(applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/component/group-indicator.fxml").load());    //歌单指示器组件
                user.getGroupList().forEach(group -> {
                    try {
                        leftController.addGroupTab(group);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
