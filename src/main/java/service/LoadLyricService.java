package service;

import com.alibaba.fastjson.JSON;
import controller.content.AlbumLyricContentController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import mediaplayer.Config;
import mediaplayer.MyMediaPlayer;
import model.PlayListSong;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pojo.Song;
import util.HttpClientUtils;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author super lollipop
 * @date 20-2-29
 */
@Service
@Scope("prototype")
public class LoadLyricService extends javafx.concurrent.Service<Void> {

    @Resource
    private AlbumLyricContentController albumLyricContentController;

    @Resource
    private Config config;

    @Resource
    private MyMediaPlayer myMediaPlayer;

    @Override
    protected Task<Void> createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                //获取当前播放歌曲
                PlayListSong playListSong = myMediaPlayer.getCurrentPlaySong();
                File lyricFile = null; //歌词文件句柄
                //查找缓存目录有没有歌词文件，根据文件的歌曲名称和歌手名称判断
                Path lyricPath = config.getLyricPath();
                File[] files = lyricPath.toFile().listFiles((dir, name) -> {
                    if (name.contains(playListSong.getName()) && name.contains(playListSong.getSinger()) && name.endsWith(".lrc")) {
                        return true;
                    } else {
                        return false;
                    }
                });
                if (files.length > 0){  //文件数组长度大于０，证明本地查找到了歌词文件
                    lyricFile = files[0];
                }else { //否则，查找在线服务器资源
                    try{
                        //查找在线服务器，是否有相关歌词资源，根据歌名，歌手和专辑查找，三者都匹配才算找到。
                        String url = config.getSongURL() + "/queryLyric";
                        Song song = new Song();
                        song.setName(playListSong.getName());
                        song.setAlbum(playListSong.getAlbum());
                        song.setSinger(playListSong.getSinger());
                        StringEntity entity = new StringEntity(JSON.toJSONString(song), ContentType.create("application/json", Charset.forName("UTF-8")));
                        String responseString = HttpClientUtils.executePost(url,entity);    //执行post请求

                        if (responseString != null){
                            song = JSON.parseObject(responseString,Song.class);
                            String lyricURL = song.getLyricURL();
                            lyricFile = new File(lyricPath + File.separator + playListSong.getName() + " - " + playListSong.getSinger() + lyricURL.substring(lyricURL.lastIndexOf(".")));
                            HttpClientUtils.download(lyricURL,lyricFile);   //下载歌词文件
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                }
                if (lyricFile != null && lyricFile.exists()) {
                    //读取没一行，封装歌词Label
                    BufferedReader bufIn = new BufferedReader(new InputStreamReader(new FileInputStream(lyricFile), "UTF-8"));
                    String row;
                    while ((row = bufIn.readLine()) != null) {
                        if (row.indexOf("[") == -1 || row.indexOf("]") == -1) { //如果没有“[]”样式的行，不是歌词行，退出本次循环
                            continue;
                        }
                        if (row.charAt(1) < '0' || row.charAt(1) > '9') {   //[00:00.76]瞬间 如果第二个字符不是数字格式，也不是歌词，退出本次循环
                            continue;
                        }
                        String strRow = row.trim().substring(row.indexOf("]") + 1);
                        if (strRow.equals("")){     //[00:00.76]    如果歌词存在时间，却没有文本，也退出本次循环
                            continue;
                        }

                        String strTime = row.substring(1, row.indexOf("]"));//00:03.29
                        String strMinute = strTime.substring(0, strTime.indexOf(":"));//取出：分钟
                        String strSecond = strTime.substring(strTime.indexOf(":") + 1);//取出：秒和毫秒
                        double totalMilli = Integer.parseInt(strMinute) * 60 * 1000 + Double.parseDouble(strSecond) * 1000;   //换算为总的毫秒
                        float second = (float) (Math.round(totalMilli / 1000.0 *10) / 10.0);    //将结果描述保留一位小数

                        try {
                            albumLyricContentController.getLyricTimeList().add(second);

                            Label labLyric = new Label(strRow); //创建歌词Label
                            labLyric.getStyleClass().add("labLyric");   //添加ｃｓｓ类名，在ｃｓｓ文件添加样式,见AlbumLyricStyle.css文件
                            albumLyricContentController.getVBoxLyric().getChildren().add(labLyric);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }
                return null;
            }
        };
        return task;
    }
}
