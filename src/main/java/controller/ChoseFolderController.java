package controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import mediaplayer.MyMediaPlayer;
import org.dom4j.DocumentException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import service.LoadLocalSongService;
import util.CheckListUtils;
import util.WindowUtils;
import util.XMLUtils;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ChoseFolderController {

    /**右上角"关闭"图标*/
    @FXML
    private Label labCloseIcon;

    /**"将自动扫描您勾选的目录。"Label组件*/
    @FXML
    private Label labTips;

    /**存放组件checkbox的容器，CheckBox为选择的目录路径显示组件*/
    @FXML
    private VBox vWrapCheckBox;

    /**注入窗体根容器（BorderPane）的中间容器的控制器*/
    @Resource
    private MainController mainController;

    /**注入“本地音乐”面板的控制类*/
    @Resource
    private LocalMusicContentController localMusicContentController;

    /**播放器配置文件的存放路径*/
    private String CHOSE_FOLDER_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "config" + File.separator + "chose-folder.xml";

    /**播放器配置文件*/
    private File CHOSE_FOLDER_FILE;

    /**记录存储文件保存的音乐目录的字符串集合*/
    private List<String> folderPathList;

    /**记录ＵＩ界面选择的目录的字符串集合*/
    private List<String> selectedPaths;

    /**注入Spring上下文对象*/
    @Resource
    private ConfigurableApplicationContext applicationContext;

    /**注入自定义的播放器对象*/
    @Resource
    private MyMediaPlayer myMediaPlayer;

    /**注入底部显示音乐进度的控制器类*/
    @Resource
    private BottomController bottomController;

    public List<String> getSelectedPaths() {
        return selectedPaths;
    }

    public void initialize() throws IOException, DocumentException {
        CHOSE_FOLDER_FILE = new File(CHOSE_FOLDER_PATH);
        if (!CHOSE_FOLDER_FILE.exists()){  //如果文件不存在，创建文件
            CHOSE_FOLDER_FILE.createNewFile();                          //创建XML文件
            XMLUtils.createXML(CHOSE_FOLDER_FILE,"FolderList");//添加根元素
        }
        else {   //文件存在，读取记录
            folderPathList =  XMLUtils.getAllRecord(CHOSE_FOLDER_FILE,"Folder","path");
            for (String folderPath:folderPathList){
                CheckBox checkBox = new CheckBox(folderPath);  //创建CheckBox组件
                checkBox.getStylesheets().add("css/CheckBoxStyle.css"); //添加样式
                checkBox.setSelected(true);
                vWrapCheckBox.getChildren().add(checkBox);     //添加组件
            }
        }

    }

    /**右上角关闭图标的事件处理*/
    @FXML
    public void onClickedCloseIcon(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){  //鼠标左击
            ((Stage)labCloseIcon.getScene().getWindow()).close();      //关闭窗口
            WindowUtils.releaseBorderPane(mainController.getBorderPane());
        }
    }

    /**”确定“按钮的事件处理*/
    @FXML
    public void onConfirmAction(ActionEvent actionEvent) throws Exception {

        //先删除原先的文件，然后再重新创建新文件。
        CHOSE_FOLDER_FILE.delete();
        CHOSE_FOLDER_FILE.createNewFile();                          //创建XML文件
        XMLUtils.createXML(CHOSE_FOLDER_FILE,"FolderList");//添加根元素

        ObservableList<Node> observableList = vWrapCheckBox.getChildren();  //获取vWrapCheckBox的子组件
        observableList.remove(labTips);  //移除用作提示的label组件

        selectedPaths = new ArrayList<>();  //存储选择的目录集合
        for (Node node:observableList){  //遍历所有的checkbox集合
            CheckBox checkBox = (CheckBox)node;
            if (checkBox.isSelected()){
                selectedPaths.add(checkBox.getText());
            }
        }
        for(String pathValue:selectedPaths){  //遍历逐个目录路径保存
            XMLUtils.addOneRecord(CHOSE_FOLDER_FILE,"Folder","path",pathValue);
        }
        labCloseIcon.getScene().getWindow().hide();      //关闭窗口
        WindowUtils.releaseBorderPane(mainController.getBorderPane());

        if (!CheckListUtils.checkWeatherSame(selectedPaths,folderPathList)){   //如果不一样，证明更改了目录，重新加载目录下的歌曲文件
            System.out.println("need to load song");
            LoadLocalSongService loadLocalSongService = applicationContext.getBean(LoadLocalSongService.class);
            localMusicContentController.getProgressIndicator().visibleProperty().bind(loadLocalSongService.runningProperty());
            localMusicContentController.getTableViewSong().itemsProperty().bind(loadLocalSongService.valueProperty());
            loadLocalSongService.start();

            //因为需要重新加载歌曲,所以需要判断是否播放器有歌曲正在播放中
            if (myMediaPlayer.getMediaPlayer()!=null){  //如果媒体播放器对象存在,销毁它
                myMediaPlayer.destroy();    //销毁
                myMediaPlayer.getPlayListSongs().clear();    //清空播放列表
                myMediaPlayer.setPlayListSongs(null);
                bottomController.getLabPlayListCount().setText("0");    //并且更新显示播放列表数量的组件
                localMusicContentController.getLabSongCount().setText("0"); //更新显示歌曲数目的label组件
            }
        }

    }

    /**"添加文件夹"按钮的事件处理*/
    @FXML
    public void onAddFolderAction(ActionEvent actionEvent) throws IOException, DocumentException {
        DirectoryChooser directoryChooser = new DirectoryChooser();  //创建目录选择舞台
        File directory = directoryChooser.showDialog(labCloseIcon.getScene().getWindow());  //设置文件目录框的owner
        if(directory!=null) {  //选择了目录
            String folderPath=directory.getPath();  //提取目录的路径
            ObservableList<Node> observableList = vWrapCheckBox.getChildren();  //获取vWrapCheckBox的所有子组件
            for (Node node : observableList){  //如果observableList中已经有选择的folderPath，直接返回，不做处理
                if (node instanceof CheckBox){
                    if (((CheckBox)node).getText().equals(folderPath)){
                        return;
                    }
                }
            }
            CheckBox checkBox = new CheckBox(folderPath);  //创建CheckBox组件
            checkBox.getStylesheets().add("css/CheckBoxStyle.css"); //添加样式
            checkBox.setSelected(true);
            vWrapCheckBox.getChildren().add(checkBox);     //添加组件
        }
    }
}
