package application;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.stereotype.Component;
import service.LoadUserService;
import util.WindowUtils;

@Component
public class FXApplication extends Application {

    /**Spring上下文*/
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        /**Spring配置文件路径*/
        String APPLICATION_CONTEXT_PATH = "/config/application-context.xml";
        applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_PATH);
        applicationContext.getBean(LoadUserService.class).start();  //启动加载用户的服务
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
        
        primaryStage.show();  //显示主舞台
        WindowUtils.addWindowsPlatformTaskBarIconifyBehavior();  //为primaryStage添加Windows平台显示窗体时单击任务栏图标可以最小化
    }

    @Override
    public void stop() {
        applicationContext.close();
    }

}
