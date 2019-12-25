package util;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import static com.sun.jna.platform.win32.WinUser.GWL_STYLE;

/**
 * 使窗体在initStayle为UNDECORATED时能够缩放的类
 * **/
public final class WindowUtils {

	/**记录鼠标按下时需要记录的某个X，Y坐标*/
	private static double mousePressedForResizeX;
	private static double mousePressedForResizeY;

	/**
	 * 设置stage对象能够拖拽边缘的像素实现缩放的静态方法
	 * @param stage 需要添加自由缩放的舞台对象
	 * @param stageMinHeight 缩放的最小高度
	 * @param stageMinWidth  缩放的最小宽度
	 */
	public static void addResizable(Stage stage,double stageMinWidth,double stageMinHeight) {
		try {
			//记录stage的scene对象
			Scene scene=stage.getScene();
			//记录鼠标按下时的scene坐标
			scene.setOnMousePressed(e->{
				mousePressedForResizeX=e.getSceneX();
				mousePressedForResizeY=e.getSceneY();
			});
			scene.setOnMouseMoved(e -> {
				if (!stage.isMaximized()) {
					if (e.getSceneX() <= 10 && e.getSceneY() > 10 && stage.getHeight() - e.getSceneY() > 10) {
						// 改变鼠标的形状
						scene.setCursor(Cursor.W_RESIZE);
					} else if (stage.getWidth() - e.getSceneX() <= 10 && e.getSceneY() > 10 && stage.getHeight() - e.getSceneY() > 10) {
						scene.setCursor(Cursor.E_RESIZE);
					} else if (e.getSceneY() <= 10 && e.getSceneX() > 10 && stage.getWidth() - e.getSceneX() > 10) {
						scene.setCursor(Cursor.N_RESIZE);
					} else if (stage.getHeight() - e.getSceneY() <= 10 && e.getSceneX() > 10 && stage.getWidth() - e.getSceneX() > 10) {
						scene.setCursor(Cursor.S_RESIZE);
					} else if (e.getSceneX() <= 10 && e.getSceneY() <= 10) {
						scene.setCursor(Cursor.NW_RESIZE);
					} else if (stage.getWidth() - e.getSceneX() <= 10 && e.getSceneY() <= 10) {
						scene.setCursor(Cursor.NE_RESIZE);
					} else if (e.getSceneX() <= 10 && stage.getHeight() - e.getSceneY() <= 10) {
						scene.setCursor(Cursor.SW_RESIZE);
					} else if (stage.getWidth() - e.getSceneX() <= 10 && stage.getHeight() - e.getSceneY() <= 10) {
						scene.setCursor(Cursor.SE_RESIZE);
					} else {
						scene.setCursor(Cursor.DEFAULT);
					}
				}
			});
			scene.setOnMouseDragged(e -> {
				if(scene.getCursor()==Cursor.S_RESIZE){
					if ((stage.getHeight() + (e.getSceneY() - mousePressedForResizeY) >= stageMinHeight
							&& e.getScreenY() < Screen.getPrimary().getVisualBounds().getHeight()-2)) {
						stage.setHeight(stage.getHeight() + (e.getSceneY() - mousePressedForResizeY));
						mousePressedForResizeY = e.getSceneY();
					}
				}
				else if(scene.getCursor()==Cursor.E_RESIZE){
					if (stage.getWidth() + (e.getSceneX() - mousePressedForResizeX) >= stageMinWidth
							&& e.getScreenX() < Screen.getPrimary().getVisualBounds().getWidth()-2) {
						stage.setWidth(stage.getWidth() + (e.getSceneX() - mousePressedForResizeX));
						mousePressedForResizeX = e.getSceneX();
					}
				}
				else if(scene.getCursor()==Cursor.SE_RESIZE){
					if ((stage.getHeight() + (e.getSceneY() - mousePressedForResizeY) >= stageMinHeight
							&& e.getScreenY() < Screen.getPrimary().getVisualBounds().getHeight()-2)) {
						stage.setHeight(stage.getHeight() + (e.getSceneY() - mousePressedForResizeY));
						mousePressedForResizeY = e.getSceneY();
					}
					if (stage.getWidth() + (e.getSceneX() - mousePressedForResizeX) >= stageMinWidth
							&& e.getScreenX() < Screen.getPrimary().getVisualBounds().getWidth()-2) {
						stage.setWidth(stage.getWidth() + (e.getSceneX() - mousePressedForResizeX));
						mousePressedForResizeX = e.getSceneX();
					}
				}
				else if(scene.getCursor()==Cursor.N_RESIZE){
					if ((stage.getHeight() + (mousePressedForResizeY-e.getSceneY() ) >= stageMinHeight
							&& e.getScreenY() < Screen.getPrimary().getVisualBounds().getHeight()-2)) {
						stage.setHeight(stage.getY() - e.getScreenY() + stage.getHeight());
						stage.setY(e.getScreenY());
					}
				}
				else if(scene.getCursor()==Cursor.W_RESIZE){
					if ((stage.getWidth() + (mousePressedForResizeX-e.getSceneX() ) >= stageMinWidth
							&& e.getScreenX() < Screen.getPrimary().getVisualBounds().getWidth()-2)) {
						stage.setWidth(stage.getX() - e.getScreenX() + stage.getWidth());
						stage.setX(e.getScreenX());
					}
				}
				else if(scene.getCursor()==Cursor.NW_RESIZE){
					if ((stage.getHeight() + (mousePressedForResizeY-e.getSceneY() ) >= stageMinHeight
							&& e.getScreenY() < Screen.getPrimary().getVisualBounds().getHeight()-2)) {
						stage.setHeight(stage.getY() - e.getScreenY() + stage.getHeight());
						stage.setY(e.getScreenY());
					}
					if ((stage.getWidth() + (mousePressedForResizeX-e.getSceneX() ) >= stageMinWidth
							&& e.getScreenX() < Screen.getPrimary().getVisualBounds().getWidth()-2)) {
						stage.setWidth(stage.getX() - e.getScreenX() + stage.getWidth());
						stage.setX(e.getScreenX());
					}
				}
				else if(scene.getCursor()==Cursor.NE_RESIZE){
					if ((stage.getHeight() + (mousePressedForResizeY-e.getSceneY() ) >= stageMinHeight
							&& e.getScreenY() < Screen.getPrimary().getVisualBounds().getHeight()-2)) {
						stage.setHeight(stage.getY() - e.getScreenY() + stage.getHeight());
						stage.setY(e.getScreenY());
					}
					if (stage.getWidth() + (e.getSceneX() - mousePressedForResizeX) >= stageMinWidth
							&& e.getScreenX() < Screen.getPrimary().getVisualBounds().getWidth()-2) {
						stage.setWidth(stage.getWidth() + (e.getSceneX() - mousePressedForResizeX));
						mousePressedForResizeX = e.getSceneX();
					}
				}
				else if(scene.getCursor()==Cursor.SW_RESIZE){
					if ((stage.getWidth() + (mousePressedForResizeX-e.getSceneX() ) >= stageMinWidth
							&& e.getScreenX() < Screen.getPrimary().getVisualBounds().getWidth()-2)) {
						stage.setWidth(stage.getX() - e.getScreenX() + stage.getWidth());
						stage.setX(e.getScreenX());
					}
					if ((stage.getHeight() + (e.getSceneY() - mousePressedForResizeY) >= stageMinHeight
							&& e.getScreenY() < Screen.getPrimary().getVisualBounds().getHeight()-2)) {
						stage.setHeight(stage.getHeight() + (e.getSceneY() - mousePressedForResizeY));
						mousePressedForResizeY = e.getSceneY();
					}
				}
			});
		}catch (Exception e) {
			System.out.println("舞台对象未设置scene对象");
			return;
		}
	}


