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
                try {
                    //播放状态加载
                    File playerStatusFile = applicationContext.getBean(Config.class).getPlayerStatusFile();   //获取媒体播放器状态保存的文件句柄
                    if (playerStatusFile.exists()){ //如果存储媒体播放器状态的文件存在
                        PlayerStatus playerStatus = JSONObjectUtils.parseMediaPlayerState(playerStatusFile);    //解析配置文件，转换成对象
                        //创建播放列表，并且设置自定义媒体播放器对象myMediaPlayer的播放列表
                        ObservableList<PlayListSong> playListSongs = FXCollections.observableArrayList();
                        playListSongs.addAll(playerStatus.getPlayListSongs());
                        myMediaPlayer.setPlayListSongs(playListSongs);
                        myMediaPlayer.setCurrentPlayIndex(playerStatus.getCurrentPlayIndex());  //设置当前播放的索引
                        myMediaPlayer.setPlayMode(playerStatus.getPlayMode());  //设置播放播放
                        myMediaPlayer.setVolume(playerStatus.getVolume());
                        Platform.runLater(() -> {
                            bottomController.getLabPlayListCount().setText(String.valueOf(playListSongs.size()));
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
                    //登录状态加载
                    File userStatusFile = applicationContext.getBean(Config.class).getUserStatusFile();
                    if (userStatusFile.exists()){
                        User user = JSONObjectUtils.parseUser(userStatusFile);
                        //验证服务器user的状态是否合法
                        String url = applicationContext.getBean(Config.class).getUserURL() + "/validate";
                        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().addTextBody("token",user.getToken(), ContentType.create("text/pain", Charset.forName("UTF-8")));

                        try {
                            String responseString = HttpClientUtils.executePost(url,multipartEntityBuilder.build());
                            User validUser = JSON.parseObject(responseString,User.class);
                            File file = new File(user.getImageURL().substring(5));
                            if (file.exists()){ //如果原来存储的头像文件存在，删除它
                                file.delete();
                            }
                            HttpClientUtils.download(validUser.getImageURL(),file); //下载新的头像图片保存
                            user.setName(validUser.getName());
                            user.setBirthday(validUser.getBirthday());
                            user.setSex(validUser.getSex());
                            user.setDescription(validUser.getDescription());
                            user.setLoginTime(validUser.getLoginTime());
                            user.setGroupList(validUser.getGroupList());
                            JSONObjectUtils.saveObject(user,applicationContext.getBean(Config.class).getUserStatusFile());   //保存更新的用户信息到本地存储文件
                            applicationContext.getBean(UserStatus.class).setUser(validUser);
                            //加载歌单指示器和"我喜欢的音乐"tab标签
                            Platform.runLater(()->{
                                try {
                                    //用户呢称和头像文件UI更新
                                    leftController.getLabUserName().setText(validUser.getName());
                                    leftController.getLabUserImage().setGraphic(ImageUtils.createImageView(validUser.getImageURL(),38,38));
                                    leftController.getVBoxTabContainer().getChildren().add(applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/component/group-indicator.fxml").load());    //歌单指示器组件
                                    validUser.getGroupList().forEach(group -> {
                                        try {
                                            leftController.addGroupTab(group);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //启动同步歌单的服务
//                            applicationContext.getBean(SynchronizeGroupService.class).start();
                            });

                        }catch (JSONException e){
                            System.out.println("身份验证不通过");
                            applicationContext.getBean(Config.class).getUserStatusFile().delete();
                            File userImageFile = new File(user.getImageURL().substring(5));
                            userImageFile.delete();
                            Platform.runLater(()->{
                                WindowUtils.toastInfo(mainController.getStackPane(),new Label("身份验证不通过"));
                            });
                        }catch (HttpHostConnectException e){
                            System.out.println("无网络连接");
                            //设置缓存保存的用户信息文件
                            applicationContext.getBean(UserStatus.class).setUser(user);
                            Platform.runLater(()->{
                                try {
                                    leftController.getVBoxTabContainer().getChildren().add(applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/component/group-indicator.fxml").load());    //歌单指示器组件
                                    //用户呢称和头像文件UI更新
                                    leftController.getLabUserName().setText(user.getName());
                                    leftController.getLabUserImage().setGraphic(ImageUtils.createImageView(user.getImageURL(),38,38));
                                    WindowUtils.toastInfo(mainController.getStackPane(),new Label("无网络连接"));
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            });
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                return null;
            }
        };
        return task;
    }
}
