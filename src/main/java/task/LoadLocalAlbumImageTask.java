package task;

import controller.content.LocalMusicContentController;
import dao.AlbumDao;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import model.Album;
import model.LocalAlbum;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import util.ImageUtils;
import util.SongUtils;

import javax.annotation.Resource;

/**
 * @author super lollipop
 * @date 20-2-10
 */
@Service
@Scope("prototype")
public class LoadLocalAlbumImageTask extends Task<Boolean> {

    @Resource
    private LocalMusicContentController localMusicContentController;

    @Resource
    private AlbumDao albumDao;

    @Override
    protected Boolean call() throws Exception {
        ObservableList<LocalAlbum> observableLocalAlbumList = localMusicContentController.getTableViewAlbum().getItems();

        for (int i = 0; i < observableLocalAlbumList.size(); i++) {
            Label labAlbum = observableLocalAlbumList.get(i).getLabAlbum();
            if (!SongUtils.isCharacterCategory(labAlbum.getText())){   //如果不是字母分类行
                /**专辑图片*/
                Album album = albumDao.queryAlbumByName(labAlbum.getText()); //数据库查询专辑图片url
                if (album != null){    //不为空，证明数据库查询到对象
                    Platform.runLater(()->{
                        Image imageAlbum = new Image(album.getImageURL58(),48,48,false,true); //根据查询得到的url创建图片对象
                        if (!imageAlbum.isError()){ //如果没错误，设置歌手图片
                            labAlbum.setGraphic(ImageUtils.createImageView(imageAlbum,48,48));    //设置Label显示图片
                        }
                    });
                }
            }
        }
        return true;
    }
}
