package service;

import controller.content.LocalMusicContentController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import model.LocalAlbum;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.Album;
import util.ImageUtils;
import util.SongUtils;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author super lollipop
 * @date 20-2-10
 */
@Service
@Scope("prototype")
public class LoadLocalAlbumImageService extends javafx.concurrent.Service<Boolean> {

    @Resource
    private LocalMusicContentController localMusicContentController;


    @Override
    protected Task<Boolean> createTask() {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                //获取专辑表格所有的专辑集合
//                ObservableList<LocalAlbum> observableLocalAlbumList = localMusicContentController.getTableViewAlbum().getItems();
//
//                for (int i = 0; i < observableLocalAlbumList.size(); i++) {
//                    Label labAlbum = observableLocalAlbumList.get(i).getLabAlbum();
//                    if (!SongUtils.isCharacterCategory(labAlbum.getText())){   //如果不是字母分类行
//                        /**专辑图片*/
//                        Album album = albumDao.queryAlbumByName(labAlbum.getText());
//                        if (album != null){    //不为空，证明数据库查询到对象
//                            Platform.runLater(()->{
//                                Image imageAlbum = new Image(album.getImageURL(),48,48,false,true); //根据查询得到的url创建图片对象
//                                if (!imageAlbum.isError()){ //如果没错误，设置歌手图片
//                                    labAlbum.setGraphic(ImageUtils.createImageView(imageAlbum,48,48));    //设置Label显示图片
//                                }
//                            });
//                        }
//                    }
//                }

                return true;
            }
        };
        return task;
    }
}
