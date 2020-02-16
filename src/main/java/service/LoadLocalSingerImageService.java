package service;

import controller.content.LocalMusicContentController;
import dao.SingerDao;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import model.LocalSinger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.Singer;
import util.ImageUtils;
import util.SongUtils;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 20-2-9
 */
@Service
@Scope("prototype")
public class LoadLocalSingerImageService extends javafx.concurrent.Service<Boolean> {

    @Resource
    private LocalMusicContentController localMusicContentController;

    @Resource
    private SingerDao singerDao;

    @Override
    protected Task<Boolean> createTask() {
        Task<Boolean> task = new Task<Boolean>() {


            @Override
            protected Boolean call() throws Exception {

                ObservableList<LocalSinger> observableLocalSingerList = localMusicContentController.getTableViewSinger().getItems();

                for (int i = 0; i < observableLocalSingerList.size(); i++) {
                    Label labSinger = observableLocalSingerList.get(i).getLabSinger();
                    if (!SongUtils.isCharacterCategory(labSinger.getText())){   //如果不是字母分类行
                        /**歌手图片*/
                        Singer singer = singerDao.querySinger(labSinger.getText()); //数据库查询歌手图片url
                        if (singer != null){    //不为空，证明数据库查询到用户对象
                            Platform.runLater(()->{
                                Image imageSinger = new Image(singer.getImageURL(),48,48,false,true); //根据查询得到的url创建图片对象
                                if (!imageSinger.isError()){ //如果没错误，设置歌手图片
                                    labSinger.setGraphic(ImageUtils.createImageView(imageSinger,48,48));    //设置Label显示图片
                                }
                            });
                        }
                    }
                }

                return true;
            }
        };
        return task;
    }
}
