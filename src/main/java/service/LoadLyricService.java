package service;

import com.alibaba.fastjson.JSON;
import controller.content.LyricContentController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import mediaplayer.Config;
import mediaplayer.MyMediaPlayer;
import model.PlayListSong;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pojo.Singer;
import pojo.Song;
import util.HttpClientUtils;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * @author super lollipop
 * @date 20-2-29
 */
@Service
@Scope("prototype")
public class LoadLyricService extends javafx.concurrent.Service<Void> {

    @Resource
    private LyricContentController lyricContentController;

    @Resource
    private Config config;

    @Resource
    private MyMediaPlayer myMediaPlayer;

    @Override
    protected Task<Void> createTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    //获取当前播放歌曲
                    PlayListSong playListSong = myMediaPlayer.getCurrentPlaySong();
                    Thread.sleep(100);  //睡眠
                    File lyricFile = null; //歌词文件句柄
                    Path lyricPath = config.getLyricPath();
                    if (!StringUtils.isEmpty(playListSong.getLyricURL())){  //如果是在线资源，就会存在歌词URL
                        lyricFile = new File(lyricPath + File.separator + playListSong.getName() + " - " + playListSong.getSinger() + playListSong.getLyricURL().substring(playListSong.getLyricURL().lastIndexOf(".")));
                        HttpClientUtils.download(playListSong.getLyricURL(),lyricFile);
                    }else {
                        //查找缓存目录有没有歌词文件，根据文件的歌曲名称和歌手名称判断
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
                                song.setAlbumName(playListSong.getAlbum());
                                List<Singer> singerList = new LinkedList<>();   //创建歌手集合对象
                                if (playListSong.getSinger().contains("/")){    //包含两个歌手字符串
                                    String[] singerArray = playListSong.getSinger().split("/"); //切割字符串
                                    for (int i = 0; i < singerArray.length; i++) {
                                        Singer singer = new Singer();
                                        singer.setName(singerArray[i]);
                                        singerList.add(singer);
                                    }
                                }else { //否则，只有一个歌手
                                    Singer singer = new Singer();
                                    singer.setName(playListSong.getSinger());
                                    singerList.add(singer);
                                }
                                song.setSingerList(singerList);
                                StringEntity entity = new StringEntity(JSON.toJSONString(song), ContentType.create("application/json", Charset.forName("UTF-8")));
                                String responseString = HttpClientUtils.executePost(url,entity);    //执行post请求

                                if (responseString != null && responseString.length() > 0){
                                    song = JSON.parseObject(responseString,Song.class);
                                    if (song.getLyricURL() != null){
                                        String lyricURL = song.getLyricURL();
                                        lyricFile = new File(lyricPath + File.separator + playListSong.getName() + " - " + playListSong.getSinger() + lyricURL.substring(lyricURL.lastIndexOf(".")));
                                        HttpClientUtils.download(lyricURL,lyricFile);   //下载歌词文件
                                    }
                                }
                            }catch (HttpHostConnectException e){
                                System.out.println("加载歌词无网络导致异常");
                                return null;
                            }
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
                                lyricContentController.getLyricTimeList().add(second);
                                Platform.runLater(()->{
                                    Label labLyric = new Label(strRow); //创建歌词Label
                                    labLyric.getStyleClass().add("labLyric");   //添加ｃｓｓ类名，在ｃｓｓ文件添加样式,见AlbumLyricStyle.css文件
                                    lyricContentController.getVBoxLyric().getChildren().add(labLyric);
                                });

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }else {
                        Platform.runLater(()->{
                            Label label = new Label("暂无歌词");
                            label.getStyleClass().add("labNoLyric");
                            label.prefWidthProperty().bind(lyricContentController.getVBoxLyric().widthProperty());
                            label.prefHeightProperty().bind(lyricContentController.getVBoxLyric().heightProperty());
                            lyricContentController.getVBoxLyric().getChildren().add(label);
                        });
                    }
                }catch (Exception e){e.printStackTrace();}
                return null;
            }
        };
        return task;
    }
}
