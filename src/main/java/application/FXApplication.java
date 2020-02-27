package application;

import controller.main.BottomController;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import mediaplayer.Config;
import model.MediaPlayerState;
import mediaplayer.MyMediaPlayer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.stereotype.Component;
import service.LoadMediaPlayerStateService;
import service.ValidateUserService;
import util.JSONObjectUtils;
import util.WindowUtils;
import java.io.IOException;

@Component
public class FXApplication extends Application {

    /**Spring上下文*/
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        /**Spring配置文件路径*/
        String APPLICATION_CONTEXT_PATH = "/config/application-context.xml";
        applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_PATH);
//        ScheduledQueryUserService scheduledQueryUserService = applicationContext.getBean(ScheduledQueryUserService.class);  //启动加载用户的服务
//        scheduledQueryUserService.setPeriod(Duration.seconds(15));
//        scheduledQueryUserService.start();
        applicationContext.getBean(LoadMediaPlayerStateService.class).start();
    }

    public static void main(String[] args) {
        Application.launch(FXApplication.class,args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/main/main-pane.fxml");
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(this.getClass().getResource("/css/ContextMenuStyle.css").toExternalForm());  //添加contextmenu的样式
        primaryStage.setTitle("音乐"); // 设置标题
        primaryStage.getIcons().add(new Image("/image/NeteaseMusicPlayerIcon.png")); //设置图标
        primaryStage.setScene(scene);
        if (WindowUtils.isWindowsPlatform()){   //如果是Windows平台
            primaryStage.initStyle(StageStyle.TRANSPARENT);   //去掉Windows自带的标题栏
            WindowUtils.addResizable(primaryStage,860,570);  //为primaryStage添加自由缩放
            WindowUtils.addWindowsStyle(primaryStage);  //为primaryStage添加一些GUI的修复代码
        }else { //minWidth="870.0" minHeight="580.0"
            primaryStage.setMinWidth(870.0);
            primaryStage.setMinHeight(580.0);
        }
        this.addIconifiedBehavior(primaryStage);    //添加最大化最小化的行为
        primaryStage.show();  //显示主舞台
        // 获取屏幕可视化的宽高（Except TaskBar），把窗体设置在可视化的区域居中
        primaryStage.setX((Screen.getPrimary().getVisualBounds().getWidth() - primaryStage.getWidth()) / 2.0);
        primaryStage.setY((Screen.getPrimary().getVisualBounds().getHeight() - primaryStage.getHeight()) / 2.0);
        WindowUtils.addWindowsPlatformTaskBarIconifyBehavior();  //为primaryStage添加Windows平台显示窗体时单击任务栏图标可以最小化
    }

    @Override
    public void stop() throws IOException {
        this.saveState();
        applicationContext.close();
    }

    /**保存媒体播放器的状态函数*/
    private void saveState() throws IOException{
        MyMediaPlayer myMediaPlayer = applicationContext.getBean(MyMediaPlayer.class);
        MediaPlayerState mediaPlayerState = new MediaPlayerState();
        mediaPlayerState.setVolume(applicationContext.getBean(BottomController.class).getSliderVolume().getValue());
        mediaPlayerState.setCurrentPlayIndex(myMediaPlayer.getCurrentPlayIndex());
        mediaPlayerState.setPlayListSongs(myMediaPlayer.getPlayListSongs());
        mediaPlayerState.setPlayMode(myMediaPlayer.getPlayMode());
        JSONObjectUtils.saveObject(mediaPlayerState,applicationContext.getBean(Config.class).getMediaPlayerStateFile());
        //保存用户的登录信息
//        JSONObjectUtils.saveObject(applicationContext.getBean(Config.class).getUser(),applicationContext.getBean(Config.class).getLoginConfigFile());
    }

    /**
     * 修复最大化状态下，最小化窗体之后单击任务栏图标恢复时，窗体的高度和宽度是全屏的问题。修复后，宽度和高度是为屏幕可视化的宽度和高度
     */
    private void addIconifiedBehavior(Stage primaryStage) {
        primaryStage.iconifiedProperty().addListener((observable, oldValue, newValue) -> {
            //确保Windows平台下,窗体在最大化状态下最小化后，单击任务栏图标显示时占据的屏幕大小是可视化的全屏
            if (primaryStage.isMaximized() && WindowUtils.isWindowsPlatform()) {
                primaryStage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
                primaryStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
            }
            if (observable.getValue()){
                System.out.println("cancel");
                applicationContext.getBean(ValidateUserService.class).cancel();
            }else {
                System.out.println("restart");
                applicationContext.getBean(ValidateUserService.class).restart();
            }
        });
    }

}
