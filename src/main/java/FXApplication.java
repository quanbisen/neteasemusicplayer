
import util.SpringUtil;
import util.WindowUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

public class FXApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle("音乐"); // 设置标题
//        primaryStage.getIcons().add(new Image("/image/NeteaseMusicPlayerIcon.png")); //设置图标
        Scene scene = new Scene(this.getRoot());
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED); //去掉默认的标题栏
        primaryStage.show();  //显示主舞台
        WindowUtils.addResizable(primaryStage,858,570);  //为primaryStage添加自由缩放
        WindowUtils.addFixedCode(primaryStage);  //为primaryStage添加一些GUI的修复代码
        WindowUtils.addWindowsPlatformTaskBarIconifyBehavior();  //为primaryStage添加Windows平台显示窗体时单击任务栏图标可以最小化
    }

    public static void main(String[] args) {
        Application.launch(FXApplication.class,args);
    }

    private Parent getRoot() throws IOException {
        FXMLLoader fxmlLoader = SpringUtil.getFXMLLoaderWithSpring();
        fxmlLoader.setLocation(this.getClass().getResource("/fxml/main.fxml"));
        return fxmlLoader.load();
    }

}
