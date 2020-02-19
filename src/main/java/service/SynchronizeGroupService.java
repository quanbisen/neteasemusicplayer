package service;

import controller.main.LeftController;
import dao.GroupDao;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import mediaplayer.Config;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.Group;
import pojo.User;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author super lollipop
 * @date 20-2-18
 */
@Service
@Scope("prototype")
public class SynchronizeGroupService extends javafx.concurrent.Service<Void> {

    @Resource
    private GroupDao groupDao;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private LeftController leftController;

    @Override
    protected Task<Void> createTask() {
        Task<Void> task = new Task<Void>() {

            /**判断groupList是否存在歌单名称为tabName的歌单，存在返回true，否则返回false
             * @param groupList
             * @param tabName
             * @return boolean*/
            private boolean exist(List<Group> groupList,String tabName){
                for (int i = 0; i < groupList.size(); i++) {
                    if (groupList.get(i).getName().equals(tabName)){
                        return true;
                    }
                }
                return false;
            }

            @Override
            protected Void call() throws Exception {
                System.out.println(Thread.currentThread().getName());
                User user = applicationContext.getBean(Config.class).getUser();
                if (user != null){  //用户存在
                    List<Group> groupList = groupDao.queryAll(user.getId());    //查询用户创建的所有歌单
                    Platform.runLater(()->{
                        groupList.forEach(group -> {    //遍历查询到的歌单集合，逐个添加不存在的到左侧的标签tab栏
                            if (!leftController.exist(group.getName())){    //如果左侧的标签歌单没有查询到的歌单列表，添加
                                try {
                                    leftController.addGroupTab(group.getName());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        List<HBox> tabList = leftController.getTabList();
                        if (tabList.size() >= 6){    //数量大于等于6证明有加载数据库的自定义歌单，从0开始，第四个是“我喜欢的音乐”tab，第五个用户自定义创建的歌单tab
                            int size = tabList.size();
                            for (int i = 5; i < size; i++) {
                                if (!this.exist(groupList,((Label)tabList.get(i).getChildren().get(0)).getText())){   //如果查询到的歌单集合没有包含这个标签，移除它
                                    leftController.removeGroupTab(((Label)tabList.get(i).getChildren().get(0)).getText());
                                }
                            }
                        }
                    });
                }
                return null;
            }
        };
        return task;
    }


}
