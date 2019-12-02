package controller;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;

@Controller
public class TopController {
    @FXML
    private Label labelMinimize;  //标题栏的最小化Label按钮
    @FXML
    private Label labelMaximize;  //标题栏的最大化Label按钮
    @FXML
    private Label labelExit;  //标题栏的关闭/退出Label按钮
    @FXML
    private BorderPane titleBar;  //包裹标题文字和最小化、最大化、关闭/退出按钮的BorderPane


    public void initialize(){
        labelMinimize.setCursor(Cursor.DEFAULT);
        labelMaximize.setCursor(Cursor.DEFAULT);
        labelExit.setCursor(Cursor.DEFAULT);
        titleBar.setCursor(Cursor.DEFAULT);
    }

    //最小化Label按钮事件处理
    @FXML
    public void onClickedMinimize(MouseEvent mouseEvent){  //最小化按钮鼠标单击事件
        if (mouseEvent.getButton() == MouseButton.PRIMARY){ //如果按下鼠标左键，最小化primaryStage
            Stage primaryStage = (Stage) labelMinimize.getParent().getScene().getWindow();  //窗体primaryStage对象
            primaryStage.setIconified(true);
        }
    }
    @FXML
    public void onEnteredMinimize(MouseEvent mouseEvent){  //最小化按钮鼠标进入事件
        if (labelMinimize.getCursor() == Cursor.DEFAULT){
            labelMinimize.setGraphic(new ImageView(new Image("/image/NeteaseMinimize.png",46,32,false,false,false)));
        }
    }
    @FXML
    public void onExitedMinimize(MouseEvent mouseEvent){  //最小化按钮鼠标推退出事件
        if (labelMinimize.getCursor() == Cursor.DEFAULT){
            labelMinimize.setGraphic(new ImageView(new Image("/image/NeteaseMinimizeDefault.png",46,32,false,false,false)));
        }
    }

    //最大化Label按钮事件处理
    @FXML
    public void onClickedMaximize(MouseEvent mouseEvent){
        if (mouseEvent.getButton() == MouseButton.PRIMARY){  //如果按下鼠标左键，最大化/最小化primaryStage
            Stage primaryStage = (Stage) labelMaximize.getParent().getScene().getWindow();  //窗体primaryStage对象
            if (!primaryStage.isMaximized()){  //如果primaryStage是最小化，设置成最大化
                primaryStage.setMaximized(true);
                labelMaximize.setGraphic(new ImageView(new Image("/image/NeteaseMaximizedDefault.png",46,32,false,false,false)));
                //设置primaryStage高度、宽度为屏幕的可视化高度、宽度（不包括Windows底下的任务栏）
                primaryStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
                primaryStage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
            }
            else {  //如果primaryStage不是最小化，设置成最小化
                primaryStage.setMaximized(false);
                labelMaximize.setGraphic(new ImageView(new Image("/image/NeteaseMaximizeDefault.png",46,32,false,false,false)));
            }
        }
    }
    @FXML
    public void onEnteredMaximize(MouseEvent mouseEvent){  //最大化按钮鼠标进入事件
        if (labelMaximize.getCursor() == Cursor.DEFAULT){
            Stage primaryStage = (Stage) labelMaximize.getParent().getScene().getWindow();  //窗体primaryStage对象
            if (!primaryStage.isMaximized()){
                labelMaximize.setGraphic(new ImageView(new Image("/image/NeteaseMaximize.png",46,32,false,false,false)));
            }
            else {
                labelMaximize.setGraphic(new ImageView(new Image("/image/NeteaseMaximized.png",46,32,false,false,false)));
            }
        }
    }
    @FXML
    public void onExitedMaximize(MouseEvent mouseEvent){  //最大化按钮鼠标推退出事件
        if (labelMaximize.getCursor() == Cursor.DEFAULT){
            Stage primaryStage = (Stage) labelMaximize.getParent().getScene().getWindow();  //窗体primaryStage对象
            if (!primaryStage.isMaximized()){
                labelMaximize.setGraphic(new ImageView(new Image("/image/NeteaseMaximizeDefault.png",46,32,false,false,false)));
            }
            else {
                labelMaximize.setGraphic(new ImageView(new Image("/image/NeteaseMaximizedDefault.png",46,32,false,false,false)));
            }
        }
    }

