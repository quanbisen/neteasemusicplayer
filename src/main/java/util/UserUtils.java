package util;

import com.alibaba.fastjson.JSON;
import model.User;
import java.io.*;

/**
 * @author super lollipop
 * @date 19-12-7
 */
public final class UserUtils {

    /**根据用户登录文件解析出用户对象的函数
     * @param LOGIN_CONFIG_FILE 登录的存储文件*/
    public static User parseUser(File LOGIN_CONFIG_FILE) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(LOGIN_CONFIG_FILE)));
        String line="";
        StringBuffer stringBuffer = new StringBuffer();
        while ((line=bufferedReader.readLine())!=null){
            stringBuffer.append(line);
        }
        return JSON.parseObject(stringBuffer.toString(),User.class);
    }

    /**保存用户对象为Json文件的函数
     * @param user 用户对象
     * @param LOGIN_CONFIG_FILE 登录配置存储文件*/
    public static void saveUser(User user,File LOGIN_CONFIG_FILE) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(LOGIN_CONFIG_FILE);  //获取输出流
        fileOutputStream.write(JSON.toJSONString(user).getBytes());  //写入字节
        fileOutputStream.close(); //关闭
    }
}
