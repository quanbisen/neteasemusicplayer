package util;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author super lollipop
 * @date 19-11-30
 */
public final class StageUtils {
    
    /**@param primaryStage 主窗体stage舞台对象
     * @param root 根容器
     * 获取一个stage对象*/
    public static Stage getStage(Stage primaryStage, Region root){
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        //线程延迟运行，不然无法获取到root容器的宽度、高度
        Platform.runLater(() -> {
            stage.setX((primaryStage.getWidth()-root.getWidth())/2.0 + primaryStage.getX());
            stage.setY((primaryStage.getHeight()-root.getHeight())/2.0 + primaryStage.getY());
        });
        return stage;
    }
    
    /**@param primaryStage 主窗体stage舞台对象
     * @param centerStage  需要同步居中到primaryStage的stage对象
     * */
    public static void syncCenter(Stage primaryStage,Stage centerStage){
        //主窗体坐标和宽度改变时需要触发的监听器，更新子窗体的位置，让子窗体一直居中显示
        primaryStage.xProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                centerStage.setX((primaryStage.getWidth()-centerStage.getWidth())/2.0+newValue.doubleValue());
            }

        });
        primaryStage.yProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                centerStage.setY((primaryStage.getHeight()-centerStage.getHeight())/2.0+newValue.doubleValue());
            }

        });
        primaryStage.widthProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                centerStage.setX((newValue.doubleValue()-centerStage.getWidth())/2.0+primaryStage.getX());
            }

        });
        primaryStage.heightProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                centerStage.setY((newValue.doubleValue()-centerStage.getHeight())/2.0+primaryStage.getY());
            }

        });
    }
}
