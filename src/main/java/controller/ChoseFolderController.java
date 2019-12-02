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
import org.dom4j.DocumentException;
import org.springframework.stereotype.Controller;
import util.WindowUtils;
import util.XMLUtil;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
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

    /**注入window工具类*/
    @Resource
    private WindowUtils windowUtils;

    /**播放器配置文件的存放路径*/
    private String CONFIG_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "config" + File.separator + "chose-folder.xml";

    /**播放器配置文件*/
    private File CONFIG_FILE;

    /**标记“确定”按钮是否是按下状态*/
    private boolean confirm;

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    @Resource
    private XMLUtil xmlUtil;

    public void initialize() throws IOException, DocumentException {
        CONFIG_FILE = new File(CONFIG_PATH);
        if (!CONFIG_FILE.exists()){  //如果文件不存在，创建文件
            CONFIG_FILE.createNewFile();                          //创建XML文件
            xmlUtil.createXML(CONFIG_FILE,"FolderList");//添加根元素
        }
        else {   //文件存在，读取记录
            List<String> folderPathList =  xmlUtil.getAllRecord(CONFIG_FILE,"Folder","path");
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
            windowUtils.releaseBorderPane(mainController.getBorderPane());
        }
    }

    /**”确定“按钮的事件处理*/
    @FXML
    public void onConfirmAction(ActionEvent actionEvent) throws DocumentException, IOException {
        confirm = true; //设置“确定”被按下
        //先删除原先的文件，然后再重新创建新文件。
        CONFIG_FILE.delete();
        CONFIG_FILE = new File(CONFIG_PATH);
        if (!CONFIG_FILE.exists()){  //如果文件不存在，创建文件
            CONFIG_FILE.createNewFile();                          //创建XML文件
            xmlUtil.createXML(CONFIG_FILE,"FolderList");//添加根元素
        }

        ObservableList<Node> observableList = vWrapCheckBox.getChildren();  //获取vWrapCheckBox的子组件
        observableList.remove(labTips);  //移除用作提示的label组件
        for (Node node:observableList){  //遍历所有的checkbox集合
            CheckBox checkBox = (CheckBox)node;
            if (checkBox.isSelected()){  //如果是选中“打勾”状态
                String pathValue = checkBox.getText();
                if (!xmlUtil.isExist(CONFIG_FILE,"Folder","path",pathValue)){  //如果文件的路径没有在文件中存储，才添加记录进去存储
                    xmlUtil.addOneRecord(CONFIG_FILE,"Folder","path",pathValue);
                }
            }
        }
        labCloseIcon.getScene().getWindow().hide();      //关闭窗口
        windowUtils.releaseBorderPane(mainController.getBorderPane());
    }

    /**"添加文件夹"按钮的事件处理*/
    @FXML
    public void onAddFolderAction(ActionEvent actionEvent) throws IOException, DocumentException {
        DirectoryChooser directoryChooser = new DirectoryChooser();  //创建目录选择舞台
        File directory = directoryChooser.showDialog(labCloseIcon.getScene().getWindow());  //设置文件目录框的owner
        if(directory!=null) {  //选择了目录
            String folderPath=directory.getPath();  //提取目录的路径
            ObservableList<Node> observableList = vWrapCheckBox.getChildren();  //获取vWrapCheckBox的所有子组件
            observableList.remove(labTips);  //移除用作提示的label组件
            for (Node node : observableList){  //如果observableList中已经有选择的folderPath，直接返回，不做处理
                if (((CheckBox)node).getText().contains(folderPath)){
                    return;
                }
            }
            CheckBox checkBox = new CheckBox(folderPath);  //创建CheckBox组件
            checkBox.getStylesheets().add("css/CheckBoxStyle.css"); //添加样式
            checkBox.setSelected(true);
            vWrapCheckBox.getChildren().add(checkBox);     //添加组件
        }
    }
}
