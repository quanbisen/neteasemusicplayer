package mediaplayer;

import controller.content.LyricContentController;
import controller.main.BottomController;
import controller.content.RecentPlayContentController;
import controller.main.CenterController;
import controller.main.MainController;
import controller.popup.PlayListController;
import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
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
import util.*;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author super lollipop
 * @date 19-12-8
 */
@Component
public class MyMediaPlayer extends PlayerStatus implements IMediaPlayer {

    /**
     * 定义JavaFX媒体播放器对象
     */
    private MediaPlayer mediaPlayer;

    /**
     * 记录上一首播放的索引记录
     */
    private List<Integer> lastPlayIndexList = new LinkedList<>();

    /**
     * 记录下一首播放的索引记录
     */
    private List<Integer> nextPlayIndexList = new LinkedList<>();

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
    private LyricContentController lyricContentController;

    @Resource
    private CenterController centerController;

    @Resource
    private MainController mainController;

    @Resource
    private PlayListController playListController;

    public MediaPlayer getPlayer() {
        return mediaPlayer;
    }

    public List<PlayListSong> getPlayListSongs() {
        if (playListSongs == null){
            playListSongs = new LinkedList<>();
        }
        return playListSongs;
    }

    public int getCurrentPlayIndex() {
        return currentPlayIndex;
    }

    public PlayListSong getCurrentPlaySong(){
        return playListSongs.get(currentPlayIndex);
    }

    public List<Integer> getNextPlayIndexList() {
        if (nextPlayIndexList == null) {
            nextPlayIndexList = new LinkedList<>();
        }
        return nextPlayIndexList;
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
    public void playSong() throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException {
        prepareGUI();
        prepareMediaPlayer();
        play();   //播放
        playAfter();
    }

    @Override
    public void prepareMediaPlayer(){
        if (mediaPlayer != null) {  //如果当前的媒体播放器不为空,销毁它
            this.mediaPlayer.dispose();
            this.mediaPlayer = null;
        }
        /**创建MediaPlayer播放*/
        String resource = playListSongs.get(currentPlayIndex).getResource();
        if (resource.contains("http:")){    //如果是在线资源，加载专辑图，并设置显示
            mediaPlayer = new MediaPlayer(new Media(resource)); //创建在线资源的媒体播放器对象
        }else {
            mediaPlayer = new MediaPlayer(new Media(new File(resource).toURI().toString()));  //创建本地资源的媒体播放器对象
        }
        mediaPlayer.volumeProperty().bind(bottomController.getSliderVolume().valueProperty());  //设置媒体播放器的音量绑定音量条组件的音量
        mediaPlayer.setMute(isMute());
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            //底部进度条
            if (!bottomController.getSliderSong().isPressed()) {  //没有被鼠标按下时
                bottomController.getSliderSong().setValue(observable.getValue().toSeconds());
            }
            if (lyricContentController.isShow()){
                //歌词滚动
                float second = (float) (Math.round(observable.getValue().toSeconds() * 10 ) / 10.0);    //保留一位小数
                lyricContentController.scrollLyric(second);
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
                                this.playSong();
                                this.pause();
                                return;
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        this.playSong(); //播放当前索引
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
                        playSong();  //播放歌曲
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
                                this.playSong();
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
                                this.playSong();
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

    /**播放之前,需要设置播放器的UI更新,还有播放进度结束之后的事件*/
    public void playBefore(PlayListSong playListSong) throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException {
        /**专辑歌词面板*/
        lyricContentController.loadAlbumLyric();
    }

    /**播放之后需要处理"最近播放"*/
    public void playAfter() throws IOException {
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
            add(getCurrentPlaySong().getName());
            add(getCurrentPlaySong().getSinger());
            add(getCurrentPlaySong().getAlbum());
            add(getCurrentPlaySong().getTotalTime());
            add(getCurrentPlaySong().getResource());
        }};
        try {
            List<RecentSong> playedSongs = XMLUtils.getRecentPlaySongs(recentPlayFile,"PlayedSong");   //获取存储文件中的所有最近播放歌曲，存储在集合中
            if (SongUtils.isContains(playedSongs,getCurrentPlaySong())){   //，直接
                XMLUtils.removeOneRecord(recentPlayFile,getCurrentPlaySong()); //删除存储文件中的这条最近播放记录
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
            if (SongUtils.isContains(tableItems,getCurrentPlaySong())){
                tableItems.remove(SongUtils.getIndex(tableItems,getCurrentPlaySong()));
            }
            recentPlayContentController.getTableViewRecentPlaySong().getItems().add(0,SongUtils.toRecentSong(getCurrentPlaySong()));
            recentPlayContentController.updateRecentPlayPane(); //更新最近播放面板的GUI
        }
    }



    /**自定义媒体播放器“播放全部”行为
     * @param tableItems 需要播放的表格集合*/
    @Override
    public void playAll(List tableItems) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        this.initializePlayList(tableItems);
        if (playMode == PlayMode.SHUFFLE){   //如果当前播放模式为"随机播放"
            //生成一个随机数，执行播放
            int randomIndex=new Random().nextInt(playListSongs.size());
            currentPlayIndex = randomIndex;     //设置当前播放的索引为生成的随机索引
        }
        else {  //否则,不是"随机播放"模式,这些都是播放播放列表中的第一首歌曲
            currentPlayIndex = 0;  //设置当前播放的歌曲为播放列表第一首歌曲
        }
        this.playSong();      //播放当前的索引歌曲
    }

    /**自定义媒体播放器“播放全部”行为
     * @param tableItems 需要播放的表格集合
     * @param index 播放索引*/
    @Override
    public void playAll(List tableItems,int index) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        this.initializePlayList(tableItems);
        currentPlayIndex = index;
        this.playSong();      //播放当前的索引歌曲
    }

