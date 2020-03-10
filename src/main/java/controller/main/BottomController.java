package controller.main;

import application.SpringFXMLLoader;
import controller.content.AlbumLyricContentController;
import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import mediaplayer.MyMediaPlayer;
import mediaplayer.PlayMode;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import util.ImageUtils;
import util.WindowUtils;
import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Controller
public class BottomController {

    @FXML
    private Label labPlay;

    @FXML
    private Label labAlbum;

    @FXML
    private Label labMusicName;

    @FXML
    private Label labMusicSinger;

    @FXML
    private Label labPlayedTime;

    @FXML
    private Label labTotalTime;

    @FXML
    private ProgressBar progressBarSong;

    @FXML
    private Slider sliderSong;

    @FXML
    private Label labSoundIcon;

    @FXML
    private ProgressBar progressBarVolume;

    @FXML
    private Slider sliderVolume;

    @Resource
    private MainController mainController;

    @FXML
    private Label labPlayModeIcon;

    @FXML
    private Label labPlayListCount;

    @FXML
    private HBox hBoxPlayListIcon;

    @Resource
    private MyMediaPlayer myMediaPlayer;

    @Resource
    private CenterController centerController;

    /**注入Spring上下文工具类*/
    @Resource
    private ApplicationContext applicationContext;

    /**装显示专辑、歌词面板的VBox容器*/
    private VBox vBoxAlbumLyricContainer;

    @Resource
    private AlbumLyricContentController albumLyricContentController;

    /**显示专辑歌词面板的动画*/
    private Timeline timelineShow;

    /**y隐藏专辑歌词面板的动画*/
    private Timeline timelineHide;

    public Label getLabPlay() {
        return labPlay;
    }

    public Label getLabAlbum() {
        return labAlbum;
    }

    public Label getLabMusicName() {
        return labMusicName;
    }

    public Label getLabMusicSinger() {
        return labMusicSinger;
    }

    public Label getLabPlayTime() {
        return labPlayedTime;
    }

    public Label getLabTotalTime() {
        return labTotalTime;
    }

    public Slider getSliderVolume() {
        return sliderVolume;
    }

    public ProgressBar getProgressBarSong() {
        return progressBarSong;
    }

    public Slider getSliderSong() {
        return sliderSong;
    }

    public Label getLabPlayListCount() {
        return labPlayListCount;
    }

    public Label getLabPlayModeIcon() {
        return labPlayModeIcon;
    }

    public VBox getVBoxAlbumLyricContainer() {
        return vBoxAlbumLyricContainer;
    }

