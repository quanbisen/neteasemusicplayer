package util;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
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
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setX((primaryStage.getWidth()-root.getMaxWidth())/2.0 + primaryStage.getX());
        stage.setY((primaryStage.getHeight()-root.getMaxHeight())/2.0 + primaryStage.getY());
        return stage;
    }
    
    /**@param primaryStage 主窗体stage舞台对象
     * @param centerStage  需要同步居中到primaryStage的stage对象
     * */
    public static void synchronizeCenter(Stage primaryStage,Stage centerStage){
        //主窗体坐标和宽度改变时需要触发的监听器，更新子窗体的位置，让子窗体一直居中显示
        primaryStage.xProperty().addListener((observable, oldValue, newValue) -> centerStage.setX((primaryStage.getWidth()-centerStage.getWidth())/2.0+observable.getValue().doubleValue()));
        primaryStage.yProperty().addListener((observable, oldValue, newValue) -> centerStage.setY((primaryStage.getHeight()-centerStage.getHeight())/2.0+observable.getValue().doubleValue()));
        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> centerStage.setX((observable.getValue().doubleValue()-centerStage.getWidth())/2.0+primaryStage.getX()));
        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> centerStage.setY((observable.getValue().doubleValue()-centerStage.getHeight())/2.0+primaryStage.getY()));
    }

    public static void synchronizeRightEdge(Stage primaryStage,Stage stage,double offSetX,double offSetY){
        BorderPane borderPane = (BorderPane)((StackPane)primaryStage.getScene().getRoot()).getChildren().get(0);
        System.out.println(borderPane.getWidth());
        primaryStage.xProperty().addListener(((observable, oldValue, newValue) -> {
            stage.setX( borderPane.getWidth() - stage.getWidth() + primaryStage.getX() + offSetX);
        }));
        primaryStage.yProperty().addListener(((observable, oldValue, newValue) -> {
            stage.setY(borderPane.getHeight() - stage.getHeight() + primaryStage.getY() + offSetY);
        }));
        primaryStage.widthProperty().addListener(((observable, oldValue, newValue) -> {
            stage.setX( borderPane.getWidth() - stage.getWidth() + primaryStage.getX() + offSetX);
        }));
        primaryStage.heightProperty().addListener(((observable, oldValue, newValue) -> {
            stage.setY(borderPane.getHeight() - stage.getHeight() + primaryStage.getY() + offSetY);
        }));
    }
}