    @Override
    public void playLast() throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        if (playListSongs.size() == 1) {     //播放列表的歌曲只有一首歌时执行的处理
            mediaPlayer.seek(new Duration(0));
        } else {      //否则播放列表的歌曲大于1，播放下一首歌曲
            if (nextPlayIndexList.contains(currentPlayIndex)) {
                nextPlayIndexList.remove((Object) currentPlayIndex);
            }
            nextPlayIndexList.add(currentPlayIndex);  //播放上一首歌曲之前，把当前的索引添加到下一次播放的索引列表
            if (lastPlayIndexList.size() == 0) {    //如果记录上一首播放的歌曲的列表等于0，证明当前没有上一首歌播放
                if (playMode == PlayMode.SHUFFLE) {   //“随机播放”模式
                    while (true) {  //直到生成的随机数不是当前播放的索引值，执行播放
                        int randomIndex = new Random().nextInt(playListSongs.size());
                        if (randomIndex != currentPlayIndex) {  //如果随机索引值不是当前播放的索引值
                            currentPlayIndex = randomIndex;       //替换当前的播放索引值,退出循环
                            break;
                        }
                    }
                } else { //否则，则为“顺序播放”或“单曲循环”或“顺序循环”模式，且在播放列表歌曲大于1的情况下
                    if (currentPlayIndex == 0) { //如果当前播放歌曲索引为第0位置，设置为播放列表最后的歌曲索引
                        currentPlayIndex = playListSongs.size() - 1;
                    } else { //否则，都是当前播放索引+1
                        currentPlayIndex = currentPlayIndex - 1;
                    }
                }
            } else {       //否则,则lastPlayIndexList的大小大于零,存储有索引,取出记录上一首歌列表里的最后一次添加的那一个歌曲播放
                int index = lastPlayIndexList.size() - 1;
                currentPlayIndex = lastPlayIndexList.get(index);
                lastPlayIndexList.remove(index);
            }
            playSong();  //执行播放索引值对应的歌曲
        }
    }

    @Override
    public void playNext() throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        if (playListSongs.size() == 1) {     //播放列表的歌曲只有一首歌时执行的处理
            mediaPlayer.seek(new Duration(0));
        } else {      //否则播放列表的歌曲大于1，播放下一首歌曲
            if (lastPlayIndexList.contains(currentPlayIndex)) {
                lastPlayIndexList.remove((Object) currentPlayIndex);
            }
            lastPlayIndexList.add(currentPlayIndex);  //播放下一首歌曲之前，把当前的索引添加到上一次播放的索引列表
            if (nextPlayIndexList.size() == 0) {    //如果记录下一首播放的歌曲的列表小于0，证明当前没有下一首歌播放
                if (playMode == PlayMode.SHUFFLE) {   //“随机播放”模式
                    while (true) {  //直到生成的随机数不是当前播放的索引值，执行播放
                        int randomIndex = new Random().nextInt(playListSongs.size());
                        if (randomIndex != currentPlayIndex) {  //如果随机索引值不是当前播放的索引值
                            currentPlayIndex = randomIndex;       //替换当前的播放索引值,退出循环
                            break;
                        }
                    }
                } else { //否则，则为“顺序播放”或“单曲循环”或“顺序循环”模式，且在播放列表歌曲大于1的情况下
                    if (currentPlayIndex == playListSongs.size() - 1) { //如果当前播放歌曲索引为第0位置，设置为播放列表最后的歌曲索引
                        currentPlayIndex = 0;
                    } else { //否则，都是当前播放索引+1
                        currentPlayIndex = currentPlayIndex + 1;
                    }
                }
            } else {       //否则,则nextPlayIndexList的大小大于零,存储有索引,取出记录下一首歌列表里的最后一次添加的那一个歌曲播放
                int index = nextPlayIndexList.size() - 1;
                currentPlayIndex = nextPlayIndexList.get(index);
                nextPlayIndexList.remove(index);
            }
            playSong();  //执行播放索引值对应的歌曲
        }
    }

    @Override
    public void playOrPause() throws IOException {
        if (mediaPlayer != null){
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING){
                pause();
            } else {
                play();
            }
        }else { //否则,需要创建媒体资源播放
            prepareMediaPlayer();
            play();   //播放
            playAfter();
        }
    }

    @Override
    public void addToPlayList(PlayListSong playListSong) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        if (!playListSongs.contains(playListSong)){  //如果播放列表集合不包含此播放列表歌曲
            if (playListSongs.size() == 0){ //如果播放列表没有歌曲，直接播放
                playListSongs.add(playListSong); //添加到播放列表集合中去
                currentPlayIndex = 0;
                playSong();
            }else { //否则，播放列表有歌曲，还需进一步处理
                playListSongs.add(currentPlayIndex + 1,playListSong); //添加到播放列表集合的当前播放索引后面中去
                for (int i = 0; i < nextPlayIndexList.size(); i++) {  //播放列表集合增加了歌曲，记录的索引需要更新处理
                    int indexValue = nextPlayIndexList.get(i);
                    if ( indexValue > currentPlayIndex){
                        nextPlayIndexList.remove(i);
                        nextPlayIndexList.add(i,indexValue + 1);
                    }
                }
                nextPlayIndexList.add(currentPlayIndex+1);    //把这个索引记录下来
            }
            bottomController.getLabPlayListCount().setText(String.valueOf(playListSongs.size()));   //更新右下角播放列表图标GUI显示信息
        }else { //否则，播放列表存在这首歌曲
            int indexValue = playListSongs.indexOf(playListSong);   //获取得到在播放列表中的索引位置
            if (currentPlayIndex != indexValue){ //如果不是当前播放的索引
                if (nextPlayIndexList.contains(indexValue)){    //如果
                    nextPlayIndexList.remove((Object)indexValue);
                }
                nextPlayIndexList.add(indexValue);  //执行添加
            }
        }
        WindowUtils.toastInfo(mainController.getStackPane(),new Label("已添加到播放列表"));
    }

    @Override
    public void removeFromPlayList(int index) throws ReadOnlyFileException, IOException, TagException, InvalidAudioFrameException, CannotReadException {
        /**更新记录下一首索引的集合*/
        if (nextPlayIndexList != null && nextPlayIndexList.size() > 0){
            for (int i = 0; i < nextPlayIndexList.size(); i++) {  //播放列表集合移除了歌曲，记录的索引需要更新处理
                int indexValue = nextPlayIndexList.get(i);
                if (indexValue > index) {  //如果记录下一首播放记录的集合大于此选中的移除索引，需要更新记录下一首索引的集合
                    nextPlayIndexList.remove(i);     //先移除
                    nextPlayIndexList.add(i, indexValue - 1); //再-1操作
                } else if (indexValue == index) {    //如果记录下一首播放记录的集合等于此索引，移除
                    nextPlayIndexList.remove(i);
                }
            }
        }

        /**需要更新记录上一首索引的集合*/
        for (int i = 0; i < lastPlayIndexList.size(); i++) {  //播放列表集合移除了歌曲，记录的索引需要更新处理
            int indexValue = lastPlayIndexList.get(i);
            if (indexValue > index) {  //如果记录上一首播放记录的集合大于此选中的移除索引，需要更新记录上一首索引的集合
                lastPlayIndexList.remove(i);     //先移除
                lastPlayIndexList.add(i, indexValue - 1); //再-1操作
            } else if (indexValue == index) {    //如果记录上一首播放记录的集合等于此索引，移除
                lastPlayIndexList.remove(i);
            }
        }
        /**更新当前播放索引*/
        if (currentPlayIndex > index) {
            currentPlayIndex = currentPlayIndex - 1;
        } else if (currentPlayIndex == index) { //如果清除的为当前的播放歌曲
            playNext();
            if (currentPlayIndex != 0){
                currentPlayIndex = currentPlayIndex - 1;
            }
        }
        playListController.getTableViewPlayList().getItems().remove(index);   //移除选中的行
        playListSongs.remove(index);
        playListController.getTableViewPlayList().getSelectionModel().clearSelection();    //清除选择选中行状态
        bottomController.getLabPlayListCount().setText(String.valueOf(playListSongs.size()));    //更新底部右下角的歌曲数目显示
        if (playListSongs.size() == 0){ //如果播放列表没有歌曲了,销毁播放器
            destroy();
        }
    }

    @Override
    public void addFavor() {

    }

    /**初始化播放列表
     * @param tableItems 需要播放的表格集合*/
    private void initializePlayList(List tableItems){
        if (nextPlayIndexList != null && nextPlayIndexList.size() > 0){
            nextPlayIndexList.clear();
        }
        if (lastPlayIndexList != null && lastPlayIndexList.size() > 0){
            lastPlayIndexList.clear();
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
        if (mediaPlayer != null){
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        currentPlayIndex = -1;
        if (lastPlayIndexList != null && lastPlayIndexList.size() > 0){
            lastPlayIndexList.clear();
        }
        if (nextPlayIndexList != null && nextPlayIndexList.size() > 0){
            nextPlayIndexList.clear();
        }
        if (playListSongs != null && playListSongs.size() > 0){
            playListSongs.clear(); //清空播放列表
        }

        //还需要更新底部显示音乐进度的GUI显示
        bottomController.getLabAlbum().setGraphic(ImageUtils.createImageView("image/DefaultAlbumImage_200.png", 58, 58));    //设置默认的图片专辑图
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

    @Override
    public void seek(Duration duration) {
        if (mediaPlayer != null){
            mediaPlayer.seek(duration);
        }
    }

    @Override
    public void setMute(boolean mute) {
        this.mute = mute;
        if (mediaPlayer != null){
            mediaPlayer.setMute(mute);
        }
        if (mute){  //设置静音和一些GUI显示
            bottomController.getLabSoundIcon().setGraphic(ImageUtils.createImageView("image/NeteaseVolumeMuteIcon.png",19,19));
        } else{
            bottomController.getLabSoundIcon().setGraphic(ImageUtils.createImageView("image/NeteaseVolumeIcon.png",19,19));
        }
    }

    @Override
    public void switchMute(){
        if (isMute()){
            setMute(false);
            bottomController.getSliderVolume().setValue(volume);
        }else {
            setMute(true);
            volume = bottomController.getSliderVolume().getValue(); //存储当前的值
            bottomController.getSliderVolume().setValue(0);
        }
    }

    @Override
    public void switchPlayMode() {
        if (playMode == PlayMode.SEQUENCE){
            playMode = PlayMode.SEQUENCE_LOOP;
            bottomController.getLabPlayModeIcon().setGraphic(ImageUtils.createImageView("image/NeteaseSequenceLoopMode.png",24,24));
            WindowUtils.toastInfo(centerController.getStackPane(),new Label("顺序循环"));
        }
        else if (playMode == PlayMode.SEQUENCE_LOOP){
            playMode = PlayMode.SINGLE_LOOP;
            bottomController.getLabPlayModeIcon().setGraphic(ImageUtils.createImageView("image/NeteaseSingleRoopIcon.png",24,24));
            WindowUtils.toastInfo(centerController.getStackPane(),new Label("单曲循环"));
        }
        else if (playMode == PlayMode.SINGLE_LOOP){
            playMode = PlayMode.SHUFFLE;
            nextPlayIndexList.clear();
            lastPlayIndexList.clear();
            bottomController.getLabPlayModeIcon().setGraphic(ImageUtils.createImageView("image/NeteaseShufflePlayMode.png",24,24));
            WindowUtils.toastInfo(centerController.getStackPane(),new Label("随机播放"));
        }
        else if (playMode == PlayMode.SHUFFLE){
            playMode = PlayMode.SEQUENCE;
            bottomController.getLabPlayModeIcon().setGraphic(ImageUtils.createImageView("image/NeteaseSequencePlayMode.png",24,24));
            WindowUtils.toastInfo(centerController.getStackPane(),new Label("顺序播放"));
        }
    }

    /**
     * 自定义媒体播放器暂停行为
     */
    @Override
    public void pause() {
        mediaPlayer.pause();
        bottomController.getLabPlay().setGraphic(ImageUtils.createImageView("image/NeteasePause.png", 32, 32));  //"播放、暂停"按钮图片
        if (lyricContentController.isShow() && lyricContentController.getRotateTransition().getStatus() != Animation.Status.PAUSED){
            lyricContentController.getRotateTransition().pause();
        }
    }

    @Override
    public void prepareGUI() throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        //.播放、暂停"按钮图片
        bottomController.getLabPlay().setGraphic(ImageUtils.createImageView("image/NeteasePause.png", 32, 32));
        //歌曲名称、歌手、歌曲总时间
        bottomController.getLabMusicName().setText(getCurrentPlaySong().getName());
        bottomController.getLabMusicSinger().setText(getCurrentPlaySong().getSinger());
        bottomController.getLabTotalTime().setText(getCurrentPlaySong().getTotalTime());
        //播放进度条设置
        bottomController.getSliderSong().setValue(0);
        bottomController.getSliderSong().setMax(TimeUtils.toSeconds(getCurrentPlaySong().getTotalTime()));  //设置歌曲滑动条的最大值为歌曲的秒数
        //专辑图片
        if (getCurrentPlaySong().getImageURL() != null){    //如果URL不为空,就是在线资源了.
            Image image = new Image(getCurrentPlaySong().getImageURL(),58,58,true,true);
            if (!image.isError()){
                bottomController.getLabAlbum().setGraphic(ImageUtils.createImageView(image,58,58));
            }else { //无网络时

            }
        }else { //本地资源加载专辑图
            bottomController.getLabAlbum().setGraphic(ImageUtils.getAlbumImageView(getCurrentPlaySong(),58,58));
        }
    }

    /**
     * 自定义媒体播放器播放行为
     */
    @Override
    public void play() {
        mediaPlayer.play();
        bottomController.getLabPlay().setGraphic(ImageUtils.createImageView("image/NeteasePlaying.png", 32, 32));  //"播放、暂停"按钮图片
        if (lyricContentController.isShow()
                && lyricContentController.getRotateTransition().getStatus() != Animation.Status.RUNNING){
            lyricContentController.getRotateTransition().play();
        }
    }
}
