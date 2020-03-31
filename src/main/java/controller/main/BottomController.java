package controller.main;

import application.SpringFXMLLoader;
import controller.content.LyricContentController;
import javafx.animation.*;
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
    private LyricContentController lyricContentController;

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

    public Label getLabSoundIcon() {
        return labSoundIcon;
    }



    public void initialize(){
        progressBarSong.prefWidthProperty().bind(((StackPane)progressBarSong.getParent()).widthProperty());  //宽度绑定
        sliderSong.prefWidthProperty().bind(((StackPane)sliderSong.getParent()).widthProperty());            //宽度绑定
        //设置播放进度滑动条的监听事件，使进度条始终跟随滚动条更新
        sliderSong.valueProperty().addListener((observable, oldValue, newValue) -> {
            Date date = new Date((int)newValue.doubleValue()*1000); //乘以一千变成秒数
            labPlayedTime.setText(new SimpleDateFormat("mm:ss").format(date));
            progressBarSong.setProgress(newValue.doubleValue()/sliderSong.getMax());
        });
        //设置音量滑动条的监听事件，使进度条始终跟随滚动条更新
        sliderVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
            progressBarVolume.setProgress(newValue.doubleValue());
            myMediaPlayer.setVolume(observable.getValue().doubleValue());
            if (observable.getValue().doubleValue() == 0 && !myMediaPlayer.isMute()){
                myMediaPlayer.setMute(true);
            }
            else if (observable.getValue().doubleValue() > 0 && myMediaPlayer.isMute()){
                myMediaPlayer.setMute(false);
            }
        });

    }

    /**专辑图片单击事件处理*/
    @FXML
    public void onClickedAlbum(MouseEvent mouseEvent) throws Throwable {
        //如果容器属性变量为null，初始化它
        if (vBoxAlbumLyricContainer == null){
            vBoxAlbumLyricContainer = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/content/album-lyric-content.fxml").load();
        }
        if (!lyricContentController.isShow()){  //如果歌词面板不是显示状态
            showLyricPane();   //显示歌词面板
        } else{ //否则
            hideLyricPane();   //隐藏歌词面板
        }
    }

    /**显示专辑歌词面板函数*/
    public void showLyricPane() throws ReadOnlyFileException, IOException, TagException, InvalidAudioFrameException, CannotReadException {
        lyricContentController.setShow(true);
        lyricContentController.loadAlbumLyric();  //调用加载专辑歌曲函数
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
                new KeyFrame((Duration.seconds(0.2)),new KeyValue(lyricContentController.getIvAlbumOutdoor().fitHeightProperty(),320)),
                new KeyFrame((Duration.seconds(0.2)),new KeyValue(lyricContentController.getIvAlbumOutdoor().fitWidthProperty(),320)),
                new KeyFrame((Duration.seconds(0.2)),new KeyValue(lyricContentController.getIvAlbum().fitHeightProperty(),190)),
                new KeyFrame((Duration.seconds(0.2)),new KeyValue(lyricContentController.getIvAlbum().fitWidthProperty(),190))
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
        if (myMediaPlayer.getPlayer() != null && myMediaPlayer.getPlayer().getStatus() == MediaPlayer.Status.PLAYING
        && lyricContentController.getRotateTransition() != null && lyricContentController.getRotateTransition().getStatus() != Animation.Status.RUNNING){
            lyricContentController.getRotateTransition().play();
        }
    }

    /**隐藏专辑歌词面板函数*/
    public void hideLyricPane(){
        lyricContentController.setShow(false);  //设置显示标记为false,隐藏了,不是正在显示
        vBoxAlbumLyricContainer.maxWidthProperty().unbind();
        vBoxAlbumLyricContainer.maxHeightProperty().unbind();

        if (timelineShow != null && timelineShow.getStatus() == Animation.Status.RUNNING){
            timelineShow.pause();
        }
        timelineHide = new Timeline();
        timelineHide.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(0.2), new KeyValue(vBoxAlbumLyricContainer.maxHeightProperty(), 0,Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(0.2), new KeyValue(vBoxAlbumLyricContainer.maxWidthProperty(), 0,Interpolator.EASE_IN)),
                new KeyFrame((Duration.seconds(0.2)),new KeyValue(lyricContentController.getIvAlbumOutdoor().fitHeightProperty(),0)),
                new KeyFrame((Duration.seconds(0.2)),new KeyValue(lyricContentController.getIvAlbumOutdoor().fitWidthProperty(),0)),
                new KeyFrame((Duration.seconds(0.2)),new KeyValue(lyricContentController.getIvAlbum().fitHeightProperty(),0)),
                new KeyFrame((Duration.seconds(0.2)),new KeyValue(lyricContentController.getIvAlbum().fitWidthProperty(),0))
        );
        centerController.getStackPane().setAlignment(Pos.BOTTOM_LEFT);
        timelineHide.play();
        timelineHide.setOnFinished((event -> {
            centerController.getStackPane().getChildren().remove(vBoxAlbumLyricContainer);
            centerController.getStackPane().setAlignment(Pos.CENTER);
        }));

        //控制专辑图旋转动画,暂停旋转动画,减少资源使用
        if (lyricContentController.getRotateTransition() != null && lyricContentController.getRotateTransition().getStatus() != Animation.Status.PAUSED){
            lyricContentController.getRotateTransition().pause();
        }
    }

    /**播放上一首单击事件处理*/
    @FXML
    public void onClickedPlayLast(MouseEvent mouseEvent) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            myMediaPlayer.playLast();
        }
    }

    /**播放/暂停单击事件处理*/
    @FXML
    public void onClickedPlayOrPause(MouseEvent mouseEvent) throws ReadOnlyFileException, IOException, TagException, InvalidAudioFrameException, CannotReadException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            myMediaPlayer.playOrPause();
        }
    }

    /**播放下一首单击事件处理*/
    @FXML
    public void onClickedPlayNext(MouseEvent mouseEvent) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            myMediaPlayer.playNext();
        }
    }

    /**歌曲进度滑动条的鼠标释放事件*/
    @FXML
    public void onReleasedSliderSong(MouseEvent mouseEvent) {
        myMediaPlayer.seek(new Duration(1000 * sliderSong.getValue()));
    }

    /**歌曲进度滑动条的鼠标单击事件*/
    @FXML
    public void onClickedSliderSong(MouseEvent mouseEvent) {
        myMediaPlayer.seek(new Duration(1000 * sliderSong.getValue()));
    }

    /**音量图标的单击事件*/
    @FXML
    public void onClickedSoundIcon(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){  //鼠标左击
            myMediaPlayer.switchMute();
        }
    }

    /**播放模式图标切换的鼠标事件处理*/
    @FXML
    public void onClickedPlayMode(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){     //鼠标左击才执行
            myMediaPlayer.switchPlayMode();
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



}
