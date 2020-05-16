package controller.content;

import controller.main.BottomController;
import controller.main.CenterController;
import controller.main.MainController;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.CacheHint;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import mediaplayer.MyMediaPlayer;
import model.PlayListSong;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import service.HideScrollerBarService;
import service.LoadLyricService;
import util.ImageUtils;
import util.WindowUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @author super lollipop
 * @date 20-2-28
 */
@Controller
public class LyricContentController {

    /**VBox根容器*/
    @FXML
    private VBox vBoxLyricContentContainer;

    @FXML
    private StackPane stackPane;

    @FXML
    private Label labBlur;

    @FXML
    private BorderPane root;

    /**root 的左孩子容器*/
    @FXML
    public BorderPane albumPane;

    /**root 的中间孩子容器*/
    @FXML
    private BorderPane lyricPane;

    /**存放黑色专辑图和歌曲专辑图的StackPane容器*/
    @FXML
    private StackPane stackPaneAlbum;

    @FXML
    private ImageView ivAlbumOutdoor;

    @FXML
    private ImageView ivAlbum;

    @FXML
    private Label labTitle;

    @FXML
    private ImageView labResizeSmall;

    @FXML
    private Label labAlbum;

    @FXML
    private Label labArtist;

    @FXML
    private ScrollPane scrollLyric;

    @FXML
    private VBox vBoxLyric;

    @FXML
    private Label labAddFavor;

    @FXML
    private Label labCollect;

    @FXML
    private ProgressIndicator progressIndicator;

    @Resource
    private MainController mainController;

    @Resource
    private BottomController bottomController;

    @Resource
    private MyMediaPlayer myMediaPlayer;

    private RotateTransition rotateTransition;

    @Resource
    private CenterController centerController;

    @Resource
    private ApplicationContext applicationContext;

    /**存储歌词时间的集合*/
    private List<Float> lyricTimeList;

    private int lyricIndex;

    /**标记这个面板是否正在显示在centerPane里,在bottomController里控制,当单击专辑图片时,设置显示true*/
    private boolean show;

    public LyricContentController() {
        lyricTimeList = new ArrayList<>();
    }

    public ImageView getIvAlbumOutdoor() {
        return ivAlbumOutdoor;
    }

    public ImageView getIvAlbum() {
        return ivAlbum;
    }

