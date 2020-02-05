package util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.LocalSong;
import model.OnlineSong;
import model.PlayListSong;
import model.RecentSong;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.wav.WavTag;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author super lollipop
 * @date 19-12-7
 */
public final class SongUtils {

    /**根据目录获取目录集合下所有的歌曲文件
     * @param folderList 目录字符串集合
     * @return 歌曲文件集合*/
    public static List<File> getSongsFile(List<String> folderList){
        List<File> songsFile = new ArrayList<>();
        for (String folderPath : folderList){
            File folder = new File(folderPath);
            File[] mp3Files = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.endsWith(".mp3") || name.endsWith(".wav")){   //不添加.flac歌曲,MediaPlayer无法播放 || name.endsWith(".flac")
                        return true;
                    }
                    else{
                        return false;
                    }
                }
            });
            if (mp3Files!=null){
                songsFile.addAll(Arrays.asList(mp3Files));
            }
        }
        return songsFile;
    }

    public static ObservableList<LocalSong> getObservableSongList(List<String> folderList)  {
        //设置日志的输出级别，音乐文件解析时有某些音乐文件会输出警告提示在控制台，关闭它方便调试

        Logger.getLogger("org.jaudiotagger").setLevel(Level.SEVERE);
//        Logger.getLogger("org.jaudiotagger.tag").setLevel(Level.OFF);
//        Logger.getLogger("org.jaudiotagger.audio.mp3.MP3File").setLevel(Level.SEVERE);
//        Logger.getLogger("org.jaudiotagger.tag.id3.ID3v23Tag").setLevel(Level.WARNING);

        List<File> songsFile = getSongsFile(folderList);
        ObservableList<LocalSong> observableLocalSongList = FXCollections.observableArrayList();  //获取表格显示内容的集合
        Map<Character,List<LocalSong>> characterListHashMap = new HashMap<>();   //创建存储歌曲歌名首字的拼音字符映射的map
        for (File songFile:songsFile){
            try {
                String name = "";
                String singer = "";
                String album = "";
                String totalTime = "";
                String size = "";
                String resource = "";
                String lyrics = "";
                AudioFile audioFile = AudioFileIO.read(songFile);    //读取歌曲文件
                /**mp3文件的处理部分*/
                if (songFile.getPath().endsWith(".mp3")){
                    MP3File mp3File = (MP3File) audioFile;

                    if (mp3File.hasID3v2Tag()){
                        Set<String> keySet = mp3File.getID3v2Tag().frameMap.keySet();
                        if(keySet.contains("TIT2")){ //读取歌名
                            name = mp3File.getID3v2Tag().frameMap.get("TIT2").toString();
                            if(name!=null&&!name.equals("null")) {
                                name=name.substring(name.indexOf("\"")+1, name.lastIndexOf("\""));
                            }
                        }
                        if(keySet.contains("TPE1")){  //读取歌手
                            singer = mp3File.getID3v2Tag().frameMap.get("TPE1").toString();
                            if(singer!=null&&!singer.equals("null")) {
                                singer=singer.substring(singer.indexOf("\"")+1, singer.lastIndexOf("\""));
                            }
                        }
                        if(keySet.contains("TALB")){  //读取专辑名
                            album = mp3File.getID3v2Tag().frameMap.get("TALB").toString();
                            if(album!=null&&!album.equals("null")) {
                                album=album.substring(album.indexOf("\"")+1, album.lastIndexOf("\""));
                            }
                        }
                    }
                    else if(mp3File.hasID3v1Tag()) {
                        ID3v1Tag id3v1Tag = mp3File.getID3v1Tag();
                        name = id3v1Tag.getFirst(FieldKey.TITLE);
                        singer = id3v1Tag.getFirst(FieldKey.ARTIST);
                        album = id3v1Tag.getFirst(FieldKey.ALBUM);
                    }
                    MP3AudioHeader mp3AudioHeader = (MP3AudioHeader)mp3File.getAudioHeader();
                    totalTime = mp3AudioHeader.getTrackLengthAsString();    //读取总时长，返回字符串类型，如“04：30”
                }
                /**wav文件的处理部分*/
                else if (songFile.getPath().endsWith(".wav")){          //
                    WavTag wavTag = (WavTag)audioFile.getTag();
                    name = wavTag.getFirst(FieldKey.TITLE);
                    singer = wavTag.getFirst(FieldKey.ARTIST);
                    album = wavTag.getFirst(FieldKey.ALBUM);
                    totalTime = TimeUtils.toString(audioFile.getAudioHeader().getTrackLength());
                }
                else if (songFile.getPath().endsWith(".flac")){
                    Tag tag = audioFile.getTag();
                    name = tag.getFirst(FieldKey.TITLE);
                    singer = tag.getFirst(FieldKey.ARTIST);
                    album=tag.getFirst(FieldKey.ALBUM);
                    totalTime = TimeUtils.toString(audioFile.getAudioHeader().getTrackLength());
                }
                String m =String.valueOf(songFile.length()/1024.0/1024.0);
                size=m.substring(0, m.indexOf(".")+3)+"MB";   //文件大小
                resource = songFile.getPath();                //资源路径

                /**trim name,singer,album String*/
                name = name.trim();
                singer = singer.trim();
                album = album.trim();

                char head = Pinyin4jUtils.getFirstPinYinHeadChar(name);
                if (!characterListHashMap.containsKey(head)){   //如果没有这个字符的map映射，创建集合存储
                    List<LocalSong> characterList = new ArrayList<>();
                    characterList.add(new LocalSong(name,singer,album,totalTime,size,resource,lyrics));
                    characterListHashMap.put(head,characterList);
                }
                else {  //否则，就有了这个字符的map映射了，追加到字符对应的value的List集合
                    List<LocalSong> characterListValue = characterListHashMap.get(head);
                    characterListValue.add(new LocalSong(name,singer,album,totalTime,size,resource,lyrics));
                }
            }catch (Exception e){
                System.out.println(songFile.getPath()+" cause exception.");
            }
        }
        for (char c:characterListHashMap.keySet()){ //遍历字符集合map，把对应的value集合添加到observableSongList
            LocalSong localSong = new LocalSong(String.valueOf(c));    //显示字母的对象，对应表格的一行
            observableLocalSongList.add(localSong);
            observableLocalSongList.addAll(characterListHashMap.get(c));
        }

        return observableLocalSongList;
    }


    /**获取表格中的集合实际上是歌曲的行记录
     * @param observableList 表格的item集合
     * @return int 实际上的歌曲数量*/
    public static int getSongCount(ObservableList<LocalSong> observableList){
        int count = 0;
        for (LocalSong localSong :observableList){ //遍历集合
            if (!isCharacterCategory(localSong.getName())){ //如果歌曲的名称不是类别，count加1
                count++;
            }
        }
        return count;
    }

    /**判断是否是类别字符的函数
     * @param name 歌曲名称
     * @return boolean */
    public static boolean isCharacterCategory(String name){
        if (name.length()==1){  //首先判断是否是一个字符
            char character = name.charAt(0);    //取出第一个字符
            if ((character >='A' && character <='Z') || character=='#'){    //如果字符是A-Z或者是#，则返回true
                return true;
            }else{      //否则返回false
                return false;
            }
        }else { //如果不是一个字符，那么肯定不是类别的字母了，直接返回false
            return false;
        }
    }

    /**获取表格items中是可播放的实际歌曲的函数
     * @param tableItems 表格items
     * @return ObservableList<LocalSong>*/
    public static ObservableList<LocalSong> getLocalSongList(List<LocalSong> tableItems){
        ObservableList<LocalSong> observableList = FXCollections.observableArrayList();
        tableItems.forEach(item->{
            if (!SongUtils.isCharacterCategory(item.getName())){
                observableList.add(item);
            }
        });
        return observableList;
    }

    /**把表格中的items转换成播放列表集合的函数
     * @param tableItems 表格items
     * @return ObservableList<PlayListSong>*/
    public static ObservableList<PlayListSong> getPlayListSongs(List<LocalSong> tableItems){
        ObservableList<PlayListSong> observableList = FXCollections.observableArrayList();
        tableItems.forEach(item->{
            if (!SongUtils.isCharacterCategory(item.getName())){
                observableList.add(new PlayListSong(item.getName(),item.getSinger(),item.getAlbum(),item.getTotalTime(),item.getResource()));
            }
        });
        return observableList;
    }

    /**把本地音乐对象模型转换成播放列表模型函数
     * @param localSong
     * @return PlayListSong*/
    public static PlayListSong toPlayListSong(LocalSong localSong){
        return new PlayListSong(localSong.getName(),localSong.getSinger(),localSong.getAlbum(),localSong.getTotalTime(),localSong.getResource());
    }

    /**把在线音乐对象模型转换成播放列表模型函数
     * @param onlineSong
     * @return PlayListSong*/
    public static PlayListSong toPlayListSong(OnlineSong onlineSong){
        return new PlayListSong(onlineSong.getName(),onlineSong.getSinger(),onlineSong.getAlbum(),onlineSong.getTotalTime(),onlineSong.getResource());
    }

    /**把播放列表模型歌曲转变成最近播放歌曲模型
     * @param playListSong
     * @return RecentSong*/
    public static RecentSong toRecentSong(PlayListSong playListSong){
        return new RecentSong(playListSong.getName(),playListSong.getSinger(),playListSong.getAlbum(),playListSong.getTotalTime(),playListSong.getResource());
    }

    /**判断集合playedSongs中的资源字符和参数playListSong中的资源字符是否相等
     * @param playedSongs
     * @param playListSong
     * @return boolean*/
    public static boolean isContains(List<RecentSong> playedSongs,PlayListSong playListSong){
        for (int i = 0; i < playedSongs.size(); i++) {
            if (playedSongs.get(i).getResource().equals(playListSong.getResource()) &&
                    playedSongs.get(i).getName().equals(playListSong.getName()) &&
                            playedSongs.get(i).getSinger().equals(playListSong.getSinger())&&
                            playedSongs.get(i).getAlbum().equals(playListSong.getAlbum())){
                return true;
            }
        }
        return false;
    }

    /**获取播放列表歌曲在最近播放表格items中的位置
     * @param recentSongs
     * @param playListSong
     * @return int */
    public static int getIndex(List<RecentSong> recentSongs,PlayListSong playListSong){
        int index = -1;
        for (int i = 0; i < recentSongs.size(); i++) {
            if (recentSongs.get(i).getResource().equals(playListSong.getResource()) &&
                    recentSongs.get(i).getName().equals(playListSong.getName()) &&
                    recentSongs.get(i).getSinger().equals(playListSong.getSinger())&&
                    recentSongs.get(i).getAlbum().equals(playListSong.getAlbum())){
                index = i;
                break;
            }
        }
        return index;
    }
}
