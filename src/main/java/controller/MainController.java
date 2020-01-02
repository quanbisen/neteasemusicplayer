package controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import application.SpringFXMLLoader;
import util.WindowUtils;
import javax.annotation.Resource;

@Controller
public class MainController{

    /**根容器stackPane*/
    @FXML
    private StackPane stackPane;

    /**根容器stackPane的第一个孩子容器borderPane，deep-index=0，表示在stackPane的第0层*/
    @FXML
    private BorderPane borderPane;

    /**注入Spring上下文对象*/
    @Resource
    private ApplicationContext applicationContext;

    private Pane shadowPane;

    public BorderPane getBorderPane() {
        return borderPane;
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public void initialize() throws IOException {

        if (WindowUtils.isWindowsPlatform()){   //如果是windows平台,加载自定义设计的标题栏、添加阴影效果
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/main-top.fxml");
            borderPane.setTop(fxmlLoader.load());
            borderPane.getStyleClass().add("borderPane");

            /**使用矩形裁剪可视化容器，然后向跟容器stackPane添加阴影容器，实现阴影效果*/
            double shadowSize = 5;
            Rectangle rectangle = new Rectangle();
            borderPane.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
                if (((Stage)borderPane.getScene().getWindow()).isMaximized()){  //是最大化吗？
                    rectangle.relocate(0,0);  //重新布局X，Y轴位置
                    rectangle.setWidth(newValue.getWidth());
                    rectangle.setHeight(newValue.getHeight());
                }
                else {
                    rectangle.relocate(shadowSize,shadowSize);  //重新布局X，Y轴位置
                    rectangle.setWidth(newValue.getWidth() - 2 * shadowSize);
                    rectangle.setHeight(newValue.getHeight() - 2 * shadowSize);
                }
                borderPane.setClip(rectangle);
            });

            shadowPane = this.createShadowPane(stackPane,shadowSize);
            stackPane.getChildren().add(shadowPane);

            Platform.runLater(()->{
                //添加最大化最小化的监听器，最大化时移除阴影效果，最小化增加阴影效果
                ((Stage)borderPane.getScene().getWindow()).maximizedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue){
                        stackPane.getChildren().remove(shadowPane);
                    }
                    else {
                        stackPane.getChildren().add(shadowPane);
                    }
                });
            });

        }

    }

    private Pane createShadowPane(Pane rootPane, double shadowSize) {
        Pane shadowPane = new Pane();
        shadowPane.prefWidthProperty().bind(rootPane.widthProperty());
        shadowPane.prefHeightProperty().bind(rootPane.heightProperty());

        shadowPane.setStyle("-fx-background-color: WHITE;" +
                        "-fx-effect: dropshadow(gaussian, #333333," + shadowSize +", 0, 0,0 );" +
                        "-fx-background-insets: " + shadowSize + ";"
        );

        Rectangle innerBounds = new Rectangle();
        Rectangle outerBounds = new Rectangle();
        shadowPane.layoutBoundsProperty().addListener((observable, oldBounds, newBounds) -> {
            innerBounds.relocate(newBounds.getMinX() + shadowSize, newBounds.getMinY() + shadowSize);
            innerBounds.setWidth(newBounds.getWidth() - shadowSize * 2);
            innerBounds.setHeight(newBounds.getHeight() - shadowSize * 2);
            outerBounds.setWidth(newBounds.getWidth());
            outerBounds.setHeight(newBounds.getHeight());

            Shape clip = Shape.subtract(outerBounds, innerBounds);
            shadowPane.setClip(clip);
        });

        return shadowPane;
    }

}
