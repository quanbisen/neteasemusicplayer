package controller.component;

import application.SpringFXMLLoader;
import controller.content.GroupContentController;
import controller.main.CenterController;
import controller.main.LeftController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import javax.annotation.Resource;
import java.io.IOException;

@Controller
@Scope("prototype")
public class GroupTabController {

    /**歌单tab的容器*/
    @FXML
    private HBox hBoxGroup;

    /**歌单tab的图标*/
    @FXML
    public ImageView ivGroupIcon;

    /**显示歌单名称的Label组件*/
    @FXML
    public Label labGroupName;

    /**注入左边放置标签的容器控制器*/
    @Resource
    private LeftController leftController;

    @Resource
    private CenterController centerController;

    @Resource
    private ApplicationContext applicationContext;

    public HBox getHBoxGroup() {
        return hBoxGroup;
    }

    public ImageView getIvGroupIcon() {
        return ivGroupIcon;
    }

    public Label getLabGroupName() {
        return labGroupName;
    }

    public void initialize() throws IOException {
        ContextMenu contextMenu;
        if (labGroupName.getText().equals("我喜欢的音乐")){   //我喜欢的音乐的右键菜单与歌单的右键歌单不一样，没有“编辑”和”删除“
            contextMenu = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/contextmenu/favorgrouptab-contextmenu.fxml").load();   //加载右键菜单
        }else {
            contextMenu = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/contextmenu/grouptab-contextmenu.fxml").load();   //加载右键菜单
        }
        labGroupName.setContextMenu(contextMenu);   //设置右键菜单
        /**通过设置contextMenu显示和隐藏事件来实现目前选中的是哪一个标签，参见LeftController的的getContextMenuShowingTabName*/
        contextMenu.setOnHidden(event -> {
            System.out.println("hidden");
            hBoxGroup.setMouseTransparent(false);
        });
        contextMenu.setOnShowing(event -> {
            System.out.println("showing");
            hBoxGroup.setMouseTransparent(true);
        });
    }

    @FXML
    public void onClickedGroupTab(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY && leftController.getSelectedTab() != hBoxGroup){
            leftController.setSelectedTab(hBoxGroup);  //设置当前选中的tab
            FXMLLoader fxmlLoader = applicationContext.getBean(SpringFXMLLoader.class).getLoader("/fxml/content/tab-group-content.fxml");
            centerController.getBorderPane().setCenter(fxmlLoader.load());
            if(labGroupName.getText().equals("我喜欢的音乐")){
                GroupContentController groupContentController = fxmlLoader.getController();
                groupContentController.gethBoxDescription().setVisible(false);  //把描述的容器组件设置不可见
            }
        }
    }
}