    public void initialize(){
        progressBarSong.prefWidthProperty().bind(((StackPane)progressBarSong.getParent()).widthProperty());  //宽度绑定
        sliderSong.prefWidthProperty().bind(((StackPane)sliderSong.getParent()).widthProperty());            //宽度绑定
        //设置播放进度滑动条的监听事件，使进度条始终跟随滚动条更新
        sliderSong.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Date date = new Date((int)newValue.doubleValue()*1000); //乘以一千变成秒数
                labPlayedTime.setText(new SimpleDateFormat("mm:ss").format(date));
                progressBarSong.setProgress(newValue.doubleValue()/sliderSong.getMax());
            }
        });
        //设置音量滑动条的监听事件，使进度条始终跟随滚动条更新
        sliderVolume.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                progressBarVolume.setProgress(newValue.doubleValue() );
                if (observable.getValue().doubleValue() == 0){
                    System.out.println("===0");
                    if (myMediaPlayer.getMediaPlayer() != null && !myMediaPlayer.getMediaPlayer().isMute()){
                        myMediaPlayer.getMediaPlayer().setMute(true);
                    }
                    labSoundIcon.setGraphic(ImageUtils.createImageView("image/NeteaseVolumeMuteIcon.png",19,19));
                }
                else if (observable.getValue().doubleValue() > 0){
                    System.out.println(">>>0");
                    if (myMediaPlayer.getMediaPlayer() != null && myMediaPlayer.getMediaPlayer().isMute()){
                        myMediaPlayer.getMediaPlayer().setMute(false);
                    }
                    labSoundIcon.setGraphic(ImageUtils.createImageView("image/NeteaseVolumeIcon.png",19,19));
                }
            }
        });

    }

    /**专辑图片单击事件处理*/
    @FXML
    public void onClickedAlbum(MouseEvent mouseEvent) throws Throwable {
        //如果容器属性变量为null，初始化它
        if (vBoxAlbumLyricContainer == null){
            vBoxAlbumLyricContainer = new VBox();
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/content/album-lyric-content.fxml");
            Pane albumLyricPane = fxmlLoader.load();
            albumLyricContentController = fxmlLoader.getController();

            vBoxAlbumLyricContainer.getChildren().add(albumLyricPane);
            albumLyricPane.maxHeightProperty().bind(vBoxAlbumLyricContainer.heightProperty());
            albumLyricPane.maxWidthProperty().bind(vBoxAlbumLyricContainer.widthProperty());
            albumLyricPane.minHeightProperty().bind(vBoxAlbumLyricContainer.heightProperty());
            albumLyricPane.minWidthProperty().bind(vBoxAlbumLyricContainer.widthProperty());
        }
        if (!albumLyricContentController.isShowing()){
            showAlbumLyricPane();
        } else{
            hideAlbumLyricPane();
        }
    }

    /**显示专辑歌词面板函数*/
    public void showAlbumLyricPane(){
        albumLyricContentController.setShowing(true);
        if (!centerController.getStackPane().getChildren().contains(vBoxAlbumLyricContainer)){
            centerController.getStackPane().getChildren().add(vBoxAlbumLyricContainer);
        }
        centerController.getStackPane().setAlignment(Pos.BOTTOM_LEFT);
        if (timelineHide != null && timelineHide.getStatus() == Animation.Status.RUNNING){
            timelineHide.pause();
        }
        timelineShow = new Timeline();
        timelineShow.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(0.2),new KeyValue(vBoxAlbumLyricContainer.maxHeightProperty(),centerController.getStackPane().getHeight(),Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(0.2),new KeyValue(vBoxAlbumLyricContainer.maxWidthProperty(),centerController.getStackPane().getWidth(),Interpolator.EASE_IN)),
                new KeyFrame((Duration.seconds(0.2)),new KeyValue(albumLyricContentController.getIvAlbumOutdoor().fitHeightProperty(),320)),
                new KeyFrame((Duration.seconds(0.2)),new KeyValue(albumLyricContentController.getIvAlbumOutdoor().fitWidthProperty(),320)),
                new KeyFrame((Duration.seconds(0.2)),new KeyValue(albumLyricContentController.getIvAlbum().fitHeightProperty(),190)),
                new KeyFrame((Duration.seconds(0.2)),new KeyValue(albumLyricContentController.getIvAlbum().fitWidthProperty(),190))
        );
        timelineShow.play();
        vBoxAlbumLyricContainer.setMinWidth(0);
        vBoxAlbumLyricContainer.setMinHeight(0);
        timelineShow.setOnFinished((event -> {
            centerController.getStackPane().setAlignment(Pos.CENTER);
            vBoxAlbumLyricContainer.maxHeightProperty().bind(centerController.getStackPane().heightProperty());
            vBoxAlbumLyricContainer.maxWidthProperty().bind(centerController.getStackPane().widthProperty());
        }));

        //控制专辑图旋转动画
        if (myMediaPlayer.getMediaPlayer() != null && myMediaPlayer.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING
        && albumLyricContentController.getRotateTransition() != null && albumLyricContentController.getRotateTransition().getStatus() != Animation.Status.RUNNING){
            albumLyricContentController.getRotateTransition().play();
        }
    }

    /**隐藏专辑歌词面板函数*/
    public void hideAlbumLyricPane(){
        albumLyricContentController.setShowing(false);  //设置显示标记为false,隐藏了,不是正在显示
        vBoxAlbumLyricContainer.maxWidthProperty().unbind();
        vBoxAlbumLyricContainer.maxHeightProperty().unbind();

        if (timelineShow != null && timelineShow.getStatus() == Animation.Status.RUNNING){
            timelineShow.pause();
        }
        timelineHide = new Timeline();
        timelineHide.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(0.2), new KeyValue(vBoxAlbumLyricContainer.maxHeightProperty(), 0,Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(0.2), new KeyValue(vBoxAlbumLyricContainer.maxWidthProperty(), 0,Interpolator.EASE_IN)),
                new KeyFrame((Duration.seconds(0.2)),new KeyValue(albumLyricContentController.getIvAlbumOutdoor().fitHeightProperty(),0)),
                new KeyFrame((Duration.seconds(0.2)),new KeyValue(albumLyricContentController.getIvAlbumOutdoor().fitWidthProperty(),0)),
                new KeyFrame((Duration.seconds(0.2)),new KeyValue(albumLyricContentController.getIvAlbum().fitHeightProperty(),0)),
                new KeyFrame((Duration.seconds(0.2)),new KeyValue(albumLyricContentController.getIvAlbum().fitWidthProperty(),0))
        );
        centerController.getStackPane().setAlignment(Pos.BOTTOM_LEFT);
        timelineHide.play();
        timelineHide.setOnFinished((event -> {
            centerController.getStackPane().getChildren().remove(vBoxAlbumLyricContainer);
            centerController.getStackPane().setAlignment(Pos.CENTER);
        }));

        //控制专辑图旋转动画,暂停旋转动画,减少资源使用
        if (albumLyricContentController.getRotateTransition() != null && albumLyricContentController.getRotateTransition().getStatus() != Animation.Status.PAUSED){
            albumLyricContentController.getRotateTransition().pause();
        }
    }

    /**播放上一首单击事件处理*/
    @FXML
    public void onClickedPlayLast(MouseEvent mouseEvent) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY && myMediaPlayer.getMediaPlayer()!=null){
            if (myMediaPlayer.getPlayListSongs().size()==1){  //如果播放列表只有一首歌,重新播放第一首歌，即原来的这首歌
                myMediaPlayer.getMediaPlayer().seek(new Duration(0));
            }
            else{  //否则表格的歌曲大于1，播放上一首歌曲
                if (myMediaPlayer.getNextPlayIndexList().contains(myMediaPlayer.getCurrentPlayIndex())){
                    myMediaPlayer.getNextPlayIndexList().remove((Object)myMediaPlayer.getCurrentPlayIndex());
                }
                myMediaPlayer.getNextPlayIndexList().add(myMediaPlayer.getCurrentPlayIndex());  //播放上一首歌曲之前，把当前的索引添加到下一次播放的索引列表
                if (myMediaPlayer.getLastPlayIndexList().size()==0){    //如果记录上一首播放的歌曲的列表小于0，证明当前没有上一首歌播放
                    if (myMediaPlayer.getPlayMode() == PlayMode.SHUFFLE) {   //如果是“随机播放”模式
                        while (true) {  //直到生成的随机数不是当前播放的索引值，执行播放
                            int randomIndex = new Random().nextInt(myMediaPlayer.getPlayListSongs().size());
                            if (randomIndex != myMediaPlayer.getCurrentPlayIndex()) {  //如果随机索引值不是当前播放的索引值
                                myMediaPlayer.setCurrentPlayIndex(randomIndex);       //替换当前的播放索引值,退出循环
                                break;
                            }
                        }
                    }else { //否则，则为“顺序播放”或“单曲循环”或“顺序循环”模式，且在播放列表歌曲大于1的情况下
                        int currentPlayIndex = myMediaPlayer.getCurrentPlayIndex();
                        if (currentPlayIndex == 0){ //如果当前播放歌曲索引为第0位置，设置为播放列表最后的歌曲索引
                            currentPlayIndex = myMediaPlayer.getPlayListSongs().size() - 1;
                        }else { //否则，都是当前播放索引-1
                            currentPlayIndex = currentPlayIndex - 1;
                        }
                        myMediaPlayer.setCurrentPlayIndex(currentPlayIndex);    //更新当前播放索引
                    }
                    myMediaPlayer.playSong(myMediaPlayer.getPlayListSongs().get(myMediaPlayer.getCurrentPlayIndex()));  //执行播放索引值对应的歌曲
                }
                else{       //否则,则lastPlayIndexList的大小大于零,存储有索引,取出记录上一首歌列表里的最后一次添加的那一个歌曲播放
                    int index = myMediaPlayer.getLastPlayIndexList().size()-1;
                    myMediaPlayer.setCurrentPlayIndex(myMediaPlayer.getLastPlayIndexList().get(index));
                    myMediaPlayer.playSong(myMediaPlayer.getPlayListSongs().get(myMediaPlayer.getCurrentPlayIndex()));
                    myMediaPlayer.getLastPlayIndexList().remove(index);
                }
            }
        }
    }

    /**播放/暂停单击事件处理*/
    @FXML
    public void onClickedPlayOrPause(MouseEvent mouseEvent) {
        if (myMediaPlayer.getMediaPlayer() != null){
            if (myMediaPlayer.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING){
                myMediaPlayer.pause();
            } else {
                myMediaPlayer.play();
            }
        }
    }

    /**播放下一首单击事件处理*/
    @FXML
    public void onClickedPlayNext(MouseEvent mouseEvent) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY && myMediaPlayer.getMediaPlayer() != null){

            if (myMediaPlayer.getPlayListSongs().size()==1){     //播放列表的歌曲只有一首歌时执行的处理
                myMediaPlayer.getMediaPlayer().seek(new Duration(0));
            }
            else {      //否则播放列表的歌曲大于1，播放下一首歌曲
                if (myMediaPlayer.getLastPlayIndexList().contains(myMediaPlayer.getCurrentPlayIndex())){
                    myMediaPlayer.getLastPlayIndexList().remove((Object)myMediaPlayer.getCurrentPlayIndex());
                }
                myMediaPlayer.getLastPlayIndexList().add(myMediaPlayer.getCurrentPlayIndex());  //播放下一首歌曲之前，把当前的索引添加到上一次播放的索引列表
                if (myMediaPlayer.getNextPlayIndexList().size() == 0){    //如果记录下一首播放的歌曲的列表小于0，证明当前没有下一首歌播放
                    if (myMediaPlayer.getPlayMode() == PlayMode.SHUFFLE) {   //“随机播放”模式
                        while (true) {  //直到生成的随机数不是当前播放的索引值，执行播放
                            int randomIndex = new Random().nextInt(myMediaPlayer.getPlayListSongs().size());
                            if (randomIndex != myMediaPlayer.getCurrentPlayIndex()) {  //如果随机索引值不是当前播放的索引值
                                myMediaPlayer.setCurrentPlayIndex(randomIndex);       //替换当前的播放索引值,退出循环
                                break;
                            }
                        }
                    }else { //否则，则为“顺序播放”或“单曲循环”或“顺序循环”模式，且在播放列表歌曲大于1的情况下
                        int currentPlayIndex = myMediaPlayer.getCurrentPlayIndex();
                        if (currentPlayIndex == myMediaPlayer.getPlayListSongs().size() - 1){ //如果当前播放歌曲索引为第0位置，设置为播放列表最后的歌曲索引
                            currentPlayIndex = 0;
                        }else { //否则，都是当前播放索引+1
                            currentPlayIndex = currentPlayIndex + 1;
                        }
                        myMediaPlayer.setCurrentPlayIndex(currentPlayIndex);    //更新当前播放索引
                    }
                    myMediaPlayer.playSong(myMediaPlayer.getPlayListSongs().get(myMediaPlayer.getCurrentPlayIndex()));  //执行播放索引值对应的歌曲
                }
                else{       //否则,则nextPlayIndexList的大小大于零,存储有索引,取出记录下一首歌列表里的最后一次添加的那一个歌曲播放
                    int index = myMediaPlayer.getNextPlayIndexList().size()-1;
                    myMediaPlayer.setCurrentPlayIndex(myMediaPlayer.getNextPlayIndexList().get(index));
                    myMediaPlayer.getNextPlayIndexList().remove(index);
                    myMediaPlayer.playSong(myMediaPlayer.getPlayListSongs().get(myMediaPlayer.getCurrentPlayIndex()));
                }
            }

        }
    }

    /**歌曲进度滑动条的鼠标释放事件*/
    @FXML
    public void onReleasedSliderSong(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){  //鼠标左击释放
            if (myMediaPlayer.getMediaPlayer()!=null && !sliderSong.isValueChanging()){
                myMediaPlayer.getMediaPlayer().seek(new Duration(1000 * sliderSong.getValue()));
            }
        }
    }

    /**歌曲进度滑动条的鼠标单击事件*/
    @FXML
    public void onClickedSliderSong(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            if (myMediaPlayer.getMediaPlayer()!=null && !sliderSong.isValueChanging()){
                myMediaPlayer.getMediaPlayer().seek(new Duration(1000 * sliderSong.getValue()));
            }
        }else {
            System.out.println("clicked");
        }
    }

    @FXML
    public void onPressedSliderSong(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){

        }else {
            System.out.println("pressed");
//            sliderSong.setMouseTransparent(true);
        }
    }

    /**音量图标的单击事件*/
    @FXML
    public void onClickedSoundIcon(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY && myMediaPlayer.getMediaPlayer() != null){  //鼠标左击
            if (!myMediaPlayer.getMediaPlayer().isMute()){   //如果mediaPlayer不为空且不是静音，设置静音和一些GUI显示
                myMediaPlayer.getMediaPlayer().setMute(true);
                labSoundIcon.setGraphic(ImageUtils.createImageView("image/NeteaseVolumeMuteIcon.png",19,19));
                myMediaPlayer.setMuteBeforeVolume(sliderVolume.getValue());   //存储当前的值
                sliderVolume.setValue(0);
            }
            else if (myMediaPlayer.getMediaPlayer().isMute()){
                myMediaPlayer.getMediaPlayer().setMute(false);
                labSoundIcon.setGraphic(ImageUtils.createImageView("image/NeteaseVolumeIcon.png",19,19));
                sliderVolume.setValue(myMediaPlayer.getMuteBeforeVolume());      //更新为静音之前的值
            }
        }
    }

    /**播放模式图标切换的鼠标事件处理*/
    @FXML
    public void onClickedPlayMode(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){     //鼠标左击才执行
            if (myMediaPlayer.getPlayMode() == PlayMode.SEQUENCE){
                myMediaPlayer.setPlayMode(PlayMode.SEQUENCE_LOOP);
                labPlayModeIcon.setGraphic(ImageUtils.createImageView("image/NeteaseSequenceLoopMode.png",24,24));
                WindowUtils.toastInfo(centerController.getStackPane(),new Label("顺序循环"));
            }
            else if (myMediaPlayer.getPlayMode() == PlayMode.SEQUENCE_LOOP){
                myMediaPlayer.setPlayMode(PlayMode.SINGLE_LOOP);
                labPlayModeIcon.setGraphic(ImageUtils.createImageView("image/NeteaseSingleRoopIcon.png",24,24));
                WindowUtils.toastInfo(centerController.getStackPane(),new Label("单曲循环"));
            }
            else if (myMediaPlayer.getPlayMode() == PlayMode.SINGLE_LOOP){
                myMediaPlayer.setPlayMode(PlayMode.SHUFFLE);
                myMediaPlayer.setLastPlayIndexList(null);   //设置记录上一首播放索引为空
//                myMediaPlayer.setNextPlayIndexList(null);   //设置记录下一首播放索引为空
                labPlayModeIcon.setGraphic(ImageUtils.createImageView("image/NeteaseShufflePlayMode.png",24,24));
                WindowUtils.toastInfo(centerController.getStackPane(),new Label("随机播放"));
            }
            else if (myMediaPlayer.getPlayMode() == PlayMode.SHUFFLE){
                myMediaPlayer.setPlayMode(PlayMode.SEQUENCE);
                labPlayModeIcon.setGraphic(ImageUtils.createImageView("image/NeteaseSequencePlayMode.png",24,24));
                WindowUtils.toastInfo(centerController.getStackPane(),new Label("顺序播放"));
            }
        }
    }

    /**“右下角播放列表图标的鼠标单击事件”*/
    @FXML
    public void onClickedPlayListIcon(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/popup/playlist-pane.fxml");  //加载音乐歌单面板容器的fxml文件
            mainController.getStackPane().getChildren().add(fxmlLoader.load()); //加进stackPane中去
        }
    }

    /**更新播放列表图标GUI显示的歌曲统计*/
    public void updatePlayListIcon(){
        labPlayListCount.setText(String.valueOf(myMediaPlayer.getPlayListSongs().size()));
    }

}