    public RotateTransition getRotateTransition() {
        return rotateTransition;
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public List<Float> getLyricTimeList() {
        return lyricTimeList;
    }

    public ScrollPane getScrollLyric() {
        return scrollLyric;
    }

    public VBox getVBoxLyric() {
        return vBoxLyric;
    }

    public int getLyricIndex() {
        return lyricIndex;
    }

    public void setLyricIndex(int lyricIndex) {
        this.lyricIndex = lyricIndex;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public void initialize() throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        //设置布局的宽高度绑定
        WindowUtils.absolutelyBind(root,stackPane);
        WindowUtils.absolutelyBind(labBlur,stackPane);
        WindowUtils.absolutelyBind(stackPane,vBoxLyricContentContainer);
//        WindowUtils.absolutelyBind(vBoxLyric,scrollLyric);  // /**annotation for test*/
        stackPane.widthProperty().addListener(((observable, oldValue, newValue) -> {
            albumPane.setPrefWidth(observable.getValue().doubleValue()/100*43);
            albumPane.setMaxWidth(observable.getValue().doubleValue()/100*43);
            albumPane.setMinWidth(observable.getValue().doubleValue()/100*43);
            lyricPane.setPrefWidth(observable.getValue().doubleValue()/100*57);
            lyricPane.setMaxWidth(observable.getValue().doubleValue()/100*57);
            lyricPane.setMinWidth(observable.getValue().doubleValue()/100*57);
        }));
        /**annotation for test*/
        vBoxLyric.prefWidthProperty().bind(scrollLyric.widthProperty());
        vBoxLyric.prefHeightProperty().bind(scrollLyric.heightProperty());
        ((ImageView)labBlur.getGraphic()).fitWidthProperty().bind(stackPane.widthProperty());
        ((ImageView)labBlur.getGraphic()).fitHeightProperty().bind(stackPane.heightProperty());

        //设置缓存
        stackPaneAlbum.setCache(true);
        stackPaneAlbum.setCacheHint(CacheHint.ROTATE);
        ivAlbumOutdoor.setCache(true);
        ivAlbumOutdoor.setCacheHint(CacheHint.ROTATE);
        ivAlbum.setCache(true);
        ivAlbum.setCacheHint(CacheHint.ROTATE);
        //初始化设置专辑图片的容器的旋转动画
        rotateTransition = new RotateTransition(Duration.seconds(16),stackPaneAlbum);
        rotateTransition.setInterpolator(Interpolator.LINEAR);  //匀速旋转
        rotateTransition.setByAngle(360);   //360度旋转
        rotateTransition.setCycleCount(Animation.INDEFINITE);   //无数次

        /**实现scrollPane的滚动条在不滚动一段时间后隐藏滚动条的效果　ｓｔａｒｔ
         * －－－－－－－－－－－－－－－－－－－－－－－－－－－－－－*/
        scrollLyric.addEventFilter(ScrollEvent.ANY,event -> {
            ScrollBar scrollBar = (ScrollBar) scrollLyric.lookup(".scroll-bar:vertical");
            if (scrollBar.isDisable()){
                scrollBar.setDisable(false);
            }
            HideScrollerBarService hideScrollerBarService = applicationContext.getBean(HideScrollerBarService.class);   //获取隐藏滚动条的服务对象
            hideScrollerBarService.setScrollBar(scrollBar);
            hideScrollerBarService.setDelay(Duration.seconds(5));   //设置延时5秒
            hideScrollerBarService.start();   //启动服务
            hideScrollerBarService.setOnSucceeded(event1 -> {   //成功后取消服务
                hideScrollerBarService.cancel();
            });
        });
        /**实现scrollPane的滚动条在不滚动一段时间后隐藏滚动条的效果　ｅｎｄ
         * －－－－－－－－－－－－－－－－－－－－－－－－－－－－－－*/

        loadAlbumLyric();
    }

    /**设置专辑图片和歌词的函数*/
    public void loadAlbumLyric() throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        if (vBoxLyric != null && ivAlbum != null){
            vBoxLyric.getChildren().clear();
            scrollLyric.vvalueProperty().set(0);
            lyricIndex = 0;
            lyricTimeList.clear();

            PlayListSong playListSong = myMediaPlayer.getCurrentPlaySong();
            if (playListSong != null){
                labArtist.setText(playListSong.getSinger());
                labTitle.setText(playListSong.getName());
                labAlbum.setText(playListSong.getAlbum());
                ivAlbum.setImage(ImageUtils.getAlbumImage(playListSong,190,190));
                Circle circle = new Circle(95,95,95);
                ivAlbum.setClip(circle);
                ivAlbum.fitHeightProperty().addListener(((observable, oldValue, newValue) -> {
                    circle.setCenterX(observable.getValue().doubleValue()/2);
                    circle.setCenterY(observable.getValue().doubleValue()/2);
                    circle.setRadius(observable.getValue().doubleValue()/2);
                }));

                BoxBlur boxBlur = new BoxBlur(150,150,3);
                labBlur.getGraphic().setEffect(boxBlur);
                ((ImageView)labBlur.getGraphic()).setImage(ivAlbum.getImage());
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5),labBlur.getGraphic());
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                if (centerController.getStackPane().getChildren().contains(stackPane.getParent())){ //如果当前的中间StackPane包含专辑歌词面板，执行播放专辑旋转动画
                    rotateTransition.play();
                }

                LoadLyricService loadLyricService = applicationContext.getBean(LoadLyricService.class);
                progressIndicator.visibleProperty().bind(loadLyricService.runningProperty());
                loadLyricService.start();
            }
        }
    }

    /**滚动歌词行为
     * @param second  当前的播放描述,数值传入保留了一位小数
     * */
    public void scrollLyric(float second){
        if (lyricTimeList != null && lyricTimeList.size() >0){
            float min;
            float max;
            if (lyricIndex == 0) {
                min = 0;
                if (!vBoxLyric.getChildren().get(lyricIndex).getStyleClass().contains("labCurrent")){   //当当前索引为第0行时,添加样式
                    vBoxLyric.getChildren().get(lyricIndex).getStyleClass().add("labCurrent");
                }
            }else{
                min = lyricTimeList.get(lyricIndex);
            }
            if (lyricIndex != lyricTimeList.size() - 1) {
                max = lyricTimeList.get(lyricIndex + 1);
            }else{
                max = lyricTimeList.get(lyricIndex);
            }
            //判断是否在正常的区间
            if (second >= min && second < max) {
                return;
            }
            System.out.println(vBoxLyric.getChildren().get(0).getStyleClass());
            if (lyricIndex < lyricTimeList.size() - 1 &&
                    second >= max) {
                lyricIndex = lyricIndex + 1;//当前歌词索引的指示器
            }else if (lyricIndex > 0 && second <= min){
                lyricIndex = lyricIndex - 1;
            }
            //滚动歌词
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5),new KeyValue(scrollLyric.vvalueProperty(),((float)lyricIndex - 3) / (vBoxLyric.getChildren().size() - 9), Interpolator.LINEAR)));
            timeline.play();

            Label labCurrent = (Label) vBoxLyric.getChildren().get(lyricIndex);
            if (!labCurrent.getStyleClass().contains("labCurrent")){
                labCurrent.getStyleClass().add("labCurrent");
            }
            if (lyricIndex - 1 >= 0 && vBoxLyric.getChildren().get(lyricIndex-1).getStyleClass().contains("labCurrent")){
                vBoxLyric.getChildren().get(lyricIndex-1).getStyleClass().remove("labCurrent");
            }
            if (lyricIndex + 1 <= vBoxLyric.getChildren().size() -1 && !vBoxLyric.getChildren().get(lyricIndex-1).getStyleClass().contains("labCurrent")){
                vBoxLyric.getChildren().get(lyricIndex+1).getStyleClass().remove("labCurrent");
            }
        }
    }

    /**变小面板事件*/
    @FXML
    public void onClickedResizeSmall(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            bottomController.hideLyricPane();
        }
    }
}
