package util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Song;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.junit.Test;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
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
                    if (name.endsWith(".mp3")){
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

    public static ObservableList<Song> getObservableSongList(List<String> folderList) throws Exception {
        //设置日志的输出级别，音乐文件解析时有某些音乐文件会输出警告提示在控制台，关闭它方便调试
        Logger.getLogger("org.jaudiotagger").setLevel(Level.SEVERE);
        Logger.getLogger("org.jaudiotagger.tag").setLevel(Level.OFF);
        Logger.getLogger("org.jaudiotagger.audio.mp3.MP3File").setLevel(Level.OFF);
        Logger.getLogger("org.jaudiotagger.tag.id3.ID3v23Tag").setLevel(Level.OFF);

        List<File> songsFile = getSongsFile(folderList);
        ObservableList<Song> observableSongList = FXCollections.observableArrayList();
        for (File songFile:songsFile){
            String name = "";
            String singer = "";
            String album = "";
            String totalTime = "";
            String size = "";
            String resource = "";
            String lyrics = "";

            MP3File mp3File = (MP3File) AudioFileIO.read(songFile);
            MP3AudioHeader mp3AudioHeader = (MP3AudioHeader)mp3File.getAudioHeader();
            totalTime = mp3AudioHeader.getTrackLengthAsString();    //读取总时长，返回字符串类型，如“04：30”
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
            String m =String.valueOf(songFile.length()/1024.0/1024.0);
            size=m.substring(0, m.indexOf(".")+3)+"MB";
            Song song = new Song(name,singer,album,totalTime,size,songFile.getPath(),lyrics);
            observableSongList.add(song);
        }
        return observableSongList;
    }

    @Test
    public void Test() throws Exception {
        List<String> folderList = new ArrayList<>();
        folderList.add("/media/ubuntu/Music/");
        ObservableList<Song> observableList = getObservableSongList(folderList);
        System.out.println(observableList.get(0));
    }
}