	/**
	 * 下面这段代码是使Windows平台任务栏图标响应单击事件，当stage的initStyle设置成UNDECORATED时，任务栏图标单击无法最小化窗体
	 * 参见StackOverflow的提问：https://stackoverflow.com/questions/26972683/javafx-minimizing-undecorated-stage
	 * **/
	public static void addWindowsPlatformTaskBarIconifyBehavior(){
		if (WindowUtils.isWindowsPlatform()){  //判断当前os是否为Windows，如果是才执行
			long lhwnd = com.sun.glass.ui.Window.getWindows().get(0).getNativeWindow();
			Pointer lpVoid = new Pointer(lhwnd);
			WinDef.HWND hwnd = new WinDef.HWND(lpVoid);
			final User32 user32 = User32.INSTANCE;
			int oldStyle = user32.GetWindowLong(hwnd, GWL_STYLE);
			int newStyle = oldStyle | 0x00020000;//WS_MINIMIZEBOX
			user32.SetWindowLong(hwnd, GWL_STYLE, newStyle);
		}
	}

	/**判断是否为Windows平台的函数
	 * @return boolean*/
	public static boolean isWindowsPlatform(){
		return System.getProperties().getProperty("os.name").contains("Windows") ? true : false;
	}

	/**为窗体primaryStage添加放置居中和最大化时图标化恢复时窗口大小为可视化大小
	 * @param primaryStage 主舞台对象
	 * */
	public static void addLocateCenter(Stage primaryStage){
		Platform.runLater(()->{
			// 获取屏幕可视化的宽高（Except TaskBar），把窗体设置在可视化的区域居中
			primaryStage.setX((Screen.getPrimary().getVisualBounds().getWidth() - primaryStage.getWidth()) / 2.0);
			primaryStage.setY((Screen.getPrimary().getVisualBounds().getHeight() - primaryStage.getHeight()) / 2.0);
		});

		primaryStage.iconifiedProperty().addListener((observable, oldValue, newValue) -> {
			//确保窗体在最大化状态下最小化后，单击任务栏图标显示时占据的屏幕大小是可视化的全屏
			if (primaryStage.isMaximized()){
				primaryStage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
				primaryStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
			}
		});

		primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue){
				((BorderPane)primaryStage.getScene().getRoot()).setPadding(new Insets(0));
			}
			else {
				((BorderPane)primaryStage.getScene().getRoot()).setPadding(new Insets(10));
			}
		});
	}

	/**阻止主舞台的borderPane响应鼠标事件和改变不透明度的函数
	 * @param borderPane 主窗体根容器borderPane
	 * */
	public static void blockBorderPane(BorderPane borderPane){
		//设置主舞台界面borderPane除了顶部的titleBar部分外，其它的部分都不响应鼠标事件
		borderPane.getCenter().setMouseTransparent(true);
		borderPane.getBottom().setMouseTransparent(true);
		//顺便设置不透明色，方便提示查看
		borderPane.getCenter().setOpacity(0.4);
		borderPane.getBottom().setOpacity(0.4);
	}

	/**@param borderPane 主窗体根容器borderPane
	 * 还原主舞台的borderPane响应鼠标事件和改变不透明度的函数*/
	public static void releaseBorderPane(BorderPane borderPane){
		//设置主舞台界面borderPane除了顶部的titleBar部分外，其它的部分都不响应鼠标事件
		borderPane.getCenter().setMouseTransparent(false);
		borderPane.getBottom().setMouseTransparent(false);
		//顺便设置不透明色，方便提示查看
		borderPane.getCenter().setOpacity(1);
		borderPane.getBottom().setOpacity(1);
	}

	/**弹出信息提示动画的函数
	 * @param stackPane 存放labFading组件的容器
	 * @param labFading 逐渐淡出的Label组件*/
	public static void toastInfo(StackPane stackPane,Label labFading){
		//设置显示Label的样式
		labFading.setStyle("-fx-alignment: center;\n" +
				"    -fx-text-fill: white;\n" +
				"    -fx-font-size: 14px;\n" +
				"    -fx-background-color: #353535;\n" +
				"    -fx-pref-width: 280;\n" +
				"    -fx-pref-height: 50;\n" +
				"    -fx-background-radius: 1em;");

		FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2),labFading);
		fadeTransition.setFromValue(1);
		fadeTransition.setToValue(0);
		stackPane.getChildren().add(labFading);
		//动画完成后移除label组件
		fadeTransition.setOnFinished(fade->{
			stackPane.getChildren().remove(1);
		});
		//开始播放渐变动画提示
		fadeTransition.play();
	}
}
