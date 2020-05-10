package util;

import com.alibaba.fastjson.JSON;
import mediaplayer.PlayerStatus;
import model.PlayListSong;
import pojo.User;

import java.io.*;

/**
 * @author super lollipop
 * @date 19-12-7
 */
public final class JSONObjectUtils {

    /**根据存储文件解析出User对象的函数
     * @param file 存储文件
     * @return User*/
    public static User parseUser(File file) throws IOException {
        return JSON.parseObject(readObject(file),User.class);
    }

    /**根据存储文件解析出MediaPlayerState对象的函数
     * @param file 存储文件
     * @return MediaPlayerState*/
    public static PlayerStatus parseMediaPlayerState(File file) throws IOException {
        return JSON.parseObject(readObject(file), PlayerStatus.class);
    }

    public static PlayListSong parsePlayListSong(Object object) throws IOException{
        return JSON.parseObject(object.toString(),PlayListSong.class);
    }

    /**保存对象为Json文件的函数
     * @param object 对象
     * @param file 存储文件*/
    public static void saveObject(Object object,File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);  //获取输出流
        fileOutputStream.write(JSON.toJSONString(object).getBytes());  //写入字节
        fileOutputStream.close(); //关闭
    }

    /**将Json文件读取成String的函数
     * @param file 存储文件
     * @return String*/
    private static String readObject(File file) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line="";
        StringBuffer stringBuffer = new StringBuffer();
        while ((line=bufferedReader.readLine())!=null){
            stringBuffer.append(line);
        }
        return stringBuffer.toString();
    }


}
