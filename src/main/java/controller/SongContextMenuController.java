package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import mediaplayer.MyMediaPlayer;
import model.LocalSong;
import model.PlayListSong;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.stereotype.Controller;
import util.SongUtils;
import util.WindowUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class SongContextMenuController {

    @Resource
    private LocalMusicContentController localMusicContentController;

    @Resource
    private MainController mainController;

    @Resource
    private MyMediaPlayer myMediaPlayer;

    @Resource
    private BottomController bottomController;


    public void initialize(){

    }

    /**“下一首播放”contextmenu事件处理*/
    @FXML
    public void onClickedNextPlay(ActionEvent actionEvent) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        LocalSong selectedLocalSong = localMusicContentController.getTableViewSong().getSelectionModel().getSelectedItem(); //获取表格选中的歌曲
        PlayListSong playListSong = SongUtils.toPlayListSong(selectedLocalSong);    //转变成播放列表歌曲模型对象
        List<PlayListSong> playListSongs = myMediaPlayer.getPlayListSongs();
        List<Integer> nextPlayIndexList = myMediaPlayer.getNextPlayIndexList();
        if (!playListSongs.contains(playListSong)){  //如果播放列表集合不包含此播放列表歌曲
            if (playListSongs.size() == 0){ //如果播放列表没有歌曲，直接播放
                playListSongs.add(playListSong); //添加到播放列表集合中去
                myMediaPlayer.setCurrentPlayIndex(0);
                myMediaPlayer.playSong(playListSongs.get(myMediaPlayer.getCurrentPlayIndex()));
            }else { //否则，播放列表有歌曲，还需进一步处理
                playListSongs.add(myMediaPlayer.getCurrentPlayIndex()+1,playListSong); //添加到播放列表集合的当前播放索引后面中去
                for (int i = 0; i < nextPlayIndexList.size(); i++) {  //播放列表集合增加了歌曲，记录的索引需要更新处理
                    int indexValue = nextPlayIndexList.get(i);
                    if ( indexValue > myMediaPlayer.getCurrentPlayIndex()){
                        nextPlayIndexList.remove(i);
                        nextPlayIndexList.add(i,indexValue + 1);
                    }
                }
                nextPlayIndexList.add(myMediaPlayer.getCurrentPlayIndex()+1);    //把这个索引记录下来
            }
            bottomController.updatePlayListIcon();  //更新右下角播放列表图标GUI显示信息
        }else { //否则，播放列表存在这首歌曲
            int indexValue = playListSongs.indexOf(playListSong);   //获取得到在播放列表中的索引位置
            if (myMediaPlayer.getCurrentPlayIndex() != indexValue){ //如果不是当前播放的索引
                if (nextPlayIndexList.contains(indexValue)){    //如果
                    nextPlayIndexList.remove((Object)indexValue);
                }
                nextPlayIndexList.add(indexValue);  //执行添加
            }
        }

        nextPlayIndexList.forEach(integer -> System.out.println(integer));
        WindowUtils.toastInfo(mainController.getStackPane(),new Label("已添加到播放列表"));
    }

    /**“删除文件”contextmenu事件处理*/
    @FXML
    public void onClickedDelete(ActionEvent actionEvent) {
        LocalSong selectedLocalSong = localMusicContentController.getTableViewSong().getSelectionModel().getSelectedItem();
        String resource = selectedLocalSong.getResource();
        if (resource!=null && !resource.equals("")){
            File file = new File(resource); //创建文件句柄
            file.delete();  //删除文件
            localMusicContentController.getTableViewSong().getItems().remove(selectedLocalSong);    //GUI更新，移除选中的歌曲行
            WindowUtils.toastInfo(mainController.getStackPane(),new Label("删除歌曲成功"));
        }
    }
}