    //关闭/退出按钮事件处理
    @FXML
    public void onClickedExit(MouseEvent mouseEvent){
        if (mouseEvent.getButton() == MouseButton.PRIMARY){  //如果按下鼠标左键，关闭primaryStage
            Stage primaryStage = (Stage) labelExit.getParent().getScene().getWindow();  //窗体primaryStage对象
            primaryStage.close();
        }
    }
    @FXML
    public void onEnteredExit(MouseEvent mouseEvent){  //关闭/退出按钮鼠标进入事件
        if (labelExit.getCursor() == Cursor.DEFAULT){
            labelExit.setGraphic(new ImageView(new Image("/image/NeteaseExit.png",46,32,false,false,false)));
        }
    }
    @FXML
    public void onExitedExit(MouseEvent mouseEvent){  //关闭/退出按钮鼠标推退出事件
        if (labelExit.getCursor() == Cursor.DEFAULT){
            labelExit.setGraphic(new ImageView(new Image("/image/NeteaseExitDefault.png",46,32,false,false,false)));
        }
    }

    //包裹标题文字和最小化、最大化、关闭/退出按钮的BorderPane拖拽事件
    private double titleBarMousePressedX;  //记录titleBar鼠标按下时的X坐标（即SceneX或X）
    private double titleBarMousePressedY;  //记录titleBar鼠标按下时的Y坐标（即SceneY或Y）
    @FXML
    public void onTitleBarPressed(MouseEvent mouseEvent){  //BorderPane鼠标按下事件
        if (titleBar.getCursor() == Cursor.DEFAULT){
            //如果按下的位置不是最小化、最大化、关闭/退出按钮的范围，记录按下的X、Y坐标
            if (mouseEvent.getSceneX() < titleBar.getWidth()-(labelMinimize.getWidth()+labelMaximize.getWidth()+labelExit.getWidth())){
                titleBarMousePressedX = mouseEvent.getX();
                titleBarMousePressedY = mouseEvent.getY();
            }
        }
    }
    @FXML
    public void onTitleBarDragged(MouseEvent mouseEvent){  //BorderPane鼠标拖拽事件
        if (titleBar.getCursor() == Cursor.DEFAULT){
            if (!labelMinimize.isPressed()&&!labelMaximize.isPressed()&&!labelExit.isPressed()){
                Stage primaryStage = (Stage) titleBar.getParent().getScene().getWindow();
                //如果鼠标的屏幕位置ScreenX、Y在屏幕的可视化区域内，才执行移动窗体操作
                if (0<=mouseEvent.getScreenX()&&mouseEvent.getScreenX()<=Screen.getPrimary().getVisualBounds().getWidth()
                        &&0<=mouseEvent.getScreenY()&&mouseEvent.getScreenY()<=Screen.getPrimary().getVisualBounds().getHeight()){
                    if(primaryStage.isMaximized()){  //如果是最大化状态下拖拽，变为未最大化的状态
                        //记录计算按下鼠标时的百分比(Y坐标不需要计算，因为Y坐标本身没有变化)
                        double validTitleBarWidth = primaryStage.getWidth()-labelMinimize.getWidth()-labelMaximize.getWidth()-labelExit.getWidth();
                        double percentageX=titleBarMousePressedX/validTitleBarWidth;
                        //设置成未最大化的状态
                        primaryStage.setMaximized(false);
                        labelMaximize.setGraphic(new ImageView(new Image("/image/NeteaseMaximizeDefault.png",46,32,false,false,false)));
                        //重新计算未最大化的状态的鼠标按下坐标
                        validTitleBarWidth = primaryStage.getWidth()-labelMinimize.getWidth()-labelMaximize.getWidth()-labelExit.getWidth();
                        titleBarMousePressedX = validTitleBarWidth*percentageX;
                        //更新主舞台的坐标
                        primaryStage.setX(mouseEvent.getScreenX() - titleBarMousePressedX);
                        primaryStage.setY(mouseEvent.getScreenY() - titleBarMousePressedY);
                    }
                    else{  //否则为最大化状态，直接更新主舞台的坐标
                        primaryStage.setX(mouseEvent.getScreenX() - this.titleBarMousePressedX);
                        primaryStage.setY(mouseEvent.getScreenY() - this.titleBarMousePressedY);
                    }
                }
            }
        }
    }
    @FXML
    public void onTitleBarDoubleClicked(MouseEvent mouseEvent){
        if (titleBar.getCursor() == Cursor.DEFAULT){
            //如果鼠标的位置不是最小化、最大化、关闭/退出按钮的范围
            if (mouseEvent.getSceneX() < titleBar.getWidth()-(labelMinimize.getWidth()+labelMaximize.getWidth()+labelExit.getWidth())){
                if (mouseEvent.getClickCount() == 2){
                    this.onClickedMaximize(mouseEvent);
                }
            }
        }
    }
}
