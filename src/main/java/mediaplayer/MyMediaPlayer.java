package mediaplayer;

import controller.BottomController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import model.PlayListSong;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.stereotype.Component;
import util.ImageUtils;
import util.TimeUtils;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author super lollipop
 * @date 19-12-8
 */
@Component
public class MyMediaPlayer implements IMediaPlayer {

    /**
     * 定义JavaFX媒体播放器对象
     */
    private MediaPlayer mediaPlayer;

    /**
     * 定义播放模式枚举类型,默认为顺序播放
     */
    private PlayMode playMode = PlayMode.SEQUENCE;

    /**
     * 定义播放列表歌曲集合
     */
    private ObservableList<PlayListSong> playListSongs;

    /**
     * 记录上一首播放的索引记录
     */
    private List<Integer> lastPlayIndexList;

    /**
     * 记录下一首播放的索引记录
     */
    private List<Integer> nextPlayIndexList;

    /**当前播放的歌曲在播放列表中位置索引*/
    private int currentPlayIndex;

    /**
     * 注入底部播放进度的控制器
     */
    @Resource
    private BottomController bottomController;

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public ObservableList<PlayListSong> getPlayListSongs() {
        return playListSongs;
    }

    public int getCurrentPlayIndex() {
        return currentPlayIndex;
    }

    public List<Integer> getLastPlayIndexList() {
        if (lastPlayIndexList == null) {
            lastPlayIndexList = new ArrayList<>();
        }
        return lastPlayIndexList;
    }

