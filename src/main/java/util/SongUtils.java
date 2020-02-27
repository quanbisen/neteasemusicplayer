package util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import model.*;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;
import org.jaudiotagger.tag.wav.WavTag;
import pojo.Song;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
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

    public static LocalSong getLocalSong(File songFile){
        //设置日志的输出级别，音乐文件解析时有某些音乐文件会输出警告提示在控制台，关闭它方便调试
        Logger.getLogger("org.jaudiotagger").setLevel(Level.SEVERE);
//        Logger.getLogger("org.jaudiotagger.tag").setLevel(Level.OFF);
//        Logger.getLogger("org.jaudiotagger.audio.mp3.MP3File").setLevel(Level.SEVERE);
//        Logger.getLogger("org.jaudiotagger.tag.id3.ID3v23Tag").setLevel(Level.WARNING);
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
            singer = singer.trim().replace(" ","");
            album = album.trim();
            return new LocalSong(null,name,singer,album,totalTime,size,resource,lyrics);
        }catch (Exception e){
            System.out.println(songFile.getPath()+" cause exception.");
        }
        return null;
    }

    public static byte[] getAlbumBytes(File songFile) throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException {
        MP3File mp3File = new MP3File(songFile);
        if (mp3File.hasID3v2Tag()) {
            try {
                AbstractID3v2Frame abstractID3v2Frame = (AbstractID3v2Frame) mp3File.getID3v2Tag().getFrame("APIC");
                FrameBodyAPIC frameBodyAPIC = (FrameBodyAPIC) abstractID3v2Frame.getBody();
                byte[] imageData = frameBodyAPIC.getImageData();
                return imageData;
            }catch (Exception e){e.printStackTrace();}
        }
        return null;
    }

    /**获取本地音乐“歌曲”tag下的表格内容
     * @param folderList 目录文件集合
     * @return ObservableList<LocalSong> */
    public static ObservableList<LocalSong> getObservableLocalSongList(List<String> folderList)  {

        List<File> songsFile = getSongsFile(folderList);    //根据目录文件集合获取所有的歌曲文件集合
        ObservableList<LocalSong> observableLocalSongList = FXCollections.observableArrayList();  //获取表格显示内容的集合

        Map<Character,List<LocalSong>> characterLocalSongListMap = new HashMap<>();   //创建存储歌曲歌名首字的拼音字符映射的map
        for (File songFile:songsFile){
            LocalSong localSong = getLocalSong(songFile);
            if (localSong != null){
                char head = Pinyin4jUtils.getFirstPinYinHeadChar(localSong.getName());
                if (!characterLocalSongListMap.containsKey(head)){   //如果没有这个字符的map映射，创建集合存储
                    List<LocalSong> characterList = new ArrayList<>();
                    characterList.add(localSong);
                    characterLocalSongListMap.put(head,characterList);
                }
                else {  //否则，就有了这个字符的map映射了，追加到字符对应的value的List集合
                    List<LocalSong> characterListValue = characterLocalSongListMap.get(head);
                    characterListValue.add(localSong);
                }
            }
        }
        for (char c:characterLocalSongListMap.keySet()){ //遍历字符集合map，把对应的value集合添加到observableSongList
            LocalSong localSong = new LocalSong(String.valueOf(c));    //显示字母的对象，对应表格的一行
            observableLocalSongList.add(localSong);
            observableLocalSongList.addAll(characterLocalSongListMap.get(c));
        }
        return observableLocalSongList;
    }

    /**根据"歌曲"tag下的表格内容获取符合参数歌手名称的集合
     * @param observableList "歌曲"tag下的表格内容
     * @param object 行对象
     * @return ObservableList<LocalSinger>*/
    public static ObservableList<LocalSong> getObservableLocalSongListBySingerOrAlbum(ObservableList<LocalSong> observableList,Object object){
        ObservableList<LocalSong> observableLocalSongList = FXCollections.observableArrayList();
        int count = 0;
        String index;
        if (object instanceof LocalSinger){ //“歌手”tag
            for (int i = 0; i < observableList.size(); i++) {
                if (!isCharacterCategory(observableList.get(i).getName()) && observableList.get(i).getSinger().equals(((LocalSinger) object).getLabSinger().getText())){
                    count++;
                    if (count < 10){
                        index = "0" + count;
                    }else {
                        index = String.valueOf(count);
                    }
                    observableList.get(i).setIndex(index);
                    observableLocalSongList.add(observableList.get(i));
                }
            }
            /*observableList.forEach(observableList.get(i) -> {
                if (!isCharacterCategory(localSong.getName()) && localSong.getSinger().equals(((LocalSinger) object).getLabSinger().getText())){
                    observableLocalSongList.add(localSong);
                }
            });*/
        } else if (object instanceof LocalAlbum) { //“专辑”tag
            for (int i = 0; i < observableList.size(); i++) {
                if (!isCharacterCategory(observableList.get(i).getName()) && observableList.get(i).getAlbum().equals(((LocalAlbum) object).getLabAlbum().getText())){
                    count++;
                    if (count < 10){
                        index = "0" + count;
                    }else {
                        index = String.valueOf(count);
                    }
                    observableList.get(i).setIndex(index);
                    observableLocalSongList.add(observableList.get(i));
                }
            }
          /*  observableList.forEach(localSong -> {
                if (!isCharacterCategory(localSong.getName()) && localSong.getAlbum().equals(((LocalAlbum) object).getLabAlbum().getText())){
                    observableLocalSongList.add(localSong);
                }
            });*/
        }
        return observableLocalSongList;
    }


    /**根据"歌曲"tag下的表格内容获取本地音乐“歌手”tag下的表格内容
     * @param observableLocalSongList "歌曲"tag下的表格内容
     * @return ObservableList<LocalSinger>*/
    public static ObservableList<LocalSinger> getObservableLocalSingerList(ObservableList<LocalSong> observableLocalSongList){
        ObservableList<LocalSinger> observableLocalSingerList = FXCollections.observableArrayList();
        Map<Character,List<LocalSinger>> characterLocalSingerListMap = new HashMap<>();
        Image singerImage = new Image("/image/DefaultSinger.png",48,48,false,true); //歌手的默认图片资源
        observableLocalSongList.forEach(localSong -> {  //遍历“歌手”tag下的表格内容
            if (localSong.getSinger() != null && !localSong.getSinger().equals("")){    //判断不为空的
                char singerHead = Pinyin4jUtils.getFirstPinYinHeadChar(localSong.getSinger());  //取出歌手字符串的对应的字母分类
                if (!characterLocalSingerListMap.keySet().contains(singerHead)){    //如果map中没有这个字母分类
                    List<LocalSinger> localSingerList = new LinkedList<>(); //实列化双链表结构集合
                    localSingerList.add(toLocalSinger(observableLocalSongList,localSong,singerImage));  //添加
                    characterLocalSingerListMap.put(singerHead,localSingerList);    //存放到map中
                }else { //否则，则已有这个字母分类
                    List<LocalSinger> localSingerList = characterLocalSingerListMap.get(singerHead);    //去除字母分类对应的集合
                    if (!isContains(localSingerList,localSong)){    //如果分类集合的没有包含
                        localSingerList.add(toLocalSinger(observableLocalSongList,localSong,singerImage));
                    }
                }
            }
        });
        Set set = characterLocalSingerListMap.keySet(); //获取字母分类关键字set
        Object[] arrSet = set.toArray();
        Arrays.sort(arrSet);    //排序
        for (int i = 0; i < arrSet.length; i++) {
            observableLocalSingerList.add(new LocalSinger(new Label(String.valueOf(arrSet[i])),null));
            observableLocalSingerList.addAll(characterLocalSingerListMap.get(arrSet[i]));
        }
        return observableLocalSingerList;
    }

    /**根据"歌曲"tag下的表格内容获取本地音乐“歌手”tag下的表格内容
     * @param observableLocalSongList "歌曲"tag下的表格内容
     * @return ObservableList<LocalSinger>*/
    public static ObservableList<LocalAlbum> getObservableLocalAlbumList(ObservableList<LocalSong> observableLocalSongList){
        ObservableList<LocalAlbum> observableLocalAlbumList = FXCollections.observableArrayList();
        Map<Character,List<LocalAlbum>> characterLocalAlbumListMap = new HashMap<>();
        Image albumImage = new Image("/image/DefaultAlbum.png",48,48,false,true); //歌手的默认图片资源
        observableLocalSongList.forEach(localSong -> {  //遍历“歌手”tag下的表格内容
            if (localSong.getAlbum() != null && !localSong.getAlbum().equals("")){    //判断不为空的
                char singerHead = Pinyin4jUtils.getFirstPinYinHeadChar(localSong.getAlbum());  //取出歌手字符串的对应的字母分类
                if (!characterLocalAlbumListMap.keySet().contains(singerHead)){    //如果map中没有这个字母分类
                    List<LocalAlbum> localAlbumList = new LinkedList<>(); //实列化双链表结构集合
                    localAlbumList.add(toLocalAlbum(observableLocalSongList,localSong,albumImage));  //添加
                    characterLocalAlbumListMap.put(singerHead,localAlbumList);    //存放到map中
                }else { //否则，则已有这个字母分类
                    List<LocalAlbum> localAlbumList = characterLocalAlbumListMap.get(singerHead);    //去除字母分类对应的集合
                    if (!isContains(localAlbumList,localSong)){    //如果分类集合的没有包含
                        localAlbumList.add(toLocalAlbum(observableLocalSongList,localSong,albumImage));
                    }
                }
            }
        });
        Set set = characterLocalAlbumListMap.keySet(); //获取字母分类关键字set
        Object[] arrSet = set.toArray();
        Arrays.sort(arrSet);    //排序
        for (int i = 0; i < arrSet.length; i++) {
            observableLocalAlbumList.add(new LocalAlbum(new Label(String.valueOf(arrSet[i])),null,null));
            observableLocalAlbumList.addAll(characterLocalAlbumListMap.get(arrSet[i]));
        }
        return observableLocalAlbumList;
    }

    /**把LocalSong模型转变成LocalSinger模型
     * @param observableLocalSongList
     * @param localSong
     * @param image
     * @return LocalSinger*/
    public static LocalSinger toLocalSinger(ObservableList<LocalSong> observableLocalSongList,LocalSong localSong,Image image){
        Label labSingerInformation = new Label(localSong.getSinger(),ImageUtils.createImageView(image,48,48));
        labSingerInformation.setGraphicTextGap(15);
        return new LocalSinger(labSingerInformation, getSongCountBySinger(observableLocalSongList, localSong.getSinger()) +"首");
    }

    /**把LocalSong模型转变成LocalAlbum模型
     * @param observableLocalSongList
     * @param localSong
     * @param image
     * @return LocalSinger*/
    public static LocalAlbum toLocalAlbum(ObservableList<LocalSong> observableLocalSongList,LocalSong localSong,Image image){
        Label labAlbumInformation = new Label(localSong.getAlbum(),ImageUtils.createImageView(image,48,48));
        labAlbumInformation.setGraphicTextGap(20);
        return new LocalAlbum(labAlbumInformation,localSong.getSinger(), getSongCountByAlbum(observableLocalSongList, localSong.getAlbum()) +"首");
    }

    /**根据歌手名称获取表格中的符合歌手名称歌曲数目
     * @param observableLocalSongList 歌曲表格的item集合
     * @param singer 歌手名称
     * @return int 歌曲数目*/
    public static int getSongCountBySinger(ObservableList<LocalSong> observableLocalSongList,String singer){
        int count = 0;
        for (int i = 0; i < observableLocalSongList.size(); i++) {
            if (observableLocalSongList.get(i).getSinger() != null &&
                    observableLocalSongList.get(i).getSinger().equals(singer)){
                count++;
            }
        }
        return count;
    }

    /**根据歌手名称获取表格中的符合专辑名称歌曲数目
     * @param observableLocalSongList 歌曲表格的item集合
     * @param album 歌手名称
     * @return int 歌曲数目*/
    public static int getSongCountByAlbum(ObservableList<LocalSong> observableLocalSongList,String album){
        int count = 0;
        for (int i = 0; i < observableLocalSongList.size(); i++) {
            if (observableLocalSongList.get(i).getAlbum() != null &&
                    observableLocalSongList.get(i).getAlbum().equals(album)){
                count++;
            }
        }
        return count;
    }

    /**获取表格中的集合实际上是歌曲的行记录
     * @param observableLocalSongList 歌曲表格的item集合
     * @return int 实际上的歌曲数量*/
    public static int getSongCount(ObservableList<LocalSong> observableLocalSongList){
        int count = 0;
        for (LocalSong localSong :observableLocalSongList){ //遍历集合
            if (!isCharacterCategory(localSong.getName())){ //如果歌曲的名称不是类别，count加1
                count++;
            }
        }
        return count;
    }


    /**获取表格中的集合歌手的数目
     * @param observableLocalSongList 歌曲表格的item集合
     * @return int 歌手的数量*/
    public static int getSingerCount(ObservableList<LocalSong> observableLocalSongList){
        Map<String,Object> map = new HashMap<>();
        observableLocalSongList.forEach(localSong -> {
            if (localSong.getSinger() != null && !localSong.getSinger().equals("")){
                if (!map.keySet().contains(localSong.getSinger().replace(" ",""))){
                    map.put(localSong.getSinger().replace(" ",""),null);
                }
            }
        });
        return map.size();
    }

    /**获取表格中的集合专辑的数目
     * @param observableLocalSongList 歌曲表格的item集合
     * @return int 歌手的数量*/
    public static int getAlbumCount(ObservableList<LocalSong> observableLocalSongList){
        Map<String,Object> map = new HashMap<>();
        observableLocalSongList.forEach(localSong -> {
            if (localSong.getAlbum() != null && !localSong.getSinger().equals("")){
                if (!map.keySet().contains(localSong.getAlbum().replace(" ",""))){
                    map.put(localSong.getAlbum().replace(" ",""),null);
                }
            }

        });
        return map.size();
    }

    /**判断是否是类别字符的函数
     * @param string 字符串
     * @return boolean */
    public static boolean isCharacterCategory(String string){
        if (string.length()==1){  //首先判断是否是一个字符
            char character = string.charAt(0);    //取出第一个字符
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
    public static ObservableList<PlayListSong> getPlayListSongs(List tableItems){
        ObservableList<PlayListSong> observableList = FXCollections.observableArrayList();
        for (int i = 0; i < tableItems.size(); i++) {
            Object tableItem = tableItems.get(i);
            if (tableItem instanceof LocalSong){    //如果是LocalSong模型
                LocalSong item = (LocalSong) tableItem; //拆箱
                if (!SongUtils.isCharacterCategory(item.getName())){
                    observableList.add(new PlayListSong(item.getName(),item.getSinger(),item.getAlbum(),item.getTotalTime(),item.getResource()));
                }
            }else if (tableItem instanceof RecentSong){
                RecentSong item = (RecentSong) tableItem;
                observableList.add(new PlayListSong(item.getName(),item.getSinger(),item.getAlbum(),item.getTotalTime(),item.getResource()));
            }
        }
        return observableList;
    }

    /**把本地音乐对象模型转换成播放列表模型函数
     * @param localSong
     * @return PlayListSong*/
    public static PlayListSong toPlayListSong(LocalSong localSong){
        return new PlayListSong(localSong.getName(),localSong.getSinger(),localSong.getAlbum(),localSong.getTotalTime(),localSong.getResource());
    }

    /**把在线音乐对象模型转换成播放列表模型函数
     * @param song
     * @return PlayListSong*/
    public static PlayListSong toPlayListSong(Song song){
        return new PlayListSong(song.getName(), song.getSinger(), song.getAlbum(), song.getTotalTime(), song.getResourceURL(),song.getLyricURL(),song.getAlbumURL());
    }

    /**把在线音乐对象模型转换成播放列表模型函数
     * @param recentSong
     * @return PlayListSong*/
    public static PlayListSong toPlayListSong(RecentSong recentSong){
        return new PlayListSong(recentSong.getName(),recentSong.getSinger(),recentSong.getAlbum(),recentSong.getTotalTime(),recentSong.getResource());
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

    /**判断List集合的歌手或专辑是否已经包含了localSong的歌手
     * @param list “歌手”或“专辑”tag的表格内容集合
     * @param localSong ”歌曲“模型
     * @return boolean */
    public static boolean isContains(List list,LocalSong localSong){
        if (list.get(0) instanceof LocalSinger){    //LocalSinger 针对“歌手”tag的判断
            for (int i = 0; i < list.size(); i++) {
                if (((LocalSinger)list.get(i)).getLabSinger().getText().equals(localSong.getSinger())){
                    return true;
                }
            }
        }else if (list.get(0) instanceof LocalAlbum){   //LocalAlbum 针对“专辑”tag的判断
            for (int i = 0; i < list.size(); i++) {
                if (((LocalAlbum)list.get(i)).getLabAlbum().getText().equals(localSong.getAlbum())){
                    return true;
                }
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

    /**获取字符string在集合元素中的位置索引
     * @param observableList 表格集合
     * @param string 匹配的字符串
     * @return int*/
    public static int getIndex(ObservableList observableList,String string){
        int index = -1;
        if (observableList.get(0) instanceof LocalSinger){
            for (int i = 0; i < observableList.size(); i++) {
                if (((LocalSinger)observableList.get(i)).getLabSinger().getText().equals(string)){
                    index = i;
                    break;
                }
            }
        } else if (observableList.get(0) instanceof LocalAlbum) {
            for (int i = 0; i < observableList.size(); i++) {
                if (((LocalAlbum)observableList.get(i)).getLabAlbum().getText().equals(string)){
                    index = i;
                    break;
                }
            }
        }
        return index;
    }
}
