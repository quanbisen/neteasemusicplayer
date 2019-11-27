package util;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import static com.sun.jna.platform.win32.WinUser.GWL_STYLE;

/**
 * 使窗体在initStayle为UNDECORATED时能够缩放的工具类
 * **/
public final class WindowUtils {
	/**记录鼠标按下时需要记录的某个X，Y坐标*/
	private static double mousePressedForResizeX;
	private static double mousePressedForResizeY;
	/**记录窗体的高度和宽度*/
	private static double stageWidth;
	private static double stageHeigth;
	/**记录屏幕的可视化宽度和高度*/
	static double ScreenWidth = Screen.getPrimary().getVisualBounds().getWidth();
	static double ScreenHeight = Screen.getPrimary().getVisualBounds().getHeight();

	/**
	 * 设置stage对象能够拖拽边缘的像素实现缩放的静态方法
	 */
	public static void addResizable(Stage stage,double stageMinWidth,double stageMinHeight) {
		try {
			//记录stage的scene对象
			Scene scene=stage.getScene();
			stageWidth = stage.getWidth();
			stageHeigth = stage.getHeight();
			//记录鼠标按下时的scene坐标
			scene.setOnMousePressed(e->{
				mousePressedForResizeX=e.getSceneX();
				mousePressedForResizeY=e.getSceneY();
			});
			scene.setOnMouseMoved(e -> {
				if (!stage.isMaximized()) {
					if (e.getSceneX() <= 5 && e.getSceneY() > 5 && stage.getHeight() - e.getSceneY() > 5) {
						// 改变鼠标的形状
						scene.setCursor(Cursor.W_RESIZE);
					} else if (stage.getWidth() - e.getSceneX() <= 5 && e.getSceneY() > 5 && stage.getHeight() - e.getSceneY() > 5) {
						scene.setCursor(Cursor.E_RESIZE);
					} else if (e.getSceneY() <= 5 && e.getSceneX() > 5 && stage.getWidth() - e.getSceneX() > 5) {
						scene.setCursor(Cursor.N_RESIZE);
					} else if (stage.getHeight() - e.getSceneY() <= 5 && e.getSceneX() > 5 && stage.getWidth() - e.getSceneX() > 5) {
						scene.setCursor(Cursor.S_RESIZE);
					} else if (e.getSceneX() <= 5 && e.getSceneY() <= 5) {
						scene.setCursor(Cursor.NW_RESIZE);
					} else if (stage.getWidth() - e.getSceneX() <= 5 && e.getSceneY() <= 5) {
						scene.setCursor(Cursor.NE_RESIZE);
					} else if (e.getSceneX() <= 5 && stage.getHeight() - e.getSceneY() <= 5) {
						scene.setCursor(Cursor.SW_RESIZE);
					} else if (stage.getWidth() - e.getSceneX() <= 5 && stage.getHeight() - e.getSceneY() <= 5) {
						scene.setCursor(Cursor.SE_RESIZE);
					} else {
						scene.setCursor(Cursor.DEFAULT);
					}
				}
			});
			scene.setOnMouseDragged(e -> {
				if(scene.getCursor()==Cursor.S_RESIZE){
					if ((stage.getHeight() + (e.getSceneY() - mousePressedForResizeY) >= stageMinHeight
							&& e.getScreenY() < ScreenHeight-2)) {
						stage.setHeight(stage.getHeight() + (e.getSceneY() - mousePressedForResizeY));
						mousePressedForResizeY = e.getSceneY();
					}
				}
				else if(scene.getCursor()==Cursor.E_RESIZE){
					if (stage.getWidth() + (e.getSceneX() - mousePressedForResizeX) >= stageMinWidth
							&& e.getScreenX() < ScreenWidth-2) {
						stage.setWidth(stage.getWidth() + (e.getSceneX() - mousePressedForResizeX));
						mousePressedForResizeX = e.getSceneX();
					}
				}
				else if(scene.getCursor()==Cursor.SE_RESIZE){
					if ((stage.getHeight() + (e.getSceneY() - mousePressedForResizeY) >= stageMinHeight
							&& e.getScreenY() < ScreenHeight-2)) {
						stage.setHeight(stage.getHeight() + (e.getSceneY() - mousePressedForResizeY));
						mousePressedForResizeY = e.getSceneY();
					}
					if (stage.getWidth() + (e.getSceneX() - mousePressedForResizeX) >= stageMinWidth
							&& e.getScreenX() < ScreenWidth-2) {
						stage.setWidth(stage.getWidth() + (e.getSceneX() - mousePressedForResizeX));
						mousePressedForResizeX = e.getSceneX();
					}
				}
				else if(scene.getCursor()==Cursor.N_RESIZE){
					if ((stage.getHeight() + (mousePressedForResizeY-e.getSceneY() ) >= stageMinHeight
							&& e.getScreenY() < ScreenHeight-2)) {
						stage.setHeight(stage.getY() - e.getScreenY() + stage.getHeight());
						stage.setY(e.getScreenY());
					}
				}
				else if(scene.getCursor()==Cursor.W_RESIZE){
					if ((stage.getWidth() + (mousePressedForResizeX-e.getSceneX() ) >= stageMinWidth
							&& e.getScreenX() < ScreenWidth-2)) {
						stage.setWidth(stage.getX() - e.getScreenX() + stage.getWidth());
						stage.setX(e.getScreenX());
					}
				}
				else if(scene.getCursor()==Cursor.NW_RESIZE){
					if ((stage.getHeight() + (mousePressedForResizeY-e.getSceneY() ) >= stageMinHeight
							&& e.getScreenY() < ScreenHeight-2)) {
						stage.setHeight(stage.getY() - e.getScreenY() + stage.getHeight());
						stage.setY(e.getScreenY());
					}
					if ((stage.getWidth() + (mousePressedForResizeX-e.getSceneX() ) >= stageMinWidth
							&& e.getScreenX() < ScreenWidth-2)) {
						stage.setWidth(stage.getX() - e.getScreenX() + stage.getWidth());
						stage.setX(e.getScreenX());
					}
				}
				else if(scene.getCursor()==Cursor.NE_RESIZE){
					if ((stage.getHeight() + (mousePressedForResizeY-e.getSceneY() ) >= stageMinHeight
							&& e.getScreenY() < ScreenHeight-2)) {
						stage.setHeight(stage.getY() - e.getScreenY() + stage.getHeight());
						stage.setY(e.getScreenY());
					}
					if (stage.getWidth() + (e.getSceneX() - mousePressedForResizeX) >= stageMinWidth
							&& e.getScreenX() < ScreenWidth-2) {
						stage.setWidth(stage.getWidth() + (e.getSceneX() - mousePressedForResizeX));
						mousePressedForResizeX = e.getSceneX();
					}
				}
				else if(scene.getCursor()==Cursor.SW_RESIZE){
					if ((stage.getWidth() + (mousePressedForResizeX-e.getSceneX() ) >= stageMinWidth
							&& e.getScreenX() < ScreenWidth-2)) {
						stage.setWidth(stage.getX() - e.getScreenX() + stage.getWidth());
						stage.setX(e.getScreenX());
					}
					if ((stage.getHeight() + (e.getSceneY() - mousePressedForResizeY) >= stageMinHeight
							&& e.getScreenY() < ScreenHeight-2)) {
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
	/**为窗体primaryStage添加关于UI的修复代码
	 * */
	public static void addFixedCode(Stage primaryStage){
		// 获取屏幕可视化的宽高（Except TaskBar），把窗体设置在可视化的区域居中
		primaryStage.setX((Screen.getPrimary().getVisualBounds().getWidth() - primaryStage.getWidth()) / 2.0);
		primaryStage.setY((Screen.getPrimary().getVisualBounds().getHeight() - primaryStage.getHeight()) / 2.0);

		primaryStage.iconifiedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				//确保窗体在最大化状态下最小化后，单击任务栏图标显示时占据的屏幕大小是可视化的全屏
				if (primaryStage.isMaximized()){
					primaryStage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
					primaryStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
				}
				//修复窗体在非最大化状态下单击最小化按钮最小化窗体后再恢复窗体时最小化按钮图片没有更新的问题
				else {

					ObservableList<Node> labelList = ((HBox)(((BorderPane)(((BorderPane)primaryStage.getScene().getRoot()).getTop())).getRight())).getChildren();
					((Label)labelList.get(0)).setGraphic(new ImageView(new Image("/image/NeteaseMinimizeDefault.png",46,32,false,false,false)));
				}
			}
		});
	}
	/**
	 * 下面这段代码是使Windows平台任务栏图标响应单击事件，当stage的initStyle设置成UNDECORATED时，任务栏图标单击无法最小化窗体
	 * 参见StackOverflow的提问：https://stackoverflow.com/questions/26972683/javafx-minimizing-undecorated-stage
	 * **/
	public static void addWindowsPlatformTaskBarIconifyBehavior(){
		if (System.getProperties().getProperty("os.name").contains("Windows")){  //判断当前os是否为Windows，如果是才执行
			long lhwnd = com.sun.glass.ui.Window.getWindows().get(0).getNativeWindow();
			Pointer lpVoid = new Pointer(lhwnd);
			WinDef.HWND hwnd = new WinDef.HWND(lpVoid);
			final User32 user32 = User32.INSTANCE;
			int oldStyle = user32.GetWindowLong(hwnd, GWL_STYLE);
			int newStyle = oldStyle | 0x00020000;//WS_MINIMIZEBOX
			user32.SetWindowLong(hwnd, GWL_STYLE, newStyle);
		}
	}
}
