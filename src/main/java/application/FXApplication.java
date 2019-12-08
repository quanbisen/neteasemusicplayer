package application;

import javafx.scene.image.Image;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.stereotype.Component;
import util.WindowUtils;

@Component
public class FXApplication extends Application {

    private String APPLICATION_CONTEXT_PATH = "/config/application-context.xml";

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_PATH);
    }

    public static void main(String[] args) {
        Application.launch(FXApplication.class,args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/main.fxml");
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("音乐"); // 设置标题
        primaryStage.getIcons().add(new Image("/image/NeteaseMusicPlayerIcon.png")); //设置图标
        primaryStage.setScene(scene);
        if (WindowUtils.isWindowsPlatform()){
            primaryStage.initStyle(StageStyle.UNDECORATED);   //去掉Windows自带的标题栏
        }
        primaryStage.show();  //显示主舞台

    }

    @Override
    public void stop() throws Exception {
        applicationContext.close();
    }

}