    public List<Integer> getNextPlayIndexList() {
        if (nextPlayIndexList == null) {
            nextPlayIndexList = new ArrayList<>();
        }
        return nextPlayIndexList;
    }

    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }

    public void setPlayListSongs(ObservableList<PlayListSong> playListSongs) {
        this.playListSongs = playListSongs;
    }

    public void setCurrentPlayIndex(int currentPlayIndex) {
        this.currentPlayIndex = currentPlayIndex;
    }

    /**
     * 自定义媒体播放器播放新歌曲行为
     */
    @Override
    public void playSong(PlayListSong playListSong) throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException {

        if (mediaPlayer != null) {  //如果当前的媒体播放器不为空,销毁它
            this.destroy();
        }
        /**创建MediaPlayer播放*/
        String resource = playListSong.getResource();
        if (resource.contains("http:")){
            mediaPlayer = new MediaPlayer(new Media(resource)); //创建在线资源的媒体播放器对象
        }else {
            mediaPlayer = new MediaPlayer(new Media(new File(playListSong.getResource()).toURI().toString()));  //创建本地资源的媒体播放器对象
        }
        mediaPlayer.volumeProperty().bind(bottomController.getSliderVolume().valueProperty());  //设置媒体播放器的音量绑定音量条组件的音量
        mediaPlayer.play();   //播放

        /**设置播放时底部的UI组件显示*/
        //1.专辑图片
        bottomController.getLabAlbum().setGraphic(ImageUtils.getAlbumImage(playListSong.getResource()));
        //2."播放、暂停"按钮图片
        bottomController.getLabPlay().setGraphic(ImageUtils.createImageView("image/NeteasePlaying.png", 32, 32));
        //3.歌曲名称、歌手、歌曲总时间
        bottomController.getLabMusicName().setText(playListSong.getName());
        bottomController.getLabMusicSinger().setText(playListSong.getSinger());
        bottomController.getLabTotalTime().setText(playListSong.getTotalTime());
        //4.播放进度条设置
        bottomController.getSliderSong().setMax(TimeUtils.toSeconds(playListSong.getTotalTime()));  //设置歌曲滑动条的最大值为歌曲的秒数
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                if (!bottomController.getSliderSong().isPressed()) {  //没有被鼠标按下时
                    bottomController.getSliderSong().setValue(observable.getValue().toSeconds());
                }
            }
        });

        mediaPlayer.setOnEndOfMedia(() -> {   //媒体播放器结束后触发的事件.
            switch (playMode) {
                case SINGLE_LOOP: {                        //单曲循环模式
                    mediaPlayer.seek(new Duration(0));  //定位到0毫秒(0秒)的时间，重新开始播放
                    mediaPlayer.play();
                    break;
                }
                case SEQUENCE: {                             //顺序播放模式
                    if (playListSongs.size() == 1) {              //顺序播放模式下，如果歌曲表格只有一首歌，那就定位到0毫秒(0秒)的时间，等待下一次播放
                        mediaPlayer.seek(new Duration(0));
                    } else {            //否则，歌曲表格大于1，顺序播放，直到最后一首歌播放结束后，释放媒体播放器资源
                        currentPlayIndex = currentPlayIndex + 1;   //当前的播放索引加1
                        if (currentPlayIndex <= playListSongs.size() - 1) {   //如果索引还在播放列表中，播放它
                            try {
                                this.playSong(playListSongs.get(currentPlayIndex));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {              //否则，释放媒体播放器资源,并且定位到歌曲表格第一首歌，等待下一次播放
                            this.destroy();
                            bottomController.getLabPlay().setGraphic(ImageUtils.createImageView("image/NeteasePause.png", 32, 32));  //设置暂停状态的图标
                            bottomController.getLabMusicName().setText(playListSongs.get(0).getName());
                            bottomController.getLabMusicSinger().setText(playListSongs.get(0).getSinger());
                            bottomController.getSliderSong().setMax(TimeUtils.toSeconds(playListSongs.get(0).getTotalTime()));
                            bottomController.getSliderSong().setValue(0);
                            bottomController.getLabTotalTime().setText(playListSongs.get(0).getTotalTime());
                            mediaPlayer = new MediaPlayer(new Media((new File(playListSongs.get(0).getResource())).toURI().toString()));
                            mediaPlayer.setVolume(bottomController.getSliderVolume().getValue());
                            System.out.println(mediaPlayer.getStatus());
                        }
                    }
                    break;
                }
                case SEQUENCE_LOOP: {       //列表循环模式
                    //列表循环模式下，如果歌曲表格只有一首歌，只要把mediaPlayer的当前播放时间重新设置为0秒就可以了
                    if (playListSongs.size() == 1) {
                        mediaPlayer.seek(new Duration(0));  //定位到0毫秒(0秒)的时间，重新开始播放
                        mediaPlayer.play();
                    }
                    //否则，执行下一首歌曲播放，形成循环列表
                    else {
                        currentPlayIndex = currentPlayIndex + 1;
                        if (currentPlayIndex > playListSongs.size() - 1) {  //如果当前索引越界，值为0，形成一个循环
                            currentPlayIndex = 0;
                        }
                        try {
                            playSong(playListSongs.get(currentPlayIndex));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case SHUFFLE: {              //"随机播放"
                    //随机播放模式下，如果播放列表只有一首歌，只要把mediaPlayer的当前播放时间重新设置为0秒就可以了
                    if (playListSongs.size() == 1) {
                        mediaPlayer.seek(new Duration(0));  //定位到0毫秒(0秒)的时间，重新开始播放
                        mediaPlayer.play();
                    }
                    //否则，播放列表大于1，生成一个非当前播放的索引值来播放
                    else {
                        if (lastPlayIndexList == null){
                            lastPlayIndexList = new ArrayList<>();
                        }
                        lastPlayIndexList.add(currentPlayIndex);   //先记录当前的索引是上一首需要的索引

                        if (nextPlayIndexList.size() == 0) {  //nextPlayIndexList的大小等0，证明当前没有需要播放下一首歌曲的索引，直接生成随机索引数播放
                            //然后生成一个随机数不是当前播放的索引值，执行播放
                            while (true) {
                                int randomIndex = new Random().nextInt(playListSongs.size());
                                if (randomIndex != currentPlayIndex) {  //如果随机数不是当前播放的索引值
                                    currentPlayIndex = randomIndex;    //当前的播放索引替换成生成的随机索引
                                    break;                           //退出循环
                                }
                            }
                            try {
                                this.playSong(playListSongs.get(currentPlayIndex));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {   //否则,则nextPlayIndexList的大小大于零,存储有索引,取出记录上一首歌列表里的最后一次添加的那一个歌曲播放
                            int index = nextPlayIndexList.size() - 1;
                            currentPlayIndex = nextPlayIndexList.get(index);  //设置当前的播放索引值
                            try {
                                this.playSong(playListSongs.get(currentPlayIndex));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            nextPlayIndexList.remove(index);  //移除本条索引值,因为已经播放了.
                        }
                    }
                    break;
                }
                default:
            }
        });

    }

    /**
     * 自定义媒体播放器销毁行为
     */
    @Override
    public void destroy() {
        this.mediaPlayer.dispose();
        this.mediaPlayer = null;
        //还需要更新底部显示音乐进度的GUI显示
        bottomController.getLabAlbum().setGraphic(ImageUtils.createImageView("image/NeteaseDefaultAlbumWhiteBackground.png", 58, 58));    //设置默认的图片专辑图
        bottomController.getLabPlay().setGraphic(ImageUtils.createImageView("image/NeteasePause.png", 32, 32));           //设置为暂停的图片
        bottomController.getLabMusicName().setText("无");    //设置播放歌曲信息"无"
        bottomController.getLabMusicSinger().setText("无");  //设置播放歌手信息"无"
        bottomController.getLabPlayTime().setText("00:00"); //设置播放时间为"00:00"
        bottomController.getLabTotalTime().setText("00:00");
        bottomController.getSliderSong().setValue(0);
        System.gc();
    }

    /**
     * 自定义媒体播放器暂停行为
     */
    @Override
    public void pause() {
        mediaPlayer.pause();
        bottomController.getLabPlay().setGraphic(ImageUtils.createImageView("image/NeteasePause.png", 32, 32));  //"播放、暂停"按钮图片
    }

    /**
     * 自定义媒体播放器继续播放行为
     */
    @Override
    public void play() {
        mediaPlayer.play();
        bottomController.getLabPlay().setGraphic(ImageUtils.createImageView("image/NeteasePlaying.png", 32, 32));  //"播放、暂停"按钮图片
    }
}
