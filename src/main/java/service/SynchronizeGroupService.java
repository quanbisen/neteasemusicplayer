package service;

import com.alibaba.fastjson.JSON;
import controller.main.LeftController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import mediaplayer.Config;
import mediaplayer.UserStatus;
import org.dom4j.DocumentException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.Group;
import pojo.User;
import util.HttpClientUtils;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author super lollipop
 * @date 20-2-18
 */
@Service
@Scope("singleton")
public class SynchronizeGroupService extends javafx.concurrent.ScheduledService<Void> {

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
                System.out.println("schedule");
                User user = applicationContext.getBean(UserStatus.class).getUser();
                if (user != null){  //用户存在
                    try {
                        String url = applicationContext.getBean(Config.class).getGroupURL() + "/query/" + user.getToken();
                        String responseString = HttpClientUtils.executeGet(url);    //执行查询歌单
                        List<Group> groupList = JSON.parseArray(responseString,Group.class);
                        if (groupList != null && groupList.size() > 0){
                            Platform.runLater(()->{
                                try {
                                    leftController.updateGroup(groupList);  //调用更新歌单分组函数
                                } catch (DocumentException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else { //否则,取消定时任务
                    this.cancel();
                }
                return null;
            }
        };
        return task;
    }


}
