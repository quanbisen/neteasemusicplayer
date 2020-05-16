package service;

import com.alibaba.fastjson.JSON;
import controller.content.LocalMusicContentController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import mediaplayer.Config;
import model.LocalAlbum;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.Album;
import pojo.Singer;
import util.HttpClientUtils;
import util.ImageUtils;
import util.SongUtils;

import javax.annotation.Resource;
import java.nio.charset.Charset;
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

    @Resource
    private Config config;

    @Override
    protected Task<Boolean> createTask() {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                //获取专辑表格所有的专辑集合
                ObservableList<LocalAlbum> observableLocalAlbumList = localMusicContentController.getTableViewAlbum().getItems();
                String url = config.getAlbumURL() + "/queryByName/";
                for (int i = 0; i < observableLocalAlbumList.size(); i++) {
                    Label labAlbum = observableLocalAlbumList.get(i).getLabAlbum();
                    if (!SongUtils.isCharacterCategory(labAlbum.getText())){   //如果不是字母分类行
                        /**专辑图片*/
                        try {
                            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().addTextBody("name",labAlbum.getText(), ContentType.create("text/plain", Charset.forName("utf-8")));
                            String responseString = HttpClientUtils.executePost(url,multipartEntityBuilder.build());
                            List<Album> albumList = JSON.parseArray(responseString, Album.class);
                            if (albumList != null && albumList.size() > 0){    //查询得到的专辑对象集合不为空
                                Album album = albumList.get(0);  //取查询得到的第一个
                                Platform.runLater(()->{
                                    Image imageSinger = new Image(album.getImageURL(),48,48,false,true); //根据查询得到的url创建图片对象
                                    if (!imageSinger.isError()){ //如果没错误，设置歌手图片
                                        labAlbum.setGraphic(ImageUtils.createImageView(imageSinger,48,48));    //设置Label显示图片
                                    }
                                });
                            }
                        }catch (HttpHostConnectException e){
                            System.out.println("无网络连接，加载专辑图片任务取消");
                            break;
                        }catch (Exception exception){
                            System.out.println(labAlbum.getText() + " cause exception.");
                        }

                    }
                }

                return true;
            }
        };
        return task;
    }
}
