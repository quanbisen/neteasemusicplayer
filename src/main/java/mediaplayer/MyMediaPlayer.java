package mediaplayer;

import controller.content.AlbumLyricContentController;
import controller.main.BottomController;
import controller.content.RecentPlayContentController;
import controller.main.CenterController;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import model.PlayListSong;
import model.RecentSong;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.stereotype.Component;
import util.ImageUtils;
import util.SongUtils;
import util.TimeUtils;
import util.XMLUtils;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;

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

    /**播放器静音之前的音量*/
    private Double muteBeforeVolume;

    /**
     * 注入底部播放进度的控制器
     */
    @Resource
    private BottomController bottomController;

    @Resource
    private RecentPlayContentController recentPlayContentController;

    @Resource
    private Config config;

    @Resource
    private AlbumLyricContentController albumLyricContentController;

    @Resource
    private CenterController centerController;

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public ObservableList<PlayListSong> getPlayListSongs() {
        if (playListSongs == null){
            playListSongs = FXCollections.observableArrayList();
        }
        return playListSongs;
    }

    public int getCurrentPlayIndex() {
        return currentPlayIndex;
    }

    public PlayListSong getCurrentPlaySong(){
        return playListSongs.get(currentPlayIndex);
    }

    public List<Integer> getLastPlayIndexList() {
        if (lastPlayIndexList == null) {
            lastPlayIndexList = new LinkedList<>();
        }
        return lastPlayIndexList;
    }

    public List<Integer> getNextPlayIndexList() {
        if (nextPlayIndexList == null) {
            nextPlayIndexList = new LinkedList<>();
        }
        return nextPlayIndexList;
    }

    public Double getMuteBeforeVolume() {
        return muteBeforeVolume;
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

    public void setLastPlayIndexList(List<Integer> lastPlayIndexList) {
        this.lastPlayIndexList = lastPlayIndexList;
    }

    public void setNextPlayIndexList(List<Integer> nextPlayIndexList) {
        this.nextPlayIndexList = nextPlayIndexList;
    }

    public void setMuteBeforeVolume(Double muteBeforeVolume) {
        this.muteBeforeVolume = muteBeforeVolume;
    }

    /**
     * 自定义媒体播放器播放新歌曲行为
     */
    @Override
    public void playSong(PlayListSong playListSong) throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException {
        playBefore(playListSong);
        mediaPlayer.play();   //播放
        playAfter(playListSong);
    }

    /**播放之前,需要设置播放器的UI更新,还有播放进度结束之后的事件*/
    public void playBefore(PlayListSong playListSong) throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException {

        /**专辑歌词面板*/
        albumLyricContentController.loadAlbumLyric();

        boolean isMute = false;
        if (mediaPlayer != null) {  //如果当前的媒体播放器不为空,销毁它
            isMute = mediaPlayer.isMute();
            this.mediaPlayer.dispose();
            this.mediaPlayer = null;
        }
        /**创建MediaPlayer播放*/
        String resource = playListSong.getResource();
        if (resource.contains("http:")){    //如果是在线资源，加载专辑图，并设置显示
            mediaPlayer = new MediaPlayer(new Media(resource)); //创建在线资源的媒体播放器对象
            Image image = new Image(playListSong.getImageURL(),58,58,true,true);
            if (!image.isError()){
                bottomController.getLabAlbum().setGraphic(ImageUtils.createImageView(image,58,58));
            }

        }else {
            mediaPlayer = new MediaPlayer(new Media(new File(playListSong.getResource()).toURI().toString()));  //创建本地资源的媒体播放器对象
            //1.专辑图片
            bottomController.getLabAlbum().setGraphic(ImageUtils.getAlbumImageView(playListSong,58,58));
        }
        mediaPlayer.setMute(isMute);
        mediaPlayer.volumeProperty().bind(bottomController.getSliderVolume().valueProperty());  //设置媒体播放器的音量绑定音量条组件的音量


        //2."播放、暂停"按钮图片
        bottomController.getLabPlay().setGraphic(ImageUtils.createImageView("image/NeteasePause.png", 32, 32));
        //3.歌曲名称、歌手、歌曲总时间
        bottomController.getLabMusicName().setText(playListSong.getName());
        bottomController.getLabMusicSinger().setText(playListSong.getSinger());
        bottomController.getLabTotalTime().setText(playListSong.getTotalTime());
        //4.播放进度条设置
        bottomController.getSliderSong().setValue(0);
        bottomController.getSliderSong().setMax(TimeUtils.toSeconds(playListSong.getTotalTime()));  //设置歌曲滑动条的最大值为歌曲的秒数
        //
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            //底部进度条
            if (!bottomController.getSliderSong().isPressed()) {  //没有被鼠标按下时
                bottomController.getSliderSong().setValue(observable.getValue().toSeconds());
            }

            if (albumLyricContentController.isShow()){
                //歌词滚动
                float second = (float) (Math.round(observable.getValue().toSeconds() * 10 ) / 10.0);    //保留一位小数
                albumLyricContentController.scrollLyric(second);
            }
        });

        /**媒体播放器结束后触发的事件
         * start*/
        mediaPlayer.setOnEndOfMedia(() -> {   //媒体播放器结束后触发的事件
            switch (playMode) {
                case SINGLE_LOOP: {                        //单曲循环模式
                    mediaPlayer.seek(new Duration(0));  //定位到0毫秒(0秒)的时间，重新开始播放
                    break;
                }
                case SEQUENCE: {                             //顺序播放模式
                    if (lastPlayIndexList == null){ //如果记录下一首播放的索引记录等于null，则初始化
                        lastPlayIndexList = new LinkedList<>();
                    }else if (lastPlayIndexList.contains(currentPlayIndex)){    //否则，判断是否包含了当前的播放索引
                        lastPlayIndexList.remove((Object)currentPlayIndex); //包含就移除
                    }
                    lastPlayIndexList.add(currentPlayIndex);    //添加到下一首播放的索引记录

                    if (nextPlayIndexList != null && nextPlayIndexList.size() > 0) {              //顺序播放模式下，如果记录下一首播放的索引记录大于0
                        currentPlayIndex = nextPlayIndexList.get(nextPlayIndexList.size()-1);   //取出最后一次添加的“下一首”记录
                        nextPlayIndexList.remove(nextPlayIndexList.size()-1);   //移除
                        if (nextPlayIndexList.size() == 0){ //如果大小为0，赋值为null
                            nextPlayIndexList = null;
                        }
                    } else {            //否则，顺序播放，当前播放索引+1，如果是最后一首歌播放结束后，释放媒体播放器资源
                        currentPlayIndex = currentPlayIndex + 1;   //当前的播放索引加1
                        if (currentPlayIndex > playListSongs.size() - 1) {   //如果索引不在播放列表中，播放第一首歌，然后暂停
                            currentPlayIndex = 0;   //当前播放索引设置为0
                            lastPlayIndexList = null;   //清空记录上一首播放的索引
                            nextPlayIndexList = null;
                            try {
                                this.playSong(playListSongs.get(currentPlayIndex));
                                this.pause();
                                return;
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        this.playSong(playListSongs.get(currentPlayIndex)); //播放当前索引
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case SEQUENCE_LOOP: {       //列表循环模式
                    if (playListSongs.size() == 1) {    //列表循环模式下，如果歌曲表格只有一首歌，只要把mediaPlayer的当前播放时间重新设置为0秒就可以了
                        mediaPlayer.seek(new Duration(0));  //定位到0毫秒(0秒)的时间，重新开始播放
                        return;
                    }
                    if (lastPlayIndexList == null) { //如果记录下一首播放的索引记录等于null，则初始化它
                        lastPlayIndexList = new LinkedList<Integer>();
                    } else if (lastPlayIndexList.contains(currentPlayIndex)) {    //否则，lastPlayIndexList不等于null,需判断是否包含了当前的播放索引
                        lastPlayIndexList.remove((Object) currentPlayIndex); //包含就移除
                    }
                    lastPlayIndexList.add(currentPlayIndex);    //添加到下一首播放的索引记录

                    if (nextPlayIndexList != null && nextPlayIndexList.size() > 0) { //列表循环模式下，如果记录下一首播放不等于null且大于0，执行索引下一首播放
                        currentPlayIndex = nextPlayIndexList.get(nextPlayIndexList.size() - 1);   //取出最后一次添加的“下一首”记录
                        nextPlayIndexList.remove(nextPlayIndexList.size() - 1);   //移除
                        if (nextPlayIndexList.size() == 0) { //如果大小为0，赋值为null
                            nextPlayIndexList = null;
                        }
                    } else { //否则，执行当前播放索引+1播放
                        currentPlayIndex = currentPlayIndex + 1;
                        if (currentPlayIndex > playListSongs.size() - 1) {  //如果当前索引越界，值为0，形成一个循环
                            currentPlayIndex = 0;
                        }
                    }
                    try {
                        playSong(playListSongs.get(currentPlayIndex));  //播放歌曲
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case SHUFFLE: {              //"随机播放"
                    //随机播放模式下，如果播放列表只有一首歌，只要把mediaPlayer的当前播放时间重新设置为0秒就可以了
                    if (playListSongs.size() == 1) {
                        mediaPlayer.seek(new Duration(0));  //定位到0毫秒(0秒)的时间，重新开始播放
                    }
                    //否则，播放列表大于1，生成一个非当前播放的索引值来播放
                    else {
                        if (lastPlayIndexList == null){
                            lastPlayIndexList = new LinkedList<>();
                        }else if (lastPlayIndexList.contains(currentPlayIndex)) {    //否则，lastPlayIndexList不等于null,需判断是否包含了当前的播放索引
                            lastPlayIndexList.remove((Object) currentPlayIndex); //包含就移除
                        }
                        lastPlayIndexList.add(currentPlayIndex);   //先记录当前的索引是上一首需要的索引

                        if (nextPlayIndexList == null || nextPlayIndexList.size() ==0) {  //nextPlayIndexList的大小等0或为空，证明当前没有需要播放下一首歌曲的索引，直接生成随机索引数播放
                            //然后生成一个随机数不是当前播放的索引值，执行播放
                            while (true) {
                                int randomIndex = new Random().nextInt(playListSongs.size());
                                if (randomIndex != currentPlayIndex) {  //如果随机数不是当前播放的索引值
                                    currentPlayIndex = randomIndex;    //当前的播放索引替换成生成的随机索引
                                    break;                           //跳出循环
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
                            nextPlayIndexList.remove(index);  //移除本条索引值,因为已经播放了.
                            if (nextPlayIndexList.size() ==0){
                                nextPlayIndexList = null;
                            }
                            try {
                                this.playSong(playListSongs.get(currentPlayIndex));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                }
                default:
            }
        });
        /**end*/
    }

    /**播放之后需要处理"最近播放"*/
    public void playAfter(PlayListSong playListSong) throws IOException {
        /**添加到最近播放的存储文件处理操作
         * start*/
        File recentPlayFile = config.getRecentPlayFile();
        if (!recentPlayFile.exists()){ //如果文件不存在
            recentPlayFile.createNewFile();    //创建新文件
            XMLUtils.createXML(recentPlayFile,"RecentPlaySongs");
        }
        List<String> attributeNameList = new ArrayList<String>(){{
            add("name");
            add("singer");
            add("album");
            add("totalTime");
            add("resource");
        }};
        List<String> attributeValueList = new ArrayList<String>(){{
            add(playListSong.getName());
            add(playListSong.getSinger());
            add(playListSong.getAlbum());
            add(playListSong.getTotalTime());
            add(playListSong.getResource());
        }};
        try {
            List<RecentSong> playedSongs = XMLUtils.getRecentPlaySongs(recentPlayFile,"PlayedSong");   //获取存储文件中的所有最近播放歌曲，存储在集合中
            if (SongUtils.isContains(playedSongs,playListSong)){   //，直接
                XMLUtils.removeOneRecord(recentPlayFile,playListSong); //删除存储文件中的这条最近播放记录
            }
            XMLUtils.addOneRecord(recentPlayFile,"PlayedSong",attributeNameList,attributeValueList);   //添加存储到文件
        }catch (Exception e){
            e.printStackTrace();
        }
        /**“最近播放”tab的GUI更新处理操作
         * start*/
        TableView tableViewRecentSongs = recentPlayContentController.getTableViewRecentPlaySong();
        if (tableViewRecentSongs != null && tableViewRecentSongs.getItems() != null && tableViewRecentSongs.getItems().size() > 0){
            ObservableList<RecentSong> tableItems = recentPlayContentController.getTableViewRecentPlaySong().getItems();
            if (SongUtils.isContains(tableItems,playListSong)){
                tableItems.remove(SongUtils.getIndex(tableItems,playListSong));
            }
            recentPlayContentController.getTableViewRecentPlaySong().getItems().add(0,SongUtils.toRecentSong(playListSong));
            recentPlayContentController.updateRecentPlayPane(); //更新最近播放面板的GUI
        }
    }



    /**自定义媒体播放器“播放全部”行为
     * @param tableItems 需要播放的表格集合*/
    @Override
    public void playAll(ObservableList tableItems) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        this.initializePlayList(tableItems);
        if (playMode == PlayMode.SHUFFLE){   //如果当前播放模式为"随机播放"
            //生成一个随机数，执行播放
            int randomIndex=new Random().nextInt(playListSongs.size());
            currentPlayIndex = randomIndex;     //设置当前播放的索引为生成的随机索引
        }
        else {  //否则,不是"随机播放"模式,这些都是播放播放列表中的第一首歌曲
            currentPlayIndex = 0;  //设置当前播放的歌曲为播放列表第一首歌曲
        }
        this.playSong(playListSongs.get(currentPlayIndex));      //播放当前的索引歌曲
    }

    /**自定义媒体播放器“播放全部”行为
     * @param tableItems 需要播放的表格集合
     * @param index 播放索引*/
    @Override
    public void playAll(ObservableList tableItems,int index) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        this.initializePlayList(tableItems);
        currentPlayIndex = index;
        this.playSong(playListSongs.get(currentPlayIndex));      //播放当前的索引歌曲
    }

    /**初始化播放列表
     * @param tableItems 需要播放的表格集合*/
    private void initializePlayList(ObservableList tableItems){
        if (nextPlayIndexList != null && nextPlayIndexList.size() > 0){
            nextPlayIndexList.clear();
            nextPlayIndexList = null;
        }
        if (lastPlayIndexList != null && lastPlayIndexList.size() > 0){
            lastPlayIndexList.clear();
            lastPlayIndexList = null;
        }
        playListSongs = SongUtils.getPlayListSongs(tableItems);     //设置当前播放列表
        //设置右下角"歌单文本提示"显示数量
        bottomController.getLabPlayListCount().setText(String.valueOf(playListSongs.size()));
    }
    /**
     * 自定义媒体播放器销毁行为
     */
    @Override
    public void destroy() {
        this.mediaPlayer.dispose();
        this.mediaPlayer = null;
        this.currentPlayIndex = 0;
        if (lastPlayIndexList != null){
            this.lastPlayIndexList.clear();
            this.lastPlayIndexList = null;
        }
        if (nextPlayIndexList != null){
            this.nextPlayIndexList.clear();
            this.nextPlayIndexList = null;
        }
        if (playListSongs != null){
            this.playListSongs.clear(); //清空播放列表
            this.playListSongs = null;
        }

        //还需要更新底部显示音乐进度的GUI显示
        bottomController.getLabAlbum().setGraphic(ImageUtils.createImageView("image/NeteaseDefaultAlbumWhiteBackground.png", 58, 58));    //设置默认的图片专辑图
        bottomController.getLabPlay().setGraphic(ImageUtils.createImageView("image/NeteasePause.png", 32, 32));           //设置为暂停的图片
        bottomController.getLabMusicName().setText("无");    //设置播放歌曲信息"无"
        bottomController.getLabMusicSinger().setText("无");  //设置播放歌手信息"无"
        bottomController.getLabPlayTime().setText("00:00"); //设置播放时间为"00:00"
        bottomController.getLabTotalTime().setText("00:00");
        bottomController.getSliderSong().setValue(0);
        bottomController.getLabPlayListCount().setText("0");
        bottomController.getLabPlayListCount().setText("0");    //并且更新显示播放列表数量的组件
        System.gc();
    }

    /**
     * 自定义媒体播放器暂停行为
     */
    @Override
    public void pause() {
        mediaPlayer.pause();
        bottomController.getLabPlay().setGraphic(ImageUtils.createImageView("image/NeteasePause.png", 32, 32));  //"播放、暂停"按钮图片
        if (albumLyricContentController.isShow() && albumLyricContentController.getRotateTransition().getStatus() != Animation.Status.PAUSED){
            albumLyricContentController.getRotateTransition().pause();
        }
    }

    /**
     * 自定义媒体播放器播放行为
     */
    @Override
    public void play() {
        mediaPlayer.play();
        bottomController.getLabPlay().setGraphic(ImageUtils.createImageView("image/NeteasePlaying.png", 32, 32));  //"播放、暂停"按钮图片
        if (albumLyricContentController.isShow()
                && albumLyricContentController.getRotateTransition().getStatus() != Animation.Status.RUNNING){
            albumLyricContentController.getRotateTransition().play();
        }
    }
}
