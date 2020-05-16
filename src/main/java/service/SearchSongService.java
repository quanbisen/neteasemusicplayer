package service;

import com.alibaba.fastjson.JSON;
import controller.content.SearchInputContentController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import mediaplayer.Config;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.context.ApplicationContext;
import pojo.Song;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import util.HttpClientUtils;
import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author super lollipop
 * @date 19-12-10
 * 用户输入搜索文本确认搜索需要启动的服务
 */
@Service
@Scope("prototype")
public class SearchSongService extends javafx.concurrent.Service<ObservableList<Song>> {

    @Resource
    private SearchInputContentController searchInputContentController;

    @Resource
    private Config config;

    @Override
    protected Task<ObservableList<Song>> createTask() {
        Task<ObservableList<Song>> task = new Task<ObservableList<Song>>() {
            @Override
            protected ObservableList<Song> call() throws IOException {
                String url = config.getSongURL() + "/queryByNameSingerAlbumLike";
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
                        .addTextBody("key",searchInputContentController.getTfSearchText().getText(), ContentType.create("text/plain", Charset.forName("utf-8")));
                String responseString = HttpClientUtils.executePost(url,multipartEntityBuilder.build());
                List<Song> songList = JSON.parseArray(responseString,Song.class);
                if (songList != null && songList.size() > 0){
                    ObservableList<Song> observableSongList = FXCollections.observableArrayList();  //获取FX可视化化集合对象
                    observableSongList.addAll(songList);
                    return observableSongList;
                }else {
                    return null;
                }
            }
        };
        return task;
    }
}
